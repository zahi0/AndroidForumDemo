package com.khorosho.bbs.JavaBean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;

public class MyUser extends BmobUser {

    private int rank;   //权限等级
    private String slogan;  //个性签名
    private BmobFile image;//头像

    public void setSlogan(String slogan){
        this.slogan = slogan;}

    public String getSlogan() {
        return slogan;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setRank(int rank){
        this.rank=rank;
    }

    public int getRank(){
        return rank;
    }
}
