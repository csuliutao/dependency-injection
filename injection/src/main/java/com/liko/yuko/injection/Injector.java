package com.liko.yuko.injection;

public interface Injector<T> {
    void inject(T instance) throws Throwable;
}
