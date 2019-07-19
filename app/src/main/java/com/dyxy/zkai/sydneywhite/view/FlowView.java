package com.dyxy.zkai.sydneywhite.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dyxy.zkai.sydneywhite.Home2Activity;
import com.dyxy.zkai.sydneywhite.MainActivity;
import com.dyxy.zkai.sydneywhite.R;
import com.dyxy.zkai.sydneywhite.ScanClassActivity;
import com.dyxy.zkai.sydneywhite.entity.Task;
import com.dyxy.zkai.sydneywhite.entity.TaskForm;
import com.dyxy.zkai.sydneywhite.service.SystemService;
import com.dyxy.zkai.sydneywhite.utils.BitmapToFile;
import com.dyxy.zkai.sydneywhite.utils.MapUtil;
import com.dyxy.zkai.sydneywhite.utils.Md5Util;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.dyxy.zkai.sydneywhite.MainActivity.TASKQUERY;

public class FlowView {
    private Context context;
    private View view;
    private AlertDialog alertDialog;
    private Activity activity;

    private ListView taskList;
    private List<TaskForm> taskForms;
    private MyListAdapter listAdapter;

    private Toast toast;
    // 拍照回传码
    public final static int CAMERA_REQUEST_CODE = 71;
    // 相册选择回传吗
    public final static int GALLERY_REQUEST_CODE = 72;
    // 拍照的照片的存储位置
    private String mTempPhotoPath;
    // 照片所在的Uri地址
    private Uri imageUri;
    //高德和腾讯的终点信息
    private String gdToName = "东营职业学院南门";    //目的地名
    private String gdLat = "37.4332700000";       //目的地纬度
    private String gdLon = "118.6235000000";       //目的地经
    //百度的终点信息
    private String duToName = "东营职业学院南门";   //目的地名
    private String duLatLng = "37.4389151600,118.6300897704";   //终点纬经度
    //百度地图APP
    private String baiduPackage = "com.baidu.BaiduMap";
    private StringBuffer baiduUri = new StringBuffer("baidumap://map/direction?" +
            "destination=name:" + duToName + "|latlng:" + duLatLng +
            "&coord_type=bd09ll&mode=walking&sy=0&src=dyxy.zkai.sydneywhite");
    //高德地图APP
    private String gaodePackage = "com.autonavi.minimap";
    private StringBuffer gaodeUri = new StringBuffer("amapuri://route/plan/?" +
            "sourceApplication=sydneywhite&dlat=" + gdLat + "&dlon=" + gdLon + "&dname=" + gdToName + "&dev=0&t=2");
    //腾讯地图APP
    private String tencentPackage = "com.tencent.map";
    private StringBuffer tencentUri = new StringBuffer("qqmap://map/routeplan?" +
            "type=walk&fromcoord=CurrentLocation&to=" + gdToName + "&tocoord=" + gdLat+gdLon +
            "&referer=MI6BZ-6KNCS-CH7OT-6FEXC-YELWZ-QHFYQ");

    private String taskStr;

