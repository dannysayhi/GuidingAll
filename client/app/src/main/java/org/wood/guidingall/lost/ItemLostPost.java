package org.wood.guidingall.lost;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.wood.guidingall.MainActivity;
import org.wood.guidingall.R;
import org.wood.guidingall.tools.Config;
import org.wood.guidingall.tools.User;

/**
 * Created by Danny on 2014/10/21.
 */
public class ItemLostPost extends Activity implements View.OnClickListener{
    private EditText itemNameTxt;
    private EditText itemLostDateTxt;
    private EditText itemLocation;
    private EditText itemPhoneTxt;
    private EditText itemRewardTxt;
    private EditText itemInfoTxt;
    private ImageView itemPhotoView;
    private ImageView itemAcceptView;
    private ImageView itemCancelView;

    private String exampleString = "";
    private ProjectData projectData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("協尋項目資訊");
        setContentView(R.layout.itemlostpost);

        findView();
        function();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null)
                exampleString = null;
            else {
                exampleString = extras.getString("NAME");
                projectData = new ProjectData(exampleString);
                itemNameTxt.setText(projectData.getName());
                itemLocation.setText(projectData.getLostAddr());
                itemLostDateTxt.setText(projectData.getLostDate());
                itemInfoTxt.setText(projectData.getInfo());
                itemPhoneTxt.setText(projectData.getPhone());
                itemRewardTxt.setText(String.valueOf(projectData.getReward()));
            }
        } else {
            exampleString = (String) savedInstanceState.getSerializable("NAME");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_itemAccept:
                if (exampleString == null ) {
                    projectData = new ProjectData();
                    projectData.setName(itemNameTxt.getText().toString());
                    projectData.setLostAddr(itemLocation.getText().toString());
                    projectData.setLostDate(itemLostDateTxt.getText().toString());
                    projectData.setInfo(itemInfoTxt.getText().toString());
                    projectData.setPhone(itemPhoneTxt.getText().toString());
                    projectData.setReward(Integer.valueOf(itemRewardTxt.getText().toString()));
                }

                LostItemPoster lostPoster = new LostItemPoster(projectData);
                lostPoster.sendEmptyMessage(0);

                Toast.makeText(ItemLostPost.this, "成功上傳", Toast.LENGTH_SHORT).show();
                finish();

                break;

            case R.id.iv_itemCancel:
                if (projectData.getName().equals(Config.BT5_ITEM))
                    MainActivity.isBt5PostToFind = false;
                if (projectData.getName().equals(Config.BT6_ITEM))
                    MainActivity.isBt6PostToFind = false;
                break;
        }
    }

    private void findView() {
        itemNameTxt     = (EditText) findViewById(R.id.et_itemName);
        itemLostDateTxt = (EditText) findViewById(R.id.et_itemLostDate);
        itemLocation    = (EditText) findViewById(R.id.et_itemLocation);
        itemPhoneTxt    = (EditText) findViewById(R.id.et_itemPhoneText);
        itemRewardTxt   = (EditText) findViewById(R.id.et_itemReward);
        itemInfoTxt     = (EditText) findViewById(R.id.et_itemInfo);
        itemPhotoView   = (ImageView)findViewById(R.id.iv_itemPhoto);
        itemAcceptView  = (ImageView)findViewById(R.id.iv_itemAccept);
        itemCancelView  = (ImageView)findViewById(R.id.iv_itemCancel);
    }

    private void function() {
        itemAcceptView.setOnClickListener(this);
        itemCancelView.setOnClickListener(this);
    }

    private static class LostItemPoster extends Handler {
        ProjectData projectData;

        LostItemPoster(ProjectData projectData) {
            this.projectData = projectData;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    User.getInstance().send(projectData.toString());
                    if (projectData.getName().equals(Config.BT5_ITEM))
                        MainActivity.isBt5PostToFind = true;
                    if (projectData.getName().equals(Config.BT6_ITEM))
                        MainActivity.isBt6PostToFind = true;
                    break;
            }
        }
    }
}
