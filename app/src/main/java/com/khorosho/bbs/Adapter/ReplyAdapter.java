package com.khorosho.bbs.Adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khorosho.bbs.JavaBean.ReplyList;
import com.khorosho.bbs.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder>  {

    private List<ReplyList> mPDB;

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView replyer,timeinreply,reply,floor,title;
        CircleImageView reply_head;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            replyer = (TextView) view.findViewById(R.id.replyer);
            timeinreply=(TextView)view.findViewById(R.id.timeinreply);
            reply=(TextView)view.findViewById(R.id.reply);
            floor=(TextView)view.findViewById(R.id.floor);
            reply_head=(CircleImageView)view.findViewById(R.id.reply_head);
            title=(TextView)view.findViewById(R.id.titleinreply);
        }
    }

    public ReplyAdapter(List<ReplyList> replyLists) {
        mPDB = replyLists;
    }

    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.replylist, parent, false);
        ReplyAdapter.ViewHolder holder = new ReplyAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ReplyAdapter.ViewHolder holder, int position) {
        ReplyList pdb = mPDB.get(position);
        holder.replyer.setText(pdb.getReplyer());
        holder.timeinreply.setText(pdb.getTimeinreply());
        holder.floor.setText(pdb.getFloor());
        holder.reply.setText(pdb.getReply());
        if (position==0) {holder.title.setVisibility(View.VISIBLE);
                           holder.title.setText(pdb.getTitle());}
        final DisplayImageOptions options=new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
        ImageLoader.getInstance().displayImage(pdb.getHead(),holder.reply_head,options);
/*
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereEqualTo("username",pdb.getReplyer());
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> mlist, BmobException e) {
                if (e==null){
                    ImageLoader.getInstance().displayImage(mlist.get(0).getImage().getFileUrl(),holder.reply_head,options);
                }else {
                    e.printStackTrace();}
            }
        });        */
    }

    @Override
    public int getItemCount() {
        return mPDB.size();
    }

}
