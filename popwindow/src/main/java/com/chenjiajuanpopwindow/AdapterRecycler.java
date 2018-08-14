package com.chenjiajuanpopwindow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjiajuan on 2018/8/12.
 */

public class AdapterRecycler extends RecyclerView.Adapter<AdapterRecycler.ItemViewHolder> {
    private Context context;
    private List<String> list=new ArrayList<>();

    public AdapterRecycler(Context context){
       this.context=context;
       for (int i=0;i<20;i++){
           int a= (int) (Math.random()*10);
           list.add("i:"+i+", number :"+a);
       }
    }
    public void setList(List<String> list){
        this.list.clear();
        this.list.addAll(list);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_item_pop,parent,false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.tvText.setText(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView tvText;
        public ItemViewHolder (View itemView){
            super(itemView);
            tvText=itemView.findViewById(R.id.tvText);
        }
    }
}
