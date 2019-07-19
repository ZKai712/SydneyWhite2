package com.dyxy.zkai.sydneywhite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyxy.zkai.sydneywhite.utils.ZXingUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class MyEwmActivity extends AppCompatActivity {

    private ImageView myEwmImage;
    private TextView myEwmText;

    private TextView titleText;
    private ImageView updataBtn;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_my_ewm);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mine_setting_title);

        titleText = findViewById(R.id.title_text);
        updataBtn = findViewById(R.id.update_btn);
        backBtn = findViewById(R.id.back_btn);
        titleText.setText("我的二维码");
        updataBtn.setVisibility(View.GONE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        myEwmImage = findViewById(R.id.my_ewm_image);
        myEwmText = findViewById(R.id.my_ewm_text);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("stuName",MainActivity.stuInfo.getStuName());
            jsonObject.put("stuIdcard",MainActivity.stuInfo.getStuIdcard());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //生成二维码
        Bitmap success = ZXingUtils.createQRImage(jsonObject.toString(),280,280,
                null);
        myEwmImage.setImageBitmap(success);

        myEwmText.setText(MainActivity.stuInfo.getStuName());
    }
}
