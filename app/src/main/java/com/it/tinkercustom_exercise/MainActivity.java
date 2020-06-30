package com.it.tinkercustom_exercise;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.it.tinkercustom_exercise.utils.FileUtils;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = (TextView) findViewById(R.id.tvContent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }
    }


    public void method(View view) {
        Calculator calculator = new Calculator();
        String result = calculator.calculate(getApplicationContext());
        tvResult.setText(result);
    }


    //这里相当于时模拟重启去 将修复包加入进去  实际开发是在
    public void fix(View view) {
        FileUtils.copyAssetsAndWrite(getApplicationContext(), "classes2.dex");
        FixManager.getInstance().loadFixedDex(this);
        Toast.makeText(getApplicationContext(), "修复完成", Toast.LENGTH_SHORT).show();
    }
}
