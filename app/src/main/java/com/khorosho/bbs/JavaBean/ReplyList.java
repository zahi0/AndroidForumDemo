package com.khorosho.bbs.JavaBean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by kirito on 2017/4/30.
 */

public class ReplyList extends BmobObject {
  private  String reply,replyer,belongto,timeinreply,floor,title,head;//内容，发布者，所属贴子ID,时间，楼层，标题（只在一楼显示），头像
    public void setReply(String reply){
        this.reply=reply;
    }
    public String getReply(){
        return reply;
    }
    public void setReplyer(String replyer){
        this.replyer=replyer;
    }
    public String getReplyer(){
        return replyer;
    }
    public void setBelongto(String belongto){
        this.belongto=belongto;
    }
    public String getBelongto(){
        return belongto;
    }
    public void setFloor(String floor){
        this.floor=floor;
    }
    public String getFloor(){
        return floor;
    }
    public void setTimeinreply(String timeinreply){this.timeinreply=timeinreply;}
    public String getTimeinreply(){return timeinreply;}
    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){return title;}
    public void setHead(String head){this.head=head;}
    public String getHead(){return head;}
}
