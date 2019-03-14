package com.nonexistenware.igor.pocketmemo.callback;

import android.view.View;

public interface MemoEventListener {
    void onSingleClick(View view, int position);
    void onLongClick(View view, int position);


}
