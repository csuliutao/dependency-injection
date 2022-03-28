package com.liko.yuko.injection_compile;

import com.squareup.javapoet.ClassName;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public final class Utils {
    private Utils() {}

    public static String getTag(String name) {
        if (name == null || "".equals(name)) {
            return name;
        }
        return "tag_" + name;
    }

    public static String getClsNameByClassAnnotation(Element element, Class annotation, String method) {
        if (element == null || annotation == null || method == null) {
            return null;
        }

        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        if (annotationMirrors == null) {
            return null;
        }
        for (AnnotationMirror mirror : annotationMirrors) {
            if (mirror != null && ClassName.get(mirror.getAnnotationType())
                    .equals(ClassName.get(annotation))) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> values = mirror.getElementValues();
                if (values == null || values.isEmpty()) {
                    continue;
                }
                for (ExecutableElement executableElement : values.keySet()) {
                    if (executableElement != null && executableElement.getSimpleName().contentEquals(method)) {
                        return values.get(executableElement).toString().replace(".class", "");
                    }
                }
            }
        }

        return null;
    }

    public static String getClsNameByClassAnnotation(Element element, Class annotation) {
        return getClsNameByClassAnnotation(element, annotation, "value");
    }
}
