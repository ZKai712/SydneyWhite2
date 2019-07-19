package com.dyxy.zkai.sydneywhite.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dyxy.zkai.sydneywhite.Home2Activity;
import com.dyxy.zkai.sydneywhite.MainActivity;
import com.dyxy.zkai.sydneywhite.R;
import com.dyxy.zkai.sydneywhite.entity.StuMember;
import com.dyxy.zkai.sydneywhite.utils.Base64BitmapUtil;
import com.dyxy.zkai.sydneywhite.utils.BitmapToRoundUtils;
import com.example.xch.scanzxing.zxing.android.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DormListView {
    private Context context;
    private View view;
    private Activity activity;

    private TextView dMemberText;
    private TextView scanDormText;
    private ListView dormMemberList;
    private SwipeRefreshLayout dormListRefresh;

    private List<StuMember> dormMembers;
    private MyListAdapter listAdapter;
    private static final int MSG_TWO = 2;

//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case MSG_TWO :
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            dormMembers.clear();
//                            initViewData(MainActivity.stuInfo.getStuDorm());
//                        }
//                    }).start();
//                    break;
//            }
//        }
//    };


    public DormListView(final Activity activity , final Context context, View view) {
        this.activity = activity;
        this.context = context;
        this.view = view;

        dormMemberList = view.findViewById(R.id.dorm_member_list);
        dMemberText = view.findViewById(R.id.dmember_text);
        scanDormText = view.findViewById(R.id.scan_dorm_text);
        dormListRefresh = view.findViewById(R.id.dorm_list_refresh);

        dormMembers = new ArrayList<>();
        listAdapter = new MyListAdapter();
        dormMemberList.setAdapter(listAdapter);

        Intent intent = activity.getIntent();
        int resultDorm = intent.getIntExtra("resultDorm", -1);
        if (resultDorm==1 || (MainActivity.stuInfo.getStuDorm()!=null && !"".equals(MainActivity.stuInfo.getStuDorm()))){
            dormListRefresh.setVisibility(View.VISIBLE);
            dMemberText.setVisibility(View.VISIBLE);
            scanDormText.setVisibility(View.GONE);

            dMemberText.setText(MainActivity.stuInfo.getStuDorm()+"宿舍成员");

            initViewData(MainActivity.stuInfo.getStuDorm());
            refresh();
//            autoRefresh();
        }

    }

    /**
     * 下拉刷新
     */
    private void refresh(){
        //下拉刷新
        dormListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dormMembers.clear();
                initViewData(MainActivity.stuInfo.getStuDorm());
            }
        });
    }

    /**
     * 自动刷新
     */
//    private void autoRefresh(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    try {
//                        Thread.sleep(5000);
//                        Message msg = new Message();
//                        msg.what = MSG_TWO;
//                        handler.sendMessage(msg);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }


    /**
     * 宿舍成员列表
     * @param dorm
     */
    private void initViewData(String dorm){
        String path = MainActivity.QUERYURL+"?stuDorm="+dorm;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(path)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"宿舍成员数据加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
//                Log.e("OKOKOKOOK：",result);

                try {
                    JSONObject object = new JSONObject(result);
                    if (object.has("list")){
                        JSONArray jsonArray = object.getJSONArray("list");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject stuJSONObject = jsonArray.getJSONObject(i);
                            final StuMember stuMember = new StuMember();
                            stuMember.setStuName(stuJSONObject.getString("stuName"));
                            if (stuJSONObject.has("stuHeadImg")) {
                                stuMember.setStuHeadImg(stuJSONObject.getString("stuHeadImg"));
                            }
                            stuMember.setStuIdcard(stuJSONObject.getString("stuIdcard"));
                            stuMember.setStuSex(stuJSONObject.getString("stuSex"));
                            stuMember.setStuPhone(stuJSONObject.getString("stuPhone"));
                            stuMember.setStuAddress(stuJSONObject.getString("stuAddress"));
                            String stuDorm = stuJSONObject.getString("stuDorm");
                            stuMember.setsBedNum(stuDorm.substring(stuDorm.indexOf("舍")+1));
                            stuMember.setSclass(stuJSONObject.getString("sclass"));

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i=0;i<dormMembers.size();i++){
                                        if (stuMember.getStuName().equals(dormMembers.get(i).getStuName())
                                                && stuMember.getStuIdcard().equals(dormMembers.get(i).getStuIdcard())){
                                            dormMembers.remove(i);
                                        }
                                    }
                                    dormMembers.add(stuMember);
                                    listAdapter.notifyDataSetChanged();
                                    dormListRefresh.setRefreshing(false);
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //ListView的适配器
    class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dormMembers.size();
        }
        @Override
        public Object getItem(int position) {
            return dormMembers.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView){
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.dorm_member_item,null);
            }
            ImageView headImage = convertView.findViewById(R.id.dorm_item_head);
            TextView dormNameText = convertView.findViewById(R.id.dorm_name_text);
            TextView dormBedNumText = convertView.findViewById(R.id.dorm_bednum_text);
            TextView dormClassText = convertView.findViewById(R.id.dorm_class_text);
            TextView dormAddresstext = convertView.findViewById(R.id.dorm_address_text);

            StuMember dormMember = (StuMember) getItem(position);

            String headStr = dormMember.getStuHeadImg();
            if (!"".equals(headStr) && headStr!=null){
                Bitmap bitmap = Base64BitmapUtil.base64ToBitmap(headStr);
                bitmap = BitmapToRoundUtils.toRoundBitmap(bitmap);
                headImage.setImageBitmap(bitmap);
            }else {
                if ("男".equals(dormMember.getStuSex())){
                    headImage.setImageResource(R.drawable.ic_class_member_boy);
                }else{
                    headImage.setImageResource(R.drawable.ic_class_member_girl);
                }
            }

            dormNameText.setText(dormMember.getStuName());
            if (dormMember.getStuAddress()!=null && !"".equals(dormMember.getStuAddress())) {
                dormAddresstext.setText(dormMember.getStuAddress());
            }
            dormBedNumText.setText(dormMember.getsBedNum());
            dormClassText.setText(dormMember.getSclass());

            return convertView;
        }
    }
}
