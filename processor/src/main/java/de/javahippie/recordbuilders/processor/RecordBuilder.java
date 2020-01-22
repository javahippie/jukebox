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

    public void writeSourceFile(Element record) throws IOException {
        String packageName = record.asType().toString();
        String className = record.getSimpleName() + "Builder";

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(packageName + "Builder");

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.println("package de.javahippie.recordbuilders.testtool;");
            out.println();
            out.println("import " + record.asType().toString() + ";");
            out.println();
            out.println("public class " + className + " {");
            out.println();
            out.println("""
                        public static %s builder() {
                            return new %s();
                        }
                    """.formatted(className, className));
            out.println();
            out.println("   public " + record.getSimpleName() + " build() {");
            out.print("       return new " + record.getSimpleName() + "(");
            out.print(record.getEnclosedElements()
                    .stream()
                    .filter(element -> element.getKind()
                            .equals(ElementKind.RECORD_COMPONENT))
                    .map(o -> o.getSimpleName())
                    .collect(Collectors.joining(", ")));
            out.println(");");
            out.println("   }");
            out.println();
            record.getEnclosedElements()
                    .stream()
                    .filter(element -> element.getKind()
                            .equals(ElementKind.RECORD_COMPONENT))
                    .forEach(component -> {
                        out.println("""
                                    private %s %s;

                                    public %s with%s(%s %s) {
                                        this.%s = %s;
                                        return this;
                                    }
                                """.formatted(component.asType().toString(),
                                component.getSimpleName(),
                                className,
                                component.getSimpleName().toString().substring(0, 1).toUpperCase() + component.getSimpleName().toString().substring(1),
                                component.asType().toString(),
                                component.getSimpleName(),
                                component.getSimpleName(),
                                component.getSimpleName()));
                    });
            out.print("}");
        }
    }

}
