package com.grace.book.fragment;


import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    private boolean mResumed;

    @Override
    public  void setUserVisibleHint(final boolean isVisibleToUser) {
        final boolean needUpdate = mResumed && isVisibleToUser != getUserVisibleHint();
        super.setUserVisibleHint(isVisibleToUser);
        if (needUpdate) {
            if (isVisibleToUser) {
                this.onVisible();
            } else {
                this.onInvisible();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mResumed = true;
        if (this.getUserVisibleHint()) {
            this.onVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mResumed = false;
        this.onInvisible();
    }

    protected abstract void onVisible();
    protected abstract void onInvisible();

}
