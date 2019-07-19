package com.dyxy.zkai.sydneywhite.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.dyxy.zkai.sydneywhite.MainActivity;
import com.dyxy.zkai.sydneywhite.R;
import com.dyxy.zkai.sydneywhite.entity.StuMember;
import com.dyxy.zkai.sydneywhite.utils.Base64BitmapUtil;
import com.dyxy.zkai.sydneywhite.utils.BitmapToRoundUtils;

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

public class ClassListView {
    private TextView scanEwmText;
    private TextView memberText;
    private LinearLayout teacherLayout;
    private TextView teacherNameText;
    private TextView teacherPhoneText;
    private SwipeRefreshLayout classListRefresh;

    private ListView classMemberList;
    private List<StuMember> stuMembers;
    private MyListAdapter listAdapter;

    private Context context;
    private View view;
    private Activity activity;
    private static final int MSG_ONE = 1;

//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case MSG_ONE :
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            stuMembers.clear();
//                            initViewData(MainActivity.stuInfo.getSclass());
//                        }
//                    }).start();
//                    break;
//            }
//        }
//    };

    public ClassListView(final Activity activity, final Context context, View view) {
        this.activity = activity;
        this.context = context;
        this.view = view;

        scanEwmText = view.findViewById(R.id.scan_ewm_text);
        classMemberList = view.findViewById(R.id.class_member_list);
        memberText = view.findViewById(R.id.member_text);
        teacherLayout = view.findViewById(R.id.teacher_layout);
        teacherNameText = view.findViewById(R.id.teacher_name_text);
        teacherPhoneText = view.findViewById(R.id.teacher_phone_text);
        classListRefresh = view.findViewById(R.id.class_list_refresh);

        listAdapter = new MyListAdapter();
        stuMembers = new ArrayList<>();
        classMemberList.setAdapter(listAdapter);

        Intent intent = activity.getIntent();
        int result = intent.getIntExtra("resultClass", -1);
        if (result == 1 || (MainActivity.stuInfo.getSclass() != null && !"".equals(MainActivity.stuInfo.getSclass()))) {
            scanEwmText.setVisibility(View.GONE);
            classListRefresh.setVisibility(View.VISIBLE);
            memberText.setVisibility(View.VISIBLE);
            teacherLayout.setVisibility(View.VISIBLE);

            memberText.setText(MainActivity.stuInfo.getSclass()+"班级成员");
            teacherNameText.setText(MainActivity.stuInfo.getClassTeacher());
            teacherPhoneText.setText(MainActivity.stuInfo.getTeacherPhone());

            initViewData(MainActivity.stuInfo.getSclass());
            refresh();
//            autoRefresh();
        }

    }

    /**
     * 下拉刷新
     */
    private void refresh(){
        //下拉刷新
        classListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                stuMembers.clear();
                initViewData(MainActivity.stuInfo.getSclass());
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
//                while (true) {
//                    try {
//                        Thread.sleep(5000);
//                        Message msg = new Message();
//                        msg.what = MSG_ONE;
//                        handler.sendMessage(msg);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        }).start();
//    }

    /**
     * 班级成员列表
     * @param className
     */
    private void initViewData(String className){
        String path = MainActivity.QUERYURL+"?sclass="+className;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(path)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ERROR","班级成员数据加载失败");
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

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i=0;i<stuMembers.size();i++){
                                        if (stuMember.getStuName().equals(stuMembers.get(i).getStuName())
                                                && stuMember.getStuIdcard().equals(stuMembers.get(i).getStuIdcard())){
                                            stuMembers.remove(i);
                                        }
                                    }
                                    stuMembers.add(stuMember);
                                    listAdapter.notifyDataSetChanged();
                                    classListRefresh.setRefreshing(false);
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
            return stuMembers.size();
        }
        @Override
        public Object getItem(int position) {
            return stuMembers.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            StuMember classMember = (StuMember) getItem(position);
            if (null == convertView){
                convertView = LayoutInflater.from(context)
                        .inflate( R.layout.class_member_item,null);
            }

            ImageView headImage = convertView.findViewById(R.id.item_head_image);
            TextView classNameText = convertView.findViewById(R.id.sname_text);
            TextView classAddressText = convertView.findViewById(R.id.saddress_text);
            TextView sphoneText = convertView.findViewById(R.id.sphone_text);

            String headImgStr = classMember.getStuHeadImg();
            if (!"".equals(headImgStr) && headImgStr!=null){
                Bitmap bitmap = Base64BitmapUtil.base64ToBitmap(headImgStr);
                bitmap = BitmapToRoundUtils.toRoundBitmap(bitmap);
                headImage.setImageBitmap(bitmap);
            }else {
                if ("女".equals(classMember.getStuSex())){
                    headImage.setImageResource(R.drawable.ic_class_member_girl);
                }else {
                    headImage.setImageResource(R.drawable.ic_class_member_boy);
                }
            }
            if (!"".equals(classMember.getStuPhone()) && classMember.getStuPhone()!=null){
                sphoneText.setText(classMember.getStuPhone());
            }
            if (!"".equals(classMember.getStuAddress()) && classMember.getStuAddress()!=null) {
                classAddressText.setText(classMember.getStuAddress());
            }
            classNameText.setText(classMember.getStuName());


            return convertView;
        }
    }

}
