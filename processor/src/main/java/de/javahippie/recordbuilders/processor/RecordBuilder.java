package de.javahippie.recordbuilders.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("de.javahippie.recordbuilders.annotation.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_14)
public class RecordBuilder extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (TypeElement annotation : annotations) {
                for (Element o : roundEnv.getElementsAnnotatedWith(annotation)) {
                    if (o.getKind().equals(ElementKind.RECORD)) {
                        writeSourceFile(o);
                    }
                }
            }
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    private void writeSourceFile(Element record) throws IOException {
        String fqcn = record.asType().toString();
        int lastDotIndex = fqcn.lastIndexOf('.');
        String packageName = fqcn.substring(0, lastDotIndex);

        String className = "%sBuilder".formatted(record.getSimpleName());

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile("%sBuilder".formatted(fqcn));

        List<Element> recordComponents = record.getEnclosedElements()
                .stream()
                .filter(element -> element.getKind()
                        .equals(ElementKind.RECORD_COMPONENT))
                .collect(Collectors.toList());

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.println("package %s;".formatted(packageName));
            out.println();
            out.println("import %s;".formatted(record.asType().toString()));
            out.println();
            out.println("public class %s {".formatted(className));
            out.println();

            renderFields(recordComponents, out);
            renderBuilderInitializer(className, out);
            renderBuildMethod(record, recordComponents, out);
            renderBuilderMethods(className, recordComponents, out);

            out.print("}");
        }
    }

    private void renderBuilderMethods(String className, List<Element> recordComponents, PrintWriter out) {
        recordComponents
                .forEach(component -> {
                    out.println("""
                                public %s with%s(%s %s) {
                                    this.%s = %s;
                                    return this;
                                }
                            """.formatted(
                            className,
                            component.getSimpleName().toString().substring(0, 1).toUpperCase() + component.getSimpleName().toString().substring(1),
                            component.asType().toString(),
                            component.getSimpleName(),
                            component.getSimpleName(),
                            component.getSimpleName()));
                });
    }

    private void renderBuildMethod(Element record, List<Element> recordComponents, PrintWriter out) {
        out.println("    public %s build() {".formatted(record.getSimpleName()));
        out.print("        return new %s(".formatted(record.getSimpleName()));
        out.print(recordComponents.stream()
                .map(o -> o.getSimpleName())
                .collect(Collectors.joining(", ")));
        out.println(");");
        out.println("    }");
        out.println();
    }

    private void renderBuilderInitializer(String className, PrintWriter out) {
        out.println("""
                    public static %s builder() {
                        return new %s();
                    }
                """.formatted(className, className));
    }

    private void renderFields(List<Element> recordComponents, PrintWriter out) {
        recordComponents.forEach(element -> out.println("   private " + element.asType().toString() + " " + element.getSimpleName() + ";"));
        out.println();
    }

}
