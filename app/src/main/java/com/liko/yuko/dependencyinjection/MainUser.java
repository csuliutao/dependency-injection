package com.liko.yuko.dependencyinjection;

import com.liko.yuko.base_rules.User;
import com.liko.yuko.injection.AssignClass;
import com.liko.yuko.injection.Provider;
import com.liko.yuko.injection.Single;

@Provider("main")
@Single
public class MainUser implements User {

    @Override
    public String print() {
        return "mainUser";
    }

    @AssignClass(User.class)
    @Provider
    public static MainUser get() {
        return new MainUser();
    }
}
