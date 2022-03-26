package com.liko.yuko.test_1;

import com.liko.yuko.base_rules.User;
import com.liko.yuko.injection.Provider;

@Provider
public class SUser implements User {

    @Provider
    public User get() {
        return new SUser();
    }

    @Override
    public String print() {
        return "SUer";
    }
}
