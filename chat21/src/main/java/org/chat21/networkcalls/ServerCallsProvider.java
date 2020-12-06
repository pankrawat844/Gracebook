package org.chat21.networkcalls;


import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.chat21.android.Myapplication;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ServerCallsProvider {
    private static final String TAG = ServerCallsProvider.class.getSimpleName();

    public static void volleyDeleteRequest(@NonNull String url, @NonNull final Map<String, String> headerParams, String TAG, @NonNull final ServerResponse serverResponse) {
        StringRequest getRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String body = response.toString();
                String statusCode = "200";
                serverResponse.onSuccess(statusCode, body);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body = "";
                String statusCode = "-1";
                try {
                    if (error.networkResponse.data != null) {
                        statusCode = String.valueOf(error.networkResponse.statusCode);
                        body = new String(error.networkResponse.data, "UTF-8");
                        serverResponse.onFailed(statusCode, body);

                    } else {
                        JSONObject mObject = new JSONObject();
                        mObject.put("msg", "Network issue.Please checked your mobile data.");
                        serverResponse.onFailed(statusCode, mObject.toString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerParams;
            }
        };

        int socketTimeout = 50000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getRequest.setRetryPolicy(policy);
        Myapplication.getInstance().addToRequestQueue(getRequest, TAG);
    }

    public static void volleyGetRequest(@NonNull String url, String TAG, @NonNull final ServerResponse serverResponse) {
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String body = response.toString();
                String statusCode = "200";
                serverResponse.onSuccess(statusCode, body);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body = "";
                String statusCode = "-1";
                try {
                    Logger.debugLog("error", "" + error.networkResponse);
                    Logger.debugLog("error", "" + error.getMessage());

                    if (error.networkResponse.data != null) {
                        statusCode = String.valueOf(error.networkResponse.statusCode);
                        body = new String(error.networkResponse.data, "UTF-8");
                        serverResponse.onFailed(statusCode, body);

                    } else {
                        JSONObject mObject = new JSONObject();
                        mObject.put("msg", "Network issue.Please checked your mobile data.");
                        serverResponse.onFailed(statusCode, mObject.toString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    serverResponse.onFailed(statusCode, e.getMessage());

                }
            }
        });

        int socketTimeout = 50000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getRequest.setRetryPolicy(policy);
        Myapplication.getInstance().addToRequestQueue(getRequest, TAG);
    }

    public static void volleyPostRequest(Context ctx, @NonNull String url, @NonNull final Map<String, String> params, @NonNull final Map<String, String> headerParams, final String TAG, @NonNull final org.chat21.networkcalls.ServerResponse serverResponse) {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String body = response.toString();
                        String statusCode = "200";
                        serverResponse.onSuccess(statusCode, body);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String body = "";
                String statusCode = "-1";
                Logger.warnLog("VolleyError", "message #### " + error.getMessage());
                try {
                    if (error.networkResponse.data != null) {
                        statusCode = String.valueOf(error.networkResponse.statusCode);
                        body = new String(error.networkResponse.data, "UTF-8");
                        serverResponse.onFailed(statusCode, body);

                    } else {
                        JSONObject mObject = new JSONObject();
                        mObject.put("msg", "Network issue.Please checked your mobile data.");
                        serverResponse.onFailed(statusCode, mObject.toString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerParams;
            }
        };

        Logger.debugLog(TAG, "volleyPostRequest  " + postRequest.getUrl());
        int socketTimeout = 50000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(ctx).add(postRequest);
    }

    public static void volleyPostRequestJSONOBJECT(@NonNull String url, JSONObject jSONObject, @NonNull final Map<String, String> headerParams, final String TAG, @NonNull final ServerResponse serverResponse) {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                url, jSONObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String body = response.toString();
                        String statusCode = "200";
                        serverResponse.onSuccess(statusCode, body);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String body = "";
                String statusCode = "-1";
                Logger.warnLog("VolleyError", "message #### " + error.getMessage());
                try {
                    if (error.networkResponse.data != null) {
                        statusCode = String.valueOf(error.networkResponse.statusCode);
                        body = new String(error.networkResponse.data, "UTF-8");
                        serverResponse.onFailed(statusCode, body);

                    } else {
                        JSONObject mObject = new JSONObject();
                        mObject.put("msg", "Network issue.Please checked your mobile data.");
                        serverResponse.onFailed(statusCode, mObject.toString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerParams;
            }
        };

        Logger.debugLog(TAG, "volleyPostRequest  " + postRequest.getUrl());
        int socketTimeout = 50000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        Myapplication.getInstance().addToRequestQueue(postRequest, TAG);
    }

    public static void volleyPutRequest(@NonNull String url, @NonNull final Map<String, String> params, @NonNull final Map<String, String> headerParams, final String TAG, @NonNull final ServerResponse serverResponse) {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PUT,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String body = response.toString();
                        String statusCode = "200";
                        serverResponse.onSuccess(statusCode, body);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String body = "";
                String statusCode = "-1";
                Logger.warnLog("VolleyError", "message #### " + error.getMessage());
                try {
                    if (error.networkResponse.data != null) {
                        statusCode = String.valueOf(error.networkResponse.statusCode);
                        body = new String(error.networkResponse.data, "UTF-8");
                        serverResponse.onFailed(statusCode, body);

                    } else {
                        JSONObject mObject = new JSONObject();
                        mObject.put("msg", "Network issue.Please checked your mobile data.");
                        serverResponse.onFailed(statusCode, mObject.toString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerParams;
            }
        };

        Logger.debugLog(TAG, "volleyPostRequest  " + postRequest.getUrl());
        int socketTimeout = 50000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        Myapplication.getInstance().addToRequestQueue(postRequest, TAG);
    }

    public static void volleyPostRequestJsonBodyContentType(@NonNull String url, @NonNull JSONObject parameters, @NonNull final Map<String, String> headerParams, final String BodyContentType, @NonNull final ServerResponse serverResponse) {

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String body = response.toString();
                String statusCode = "200";
                serverResponse.onSuccess(statusCode, body);
                Logger.debugLog("response", response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body = "";
                String statusCode = String.valueOf(error.networkResponse.statusCode);
                if (error.networkResponse.data != null) {
                    try {
                        body = new String(error.networkResponse.data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                Logger.warnLog("VolleyError", "statusCode #### " + statusCode);
                Logger.warnLog("VolleyError", "message #### " + body);
                serverResponse.onFailed(statusCode, body);


            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerParams;
            }

            @Override
            public String getBodyContentType() {
                return BodyContentType;
            }
        };

        Logger.debugLog(TAG, "volleyPostRequest  " + postRequest.getUrl());
        int socketTimeout = 50000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        Myapplication.getInstance().addToRequestQueue(postRequest, TAG);
    }

    public static void VolleyMultipartRequest(@NonNull String url, @NonNull final Map<String, String> params, @NonNull final Map<String, String> headerParams, final Map<String, VolleyMultipartRequest.DataPart> ByteData, @NonNull final ServerResponse serverResponse) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                String statusCode = "200";
                serverResponse.onSuccess(statusCode, resultResponse);
                Logger.debugLog(TAG, "response  " + resultResponse);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body = "";
                String statusCode = "-1";
                try {
                    if (error.networkResponse.data != null) {
                        statusCode = String.valueOf(error.networkResponse.statusCode);
                        body = new String(error.networkResponse.data, "UTF-8");
                        serverResponse.onFailed(statusCode, body);

                    } else {
                        JSONObject mObject = new JSONObject();
                        mObject.put("msg", "Network issue.Please checked your mobile data.");
                        serverResponse.onFailed(statusCode, mObject.toString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                return ByteData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerParams;
            }
        };

        int socketTimeout = 50000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        multipartRequest.setRetryPolicy(policy);
        Myapplication.getInstance().addToRequestQueue(multipartRequest, TAG);
    }

}
