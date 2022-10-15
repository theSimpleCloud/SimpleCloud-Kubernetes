package app.simplecloud.simplecloud.module.processor;

import app.simplecloud.simplecloud.module.api.Module;
import app.simplecloud.simplecloud.module.load.modulefilecontent.ModuleFileContent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.10.22
 * Time: 14:30
 */
@SupportedAnnotationTypes({"app.simplecloud.simplecloud.module.api.Module"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ModuleAnnotationProcessor extends AbstractProcessor {

    private String moduleClassFound;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Module.class)) {
            if (checkForClasses(element)) {
                return false;
            }

            Name qualifiedName = ((TypeElement) element).getQualifiedName();
            if (checkForModuleClassFound(qualifiedName)) {
                return false;
            }

            saveModuleFile(element, qualifiedName);
        }

        return true;
    }

    private void saveModuleFile(Element element, Name qualifiedName) {
        Module module = element.getAnnotation(Module.class);
        ModuleFileContent fileContent = constructModuleFileContent(qualifiedName, module);

        try {
            FileObject fileObject = this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "module.yml");

            try (Writer writer = fileObject.openWriter()) {
                writer
                        .append("name: " + fileContent.getName())
                        .append("main:" + fileContent.getMain())
                        .append("author:" + fileContent.getAuthor());
                //TODO: Improve file saving

                this.moduleClassFound = fileObject.getName();
            }

        } catch (IOException exception) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error while generating module.yml");
        }
    }

    @NotNull
    private static ModuleFileContent constructModuleFileContent(Name qualifiedName, Module module) {
        return new ModuleFileContent(
                module.name(),
                qualifiedName.toString(),
                module.author(),
                module.depend(),
                module.softDepend()
        );
    }

    private boolean checkForModuleClassFound(Name qualifiedName) {
        if (Objects.equals(this.moduleClassFound, qualifiedName.toString())) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Duplicated module class found");
            return true;
        }
        return false;
    }

    private boolean checkForClasses(Element element) {
        if (element.getKind() != ElementKind.CLASS) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated with " + Module.class.getCanonicalName());
            return true;
        }

        return false;
    }

}
