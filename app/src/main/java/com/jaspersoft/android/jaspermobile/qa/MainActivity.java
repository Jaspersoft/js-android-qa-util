package com.jaspersoft.android.jaspermobile.qa;

import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private static final int REMOVE_COOKIES = 0;
    private static final int DEPRECATE_COOKIES = 1;
    private static final int REMOVE_ALL_ACCOUNTS = 2;
    private static final int DOWNGRADE_SERVER_VERSION = 3;
    private static final int CHANGE_SERVER_EDITION = 4;
    private static final int SHOW_DRIVE_ACTION = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(android.R.id.list);
        String[] commands = getResources().getStringArray(R.array.util_commands);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, commands);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final UtilEvent utilEvent = UtilEvent.get(this);
        switch (position) {
            case REMOVE_COOKIES:
                utilEvent.fireRemoveCookiesEvent();
                break;
            case DEPRECATE_COOKIES:
                utilEvent.fireDeprecatedCookiesEvent();
                break;
            case REMOVE_ALL_ACCOUNTS:
                utilEvent.fireRemoveAccountsEvent();
                break;
            case DOWNGRADE_SERVER_VERSION:
                utilEvent.fireDowngradeServer();
                break;
            case CHANGE_SERVER_EDITION:
                utilEvent.fireChangeServerEdition();
                break;
            case SHOW_DRIVE_ACTION:
                DriveSelectFileDialog.display(getSupportFragmentManager(), new DriveSelectFileDialog.OnDescriptorResultListener() {
                    @Override
                    public void onDescriptorResult(ParcelFileDescriptor descriptor) {
                        utilEvent.fireProfilePopulateEvent(new ProfilesFileHolder(descriptor));
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DriveSelectFileDialog.handleActivityResult(getSupportFragmentManager(), requestCode, resultCode, data);
    }
}

