package com.dyxy.zkai.sydneywhite;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoBasicActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.sname_edit)
    EditText sNameEdit;
    @BindView(R.id.sex_radio)
    RadioGroup radioGroup;
    @BindView(R.id.sphone_edit)
    EditText sPhoneEdit;
    @BindView(R.id.idcard_edit)
    EditText idCardEdit;
    @BindView(R.id.save_basic_btn)
    Button saveBasicBtn;

    TextView titleText;
    ImageView updataBtn;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_info_basic);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mine_setting_title);
        ButterKnife.bind(this);

        titleText = findViewById(R.id.title_text);
        titleText.setText("基本信息");
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);
        updataBtn = findViewById(R.id.update_btn);
        updataBtn.setOnClickListener(this);
        saveBasicBtn.setOnClickListener(this);
        //将已有信息填入
        String sname = MainActivity.stuInfo.getStuName();
        String idcard = MainActivity.stuInfo.getStuIdcard();
        String sex = MainActivity.stuInfo.getStuSex();
        String stuPhone = MainActivity.stuInfo.getStuPhone();
        if ("男".equals(sex)){
            radioGroup.check(R.id.boy_radio);
        }else if ("女".equals(sex)){
            radioGroup.check(R.id.girl_radio);
        }
        sNameEdit.setText(sname);
        idCardEdit.setText(idcard);
        sPhoneEdit.setText(stuPhone);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_basic_btn:
                saveMineInfo();
                break;
            case R.id.update_btn:
                setEnabled(true);
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }
    /**
     * 保存修改后的信息
     */
    private void saveMineInfo(){
        //先将所有修改的信息保存到MainActivity.stuInfo中
        MainActivity.stuInfo.setStuPhone(sPhoneEdit.getText().toString());
        //将stuInfo转为json
        Gson gson = new Gson();
        String mineInfoJson = gson.toJson(MainActivity.stuInfo);
        //向后端发送请求
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(ScanClassActivity.JSON,mineInfoJson);
        Request request = new Request.Builder()
                .url(MainActivity.UPDATEURL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InfoBasicActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                        setEnabled(false);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InfoBasicActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 设置输入框等组件是否启用
     * @param isable
     */
    public void setEnabled(boolean isable){
        if (!isable) {
            saveBasicBtn.setVisibility(View.GONE);
            updataBtn.setVisibility(View.VISIBLE);
        }else {
            saveBasicBtn.setVisibility(View.VISIBLE);
            updataBtn.setVisibility(View.GONE);
        }
        sPhoneEdit.setEnabled(isable);
    }


}
