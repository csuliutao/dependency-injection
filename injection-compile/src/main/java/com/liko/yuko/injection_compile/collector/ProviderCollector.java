package com.liko.yuko.injection_compile.collector;

import com.liko.yuko.injection.Provider;
import com.liko.yuko.injection_compile.bean.ProviderBean;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class ProviderCollector implements Collector<ProviderBean>{
    @Override
    public Set<ProviderBean> collect(RoundEnvironment roundEnvironment, Elements elementUtils) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Provider.class);
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        HashSet<ProviderBean> beans = new HashSet<>(elements.size() * 3 / 4 + 1);
        for (Element element : elements) {
            if (element.getKind() != ElementKind.CONSTRUCTOR && element.getKind() != ElementKind.METHOD) {
                continue;
            }
            if (element.getEnclosingElement().getKind() != ElementKind.CLASS) {
                throw new RuntimeException("provider method must in class, " +
                        element.getEnclosingElement().asType().toString() + ","
                        + element.getSimpleName());
            }

            ProviderBean bean = new ProviderBean();
            TypeElement cls = ((TypeElement)element.getEnclosingElement());
            bean.clsPkg = elementUtils.getPackageOf(cls).toString();
            bean.clsName = cls.getQualifiedName().toString().replace(bean.clsPkg + '.', "");

            Element pre = element.getEnclosingElement();
            Element now = pre.getEnclosingElement();
            while (now != null && now.getKind() != ElementKind.PACKAGE) {
                if (!pre.getModifiers().contains(Modifier.STATIC)) {
                    throw new RuntimeException("Inner class must static, " +
                            bean.clsPkg + '.' + bean.clsName);
                }

                pre = now;
                now = pre.getEnclosingElement();
            }

            bean.isConstructor = element.getKind() == ElementKind.CONSTRUCTOR;
            bean.isStatic = element.getModifiers().contains(Modifier.STATIC);
            bean.methodName = element.getSimpleName().toString();

            String provideCls = element.getAnnotation(Provider.class).name();
            if (provideCls == null || "".equals(provideCls)) {
                if (element.getKind() == ElementKind.CONSTRUCTOR) {
                    bean.providerPkg = bean.clsPkg;
                    bean.providerName = bean.clsName;
                } else {
                    provideCls = ((ExecutableElement) element).getReturnType().toString();
                    TypeElement proCls = elementUtils.getTypeElement(provideCls);
                    bean.providerPkg = elementUtils.getPackageOf(proCls).toString();
                    bean.providerName = proCls.getQualifiedName().toString()
                            .replace(bean.providerPkg + '.', "");
                }
            } else {
                TypeElement proCls = elementUtils.getTypeElement(provideCls);
                bean.providerPkg = elementUtils.getPackageOf(proCls).toString();
                bean.providerName = proCls.getQualifiedName().toString()
                        .replace(bean.providerPkg + '.', "");
            }



            bean.tag = element.getAnnotation(Provider.class).tag();
            bean.isSingle = element.getAnnotation(Provider.class).single();

            beans.add(bean);
        }

        return beans;
    }
}
