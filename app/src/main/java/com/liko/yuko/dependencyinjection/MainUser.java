package com.liko.yuko.dependencyinjection;

import com.liko.yuko.base_rules.User;
import com.liko.yuko.injection.Provider;

public class MainUser implements User {
    @Provider(tag = "main")
    public MainUser() {}

    @Override
    public String print() {
        return "mainUser";
    }
}
