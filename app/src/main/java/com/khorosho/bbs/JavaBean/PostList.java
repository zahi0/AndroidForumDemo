package com.khorosho.bbs.JavaBean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by kirito on 2017/4/29.
 */

public class PostList extends BmobObject {
   private String title,content,publisher,time,BOID,url,pid,head; //标题，内容，发布者,时间,postlist的objectID,头像url，发布者id，发布者头像
    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return title;
    }
    public void setContent(String content){
        this.content=content;
    }
    public String getContent(){
        return content;
    }
    public void setPublisher(String publisher){
        this.publisher=publisher;
    }
    public String getPublisher(){
        return publisher;
    }
    public void setTime(String time){
        this.time=time;
    }
    public String getTime(){
        return time;
    }
    public void setUrl(String url){this.url=url;}
    public String getUrl(){return url;}
    public void setBOID(String BOID){this.BOID=BOID;}
    public String getBOID(){return BOID;}
    public void setPid(String id){this.pid=id;}
    public String getPid(){return pid;}
    public void setHead(String head){this.head=head;}
    public String getHead(){return head;}
}
