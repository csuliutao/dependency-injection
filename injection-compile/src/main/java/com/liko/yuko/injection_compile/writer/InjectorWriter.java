package com.liko.yuko.injection_compile.writer;

import com.liko.yuko.injection.Injector;
import com.liko.yuko.injection.Reflects;
import com.liko.yuko.injection_compile.bean.InjectorBean;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

public class InjectorWriter implements Writer<InjectorBean>{
    @Override
    public void handle(Filer filer, Set<String> writeFiles, Set<InjectorBean> injectorBeans) {
        if (injectorBeans == null || injectorBeans.isEmpty()) {
            return;
        }
        for (InjectorBean bean : injectorBeans) {
            String clsName = Reflects.getInjectorName(bean.clsName);
            TypeSpec.Builder injector = TypeSpec.classBuilder(clsName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ParameterizedTypeName.get(
                            ClassName.get(Injector.class), ClassName.get(bean.clsPkg, bean.clsName)
                    ));

            String paramsName = "_" + bean.clsName.replace('.', '_');
            MethodSpec.Builder inject = MethodSpec.methodBuilder("inject")
                    .addAnnotation(ClassName.get(Override.class))
                    .addParameter(ClassName.get(bean.clsPkg, bean.clsName), paramsName)
                    .addModifiers(Modifier.PUBLIC);

            for (InjectorBean.Inner inner : bean.list) {
                String factory = Reflects.getFactoryName(inner.injectName, inner.tag);
                if (writeFiles.contains(inner.injectPkg + '.' + factory)) {
                    inject.addStatement("$L.$L = $T.$L().get()",
                            paramsName, inner.fieldName,
                            ClassName.get(inner.injectPkg, factory),
                            Reflects.STATIC_METHOD_NAME_IN_FACTORY);
                } else {
                    inject.addStatement("$L.$L =($T) $T.getFactory($S).get()",
                            paramsName, inner.fieldName,
                            ClassName.get(inner.injectPkg, inner.injectName),
                            Reflects.class, inner.injectPkg + '.' + factory)
                            .addException(Throwable.class);
                }
            }

            injector.addMethod(inject.build());
            try {
                JavaFile.builder(bean.clsPkg, injector.build()).build().writeTo(filer);
                writeFiles.add(bean.clsPkg + '.' + clsName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }
}
