package com.grace.book.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.R;
import com.otaliastudios.autocomplete.RecyclerViewPresenter;

import java.util.ArrayList;
import java.util.List;

public class UserPresenter extends RecyclerViewPresenter<Usersdata> {

    private ArrayList<Usersdata> myDataset = new ArrayList<>();
    @SuppressWarnings("WeakerAccess")
    protected Adapter adapter;

    @SuppressWarnings("WeakerAccess")
    public UserPresenter(@NonNull Context context, ArrayList<Usersdata> _myDataset) {
        super(context);
        myDataset.clear();
        myDataset.addAll(_myDataset);
    }

    @NonNull
    @Override
    protected PopupDimensions getPopupDimensions() {
        PopupDimensions dims = new PopupDimensions();
        dims.width = 600;
        dims.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        return dims;
    }

    @NonNull
    @Override
    protected RecyclerView.Adapter instantiateAdapter() {
        adapter = new Adapter();
        return adapter;
    }

    @Override
    protected void onQuery(@Nullable CharSequence query) {
        List<Usersdata> all = myDataset;
        if (TextUtils.isEmpty(query)) {
            adapter.setData(all);
        } else {
            query = query.toString().toLowerCase();
            List<Usersdata> list = new ArrayList<>();
            for (Usersdata u : all) {
                if (u.getFname().toLowerCase().contains(query) ||
                        u.getLname().toLowerCase().contains(query)) {
                    list.add(u);
                }
            }
            adapter.setData(list);
        }
        adapter.notifyDataSetChanged();
    }

    protected class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List<Usersdata> data;

        @SuppressWarnings("WeakerAccess")
        protected class Holder extends RecyclerView.ViewHolder {
            private View root;
            private TextView fullname;
            private TextView username;

            Holder(View itemView) {
                super(itemView);
                root = itemView;
                fullname = itemView.findViewById(R.id.fullname);
                username = itemView.findViewById(R.id.username);
            }
        }

        @SuppressWarnings("WeakerAccess")
        protected void setData(@Nullable List<Usersdata> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return (isEmpty()) ? 1 : data.size();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getContext()).inflate(R.layout.users, parent, false));
        }

        private boolean isEmpty() {
            return data == null || data.isEmpty();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            if (isEmpty()) {
                holder.fullname.setText("No user here!");
                holder.username.setText("Sorry!");
                holder.root.setOnClickListener(null);
                return;
            }
            final Usersdata user = data.get(position);
            holder.fullname.setText(user.getFname() + " " + user.getLname());
            holder.username.setText("@" + user.getFname() + " " + user.getLname());
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchClick(user);
                }
            });
        }
    }
}