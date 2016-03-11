package com.apptao.i.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apptao.i.instafig.Instafig;

public class MainActivity extends AppCompatActivity {

    Button bt_getconfig;
    TextView tv_config;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        registerReceiver();
        bt_getconfig = (Button) findViewById(R.id.bt_getconfig);
        tv_config = (TextView) findViewById(R.id.tv_config);

        Instafig.startWithAPPKEY(getApplication(), "0916bdf4220a4a53aaecf5288ef5124d");

        bt_getconfig.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updataText();
            }
        });
    }


    MyReceiver myReceiver;

    private void registerReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Instafig.INSTAFIG_ACTION);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(MainActivity.this);
        manager.registerReceiver(myReceiver, filter);
    }

    public class MyReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            updataText();
        }
    }

    public void updataText() {
        Instafig instafig = Instafig.getInstance();
        String testString = instafig.getString("testString", "defaultString");
        float testFloat = instafig.getFloat("testFloat", 0);
        int testInt = instafig.getInt("testInt", 0);

        StringBuilder builder = new StringBuilder();
        builder.append("testString---->>>>" + testString + "\n");
        builder.append("testFloat---->>>>" + testFloat + "\n");
        builder.append("testInt---->>>>" + testInt + "\n");

        tv_config.setText(builder.toString());
    }
}
