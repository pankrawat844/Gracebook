package com.grace.book.callbackinterface;

/**
 * Created by Mr. Wasir on 2/25/2018.
 */

public interface ServerResponse {
    void onSuccess(String statusCode, String serverResponse);
    void onFailed(String statusCode, String serverResponse);
}
