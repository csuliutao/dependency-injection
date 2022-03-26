package com.liko.yuko.injection_compile.collector;

import com.liko.yuko.injection.Inject;
import com.liko.yuko.injection.Provider;
import com.liko.yuko.injection_compile.bean.InjectorBean;
import com.liko.yuko.injection_compile.bean.ProviderBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class InjectorCollector implements Collector<InjectorBean>{
    @Override
    public Set<InjectorBean> collect(RoundEnvironment roundEnvironment, Elements elementUtils) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Inject.class);
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        HashSet<InjectorBean> beans = new HashSet<>(elements.size() * 3 / 4 + 1);
        for (Element element : elements) {
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }


            String fullCls = ((TypeElement)element.getEnclosingElement()).getQualifiedName().toString();
            int index = fullCls.lastIndexOf('.');
            String clsPkg = fullCls.substring(0, index);
            String clsName = fullCls.substring(index + 1);
            InjectorBean bean = new InjectorBean();
            bean.clsName = clsName;
            bean.clsPkg = clsPkg;

            boolean alreadyCls = false;
            for (InjectorBean temp : beans) {
                if (bean.equals(temp)) {
                    alreadyCls = true;
                    bean = temp;
                    break;
                }
            }

            String fieldName = element.getSimpleName().toString();
            String provideCls = element.asType().toString();
            index = provideCls.lastIndexOf('.');
            String injectPkg = provideCls.substring(0, index);
            String injectName = provideCls.substring(index + 1);
            String tag = element.getAnnotation(Inject.class).tag();

            bean.addInject(injectPkg, injectName, fieldName, tag);

            if (!alreadyCls) {
                beans.add(bean);
            }
        }

        return beans;
    }
}
