package com.liko.yuko.injection_compile.writer;

import com.liko.yuko.injection_compile.bean.ProviderBean;

import java.util.Set;

import javax.annotation.processing.Filer;

public class ProviderWriter implements Writer<ProviderBean>{
    @Override
    public void handle(Filer filer, Set<String> writeFiles, Set<ProviderBean> collectBean) {
        return;
    }
}
