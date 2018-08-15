package com.khorosho.bbs.Activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.khorosho.bbs.JavaBean.MyUser;
import com.khorosho.bbs.R;
import com.khorosho.bbs.Adapter.ReplyAdapter;
import com.khorosho.bbs.JavaBean.ReplyList;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostInfo extends AppCompatActivity {

//    CircleImageView reply_head;
    private List<ReplyList> replyList;
    ReplyAdapter adapter ;
    RecyclerView recyclerView;
//    TextView reply,replyer,timeinreply,floor;
    private String objectId;
    private int floor;
    private boolean banReply=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        Intent intent=getIntent();
        objectId=intent.getStringExtra("objectId");
        /*
        String title=intent.getStringExtra("title");
        String content=intent.getStringExtra("content");
        String time=intent.getStringExtra("time");
        String url=intent.getStringExtra("url");
        String publishername=intent.getStringExtra("publisher");
        TextView titleinpost=(TextView)findViewById(R.id.titleinpost);
        titleinpost.setText(title);
        TextView contentinpost=(TextView)findViewById(R.id.contentinpost);
        contentinpost.setText(content);
        TextView timeinpost=(TextView)findViewById(R.id.timeinpost);
        timeinpost.setText(time);
        TextView publisherinpost=(TextView)findViewById(R.id.publisherinpost);
        publisherinpost.setText(publishername);
        CircleImageView headinpost=(CircleImageView)findViewById(R.id.headinpost);
        DisplayImageOptions options=new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
        ImageLoader.getInstance().displayImage(url,headinpost,options);
*/
        replyList=new ArrayList<>();
        initReplyList();
        /*
        reply_head=(CircleImageView)findViewById(R.id.reply_head) ;
        reply=(TextView)findViewById(R.id.reply);
        replyer=(TextView) findViewById(R.id.replyer);
        timeinreply=(TextView)findViewById(R.id.timeinreply);
        floor=(TextView)findViewById(R.id.floor);
        */
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view2);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter=new ReplyAdapter(replyList);
        recyclerView.setAdapter(adapter);
        final SwipeRefreshLayout swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swip_refresh2) ;
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(2000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initReplyList();
                                adapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    public void initReplyList(){
        replyList.clear();
        BmobQuery<ReplyList> query = new BmobQuery<ReplyList>();
        query.addWhereEqualTo("belongto",objectId);
        query.setLimit(50);
        query.findObjects(new FindListener<ReplyList>() {
            @Override
            public void done(List<ReplyList> list, BmobException e) {
                if (e==null){
                    floor=list.size();
                  //  z=y+2;
                    for (int i=0;i < floor;i++){
                        ReplyList rList =new ReplyList();
                        rList.setTitle(list.get(i).getTitle());
                        rList.setFloor(list.get(i).getFloor());
                        rList.setTimeInReply( list.get(i).getCreatedAt());
                        rList.setReplyer(list.get(i).getReplyer());
                        rList.setReply(list.get(i).getReply());
                        if (list.get(i).getHead()==null) rList.setHead("drawable://" + R.drawable.nav_icon);
                        else rList.setHead(list.get(i).getHead());
                        replyList.add(rList);
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    e.printStackTrace();
                    Toast.makeText(PostInfo.this,"初始化失败",Toast.LENGTH_SHORT).show();
                    banReply=false;
                }
            }
        });
    }

    public void send(View v){
        EditText inputText=(EditText)findViewById(R.id.replytext);
        String reply=inputText.getText().toString();
        ReplyList rList=new ReplyList();
        MyUser user= BmobUser.getCurrentUser(MyUser.class);
        rList.setReply(reply);
        rList.setReplyer(user.getUsername());
        rList.setBelongTo(objectId);
        rList.setFloor((++floor)+"楼");
        rList.setTimeInReply("刚刚");
        String url;
        if (user.getImage()==null) url="drawable://" + R.drawable.nav_icon;
        else url=user.getImage().getFileUrl();
        rList.setHead(url);
        rList.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if ((e==null)&&banReply) {Toast.makeText(PostInfo.this,"发表成功",Toast.LENGTH_SHORT).show();
               // y++;
                }
                else {
                    e.printStackTrace();
                    Toast.makeText(PostInfo.this,"发表失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
        inputText.setText(null);
        replyList.add(rList);
        adapter.notifyDataSetChanged();
    }
}
