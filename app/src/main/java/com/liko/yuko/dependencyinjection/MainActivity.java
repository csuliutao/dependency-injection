package com.liko.yuko.dependencyinjection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.liko.yuko.base_rules.User;
import com.liko.yuko.injection.AssignClass;
import com.liko.yuko.injection.Inject;
import com.liko.yuko.injection.Injection;
import com.liko.yuko.injection.Provider;

import java.security.PublicKey;

public class MainActivity extends AppCompatActivity {

    @Inject
    User user;

    @Inject
    InnerUser innerUser;

    @Inject
    User assign;

    @Inject
    @AssignClass(MainUser.class)
    User mainUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Injection.inject(this);
        Log.d("liutao", user.print() + ',' + innerUser.print());
        new Main();
    }


    public static class Main{
        @Inject
        User user;

        public Main() {
            Injection.inject(this);
            Log.d("liutao", user.print() + " from inner inject");
        }
    }

    @Provider()
    public static class InnerUser implements User {

        @Override
        public String print() {
            return "InnerUser";
        }
    }
}