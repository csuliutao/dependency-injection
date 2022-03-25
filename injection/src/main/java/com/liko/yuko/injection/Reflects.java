package com.liko.yuko.injection;

import java.lang.reflect.Method;

public final class Reflects {
    public static final String STATIC_METHOD_NAME_IN_FACTORY = "_getFactory";
    private static final String INJECTION_PREFIX = "_DI_";
    private static final String FACTORY_SUFFIX = "_Factory";
    private static final String INJECTOR_SUFFIX = "_Injector";
    private Reflects() {}

    public static Factory getFactory(String fullName) throws Throwable {
        Class factory = Class.forName(fullName);
        Method method = factory.getDeclaredMethod(STATIC_METHOD_NAME_IN_FACTORY);
        return (Factory) method.invoke(null);
    }

    public static Injector getInjector(Object instance) throws Throwable {
        if (instance == null) {
            return null;
        }
        String name = instance.getClass().getPackage().getName() + '.' + getInjectorName(instance.getClass());
        Class cls = Class.forName(name);
        return (Injector) cls.newInstance();
    }

    public static String getInjectorName(Class cls){
        if (cls != null) {
            return getInjectorName(cls.getSimpleName());
        }
        return "";
    }

    public static String getInjectorName(String name){
        return INJECTION_PREFIX + name + INJECTOR_SUFFIX;
    }

    public static String getFactoryName(Class cls, String tag){
        if (cls != null) {
            return getFactoryName(cls.getSimpleName(), tag);
        }
        return "";
    }

    public static String getFactoryName(String name, String tag){
        name = name.replace('.', '_');
        if (!"".equals(tag) && tag != null) {
            name = name + '_' + tag;
        }
        return INJECTION_PREFIX + name + FACTORY_SUFFIX;
    }
}
