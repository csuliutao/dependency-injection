package com.liko.yuko.injection_compile.collector;

import com.liko.yuko.injection.Provider;
import com.liko.yuko.injection_compile.bean.ProviderBean;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

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
            ProviderBean bean = new ProviderBean();
            bean.isConstructor = element.getKind() == ElementKind.CONSTRUCTOR;
            bean.isStatic = element.getModifiers().contains(Modifier.STATIC);
            bean.methodName = element.getSimpleName().toString();
            String fullCls = ((TypeElement)element.getEnclosingElement()).getQualifiedName().toString();
            int index = fullCls.lastIndexOf('.');
            bean.clsPkg = fullCls.substring(0, index);
            bean.clsName = fullCls.substring(index + 1);

            String provideCls = element.getAnnotation(Provider.class).name();
            index = provideCls.lastIndexOf('.');
            bean.providerPkg = provideCls.substring(0, index);
            bean.providerName = provideCls.substring(index + 1);

            beans.add(bean);
        }

        return beans;
    }
}
