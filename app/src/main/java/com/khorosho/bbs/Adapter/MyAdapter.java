package com.khorosho.bbs.Adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khorosho.bbs.Activity.MainActivity;
import com.khorosho.bbs.JavaBean.PostList;
import com.khorosho.bbs.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public MainActivity.OnRecyclerViewItemClickListener mOnItemClickListener = null;//点击

    public MainActivity.OnRecyclerViewLongItemClickListener mOnLongItemClickListener = null;//长按

    public void setOnItemClickListener(MainActivity.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnLongItemClickListener(MainActivity.OnRecyclerViewLongItemClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }

    private List<PostList> mPostList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView publisher,time,title,content;
        CircleImageView headIcon;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            publisher = (TextView) view.findViewById(R.id.publisher);
            time=(TextView)view.findViewById(R.id.time);
            title=(TextView)view.findViewById(R.id.title);
            content=(TextView)view.findViewById(R.id.content);
            headIcon=(CircleImageView)view.findViewById(R.id.head);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnLongItemClickListener != null) {
                        mOnLongItemClickListener.onLongItemClick(v, getAdapterPosition());
                    }
                    return true;
                }
            });
        }
    }

    public MyAdapter(List<PostList> postList) {
        mPostList = postList;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postlist, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PostList post = mPostList.get(position);
        holder.publisher.setText(post.getPublisher());
        holder.time.setText(post.getTime());
        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());
        final DisplayImageOptions options=new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
        ImageLoader.getInstance().displayImage(post.getHead(),holder.headIcon,options);
/*
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereEqualTo("username",post.getPublisher());
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> mlist, BmobException e) {
                if (e==null){
                    String imageurl;
                    if (mlist.get(0).getImage().getFileUrl()==null) imageurl="drawable://" + R.drawable.nav_icon;
                    else imageurl=mlist.get(0).getImage().getFileUrl();
                    ImageLoader.getInstance().displayImage(imageurl,holder.head_icon,options);
                }else {
                    e.printStackTrace();}
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }
}