package net.javahippie.jukebox.processor;

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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is used to process the {@code net.javahippie.recordbuilders.annotation.Builder} annotation. It will create a builder class for
 * every record on the classpath that is annotated with that annotation.
 */
@SupportedAnnotationTypes("net.javahippie.jukebox.annotation.Builder")
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
            renderDocumentationHeader(record.toString(), out);
            out.println("public class %s {".formatted(className));
            out.println();
            renderFields(recordComponents, out);
            renderPrivateConstructor(className, out);
            renderBuilderInitializer(className, out);
            renderBuildMethod(record, recordComponents, out);
            renderBuilderMethods(className, recordComponents, out);

            out.print("}");
        }
    }

    private void renderDocumentationHeader(String recordName, PrintWriter out) {
        out.println("""
                /**
                 * This class is an implementation of the builder pattern for the record %s.
                 * It was automatically generated on %s in timezone %s.
                 */
                """.formatted(recordName, LocalDateTime.now(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                ZoneId.systemDefault().toString()));
    }

    private void renderPrivateConstructor(String className, PrintWriter out) {
        out.println("""
                    private %s() {
                        //Instances of this class should be created with the static method 'build()', not the constructor
                    }
                """.formatted(className));
    }

    private void renderBuilderMethods(String className, List<Element> recordComponents, PrintWriter out) {
        recordComponents
                .forEach(component -> {
                    out.println("""
                                public %s %s(%s %s) {
                                    this.%s = %s;
                                    return this;
                                }
                            """.formatted(
                            className,
                            component.getSimpleName(),
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
        recordComponents.forEach(element -> out.println("    private " + element.asType().toString() + " " + element.getSimpleName() + ";"));
        out.println();
    }

}
