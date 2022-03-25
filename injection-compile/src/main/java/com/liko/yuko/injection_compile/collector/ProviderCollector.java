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
import javax.swing.plaf.TextUI;

public class ProviderCollector implements Collector<ProviderBean>{
    @Override
    public Set<ProviderBean> collect(RoundEnvironment roundEnvironment) {
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
            String fullCls = ((TypeElement)element.getEnclosingElement()).getQualifiedName().toString();
            int index = fullCls.lastIndexOf('.');
            bean.clsPkg = fullCls.substring(0, index);
            bean.clsName = fullCls.substring(index + 1);

            Element pre = element.getEnclosingElement();
            Element now = pre.getEnclosingElement();
            while (now != null && now.getKind() != ElementKind.PACKAGE) {
                if (!pre.getModifiers().contains(Modifier.STATIC)) {
                    throw new RuntimeException("Inner class must static, " +
                            bean.clsPkg + '.' + bean.clsName);
                }
                index = bean.clsPkg.lastIndexOf('.');
                bean.clsName = bean.clsPkg.substring(index + 1) + '.' + bean.clsName;
                bean.clsPkg = bean.clsPkg.substring(0, index);

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
                    index = provideCls.lastIndexOf('.');
                    bean.providerPkg = provideCls.substring(0, index);
                    bean.providerName = provideCls.substring(index + 1);
                }
            } else {
                index = provideCls.lastIndexOf('.');
                bean.providerPkg = provideCls.substring(0, index);
                bean.providerName = provideCls.substring(index + 1);
            }



            bean.tag = element.getAnnotation(Provider.class).tag();
            bean.isSingle = element.getAnnotation(Provider.class).single();

            beans.add(bean);
        }

        return beans;
    }
}
