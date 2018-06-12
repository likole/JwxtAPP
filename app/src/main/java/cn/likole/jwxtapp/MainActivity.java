package cn.likole.jwxtapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static cn.likole.jwxtapp.JwxtTool.jwxt;


public class MainActivity extends AppCompatActivity {

    private EditText et_courseId;
    private EditText et_courseId2;
    private EditText et_classId;
    private EditText et_classId2;
    private EditText et_termCode;
    private Button btn_select;
    private TextView tv_info;
    private boolean selecting=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        readData();
        et_termCode.setText("2018-2019-1-2");
        watchResult();
    }

    private void bindView(){
        et_courseId=(EditText) findViewById(R.id.et_courseId);
        et_courseId2=(EditText) findViewById(R.id.et_courseId2);
        et_classId=(EditText) findViewById(R.id.et_classId);
        et_classId2=(EditText) findViewById(R.id.et_classId2);
        et_termCode=(EditText) findViewById(R.id.et_termCode);
        btn_select=(Button) findViewById(R.id.btn_select);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                selecting=!selecting;
                if(!selecting){
                    btn_select.setEnabled(false);
                    btn_select.setText("如需再次选课，请先后退或者重开应用");
                }
                else{
                    select();
                    btn_select.setText("停止选课（选完一定要停止或者彻底关闭应用）");
                }

            }
        });
        tv_info=(TextView) findViewById(R.id.tv_info);
    }

    private void readData() {
        SharedPreferences read = getSharedPreferences("JwxtAPP", MODE_PRIVATE);
        et_courseId.setText(read.getString("et_courseId", ""));
        et_courseId2.setText(read.getString("et_courseId2", ""));
        et_classId.setText(read.getString("et_classId", ""));
        et_classId2.setText(read.getString("et_classId2", ""));
        et_termCode.setText(read.getString("et_termCode", ""));
    }

    private void saveData() {
        SharedPreferences.Editor editor = getSharedPreferences("JwxtAPP",
                MODE_PRIVATE).edit();
        editor.putString("et_courseId", et_courseId.getText().toString());
        editor.putString("et_courseId2", et_courseId2.getText().toString());
        editor.putString("et_classId", et_classId.getText().toString());
        editor.putString("et_classId2", et_classId2.getText().toString());
        editor.putString("et_termCode", et_termCode.getText().toString());
        editor.commit();
    }

    /**
     * 监测选课结果
     */
    private void watchResult(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!selecting) continue;
                    try {
                        final String result=jwxt.selectResult();
                        if(!result.contains("尚未"))
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_info.setText(result+"\n"+tv_info.getText());
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 抢课
     */
    private void select(){
        final String termCode=et_termCode.getText().toString();
        final String courseId=et_courseId.getText().toString();
        final String courseId2=et_courseId2.getText().toString();
        final String classId=et_classId.getText().toString();
        final String classId2=et_classId2.getText().toString();
        if(courseId2.length()<5){
            //一门课
            Toast.makeText(MainActivity.this,termCode+"  "+courseId+"  "+classId, Toast.LENGTH_LONG).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            if(selecting)
                            jwxt.selectLesson(courseId,classId,termCode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }else{
            //两门课
            Toast.makeText(MainActivity.this,termCode+"  "+courseId+"  "+classId+"  "+courseId2+"  "+classId2, Toast.LENGTH_LONG).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            if(selecting){
                                jwxt.selectLesson(courseId,classId,termCode);
                                jwxt.selectLesson(courseId2,classId2,termCode);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
}
