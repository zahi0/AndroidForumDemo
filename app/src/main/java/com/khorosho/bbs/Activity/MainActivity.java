package com.khorosho.bbs.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.khorosho.bbs.Adapter.MyAdapter;
import com.khorosho.bbs.JavaBean.MyUser;
import com.khorosho.bbs.JavaBean.PostList;
import com.khorosho.bbs.R;
import com.khorosho.bbs.JavaBean.ReplyList;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerlayout;
    private PopupWindow mPopWindow;
    String un, pw;
    boolean islogin=false;
    CircleImageView head_icon;
    NavigationView navView;
    private List<PostList> pl;
    MyAdapter adapter ;
    RecyclerView recyclerView;
    int rank;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "558f1a54e6f0a2462ab09b22aec8187d");
        //初始化界面
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerlayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navView=(NavigationView)findViewById(R.id.nav_view);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                switch (item.getItemId()){
                    case R.id.post:
                        Intent intent=new Intent(MainActivity.this,MyPost.class);
                        startActivity(intent);
                    case R.id.reply:
                        //TODO
                    case R.id.friend:
                        //TODO
                    case R.id.info:
                        //TODO
                    case R.id.task:
                        //TODO
                }
                mDrawerlayout.closeDrawers();
                return  true;
            }
        });
        pl=new ArrayList<>();
        initPostList();
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter=new MyAdapter(pl);
        recyclerView.setAdapter(adapter);
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swip_refresh) ;
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(1000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initPostList();
                                adapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)// 防止内存溢出的，图片太多就这这个。还有其他设置
               //如Bitmap.Config.ARGB_8888
              //  .showImageOnLoading(R.drawable.ic_launcher)   //默认图片
              //  .showImageForEmptyUri(R.drawable.kedou)    //url爲空會显示该图片，自己放在drawable里面的
             //   .showImageOnFail(R.drawable.k2k2k2k)// 加载失败显示的图片
             //   .displayer(new RoundedBitmapDisplayer(5))  //圆角，不需要请删除
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .memoryCacheExtraOptions(480, 800)// 缓存在内存的图片的宽和高度
              //  .diskCacheExtraOptions(480, 800, Bitmap.CompressFormat.PNG, 70,null) //CompressFormat.PNG类型，70质量（0-100）
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(2 * 1024 * 1024) //缓存到内存的最大数据
                .diskCacheSize(50 * 1024 * 1024)  //缓存到文件的最大数据
                .diskCacheFileCount(1000)            //文件数量
                .defaultDisplayImageOptions(options)  //上面的options对象，一些属性配置
                .build();
        ImageLoader.getInstance().init(config); //初始化

        islogin=getdata();
        if (islogin) {
            MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
            if (myUser!=null){
                String imageUrl;
                if (myUser.getImage()==null){
                    imageUrl = "drawable://" + R.drawable.nav_icon;
                }else {
                   imageUrl=myUser.getImage().getFileUrl();
                    }
                initview(myUser.getUsername(),myUser.getPersonal_signature(),imageUrl);
                Toast.makeText(MainActivity.this,"自动登陆成功",Toast.LENGTH_SHORT).show();
            }
        }
        //item的点击事件
        adapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
               PostList postList=pl.get(position);
                Intent intent=new Intent(MainActivity.this,PostInfo.class);
                intent.putExtra("objectid",postList.getBOID());
                startActivity(intent);
            }
        });

        //item的长按事件
        adapter.setOnLongItemClickListener(new OnRecyclerViewLongItemClickListener() {
            @Override
            public void onLongItemClick(View view, final int position) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("删除本帖").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(islogin){
                            PostList postList=pl.get(position);
                            MyUser thisuser=BmobUser.getCurrentUser(MyUser.class);
                            getrank(thisuser.getUsername(),true);
                            getrank(postList.getPublisher(),false);
                            boolean isMaster=isMaster(thisuser.getUsername(),postList.getPublisher());
                            if (getrankcompare()||isMaster){
                                PostList p=new PostList();
                                final String id=postList.getBOID();
                                p.delete(id,new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e==null) {Toast.makeText(MainActivity.this,"deleted",Toast.LENGTH_SHORT).show();
                                            pl.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            deleteReply(id);}
                                        else {
                                            e.printStackTrace();
                                            Toast.makeText(MainActivity.this,"deleted fail",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else Toast.makeText(MainActivity.this,"你没有此权限",Toast.LENGTH_SHORT).show();
                        }
                       else Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                            }
                        }).setCancelable(true).show();
                    }
                });
            }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.backup:
              //  Toast.makeText(this,"你点击了备份",Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
               // Toast.makeText(this,"你点击了删除",Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
               // Toast.makeText(this,"你点击了设置",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerlayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    public void popup(){
        View contentView= LayoutInflater.from(MainActivity.this).inflate(R.layout.popuplayout,null);
        mPopWindow = new PopupWindow(contentView,
                800, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        mPopWindow.setFocusable(true);
        final EditText inputusername=(EditText)contentView.findViewById(R.id.account) ;
        final EditText inputpassword=(EditText)contentView.findViewById(R.id.password) ;
        View rootview = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        mPopWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);
        mDrawerlayout.closeDrawers();
        inputusername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                un = inputusername.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pw = inputpassword.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
            }

    public void login(View v) {
        final MyUser user =new MyUser();
        user.setUsername(un);
        user.setPassword(pw);
        user.login(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e==null){
                    mPopWindow.dismiss();
                    Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    BmobQuery<MyUser> query = new BmobQuery<MyUser>();
                    query.addWhereEqualTo("username",un);
                    query.findObjects(new FindListener<MyUser>() {
                        @Override
                        public void done(List<MyUser> list, BmobException e) {
                            if (e==null){
                                String a,imageUrl;
                                a=list.get(0).getPersonal_signature();
                                if (list.get(0).getImage()==null) {imageUrl="drawable://" + R.drawable.nav_icon;
                                    initview(un, a, imageUrl);
                                }else initview(un,a,list.get(0).getImage().getFileUrl());
                            }else {Toast.makeText(MainActivity.this,"query fail"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                initview(un,null,null);
                            }
                        }
                    });
                    islogin=true;
                    savedate(islogin);
                }else {
                    Toast.makeText(MainActivity.this,"登陆失败",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public void sign(View v){
        MyUser newUser = new MyUser();
        newUser.setUsername(un);
        newUser.setPassword(pw);
        newUser.setRank(1);
        newUser.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                } else {
                    //注册失败
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"此用户名已存在，请换一个用户名",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void userinfo(View v){   //头像点击事件
        islogin=getdata();
        if (islogin){
            Intent intent=new Intent(this,UserInfo.class);
            startActivity(intent);
        }else popup();
    }

    public void fab(View v){     //悬浮按钮点击事件
        if (islogin){
            Intent intent=new Intent(this,PostEdit.class);
            startActivity(intent);
        }
        else Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initPostList();
        if (!getdata()) {
            String imageUrl = "drawable://" + R.drawable.nav_icon;
            initview("请登录",null,imageUrl);
        }else {
            MyUser m = BmobUser.getCurrentUser(MyUser.class);
            BmobQuery<MyUser> query = new BmobQuery<MyUser>();
            query.getObject(m.getObjectId(), new QueryListener<MyUser>() {
                @Override
                public void done(MyUser myUser, BmobException e) {
                    if (e == null) {
                        String imageUrl;
                        if (myUser.getImage() == null)
                            imageUrl = "drawable://" + R.drawable.nav_icon;
                        else imageUrl = myUser.getImage().getFileUrl();
                        initview(myUser.getUsername(), myUser.getPersonal_signature(), imageUrl);
                    } else {
                        Toast.makeText(MainActivity.this, "无法查询", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public interface OnRecyclerViewLongItemClickListener {
        void onLongItemClick(View view, int position);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    private void initview(String uname,String ps,String url){
        View headerView =navView.getHeaderView(0);
        TextView username= (TextView) headerView.findViewById(R.id.username);
        username.setText(uname);
        TextView personal_signature= (TextView) headerView.findViewById(R.id.personal_signature);
        personal_signature.setText(ps);
        head_icon = (CircleImageView)headerView.findViewById(R.id.icon_image);
        ImageLoader.getInstance().displayImage(url,head_icon);
    }

    public void initPostList(){
        pl.clear();
        BmobQuery<PostList> query = new BmobQuery<PostList>();
        query.setLimit(50);
        query.findObjects(new FindListener<PostList>() {
            @Override
            public void done(final List<PostList> list, BmobException e) {
                if (e==null){
                    for (int i=0;i<list.size();i++){
                         final PostList postList=new PostList();
                        postList.setTitle(list.get(i).getTitle());
                        postList.setTime( list.get(i).getCreatedAt());
                        postList.setPublisher(list.get(i).getPublisher());
                        postList.setContent(list.get(i).getContent());
                        postList.setBOID(list.get(i).getObjectId());
                        postList.setPid(list.get(i).getPid());
                        //获取头像url
                        if (list.get(i).getHead()==null) postList.setHead("drawable://" + R.drawable.nav_icon);
                        else postList.setHead(list.get(i).getHead());
                        pl.add(postList);
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"初始化失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getrank(String s, final boolean isfirst){    //get rank level from cloud database
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereEqualTo("username",s);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e==null){;
                     int a;
                    a=list.get(0).getRank();
                    if (isfirst) saveint(a);
                    else {
                        if (a<getint()) saverankcompare(true);
                        else saverankcompare(false);
                    }
                 //   Toast.makeText(MainActivity.this,"--->"+a,Toast.LENGTH_SHORT).show();
                }
                else {Toast.makeText(MainActivity.this,"无法查询权限",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();}
            }
        });
    }

    public void deleteReply(String id){
        BmobQuery<ReplyList> query = new BmobQuery<ReplyList>();
        query.setLimit(100);
        query.addWhereEqualTo("belongto",id);
        query.findObjects(new FindListener<ReplyList>() {
            @Override
            public void done(List<ReplyList> list, BmobException e) {
                if (e==null){;
                    ReplyList replylist=new ReplyList();
                    for (int i=0;i<list.size();i++){
                        replylist.delete(list.get(i).getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                            }
                        });
                    }
                }
                else {Toast.makeText(MainActivity.this,"无法查询权限",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();}
            }
        });
    }

    public boolean isMaster(String a,String b){
        if (a.equals(b)) return true;
        else return false;
    }

    public void saveint(int rank){   //save rank level
        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putInt("rank",rank);
        editor.apply();
    }

    public int getint(){   //get rank level
        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
        return  pref.getInt("rank",0);
    }

    public void savedate(boolean islogin){      //save login state
        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putBoolean("islogin",islogin);
        editor.apply();
    }

    public boolean getdata(){     //get login state
        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
        return  pref.getBoolean("islogin",false);
    }

    public void saverankcompare(boolean b){      //save rank compare result
        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putBoolean("result",b);
        editor.apply();
    }

    public boolean getrankcompare(){     //get rank compare result
        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
        return  pref.getBoolean("result",false);
    }
}