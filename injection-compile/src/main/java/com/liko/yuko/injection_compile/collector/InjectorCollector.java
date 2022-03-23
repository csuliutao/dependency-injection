package com.liko.yuko.injection_compile.collector;

import com.liko.yuko.injection.Inject;
import com.liko.yuko.injection.Provider;
import com.liko.yuko.injection_compile.bean.InjectorBean;
import com.liko.yuko.injection_compile.bean.ProviderBean;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class InjectorCollector implements Collector<InjectorBean>{
    @Override
    public Set<InjectorBean> collect(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Inject.class);
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        HashSet<InjectorBean> beans = new HashSet<>(elements.size() * 3 / 4 + 1);
        for (Element element : elements) {
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }
            InjectorBean bean = new InjectorBean();
            bean.fieldName = element.getSimpleName().toString();
            String fullCls = ((TypeElement)element.getEnclosingElement()).getQualifiedName().toString();
            int index = fullCls.lastIndexOf('.');
            bean.clsPkg = fullCls.substring(0, index);
            bean.clsName = fullCls.substring(index + 1);

            String provideCls = element.asType().toString();
            index = provideCls.lastIndexOf('.');
            bean.injectPkg = provideCls.substring(0, index);
            bean.injectName = provideCls.substring(index + 1);

            beans.add(bean);
        }

        return beans;
    }
}
