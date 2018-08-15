package com.khorosho.bbs.Activity;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.khorosho.bbs.JavaBean.MyUser;
import com.khorosho.bbs.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfo extends AppCompatActivity {

    TextView userName, slogan;
    CircleImageView iconEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        slogan=(TextView)findViewById(R.id.personal_signature1);
        userName=(TextView)findViewById(R.id.username1);
        iconEdit = (CircleImageView)findViewById(R.id.icon_edit);
        MyUser myUser=BmobUser.getCurrentUser(MyUser.class);
        if (myUser!=null){
          //  Toast.makeText(UserInfo.this,"rank is "+myUser.getRank(),Toast.LENGTH_SHORT).show();
         userName.setText(myUser.getUsername());
         slogan.setText(myUser.getSlogan());
            //创建默认的ImageLoader配置参数
            ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                    .createDefault(this);
            //Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(configuration);
            if (myUser.getImage()==null){

            }else {
                String imageUrl=myUser.getImage().getFileUrl();
                ImageLoader.getInstance().loadImage(imageUrl,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        iconEdit.setImageBitmap(loadedImage);
                    }
                });
            }

        }
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

    public void ImageEdit(View v){
        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = handleImageOnKitKat(data);
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                iconEdit.setImageBitmap(bitmap);
                 final MyUser currentUser= BmobUser.getCurrentUser(MyUser.class);
                File file=new File(path);
//                final String a=file.getName();
                final BmobFile image=new BmobFile(file);
                currentUser.setImage(image);
                image.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            currentUser.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
//                                    if (e==null) Toast.makeText(UserInfo.this,"success",Toast.LENGTH_SHORT).show();
                                }
                            });
                            Toast.makeText(UserInfo.this,"上传成功",Toast.LENGTH_SHORT).show();
                            saveBoolean(true);
                        } else {
                            e.printStackTrace();
                            Toast.makeText(UserInfo.this,"上传失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                Log.e("--->", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String handleImageOnKitKat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id= docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+ "="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath=getImagePath(uri,null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        return imagePath;
    }

    private String getImagePath(Uri uri, String seletion) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, seletion, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public void nameEdit(View v){
        final EditText input=new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改用户名").setView(input);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final BmobUser userEdit=BmobUser.getCurrentUser();
                userEdit.setUsername(input.getText().toString());
                userEdit.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            userName.setText(input.getText().toString());
                            Toast.makeText(UserInfo.this,"修改成功",Toast.LENGTH_SHORT).show();
                            saveBoolean(true);
                        } else {
                            e.printStackTrace();
                            Toast.makeText(UserInfo.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.create().show();
    }

    public void logout(View v){
        BmobUser nUser=BmobUser.getCurrentUser();
        nUser.logOut();
        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putBoolean("islogin",false);
        editor.apply();
        finish();
    }

    public void PSEdit(View v){
        final EditText inputText=new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改个性签名").setView(inputText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyUser userEdit = BmobUser.getCurrentUser(MyUser.class);
                userEdit.setSlogan(inputText.getText().toString());
                userEdit.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            slogan.setText(inputText.getText().toString());
                            Toast.makeText(UserInfo.this,"修改成功",Toast.LENGTH_SHORT).show();
                            saveBoolean(true);
                        } else {
                            //修改失败
                            e.printStackTrace();
                            Toast.makeText(UserInfo.this,"修改失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.create().show();
    }

    public void saveBoolean(boolean isEdit){
        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putBoolean("isEdit",isEdit);
        editor.apply();
    }
}