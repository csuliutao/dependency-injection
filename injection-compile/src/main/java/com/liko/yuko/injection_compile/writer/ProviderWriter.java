package com.liko.yuko.injection_compile.writer;

import com.liko.yuko.injection.Factory;
import com.liko.yuko.injection.Reflects;
import com.liko.yuko.injection_compile.bean.ProviderBean;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

public class ProviderWriter implements Writer<ProviderBean>{
    @Override
    public void handle(Filer filer, Set<String> writeFiles, Set<ProviderBean> collectBean) {
        if (collectBean == null || collectBean.isEmpty()) {
            return;
        }
        for (ProviderBean bean : collectBean) {
            MethodSpec.Builder get = MethodSpec.methodBuilder("get")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(ClassName.get(Override.class))
                    .returns(ClassName.get(bean.providerPkg, bean.providerName));

            String clsName = Reflects.getFactoryName(bean.providerName, bean.tag);
            TypeSpec.Builder factory = TypeSpec.classBuilder(clsName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ParameterizedTypeName.get(
                            ClassName.get(Factory.class), ClassName.get(bean.providerPkg, bean.providerName)
                    ));

            FieldSpec instance = FieldSpec.builder(
                    ClassName.get(bean.providerPkg, clsName), "_instance")
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new $L()", clsName)
                    .build();

            MethodSpec getInstance = MethodSpec.methodBuilder(Reflects.STATIC_METHOD_NAME_IN_FACTORY)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(ClassName.get(bean.providerPkg, clsName))
                    .addStatement("return $L", "_instance")
                    .build();

            factory.addField(instance);
            factory.addMethod(getInstance);

            if (bean.isSingle) {
                String singleName = "_" + bean.providerName.replace('.', '_');
                FieldSpec single = FieldSpec.builder(
                        ClassName.get(bean.providerPkg, bean.providerName), singleName)
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .build();

                factory.addField(single);

                get.beginControlFlow("if ($L == null)", singleName);
                if (bean.isStatic) {
                    get.addStatement("$L = $T.$L()", singleName,
                            ClassName.get(bean.clsPkg, bean.clsName),
                            bean.methodName);
                } else if (bean.isConstructor) {
                    get.addStatement("$L = new $T()", singleName,
                            ClassName.get(bean.clsPkg, bean.clsName));
                } else {
                    get.addStatement("$L = new $T().$L()", singleName,
                            ClassName.get(bean.clsPkg, bean.clsName),
                            bean.methodName);
                }
                get.endControlFlow()
                        .addStatement("return $L", singleName);
            } else {
                if (bean.isStatic) {
                    get.addStatement("return $T.$L()",
                            ClassName.get(bean.clsPkg, bean.clsName),
                            bean.methodName);
                } else if (bean.isConstructor) {
                    get.addStatement("return new $T()",
                            ClassName.get(bean.clsPkg, bean.clsName));
                } {
                    get.addStatement("return new $T().$L()",
                            ClassName.get(bean.clsPkg, bean.clsName),
                            bean.methodName);
                }
            }
            factory.addMethod(get.build());

            try {
                JavaFile.builder(bean.providerPkg, factory.build()).build().writeTo(filer);
                writeFiles.add(bean.providerPkg + "." + clsName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return;
    }
}
