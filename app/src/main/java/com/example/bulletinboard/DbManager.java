package com.example.bulletinboard;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.DataSender;

public class DbManager {
    private Query mQuery;
    private List<NewPost> newPostList;
    private DataSender dataSender;
    private FirebaseDatabase db;
    private int ads_counter = 0;
    private String[] category_ads = {"Машины", "Компьютеры", "Смартфоны", "Бытовая техника"};

    public DbManager(DataSender dataSender) {
        this.dataSender = dataSender;
        newPostList =  new ArrayList<>();
        db = FirebaseDatabase.getInstance();
    }

    public void getDataFromDb(String path)
    {
        DatabaseReference dbRef = db.getReference(path);
        mQuery = dbRef.orderByChild("bulletin/time");
        readDataUpdate();

    }
    public void getMyAdsFromDb(String uid)
    {
        if(newPostList.size() > 0) newPostList.clear();
        DatabaseReference dbRef = db.getReference(category_ads[0]);
        mQuery = dbRef.orderByChild("bulletin/uid").equalTo(uid);
        readMyAdsDataUpdate(uid);
        ads_counter++;

    }
    public void readDataUpdate()
    {
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (newPostList.size() > 0) newPostList.clear();
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    NewPost newPost = ds.child("bulletin").getValue(NewPost.class);
                    newPostList.add(newPost);

                }
                dataSender.onDataRecived(newPostList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readMyAdsDataUpdate(final String uid)
    {
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    NewPost newPost = ds.child("bulletin").getValue(NewPost.class);
                    newPostList.add(newPost);

                }
                if(ads_counter > 3)
                {
                    dataSender.onDataRecived(newPostList);
                    newPostList.clear();
                    ads_counter = 0;
                }
                else
                {
                    DatabaseReference dbRef = db.getReference(category_ads[ads_counter]);
                    mQuery = dbRef.orderByChild("bulletin/uid").equalTo(uid);
                    readMyAdsDataUpdate(uid);
                    ads_counter++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
