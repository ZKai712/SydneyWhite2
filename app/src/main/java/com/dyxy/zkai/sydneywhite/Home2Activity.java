package com.dyxy.zkai.sydneywhite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.dyxy.zkai.sydneywhite.view.ClassListView;
import com.dyxy.zkai.sydneywhite.view.DormListView;
import com.dyxy.zkai.sydneywhite.view.FlowView;
import com.dyxy.zkai.sydneywhite.view.MineView;
import com.example.xch.scanzxing.zxing.android.CaptureActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http2.Http2;

public class Home2Activity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    public ImageView scanImage;

    private static final int REQUEST_CODE_SCAN = 0x0012;
    private static final int REQUEST_CODE_SCAN2 = 0x0011;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";

    private ViewPager viewPager;
    private MyViewPagerAdapter viewPagerAdapter;
    private BottomNavigationView navBarView;
    //当bottomNav被选定时
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_flow:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_class:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_dorm:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_mine:
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_home2);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.home_scan_title);

        navBarView = findViewById(R.id.bottom_navigation);
        navBarView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);

        //点击扫码扫码
        scanImage = findViewById(R.id.scan_image);
        scanImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (viewPager.getCurrentItem()) {
                    case 1:
                        goScan(REQUEST_CODE_SCAN);
                        break;
                    case 2:
                        goScan(REQUEST_CODE_SCAN2);
                        break;
                    default:
                        break;
                }
            }
        });

        //设置viewpager与bottomNav绑定
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(getIntent().getIntExtra("page",0));

        applyPermissions();

    }

    //TODO:申请权限打开相机拍照
    private void applyPermissions() {
        String[] perms = new String[]{"android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.CAMERA"};
        //动态权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions(perms)) {
                ActivityCompat.requestPermissions(this, perms, 0x777);
            }
        }
    }
    private boolean checkPermissions(String[] permissions){
        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(this,
                    permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }




    /**
     * 跳转到扫码界面扫码
     */
    public void goScan(int CODE){
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, CODE);
    }

    private Toast flowToast;


    //不同ViewPager的视图
    private ClassListView classListView;
    private DormListView dormListView ;
    private MineView mineView;
    private FlowView flowView;
    //ViewPager适配器
    class MyViewPagerAdapter extends PagerAdapter {

        public MyViewPagerAdapter() {

        }
        @Override
        public int getCount() {
            return 4;
        }
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            //在加载布局时加载数据
            View view = null;
            if (position==0) {
                view = LayoutInflater.from(Home2Activity.this)
                        .inflate(R.layout.activity_flow, null);
                flowView = new FlowView(Home2Activity.this,Home2Activity.this,view);

            }else if (position==1){
                view = LayoutInflater.from(Home2Activity.this)
                        .inflate(R.layout.home2_class_list,null);
                classListView = new ClassListView(Home2Activity.this,Home2Activity.this,view);


            }else if (position==2){
                view = LayoutInflater.from(Home2Activity.this)
                        .inflate(R.layout.home2_dorm_list,null);
                dormListView = new DormListView(Home2Activity.this,Home2Activity.this,view);


            }else if (position==3){
                view = LayoutInflater.from(Home2Activity.this)
                        .inflate(R.layout.activity_mine_setting,null);
                mineView = new MineView(Home2Activity.this,Home2Activity.this,view);


            }

            container.addView(view);
            return view;
        }
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view==o;
        }
    }

    //TODO:是否打开相应权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0x777:
                for(int result:grantResults){
                    if(result!=PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "你拒绝了权限申请，可能无法打开相机哟！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
        }
    }
   //回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
               case MineView.TAKE_PICTURE:   //通过相机获取图片回调
                    mineView.getPhotoes(data);
                    break;
                case MineView.CHOOSE_PICTURE:   //通过本地获取图片回调
                    mineView.cutImage(data.getData()); // 对图片进行裁剪处理
                    break;
                case MineView.CROP_SMALL_PICTURE:      //裁剪
                    if (data != null) {
                        mineView.setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
                case FlowView.CAMERA_REQUEST_CODE:
                    case FlowView.GALLERY_REQUEST_CODE:

                        flowView.getPhoto(data);
                        break;
                case REQUEST_CODE_SCAN :             //二维码数据分析
                    if (data != null) {
                        //返回的文本内容
                        String content = data.getStringExtra(DECODED_CONTENT_KEY);
                        Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                        //{codedBitmap=android.graphics.Bitmap@698884e, codedContent=MECARD:faculty:电子信息与传媒学院;className:软件技术一班;stuTotal:27;teacher:王老师;;}
                        Intent intent = new Intent(Home2Activity.this, ScanClassActivity.class);
                        intent.putExtra("content",content);
                        startActivity(intent);
                    }
                    break;
                case REQUEST_CODE_SCAN2:
                    if (data != null) {
                        //返回的文本内容
                        String contentDorm = data.getStringExtra(DECODED_CONTENT_KEY);
                        Log.e("TEST",contentDorm);

                        Intent intent = new Intent(Home2Activity.this, ScanDormActivity.class);
                        intent.putExtra("contentDorm",contentDorm);
                        startActivity(intent);

                    }
                    break;
            }
        }



    }

    //当viewpager改变时的三个实现方法
    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }
    @Override
    public void onPageSelected(int i) {
        flowToast = Toast.makeText(Home2Activity.this,"点击进行拍照审核，长按进行地图导航",Toast.LENGTH_SHORT);
        if(i==0){
            navBarView.setSelectedItemId(R.id.navigation_flow);
            scanImage.setVisibility(View.GONE);

            flowToast.show();
        }else if(i==1){
            navBarView.setSelectedItemId(R.id.navigation_class);
            if (MainActivity.stuInfo.getSclass()==null || "".equals(MainActivity.stuInfo.getSclass())){
                scanImage.setVisibility(View.VISIBLE);
            }else{
                scanImage.setVisibility(View.GONE);
            }

        }else if(i==2){
            navBarView.setSelectedItemId(R.id.navigation_dorm);
            if (null == MainActivity.stuInfo.getStuDorm() || "".equals(MainActivity.stuInfo.getStuDorm())) {
                scanImage.setVisibility(View.VISIBLE);
            }else {
                scanImage.setVisibility(View.GONE);
            }

        }else if(i==3){
            navBarView.setSelectedItemId(R.id.navigation_mine);
            scanImage.setVisibility(View.GONE);

        }


    }
    @Override
    public void onPageScrollStateChanged(int i) {
        if (i!=0 && flowToast!=null){
            flowToast.cancel();
        }
    }

    //程序退出反馈
    private long exitTime=0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime)>2000){
                Toast.makeText(Home2Activity.this,"再点一下退出程序~",Toast.LENGTH_SHORT).show();
                exitTime=System.currentTimeMillis();
            }else{
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




}
