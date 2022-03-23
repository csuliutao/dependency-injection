package com.liko.yuko.injection;

import java.util.HashMap;

public final class Injection {
    private final static HashMap<Class, Injector> injectors = new HashMap<>();


    private Injection() {
        throw new RuntimeException("no need instance!");
    }


    public static void inject(Object obj) {
        if (obj == null) {
            return;
        }
        Injector inject = injectors.get(obj.getClass());
        if (inject == null) {
            try {
                inject = Reflects.getInjector(obj);
                injectors.put(obj.getClass(), inject);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        if (inject != null) {
            try {
                inject.inject(obj);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
