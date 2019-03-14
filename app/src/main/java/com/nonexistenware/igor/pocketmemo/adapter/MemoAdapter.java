package com.nonexistenware.igor.pocketmemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nonexistenware.igor.pocketmemo.R;
import com.nonexistenware.igor.pocketmemo.callback.MemoEventListener;
import com.nonexistenware.igor.pocketmemo.model.Memo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MyViewHolder> {

    private Context context;
    private List<Memo> memoList;
    private Memo memo;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView dot, memo, timestamp;

        public MyViewHolder(View view) {
            super(view);
            dot = view.findViewById(R.id.dotTxt);
            memo = view.findViewById(R.id.memoTxt);
            timestamp = view.findViewById(R.id.timestampTxt);
        }
    }

    public MemoAdapter(Context context, List<Memo> memoList) {
        this.context = context;
        this.memoList = memoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Memo memo = memoList.get(position);
        holder.memo.setText(memo.getMemo());
        holder.timestamp.setText(Html.fromHtml("&#8226;"));
        holder.timestamp.setText(formatDate(memo.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }


}
