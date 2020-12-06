package org.chat21.android.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vanniktech.emoji.EmojiEditText;


public class EmojiGifEditText extends EmojiEditText {
    public EmojiGifEditText(Context context) {
        super(context);
    }

    public EmojiGifEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        final InputConnection ic = super.onCreateInputConnection(editorInfo);
        EditorInfoCompat.setContentMimeTypes(editorInfo,
                new String[]{"image/*"});

        final InputConnectionCompat.OnCommitContentListener callback =
                new InputConnectionCompat.OnCommitContentListener() {
                    @Override
                    public boolean onCommitContent(InputContentInfoCompat inputContentInfo,
                                                   int flags, Bundle opts) {
                        // read and display inputContentInfo asynchronously
                        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) && (flags &
                                InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                            try {
                                inputContentInfo.requestPermission();
                            } catch (Exception e) {
                                return false; // return false if failed
                            }
                        }
                        //here you send the url of the gif/sticker/image using LocalBroadcastManager
                        Intent intent = new Intent("sendSticker");
                        Log.e("url..", inputContentInfo.getContentUri().getPath());
                        intent.putExtra("url", inputContentInfo.getContentUri().getPath());
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                        // call inputContentInfo.releasePermission() as needed.
                        inputContentInfo.releasePermission();
                        return true;  // return true if succeeded
                    }
                };
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
    }
}