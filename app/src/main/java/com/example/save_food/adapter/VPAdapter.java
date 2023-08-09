package com.example.save_food.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.save_food.R;
import com.example.save_food.chat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VPAdapter extends RecyclerView.Adapter<VPAdapter.ViewHolder> {
    Context context;
    ArrayList<ViewPagerItem> viewPagerItems;

    public VPAdapter(ArrayList<ViewPagerItem> viewPagerItems, Context context) {
        this.viewPagerItems = viewPagerItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewPagerItem viewPagerItem = viewPagerItems.get(position);
        Picasso.get().load(viewPagerItem.getImgaeId()).into(holder.imageView);
        holder.tvHeading.setText(viewPagerItem.Heding);
        holder.tvHeading2.setText(viewPagerItem.Heding2);
//        notifyDataSetChanged();
        holder.xemthemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TenDonhang", viewPagerItem.Heding);
                Log.d("Diachi", viewPagerItem.Heding2);
                String tendonhang = viewPagerItem.Heding;
                String Diachi = viewPagerItem.Heding2;
                String hisUid = viewPagerItems.get(position).getUid();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.custom_dialog_post, null);
                ImageView imageViewDialog;
                TextView TendonHangDialog;
                TextView DiachiDialog;
                imageViewDialog = dialogView.findViewById(R.id.dialog_post_img);
                TendonHangDialog = dialogView.findViewById(R.id.NamDonHangPostDialog);
                DiachiDialog = dialogView.findViewById(R.id.DiaChiPostDiaLog);
                Picasso.get().load(viewPagerItem.getImgaeId()).into(imageViewDialog);
                TendonHangDialog.setText(tendonhang);
                DiachiDialog.setText(Diachi);
                Log.d("TextViewValue", TendonHangDialog.getText().toString());
                AlertDialog dialog = builder.create();
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.show();

                Button closeButton = dialogView.findViewById(R.id.closeButton);
                Button lienheBtn = dialogView.findViewById(R.id.lienheBtn);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                lienheBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, chat.class);
                        intent.putExtra("hisUid", hisUid);
                        context.startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return viewPagerItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvHeading, tvHeading2;
        Button xemthemBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_viewpager);
            tvHeading = itemView.findViewById(R.id.tvHeading);
            tvHeading2= itemView.findViewById(R.id.tv_Heading2);
            xemthemBtn= itemView.findViewById(R.id.btn_xemthem);
        }
    }
}
