package com.dyxy.zkai.sydneywhite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoClassActivity extends AppCompatActivity {

    @BindView(R.id.sfaculty_edit)
    EditText sFacultyEdit;
    @BindView(R.id.sclass_edit)
    EditText sClassEdit;
    @BindView(R.id.steacher_edit)
    EditText sTeacherEdit;
    @BindView(R.id.sdorm_edit)
    EditText sDormEdit;

    TextView titleText;
    ImageView updataBtn;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_info_class);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mine_setting_title);
        ButterKnife.bind(this);

        titleText = findViewById(R.id.title_text);
        updataBtn = findViewById(R.id.update_btn);
        backBtn = findViewById(R.id.back_btn);
        titleText.setText("学校信息");
        updataBtn.setVisibility(View.GONE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String sfaculty = MainActivity.stuInfo.getFaculty();
        String sclass = MainActivity.stuInfo.getSclass();
        String steacher = MainActivity.stuInfo.getClassTeacher();
        String sdorm = MainActivity.stuInfo.getStuDorm();
        sFacultyEdit.setText(sfaculty);
        sClassEdit.setText(sclass);
        sTeacherEdit.setText(steacher);
        sDormEdit.setText(sdorm);
    }
}
