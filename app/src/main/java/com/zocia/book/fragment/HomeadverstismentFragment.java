package com.zocia.book.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.zocia.book.R;
import com.zocia.book.adapter.HomeadvertismentAdapter;
import com.zocia.book.utils.ConstantFunctions;


public final class HomeadverstismentFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";

    public static HomeadverstismentFragment newInstance(int content) {
        HomeadverstismentFragment fragment = new HomeadverstismentFragment();
        fragment.mContent = content;
        return fragment;
    }

    private int mContent = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getInt(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.row_slidingimage,
                container, false);
        ImageView sliding_image_farv = (ImageView) layout.findViewById(R.id.sliding_image_farv);
        String path = HomeadvertismentAdapter.advertisementListArrayList.get(mContent).getImage();
        ConstantFunctions.loadImageMatch(path, sliding_image_farv);


        sliding_image_farv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = HomeadvertismentAdapter.advertisementListArrayList.get(mContent).getWeb_link();
                if (path != null && (path.contains("https") || path.contains("http"))) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(path));
                    getActivity().startActivity(i);
                }
            }
        });

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CONTENT, mContent);
    }
}
