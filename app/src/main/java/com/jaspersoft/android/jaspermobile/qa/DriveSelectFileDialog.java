/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.qa;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;

/**
 * @author Tom Koptel
 * @since 2.1
 */
public class DriveSelectFileDialog extends DialogFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = DriveSelectFileDialog.class.getSimpleName();
    private static final String LOG_TAG = DriveSelectFileDialog.class.getSimpleName();
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 3;
    private static final int REQUEST_CODE_OPENER = 4;
    private GoogleApiClient mGoogleApiClient;
    private Toast mToast;

    private OnDescriptorResultListener mOnDescriptorResultListener = new OnDescriptorResultListener() {
        @Override
        public void onDescriptorResult(ParcelFileDescriptor descriptor) {
        }
    };
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Opening drive");
        return progressDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        showMessage("Google API client connected");
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setSelectionFilter(Filters.contains(SearchableField.TITLE, ".json"))
                .build(getGoogleApiClient());
        try {
            getActivity().startIntentSenderForResult(
                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.w(LOG_TAG, "Unable to send intent", e);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        showMessage("GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                showMessage("Exception while starting resolution activity");
                Log.e(LOG_TAG, "Exception while starting resolution activity", e);
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), getActivity(), 0).show();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
            case REQUEST_CODE_OPENER:
                if (resultCode == Activity.RESULT_OK) {
                    DriveId driveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    progressDialog.setMessage("Opening selected file");
                    openSelectedFile(driveId);
                } else {
                    dismiss();
                }
                break;
        }
    }

    private void openSelectedFile(DriveId driveId) {
        DriveFile driveFile = Drive.DriveApi.getFile(mGoogleApiClient, driveId);
        PendingResult<DriveApi.DriveContentsResult> pendingResult =
                driveFile.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, new EmptyDownloadProgressListener());
        pendingResult.setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult result) {
                if (!result.getStatus().isSuccess()) {
                    showMessage("Can not open following file");
                    dismiss();
                    return;
                }

                DriveContents contents = result.getDriveContents();
                ParcelFileDescriptor descriptor = contents.getParcelFileDescriptor();
                mOnDescriptorResultListener.onDescriptorResult(descriptor);
                dismiss();
            }
        });
    }

    private void showMessage(String message) {
        mToast.setText(message);
        mToast.show();
    }

    private GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    //---------------------------------------------------------------------
    // Custom API
    //---------------------------------------------------------------------

    public void setOnDescriptorResultListener(OnDescriptorResultListener onDescriptorResultListener) {
        mOnDescriptorResultListener = onDescriptorResultListener;
    }

    public static void display(FragmentManager fm, OnDescriptorResultListener descriptorResultListener) {
        DriveSelectFileDialog dialog = new DriveSelectFileDialog();
        dialog.setOnDescriptorResultListener(descriptorResultListener);
        dialog.show(fm, TAG);
    }

    public static void handleActivityResult(FragmentManager fm, int requestCode, int resultCode, Intent data) {
        DriveSelectFileDialog dialog = (DriveSelectFileDialog) fm.findFragmentByTag(TAG);
        if (dialog != null) {
            dialog.onActivityResult(requestCode, resultCode, data);
        }
    }

    //---------------------------------------------------------------------
    // Inner classes
    //---------------------------------------------------------------------

    public interface OnDescriptorResultListener {
        void onDescriptorResult(ParcelFileDescriptor descriptor);
    }

    private static class EmptyDownloadProgressListener implements DriveFile.DownloadProgressListener {
        @Override
        public void onProgress(long bytesDownloaded, long bytesExpected) {
        }
    }
}
