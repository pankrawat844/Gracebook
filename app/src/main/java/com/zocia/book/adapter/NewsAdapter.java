//package com.zocia.book.adapter;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.zocia.book.CommentActivity;
//import com.zocia.book.LoginActivity;
//import com.zocia.book.NewsFeedActivity;
//import com.zocia.book.R;
//import com.zocia.book.callbackinterface.ServerResponse;
//import com.zocia.book.model.News;
//import com.zocia.book.model.Usersdata;
//import com.zocia.book.networkcalls.ServerCallsProvider;
//import com.zocia.book.utils.AllUrls;
//import com.zocia.book.utils.BusyDialog;
//import com.zocia.book.utils.Helpers;
//import com.zocia.book.utils.Logger;
//import com.zocia.book.utils.PersistentUser;
//import com.zocia.book.utils.ToastHelper;
//
//import org.json.JSONObject;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//
//public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.viewholder> {
//    List<News> list;
//    Context context;
//
//    public NewsAdapter(Context context, List<News> list) {
//        this.list=list;
//        this.context=context;
//    }
//
//    @NonNull
//    @Override
//    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_news,parent,false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull viewholder holder, int position) {
//        int totalInterst=0;
//        final News userData=list.get(position);
//       holder.heading.setText(userData.getHeading());
//       holder.content.setText(userData.getDetails());
//       holder.date.setText(userData.getPostTime());
//       holder.commentCount.setText(userData.getCommentCount());
//        holder.comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent mIntent=new Intent(context, CommentActivity.class);
//                Bundle extra = new Bundle();
//                extra.putSerializable("news_id", userData.getId());
//                extra.putInt("screen", 4);
//                extra.putInt("position", 1);
//                mIntent.putExtra("extra", extra);
//                context.startActivity(mIntent);
//            }
//        });
//
//        if(userData.getUserId().equals(PersistentUser.getUserID(context)))
//            holder.delete.setVisibility(View.VISIBLE);
//
////        holder.delete.
//    }
//
//
//
//    public void alertfornewuser() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
////        LayoutInflater inflater = context.getApplicationContext().getLayoutInflater();
////        View mView = inflater.inflate(R.layout.dialog_app_logout, null);
////        builder.setView(mView);
//        builder.setCancelable(false);
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
////                leaverServerRequest();
//            }
//        });
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//
//            }
//        });
//        builder.setTitle("Remove News");
//        builder.setMessage("Are you sure you want remove this news?");
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        alertDialog.show();
//
////        TextView no_botton = (TextView) mView.findViewById(R.id.no_botton);
////        TextView yes_btn = (TextView) mView.findViewById(R.id.yes_btn);
////        TextView version_text = (TextView) mView.findViewById(R.id.version_text);
////        TextView version_text2 = (TextView) mView.findViewById(R.id.version_text2);
////        version_text.setText("Remove  News");
////        version_text2.setText("Are you sure you want remove this news");
////        no_botton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                alertDialog.dismiss();
////
////            }
////        });
////        yes_btn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                alertDialog.dismiss();
////                leaverServerRequest();
////            }
////        });
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    class viewholder extends RecyclerView.ViewHolder {
//        TextView heading, content,date,commentCount,comment;
//        ImageView delete;
//        public viewholder(@NonNull View itemView) {
//            super(itemView);
//            heading=itemView.findViewById(R.id.heading);
//            content=itemView.findViewById(R.id.details);
//            date=itemView.findViewById(R.id.dateOfPosting);
//            comment=itemView.findViewById(R.id.comment);
//            commentCount=itemView.findViewById(R.id.commentCount);
//            delete=itemView.findViewById(R.id.delete);
//        }
//    }
//
//    public void  deletePostion(int index) {
//        list.remove(index);
//        notifyDataSetChanged();
//    }
//
////    private void leaverServerRequest() {
////        if (!Helpers.isNetworkAvailable(context)) {
////            Helpers.showOkayDialog(context, R.string.no_internet_connection);
////            return;
////        }
////
////        String url = AllUrls.BASEURL + "newsDelete";
////        HashMap<String, String> allHashMap = new HashMap<>();
////        allHashMap.put("news_id", us);
////
////        HashMap<String, String> allHashMapHeader = new HashMap<>();
////        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
////        allHashMapHeader.put("authToken", PersistentUser.getUserToken(getActivity()));
////        mBusyDialog = new BusyDialog(getActivity());
////        mBusyDialog.show();
////
////        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, "TAG", new ServerResponse() {
////            @Override
////            public void onSuccess(String statusCode, String responseServer) {
////                mBusyDialog.dismis();
////                try {
////                    Logger.debugLog("responseServer", responseServer);
////                    JSONObject mJsonObject = new JSONObject(responseServer);
////                    if (mJsonObject.getBoolean("success")) {
////                        Intent intent = new Intent(getActivity(), NewsFeedActivity.class);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                        startActivity(intent);
////                        getActivity().finish();
////                    } else {
////                        String message = mJsonObject.getString("message");
////                        ToastHelper.showToast(getActivity(), message);
////                    }
////                } catch (Exception e) {
////
////                    mBusyDialog.dismis();
////
////                }
////            }
////
////            @Override
////            public void onFailed(String statusCode, String serverResponse) {
////                mBusyDialog.dismis();
////                if (statusCode.equalsIgnoreCase("404")) {
////                    PersistentUser.resetAllData(getActivity());
////                    Intent intent = new Intent(getActivity(), LoginActivity.class);
////                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                    startActivity(intent);
////
////                }
////            }
////        });
////    }
//
//}
