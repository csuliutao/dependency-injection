package com.liko.yuko.dependencyinjection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.liko.yuko.base_rules.User;
import com.liko.yuko.injection.Inject;
import com.liko.yuko.injection.Injection;
import com.liko.yuko.injection.Provider;

import java.security.PublicKey;

public class MainActivity extends AppCompatActivity {

    @Inject
    User user;

    @Inject(tag = "main")
    User main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Injection.inject(this);
        Log.d("liutao", user.print() + ',' + main.print());
    }

    public static class InnerUser implements User {

        @Provider()
        public InnerUser() {}

        @Override
        public String print() {
            return "InnerUser";
        }
    }
}