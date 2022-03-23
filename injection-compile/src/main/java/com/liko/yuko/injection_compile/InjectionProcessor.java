package com.liko.yuko.injection_compile;

import com.liko.yuko.injection.Injector;
import com.liko.yuko.injection.Provider;
import com.liko.yuko.injection_compile.bean.InjectorBean;
import com.liko.yuko.injection_compile.bean.ProviderBean;
import com.liko.yuko.injection_compile.collector.InjectorCollector;
import com.liko.yuko.injection_compile.collector.ProviderCollector;
import com.liko.yuko.injection_compile.writer.InjectorWriter;
import com.liko.yuko.injection_compile.writer.ProviderWriter;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

public class InjectionProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        processingEnvironment.getFiler();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<ProviderBean> providerBeans = new ProviderCollector().collect(roundEnvironment);
        Set<String> handled = new HashSet<>();
        new ProviderWriter().handle(filer, handled, providerBeans);

        Set<InjectorBean> injectorBeans = new InjectorCollector().collect(roundEnvironment);
        new InjectorWriter().handle(filer, handled, injectorBeans);
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Injector.class.getName());
        types.add(Provider.class.getName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
