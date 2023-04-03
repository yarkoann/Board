package com.example.bulletinboard.adapter;

import com.example.bulletinboard.NewPost;

import java.util.List;

public interface DataSender {
    public void onDataRecived(List<NewPost> listData);
}
