package com.liko.yuko.dependencyinjection;

import com.liko.yuko.base_rules.User;
import com.liko.yuko.injection.Provider;

@Provider(tag = "main")
public class MainUser implements User {

    @Override
    public String print() {
        return "mainUser";
    }
}
