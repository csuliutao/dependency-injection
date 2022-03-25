package com.liko.yuko.injection_compile.bean;

import java.util.ArrayList;
import java.util.Objects;

public class InjectorBean {
    public String clsPkg;
    public String clsName;
    public ArrayList<Inner> list;

    public void addInject(String injectPkg, String injectName, String fieldName, String tag) {
        if (list == null) {
            list = new ArrayList<>();
        }
        Inner bean = new Inner();
        bean.injectPkg = injectPkg;
        bean.injectName = injectName;
        bean.fieldName = fieldName;
        bean.tag = tag;
        list.add(bean);
    }

    public static class Inner {
        public String injectPkg;
        public String injectName;
        public String fieldName;
        public String tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InjectorBean)) return false;
        InjectorBean bean = (InjectorBean) o;
        return Objects.equals(clsPkg, bean.clsPkg) &&
                Objects.equals(clsName, bean.clsName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clsPkg, clsName);
    }
}
