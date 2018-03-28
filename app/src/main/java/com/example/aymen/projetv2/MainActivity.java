package com.example.aymen.projetv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG="";
    Button bc,bh,bp,bs,ba;
    private BaseLoaderCallback loader=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
           switch (status)
           {
               default:
                   super.onManagerConnected(status);
                   return;
               case 0:
           }
            Log.d(TAG,"Opencv Loaded Successfully");

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(128);
        setContentView(R.layout.activity_main);
        bc= (Button) findViewById(R.id.buttonTakePhoto);
        bh= (Button) findViewById(R.id.buttonHistory);
        bp= (Button) findViewById(R.id.buttonHelp);
        bs= (Button) findViewById(R.id.buttonSettings);
        ba= (Button) findViewById(R.id.buttonAbout);
        bc.setOnClickListener(this);
        bh.setOnClickListener(this);
        bp.setOnClickListener(this);
        bs.setOnClickListener(this);
        ba.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.buttonTakePhoto:
                Intent i=new Intent(getApplicationContext(),Camira.class );
                startActivity(i);
                break;
            case R.id.buttonHistory:
                Intent i1=new Intent(getApplicationContext(),History.class);
                startActivity(i1);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case R.id.buttonHelp:
                Intent i2=new Intent(getApplicationContext(),Help.class);
                startActivity(i2);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case R.id.buttonSettings:
                Intent i3=new Intent(getApplicationContext(),Setting.class);
                startActivity(i3);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case R.id.buttonAbout:
                Intent i4=new Intent(getApplicationContext(),About.class);
                startActivity(i4);
                overridePendingTransition(R.anim.left_in,R.anim.left_out);
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()) {
            Log.d("MED_APP", "opencv not Loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, loader);
        }
        else
        {
            Log.d("MED_APP", "opencv Loaded");
        }

    }
}