package com.khorosho.bbs.Activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.khorosho.bbs.JavaBean.MyUser;
import com.khorosho.bbs.JavaBean.PostList;
import com.khorosho.bbs.R;
import com.khorosho.bbs.JavaBean.ReplyList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class PostEdit extends AppCompatActivity {

    private EditText title,content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postedit);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        title=(EditText)findViewById(R.id.title1);
        content=(EditText)findViewById(R.id.content1);
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

    public void fab(View v){
        final MyUser myUser= BmobUser.getCurrentUser(MyUser.class);
        final PostList postList=new PostList();
        postList.setTitle(title.getText().toString());
        postList.setContent(content.getText().toString());
        postList.setPublisher(myUser.getUsername());
        postList.setPublisherID(myUser.getObjectId());
        final String url;
        if (myUser.getImage()==null) url="drawable://" + R.drawable.nav_icon;
        else url=myUser.getImage().getFileUrl();
        postList.setHead(url);
        postList.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null)  {
                    ReplyList replyList=new ReplyList();
                    replyList.setTitle(title.getText().toString());
                    replyList.setReply(content.getText().toString());
                    replyList.setReplyer(myUser.getUsername());
                    replyList.setFloor("楼主");
                    replyList.setBelongTo(s);
                    replyList.setHead(url);
                    replyList.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e==null)  {
                                Toast.makeText(PostEdit.this,"success",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                e.printStackTrace();
                                Toast.makeText(PostEdit.this,"fail",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                  //  Toast.makeText(PostEdit.this,"success",Toast.LENGTH_SHORT).show();
                }
                else {
                    e.printStackTrace();
                    Toast.makeText(PostEdit.this,"fail",Toast.LENGTH_SHORT).show();
                }
            }
        });
        finish();
    }
}
