package com.example.bulletinboard.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bulletinboard.DbManager;
import com.example.bulletinboard.EditActivity;
import com.example.bulletinboard.MainActivity;
import com.example.bulletinboard.NewPost;
import com.example.bulletinboard.R;
import com.example.bulletinboard.utils.MyConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolderData> {

    private List<NewPost> arrayPost;
    private Context context;
    private OnItemClickCustom onItemClickCustom;

    private DbManager dbManager;

    public DataAdapter(List<NewPost> arrayPost, Context context, OnItemClickCustom onItemClickCustom) {
        this.arrayPost = arrayPost;
        this.context = context;
        this.onItemClickCustom = onItemClickCustom;

    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ads, parent, false);
        return new ViewHolderData(view, onItemClickCustom);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position) {
        holder.setData(arrayPost.get(position));
    }

    @Override
    public int getItemCount() {return arrayPost.size();}

    public class ViewHolderData extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView tvPricePhone, tvDisc, tvTitle;
        private ImageView imAds;

        private LinearLayout edit_layout;

        private ImageButton deleteButton, editButton;
        private OnItemClickCustom onItemClickCustom;

        public ViewHolderData(@NonNull View itemView, OnItemClickCustom onItemClickCustom) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPricePhone = itemView.findViewById(R.id.tvPriceRhone);
            tvDisc = itemView.findViewById(R.id.tvDisc);
            imAds = itemView.findViewById(R.id.imAbs);
            edit_layout = itemView.findViewById(R.id.edit_layout);
            deleteButton = itemView.findViewById(R.id.emDeleteItem);
            editButton = itemView.findViewById(R.id.emEditItem);
            itemView.setOnClickListener(this);
            this.onItemClickCustom = onItemClickCustom;
        }

        public void setData(NewPost newPost)
        {
            if(newPost.getUid().equals(MainActivity.MAUTH))
            {
                edit_layout.setVisibility(View.VISIBLE);
            }
            else
            {
                edit_layout.setVisibility(View.GONE);
            }
            Picasso.get().load(newPost.getImageId()).into(imAds);
            tvTitle.setText(newPost.getTitle());
            String price_phone = "Цена: " + newPost.getPrice() + " Тел.: " + newPost.getPhone();
            tvPricePhone.setText(price_phone);
            String textDisc = newPost.getDisc();
            if(newPost.getDisc().length() > 50) textDisc = newPost.getDisc().substring(0, 50) + "...";
            tvDisc.setText(textDisc);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    deleteDialog(newPost, getAdapterPosition());
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(context, EditActivity.class);
                    i.putExtra(MyConstants.IMAGE_ID, newPost.getImageId());
                    i.putExtra(MyConstants.TITLE, newPost.getTitle());
                    i.putExtra(MyConstants.PRICE, newPost.getPrice());
                    i.putExtra(MyConstants.PHONE, newPost.getPhone());
                    i.putExtra(MyConstants.DISC, newPost.getDisc());
                    i.putExtra(MyConstants.KEY, newPost.getKey());
                    i.putExtra(MyConstants.UID, newPost.getUid());
                    i.putExtra(MyConstants.TIME, newPost.getTime());
                    i.putExtra(MyConstants.CAT, newPost.getCat());
                    i.putExtra(MyConstants.EDIT_STATE, true);
                    context.startActivity(i);
                }
            });
        }

        @Override
        public void onClick(View v)
        {
            onItemClickCustom.onItemSelected(getAdapterPosition());
        }
    }

    private void deleteDialog(final NewPost newPost, int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delet_title);
        builder.setMessage(R.string.delet_message);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dbManager.deleteItem(newPost);
                arrayPost.remove(position);
                notifyItemRemoved(position);
            }
        });
        builder.show();

    }
    public interface OnItemClickCustom
    {
        void onItemSelected(int position);
    }
    public void updateAdapter(List<NewPost> listData)
    {
        arrayPost.clear();
        arrayPost.addAll(listData);
        notifyDataSetChanged();

    }

    public void setDbManager(DbManager dbManager)
    {
        this.dbManager = dbManager;
    }
}
