package com.liko.yuko.injection_compile.collector;

import java.util.Set;


import javax.annotation.processing.RoundEnvironment;

public interface Collector<T> {
    Set<T> collect(RoundEnvironment roundEnvironment);
}
