package com.helloandroid.project3_chatbot;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MessageAdeapter extends RecyclerView.Adapter<MessageAdeapter.CustomViewHolder> {

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public  CustomViewHolder (View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textMessage);
        }
    }

    List<ResponseMessage> responseMessageList;

    public MessageAdeapter(List<ResponseMessage> responseMessageList) {
        this.responseMessageList = responseMessageList;
    }

    @Override
    public int getItemViewType(int position) {
        if(responseMessageList.get(position).isMe()) {
            return R.layout.me_bubble;
        }
        return R.layout.bot_bubble;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { //새로운 뷰 생성
        return new CustomViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdeapter.CustomViewHolder customViewHolder, int position) { //ListView의 getView 부분 담당
        customViewHolder.textView.setText(responseMessageList.get(position).getTextMessage());
    }

    @Override
    public int getItemCount() {
        return responseMessageList.size();
    }
}
