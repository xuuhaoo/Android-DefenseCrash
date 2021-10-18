package com.android.tony.defense;

import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(view -> {
      Snackbar.make(view, "崩溃啦...n", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();
      new Handler().post(() -> {
        throw new RuntimeException("Handler 崩溃");
      });
//      throw new ExemptCaughtException("测试崩溃 Click",null);
    });
    new Thread(() -> {
      throw new IllegalArgumentException("测试崩溃 Thread");
    }).start();
    getWindow().getDecorView().post(() -> {
      throw new RuntimeException("View post Handler 崩溃");
    });
    throw new NullPointerException("Activity OnCreate 测试崩溃");
  }

  @Override
  protected void onResume() {
    super.onResume();
    throw new NullPointerException("Activity onResume 测试崩溃");
  }

  @Override
  protected void onPause() {
    super.onPause();
    throw new NullPointerException("Activity onPause 测试崩溃");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    throw new NullPointerException("Activity onDestroy 测试崩溃");
  }
}