    public FlowView(final Context context,Activity activity, View view) {
        this.context = context;
        this.view = view;
        this.activity = activity;

        taskList = view.findViewById(R.id.task_list);
        taskForms = new ArrayList<>();
        listAdapter = new MyListAdapter();
        taskList.setAdapter(listAdapter);

        //列表数据长按打开选择地图APP
        taskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onItemLongClicked(position);
                return true;
            }
        });
        //列表数据点击是否进行拍照上传审核
        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(position);
            }
        });

        //修改任务完成情况
        if ("0".equals(MainActivity.stuTask.getRegister())) {
            //登录app标明完成盖章任务
            MainActivity.stuTask.setRegister("1");
            loadTask(MainActivity.stuTask);
        }
        //判断班级和宿舍任务完成情况
        if (MainActivity.stuInfo.getSclass()!=null && !"".equals(MainActivity.stuInfo.getSclass())
                && !"1".equals(MainActivity.stuTask.getBindClass())){
            MainActivity.stuTask.setBindClass("1");
            loadTask(MainActivity.stuTask);
        }
        if (MainActivity.stuInfo.getStuDorm()!=null && !"".equals(MainActivity.stuInfo.getStuDorm())
                && !"1".equals(MainActivity.stuTask.getBindDorm())){
            MainActivity.stuTask.setBindDorm("1");
            loadTask(MainActivity.stuTask);
        }


        //加载任务数据
        inintFlowView();


    }

    //TODO:列表长按时弹框显示选择地图APP
    private void onItemLongClicked(int position){
        final String[] items = {"高德地图", "百度地图", "腾讯地图"};
        final MapUtil mapUtil = new MapUtil(context);

        //任务列表地点名称和经纬度修改
        switch (position){
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("选择将要打开的地图");
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i){
                    case 0:
                        mapUtil.openMapApp(gaodePackage, gaodeUri.toString(), items[0]);
                        break;
                    case 1:
                        mapUtil.openMapApp(baiduPackage, baiduUri.toString(), items[1]);
                        break;
                    case 2:
                        mapUtil.openMapApp(tencentPackage,tencentUri.toString(),items[2]);
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    //TODO:列表单击时弹框显示选择上传图片方式
    private void onItemClicked(final int position){
        if (position == 0 || position ==1 || position ==4 || position ==5 ){
            return;
        }
        if (position == 2){
            taskStr = "缴纳被褥费";
        }
        if (position == 3){
           taskStr = "办理饭卡";
        }
        if (position == 6){
            taskStr = "缴纳学费";
        }
        String[] choosePhoto={"从相册选择","相机拍照"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("上传照片审核任务");
        alertBuilder.setItems(choosePhoto, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    activity.startActivityForResult(intent, GALLERY_REQUEST_CODE);
                }else if (which == 1){
                    Intent intent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE, null);
                    activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    //TODO:获取图片并上传
    public void getPhoto(Intent data){
        Uri uri = data.getData();
        //拍照上传
        if (uri == null) {
            Bundle pBundle = data.getExtras(); //从intent对象中获取数据，
            if (pBundle != null) {
                Bitmap mBitmap = (Bitmap) pBundle.get("data"); //get bitmap
                String path = BitmapToFile.saveBitmapFile(mBitmap);
                uploadPhoto(path);

            }
        }else {//选取图片上传
            String path = getUriPath(uri);//上边获取的uri不是真实路径，通过此方法转换
            try {
                Bitmap  bitmap1 = BitmapFactory.decodeFile(path);
                uploadPhoto(path);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//    SystemService service;
//    public void upload(String path){
//        File file = new File(path);
//
//        HashMap<String, RequestBody> map= new HashMap<>();
//        map.put("uploadType", toRequestBody("image"));
//        map.put("bizType", toRequestBody("0"));
//        map.put("bizKey", toRequestBody(""));
//        map.put("fileMd5", toRequestBody(Md5Util.getFileMD5(file)));
//        map.put("fileName", toRequestBody(file.getName()));
//
//
//        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
//        //关键部分，key要有filename
//        map.put("file\"; filename=\"" + file.getName(), fileBody );
//        service.uploadFile(map);
//    }
//
//    private RequestBody toRequestBody(String value) {
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), value);
//        return requestBody;
//    }

    //上传任务图片
    private void uploadPhoto(String path){
        File file = new File(path);
        String encode = "";
        try {
            encode = URLEncoder.encode(file.getName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }//App传递给后台时候编码

        String taskJson = new Gson().toJson(MainActivity.stuTask);
        OkHttpClient client = new OkHttpClient();
//        String fileMd5 = Md5Util.getFileMD5(file);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
//                .addFormDataPart("id","uploadFile")
//                .addFormDataPart("uploadType","all")
//                .addFormDataPart("bizType","task_file")
//                .addFormDataPart("bizKey",MainActivity.stuTask.getId())
//                .addFormDataPart("fileMd5", fileMd5)
                .addFormDataPart("task",taskJson)
                .addFormDataPart("taskStr",taskStr)
                .addFormDataPart("fileName",file.getName())
                .addFormDataPart("file",encode,RequestBody.create(MediaType.parse("image/*"),file))
                .build();

        Request request = new Request.Builder()
                .url("http://"+MainActivity.HOST+":8980/oas/api/android/task/photo")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FlowView","图片上传失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("FlowView","图片上传返回"+response.body().string());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inintData();
                        Toast.makeText(activity.getApplicationContext(), "上传成功，待审核", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //TODO:将uri转换成路径地址
    private String getUriPath(Uri uri) {
        String data = null;
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = activity.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }



    //TODO:加载任务列表数据源
    private void inintFlowView(){
        taskForms.clear();
        inint("招生处盖章",Integer.parseInt(MainActivity.stuTask.getRegister()),"地点：星星广场中央");
        inint("班级报道",Integer.parseInt(MainActivity.stuTask.getBindClass()),"地点：星星广场");
        inint("缴纳被褥、军训服等费用",Integer.parseInt(MainActivity.stuTask.getPayQuilt()),"地点：图书楼楼梯前");
        inint("办理饭卡",Integer.parseInt(MainActivity.stuTask.getMealCard()),"地点：图书楼西边");
        inint("领取被褥",Integer.parseInt(MainActivity.stuTask.getGetQuilt()),"地点：公寓二大厅");
        inint("宿舍报道",Integer.parseInt(MainActivity.stuTask.getBindDorm()),"地点：各学院宿舍楼");
        inint("缴纳学费",Integer.parseInt(MainActivity.stuTask.getPayTuition()),"地点：创业楼");

    }
    private void inint(String taskName,int flag,String details){
        TaskForm taskForm = new TaskForm(taskName , flag , details);
        taskForms.add(taskForm);
        listAdapter.notifyDataSetChanged();
    }
    //再次加载并获取任务数据
    private void inintData(){
        String taskUrl=TASKQUERY+"?stuName="+MainActivity.stuInfo.getStuName()+"&stuIdcard="+MainActivity.stuInfo.getStuIdcard();
        OkHttpClient client2 = new OkHttpClient();
        Request request2 = new Request.Builder()
                .url(taskUrl)
                .build();
        Call call2 = client2.newCall(request2);
        call2.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FLOWVIEW","任务信息获取失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.getBoolean("result")) {
                                String task = jsonObject.getString("task");
                                Gson gson = new Gson();
                                MainActivity.stuTask = gson.fromJson(task, Task.class);
                                Log.e("FLOWVIEW", "再次获取的任务数据：" + MainActivity.stuTask.toString());
                                if ("缴纳被褥费".equals(taskStr)){
                                    MainActivity.stuTask.setPayQuilt("2");
                                    MainActivity.stuTask.setGetQuilt("2");
                                }
                                if ("办理饭卡".equals(taskStr)){
                                    MainActivity.stuTask.setMealCard("2");
                                }
                                if ("缴纳学费".equals(taskStr)){
                                    MainActivity.stuTask.setPayTuition("2");
                                }
                                inintFlowView();
                                loadTask(MainActivity.stuTask);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    }

    //TODO:向后端发送任务是否完成情况
    private void loadTask(Task task){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(ScanClassActivity.JSON,new Gson().toJson(task));
        Request request = new Request.Builder()
                .url(MainActivity.TASKUPDATE)
                .post(body)
                .build();
        Call call3 = client.newCall(request);
        call3.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FlowView","任务信息修改失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("FlowView","发送任务信息并修改成功");
            }
        });

    }

    //TODO:ListView的适配器
    class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return taskForms.size();
        }
        @Override
        public Object getItem(int position) {
            return taskForms.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskForm taskForm = (TaskForm) getItem(position);
            if (null == convertView){
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.flow_task_item
                                ,null);
            }

            View view = convertView.findViewById(R.id.view);
            TextView taskText = convertView.findViewById(R.id.task_text);
            TextView detailsText = convertView.findViewById(R.id.details_text);

            if (taskForm.getFlag()==1) {
                view.setBackground(activity.getDrawable(R.drawable.bg_green_plus));
                taskText.setTextColor(activity.getResources().getColor(R.color.green));
                detailsText.setTextColor(activity.getResources().getColor(R.color.green));
            }else if (taskForm.getFlag()==2){
                view.setBackground(activity.getDrawable(R.drawable.bg_coral));
                taskText.setTextColor(activity.getResources().getColor(R.color.coral));
                detailsText.setTextColor(activity.getResources().getColor(R.color.coral));
            }else {
                view.setBackground(activity.getDrawable(R.drawable.bg_grey));
                taskText.setTextColor(activity.getResources().getColor(R.color.grey_plus));
                detailsText.setTextColor(activity.getResources().getColor(R.color.grey_plus));
            }

            taskText.setText(taskForm.getTaskName());
            detailsText.setText(taskForm.getDetails());

            return convertView;
        }
    }

}
