package com.liko.yuko.injection_compile.collector;

import com.liko.yuko.injection.AssignClass;
import com.liko.yuko.injection.Inject;
import com.liko.yuko.injection.Provider;
import com.liko.yuko.injection_compile.Utils;
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

            if (!(element.getEnclosingElement() instanceof TypeElement)) {
                throw new RuntimeException("inject must be clss field!!, "
                        + element.getSimpleName().toString());
            }

            InjectorBean bean = new InjectorBean();
            TypeElement inCls = (TypeElement) element.getEnclosingElement();
            bean.clsPkg = elementUtils.getPackageOf(inCls).toString();
            bean.clsName = inCls.getQualifiedName().toString().replace(bean.clsPkg + '.', "");

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

            boolean alreadyCls = false;
            for (InjectorBean temp : beans) {
                if (bean.equals(temp)) {
                    alreadyCls = true;
                    bean = temp;
                    break;
                }
            }

            String fieldName = element.getSimpleName().toString();
            String provideCls = Utils.getClsNameByClassAnnotation(element, AssignClass.class);
            if (provideCls == null) {
                provideCls = element.asType().toString();
            }
            TypeElement prvCls = elementUtils.getTypeElement(provideCls);
            String injectPkg = elementUtils.getPackageOf(prvCls).toString();
            String injectName = provideCls.replace(injectPkg + '.', "");
            String tag = element.getAnnotation(Inject.class).value();

            bean.addInject(injectPkg, injectName, fieldName, Utils.getTag(tag));

            if (!alreadyCls) {
                beans.add(bean);
            }
        }

        return beans;
    }
}
