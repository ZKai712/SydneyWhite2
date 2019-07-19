package com.dyxy.zkai.sydneywhite.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dyxy.zkai.sydneywhite.InfoBasicActivity;
import com.dyxy.zkai.sydneywhite.InfoClassActivity;
import com.dyxy.zkai.sydneywhite.InfoHomeActivity;
import com.dyxy.zkai.sydneywhite.ScanClassActivity;
import com.dyxy.zkai.sydneywhite.MainActivity;
import com.dyxy.zkai.sydneywhite.MyEwmActivity;
import com.dyxy.zkai.sydneywhite.R;
import com.dyxy.zkai.sydneywhite.utils.Base64BitmapUtil;
import com.dyxy.zkai.sydneywhite.utils.BitmapToRoundUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MineView implements View.OnClickListener {

    Button saveBtn;
    @BindView(R.id.head_imge)
    ImageView headImge;
    @BindView(R.id.head_name_text)
    TextView headText;
    @BindView(R.id.next_ewm_img3)
    View nextEwmImg3;

    private View view;
    private Context context;
    private Activity activity;
    //上传头像时的信息
    public static final int CHOOSE_PICTURE = 0;
    public static final int TAKE_PICTURE = 1;
    public static final int CROP_SMALL_PICTURE = 2;
    private Bitmap existHeadBitmap;

    private String headPicture;

    public MineView(Activity activity, final Context context, View view){
        this.activity = activity;
        this.view = view;
        this.context = context;
        ButterKnife.bind(this,view);

        //基本信息
        ConstraintLayout layout = view.findViewById(R.id.basic_layout);
        ImageView iconImage = layout.findViewById(R.id.icon_image);
        TextView functionTxt = layout.findViewById(R.id.function_text);
        iconImage.setImageResource(R.drawable.icon_basic);
        functionTxt.setText("基本信息");
        layout.setOnClickListener(this);
        //学校信息
        layout = view.findViewById(R.id.class_layout);
        iconImage = layout.findViewById(R.id.icon_image);
        functionTxt = layout.findViewById(R.id.function_text);
        iconImage.setImageResource(R.drawable.icon_class);
        functionTxt.setText("学校信息");
        layout.setOnClickListener(this);
        //班级信息
        layout = view.findViewById(R.id.home_layout);
        iconImage = layout.findViewById(R.id.icon_image);
        functionTxt = layout.findViewById(R.id.function_text);
        iconImage.setImageResource(R.drawable.icon_home);
        functionTxt.setText("家庭信息");
        layout.setOnClickListener(this);

        String sname = MainActivity.stuInfo.getStuName();
        String stuSex = MainActivity.stuInfo.getStuSex();

        headImge.setOnClickListener(this);
        headText.setText(sname);
        nextEwmImg3.setOnClickListener(this);

        String headImgStr = MainActivity.stuInfo.getStuHeadImg();
        if (headImgStr!=null && !"".equals(headImgStr)) {
            existHeadBitmap = Base64BitmapUtil.base64ToBitmap(headImgStr);
            existHeadBitmap = BitmapToRoundUtils.toRoundBitmap(existHeadBitmap);
            headImge.setImageBitmap(existHeadBitmap);
        }else {
            if ("男".equals(stuSex)) {
                headImge.setImageResource(R.drawable.ic_class_member_boy);
            } else {
                headImge.setImageResource(R.drawable.ic_class_member_girl);
            }
        }

    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_ewm_img3:       //点击查看二维码
                Intent intent = new Intent(context, MyEwmActivity.class);
                context.startActivity(intent);
                break;
            case R.id.head_imge:        //点击修改头像
                showChoosePicDialog(); //申请权限并上传头像
                break;
            case R.id.basic_layout:     //点击基本信息
                Intent intent1 = new Intent(context, InfoBasicActivity.class);
                context.startActivity(intent1);
                break;
            case R.id.class_layout:     //点击学校信息
                Intent intent2 = new Intent(context, InfoClassActivity.class);
                context.startActivity(intent2);
                break;
            case R.id.home_layout:      //点击家庭信息
                Intent intent3 = new Intent(context, InfoHomeActivity.class);
                context.startActivity(intent3);
                break;
        }
    }

    /**头像上传
     * 显示修改图片的对话框
     */
    public void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("更换头像");
        String[] items = { "从本地获取","拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        activity.startActivityForResult(intent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE:
                        Intent intent2 = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE, null);
                        activity.startActivityForResult(intent2, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * 通过相机获取图片
     *
     */
    public void getPhotoes(Intent data) {
        Uri uri = data.getData();
        if (uri == null) {
            Bundle pBundle = data.getExtras(); //从intent对象中获取数据，
            if (pBundle != null) {
                Bitmap mBitmap = (Bitmap) pBundle.get("data"); //get bitmap
                mBitmap = BitmapToRoundUtils.toRoundBitmap(mBitmap);
                headImge.setImageBitmap(mBitmap);
                //上传
                uploadPicture(mBitmap);
            } else {
                Toast.makeText(activity.getApplicationContext(), "获取失败", Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * 本地裁剪图片方法实现
     */
    public void cutImage(Uri uri) {
        if (uri == null) {
            Log.i("alanjet", "The uri is not exist.");
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, CROP_SMALL_PICTURE);
    }
    /**
     * 设置裁剪之后的图片数据
     */
    public void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap mBitmap = extras.getParcelable("data");
            mBitmap = BitmapToRoundUtils.toRoundBitmap(mBitmap);
            //上传
            uploadPicture(mBitmap);

        }
    }


    /**
     * 上传图片
     *
     */
    private void uploadPicture(final Bitmap bitmap) {
        headPicture = Base64BitmapUtil.bitmapToBase64(bitmap);
        MainActivity.stuInfo.setStuHeadImg(headPicture);

        Gson gson = new Gson();
        String stuInfoJson = gson.toJson(MainActivity.stuInfo);

        Log.e("MMMMMMMMMMMMMMMMM",stuInfoJson);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(ScanClassActivity.JSON,stuInfoJson);
        Request request = new Request.Builder()
                .url(MainActivity.UPDATEURL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"头像修改失败",Toast.LENGTH_SHORT).show();
                        headImge.setImageBitmap(existHeadBitmap);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"头像修改成功",Toast.LENGTH_SHORT).show();
                        headImge.setImageBitmap(bitmap);//显示图片
                    }
                });
            }
        });

    }


}
