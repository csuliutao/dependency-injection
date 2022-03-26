package com.liko.yuko.injection_compile.collector;

import java.util.Set;


import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;

public interface Collector<T> {
    Set<T> collect(RoundEnvironment roundEnvironment, Elements elementUtils);
}
