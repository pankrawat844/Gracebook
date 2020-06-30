package com.grace.book;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grace.book.adapter.GroupListAdapter;
import com.grace.book.adapter.GroupPostAdapter;
import com.grace.book.adapter.PrayRequestAdapter;
import com.grace.book.callbackinterface.FilterItemCallback;
import com.grace.book.callbackinterface.ServerResponse;
import com.grace.book.customview.VerticalSpaceItemDecoration;
import com.grace.book.model.FeedList;
import com.grace.book.model.GroupList;
import com.grace.book.model.Usersdata;
import com.grace.book.myapplication.Myapplication;
import com.grace.book.networkcalls.ServerCallsProvider;
import com.grace.book.utils.AllUrls;
import com.grace.book.utils.BusyDialog;
import com.grace.book.utils.Helpers;
import com.grace.book.utils.Logger;
import com.grace.book.utils.PersistentUser;
import com.grace.book.utils.ToastHelper;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.grace.book.myapplication.Myapplication.getContext;

public class GroupdetailsActivity extends BaseActivity {
    private static final String TAG = UserprofileActivity.class.getSimpleName();
    private final int VERTICAL_ITEM_SPACE = 20;
    private RecyclerView recycler_feed;
    private GroupPostAdapter mGroupListAdapter;
    private Context mContext;
    private ImageView moreInformation;
    private GroupList mGroupList;
    private TextView textHeader;
    private BusyDialog mBusyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_post);
        Bundle extra = getIntent().getBundleExtra("extra");
        mGroupList = (GroupList) extra.getSerializable("objects");
        mContext = this;
        initUI();
    }

    private void initUI() {
        findViewById(R.id.layoutBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textHeader = (TextView) this.findViewById(R.id.textHeader);
        moreInformation = (ImageView) this.findViewById(R.id.moreInformation);
        recycler_feed = (RecyclerView) this.findViewById(R.id.recycler_feed);
        mGroupListAdapter = new GroupPostAdapter(mContext, new ArrayList<FeedList>());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        recycler_feed.setLayoutManager(mLinearLayoutManager);
        recycler_feed.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        SmartRecyclerAdapter smartRecyclerAdapter = new SmartRecyclerAdapter(mGroupListAdapter);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.header_prayer_screen, null);

        recycler_feed.setAdapter(smartRecyclerAdapter);
        mGroupListAdapter.addnewItem(new FeedList());
        mGroupListAdapter.addnewItem(new FeedList());
        mGroupListAdapter.addnewItem(new FeedList());

        smartRecyclerAdapter.setHeaderView(view);

        EditText edittextChat = (EditText) view.findViewById(R.id.edittextChat);
        edittextChat.setFocusable(false);
        edittextChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(GroupdetailsActivity.this, PostallActivity.class);
                mIntent.putExtra("screenType", 1);
                startActivity(mIntent);
            }
        });
        moreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu();
            }
        });
        textHeader.setText(mGroupList.getGroup_name());

    }

    public FilterItemCallback lFilterItemCallback = new FilterItemCallback() {
        @Override
        public void ClickFilterItemCallback(int type, int position) {
            if (type == 0) {
                Intent mm = new Intent(GroupdetailsActivity.this, UserprofileActivity.class);
                startActivity(mm);
            } else if (type == 1) {
                Intent mm = new Intent(GroupdetailsActivity.this, CommentActivity.class);
                startActivity(mm);
            }

        }
    };

    public void popupMenu() {
        PopupMenu popup = new PopupMenu(GroupdetailsActivity.this, moreInformation);
        popup.getMenuInflater().inflate(R.menu.menu_doctors, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_booknow) {
                    alertfornewuser(0);
                    return true;
                } else if (id == R.id.action_chat) {
                    alertfornewuser(1);
                    return true;
                } else if (id == R.id.action_addfriend) {
                    Intent mIntent = new Intent(GroupdetailsActivity.this, AddGroupFriendActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("objects", mGroupList);
                    mIntent.putExtra("extra", extra);
                    startActivityForResult(mIntent, 1010);
                    return true;
                }

                return true;
            }
        });
        Menu popupMenu = popup.getMenu();
        if (!mGroupList.getGroup_owner().equalsIgnoreCase(PersistentUser.getUserID(mContext)))
            popupMenu.findItem(R.id.action_booknow).setEnabled(false);


        popup.show();
    }

    private void leaverServerRequest(int position) {
        if (!Helpers.isNetworkAvailable(mContext)) {
            Helpers.showOkayDialog(mContext, R.string.no_internet_connection);
            return;
        }
        String url = AllUrls.BASEURL + "groupAdd";
        if (position == 0) {
            url = AllUrls.BASEURL + "groupdelete";
        } else if (position == 1) {
            url = AllUrls.BASEURL + "groupmemebrremove";

        }

        HashMap<String, String> allHashMap = new HashMap<>();
        allHashMap.put("group_id", mGroupList.getGroup_id());

        HashMap<String, String> allHashMapHeader = new HashMap<>();
        allHashMapHeader.put("appKey", AllUrls.APP_KEY);
        allHashMapHeader.put("authToken", PersistentUser.getUserToken(mContext));
        mBusyDialog = new BusyDialog(mContext);
        mBusyDialog.show();

        ServerCallsProvider.volleyPostRequest(url, allHashMap, allHashMapHeader, TAG, new ServerResponse() {
            @Override
            public void onSuccess(String statusCode, String responseServer) {
                mBusyDialog.dismis();
                try {
                    Logger.debugLog("responseServer", responseServer);
                    JSONObject mJsonObject = new JSONObject(responseServer);
                    if (mJsonObject.getBoolean("success")) {
                        Intent mIntent = new Intent();
                        setResult(RESULT_OK, mIntent);
                        finish();

                    } else {
                        String message = mJsonObject.getString("message");
                        ToastHelper.showToast(mContext, message);

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(String statusCode, String serverResponse) {
                mBusyDialog.dismis();
                if (statusCode.equalsIgnoreCase("404")) {
                    PersistentUser.resetAllData(mContext);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    AlertDialog alertDialog = null;

    public void alertfornewuser(final int postion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupdetailsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_app_logout, null);
        builder.setView(mView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        TextView no_botton = (TextView) mView.findViewById(R.id.no_botton);
        TextView yes_btn = (TextView) mView.findViewById(R.id.yes_btn);
        TextView version_text = (TextView) mView.findViewById(R.id.version_text);
        TextView version_text2 = (TextView) mView.findViewById(R.id.version_text2);
        if (postion == 0) {
            version_text.setText("Delete Group");
            version_text2.setText("Are you sure you want delete group");

        } else if (postion == 1) {
            version_text.setText("Leave  Group");
            version_text2.setText("Are you sure you want leave group");
        }


        no_botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                leaverServerRequest(postion);

            }
        });
    }
}
