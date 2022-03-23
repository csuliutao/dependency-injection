package com.liko.yuko.injection_compile.writer;

import com.liko.yuko.injection_compile.bean.InjectorBean;

import java.util.Set;

import javax.annotation.processing.Filer;

public class InjectorWriter implements Writer<InjectorBean>{
    @Override
    public void handle(Filer filer, Set<String> writeFiles, Set<InjectorBean> collectBean) {
        return;
    }
}
