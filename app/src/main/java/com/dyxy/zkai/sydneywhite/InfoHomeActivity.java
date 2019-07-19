package com.dyxy.zkai.sydneywhite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoHomeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.address_edit)
    EditText sAddressEdit;
    @BindView(R.id.father_name_edit)
    EditText fatherNameEdit;
    @BindView(R.id.father_phone_edit)
    EditText fatherPhoneEdit;
    @BindView(R.id.mother_name_edit)
    EditText motherNameEdit;
    @BindView(R.id.mother_phone_edit)
    EditText motherPhoneEdit;
    @BindView(R.id.save_home_btn)
    Button saveHomeBtn;

    TextView titleText;
    ImageView updataBtn;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_info_home);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mine_setting_title);
        ButterKnife.bind(this);

        titleText = findViewById(R.id.title_text);
        updataBtn = findViewById(R.id.update_btn);
        backBtn = findViewById(R.id.back_btn);
        titleText.setText("家庭信息");
        updataBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        saveHomeBtn.setOnClickListener(this);

        String stuAdress = MainActivity.stuInfo.getStuAddress();
        String fatherName = MainActivity.stuInfo.getFather();
        String fatherPhone = MainActivity.stuInfo.getFatherPhone();
        String motherName = MainActivity.stuInfo.getMother();
        String motherPhone = MainActivity.stuInfo.getMotherPhone();
        if (!"".equals(stuAdress)) {
            sAddressEdit.setText(stuAdress);
        }
        fatherNameEdit.setText(fatherName);
        fatherPhoneEdit.setText(fatherPhone);
        motherNameEdit.setText(motherName);
        motherPhoneEdit.setText(motherPhone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update_btn:
                setEnabled(true);
                break;
            case R.id.back_btn:
                finish();
                break;
            case R.id.save_home_btn:
                saveMineInfo();
                break;
        }
    }

    /**
     * 保存修改后的信息
     */
    private void saveMineInfo(){
//        //先将所有修改的信息保存到MainActivity.stuInfo中
        MainActivity.stuInfo.setStuAddress(sAddressEdit.getText().toString());
        MainActivity.stuInfo.setFather(fatherNameEdit.getText().toString());
        MainActivity.stuInfo.setFatherPhone(fatherPhoneEdit.getText().toString());
        MainActivity.stuInfo.setMother(motherNameEdit.getText().toString());
        MainActivity.stuInfo.setMotherPhone(motherPhoneEdit.getText().toString());
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
                        Toast.makeText(InfoHomeActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                        setEnabled(false);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InfoHomeActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
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
            saveHomeBtn.setVisibility(View.GONE);
            updataBtn.setVisibility(View.VISIBLE);
        }else {
            saveHomeBtn.setVisibility(View.VISIBLE);
            updataBtn.setVisibility(View.GONE);
        }
        sAddressEdit.setEnabled(isable);
        fatherNameEdit.setEnabled(isable);
        fatherPhoneEdit.setEnabled(isable);
        motherNameEdit.setEnabled(isable);
        motherPhoneEdit.setEnabled(isable);
    }
}
