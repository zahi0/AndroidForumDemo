package com.khorosho.bbs.JavaBean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class PostList extends BmobObject {
    //标题，内容，发布者,时间,postlist的objectID,发布者id，发布者头像
    private String title;
    private String content;
    private String publisher;
    private String time;
    private String objectID;
    private String publisherID;
    private String head;

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

//    public void setUrl(String url){this.url=url;}
//
//    public String getUrl(){return url;}

    public void setObjectID(String objectID){this.objectID=objectID;}

    public String getObjectID(){return objectID;}

    public void setPublisherID(String id){this.publisherID=id;}

    public String getPublisherID(){return publisherID;}

    public void setHead(String head){this.head=head;}

    public String getHead(){return head;}
}
