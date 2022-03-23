package com.liko.yuko.injection_compile.writer;

import java.util.Set;

import javax.annotation.processing.Filer;

public interface Writer<T> {
    void handle(Filer filer, Set<String> writeFiles,Set<T> collectBean);
}
