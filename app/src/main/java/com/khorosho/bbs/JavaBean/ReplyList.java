package com.khorosho.bbs.JavaBean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class ReplyList extends BmobObject {
    //回复内容，回复者，所属贴子ID,时间，楼层，标题（只在一楼显示），头像
    private  String reply;//,replyer,belongto,timeinreply,floor,title,head;
    private  String replyer;
    private  String belongTo;
    private  String timeInReply;
    private  String floor;
    private  String title;
    private  String head;

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

    public void setBelongTo(String belongTo){
        this.belongTo=belongTo;
    }

    public String getBelongto(){
        return belongTo;
    }

    public void setFloor(String floor){
        this.floor=floor;
    }

    public String getFloor(){
        return floor;
    }

    public void setTimeInReply(String timeInReply){this.timeInReply=timeInReply;}

    public String getTimeInReply(){return timeInReply;}

    public void setTitle(String title){
        this.title=title;
    }

    public String getTitle(){return title;}

    public void setHead(String head){this.head=head;}

    public String getHead(){return head;}
}
