package com.it.tinkercustom_exercise;

import android.content.Context;
import android.widget.Toast;

public class Calculator {

    public String calculate(Context context){
        int a = 666;
        int b = 0;//模拟异常
//        int b = 1;//模拟修复后代码 用于打出修复的dex
        Toast.makeText(context, "calculate >>> " + a / b, Toast.LENGTH_SHORT).show();
        return String.valueOf(a / b);
    }
}
