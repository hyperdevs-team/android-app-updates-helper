/*
 * Copyright (C) 2019 BQ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bq.appupdateshelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

import java.io.IOException;

import androidx.annotation.NonNull;

import static android.app.Activity.RESULT_CANCELED;
import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;

/**
 * Helper class used to simplify the use of the In-App Updates library from Google.
 * <p>
 * Its use is as follows:
 * - Register it using {@link #startListening(InstallStateListener)}, for example in {@link Activity#onCreate(Bundle)}.
 * - Unregister it using {@link #stopListening()}, for example in {@link Activity#onDestroy()}.
 * - Call {@link #onUpdateStatusResult(int, int)} in your Activity's
 * {@link Activity#onActivityResult(int, int, Intent)} so the helper can properly report status
 * updates.
 * - When you want to check if there are any updates, call {@link #getAppUpdateInfo(GetUpdateInfoListener)}.
 * - When you want to perform an update, call {@link #startImmediateUpdate(Activity)}
 * or {@link #startFlexibleUpdate(Activity)} after successfully receiving an update via
 * {@link #getAppUpdateInfo(GetUpdateInfoListener)}.
 */
@SuppressWarnings("JavadocReference")
public class AppUpdatesHelper {
    static final int IMMEDIATE_UPDATE_REQUEST_CODE = 13371;
    static final int FLEXIBLE_UPDATE_REQUEST_CODE = 13372;

    private static final String TAG = "AppUpdatesHelper";

    private final AppUpdateManager manager;
    private InstallStateListener installStateListener;
    private boolean isListening = false;
    private AppUpdateInfo appUpdateInfo = null;
    private InstallStateUpdatedListener installStateUpdatedListener;

    /**
     * Creates a helper instance with a given context.
     *
     * @param context Context to use to build the helper.
     */
    public AppUpdatesHelper(@NonNull Context context) {
        this(AppUpdateManagerFactory.create(context));
    }

    /**
     * Creates a helper instance with a given app update manager.
     *
     * @param appUpdateManager app update manager to use.
     */
    AppUpdatesHelper(@NonNull AppUpdateManager appUpdateManager) {
        this.manager = appUpdateManager;
    }

    /**
     * Starts listening for app updates and install changes.
     */
    public void startListening(@NonNull final InstallStateListener installStateListener) {
        if (!isListening) {
            this.isListening = true;
            this.installStateListener = installStateListener;
            this.installStateUpdatedListener = new InstallStateUpdatedListener() {
                @Override public void onStateUpdate(InstallState installState) {
                    AppUpdateInstallState state = new AppUpdateInstallState(installState);
                    Log.d(TAG, "Update status result: " + state.toString());

                    installStateListener.onInstallStateUpdate(state);
                }
            };
            this.manager.registerListener(installStateUpdatedListener);
        }
    }

    /**
     * Stops listening for app updates and install changes.
     */
    public void stopListening() {
        isListening = false;
        manager.unregisterListener(installStateUpdatedListener);
    }

    /**
     * Starts an app update check.
     * <p>
     * The method must only be called after calling {@link #startListening(InstallStateListener)}.
     *
     * @param getUpdateInfoListener Callback that will emit an {@link AppUpdateInfoResult} object after finishing
     *                              querying for possible updates.
     */
    public void getAppUpdateInfo(@NonNull final GetUpdateInfoListener getUpdateInfoListener) throws IllegalStateException {
        if (!isListening)
            throw new IllegalStateException("You must call startListening() before requesting update info");

        final Task<AppUpdateInfo> appUpdateInfoTask = manager.getAppUpdateInfo();

        appUpdateInfoTask.addOnCompleteListener(new OnCompleteListener<AppUpdateInfo>() {
            @Override public void onComplete(Task<AppUpdateInfo> task) {
                Exception exception = null;
                if (task.isSuccessful()) {
                    appUpdateInfo = task.getResult();
                } else {
                    exception = task.getException();
                }

                AppUpdateInfoResult result = new AppUpdateInfoResult(appUpdateInfo, exception);
                Log.d(TAG, "Update info: " + result.toString());

                getUpdateInfoListener.onGetUpdateInfoComplete(result);
            }
        });
    }

    /**
     * Starts an immediate update.
     * It will receive callbacks in {@link InstallStateListener}.
     * <p>
     * The method must only be called after calling {@link #startListening(InstallStateListener)}.
     *
     * @param activity The {@link Activity} to link to the update.
     */
    public void startImmediateUpdate(@NonNull Activity activity) throws IllegalStateException {
        if (!isListening)
            throw new IllegalStateException("You must call startListening() " +
                "before requesting an immediate update");
        if (appUpdateInfo == null)
            throw new IllegalStateException("You must call getAppUpdateInfo() " +
                "with a successful response before requesting an immediate update");

        try {
            manager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,
                activity,
                IMMEDIATE_UPDATE_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a flexible update.
     * It will receive callbacks in {@link InstallStateListener}.
     * <p>
     * The method must only be called after calling {@link #startListening(InstallStateListener)}
     * and {@link #getAppUpdateInfo(GetUpdateInfoListener)}.
     *
     * @param activity The {@link Activity} to link to the update.
     */
    public void startFlexibleUpdate(@NonNull Activity activity) throws IllegalStateException {
        if (!isListening)
            throw new IllegalStateException("You must call startListening() " +
                "before requesting a flexible update");
        if (appUpdateInfo == null)
            throw new IllegalStateException("You must call getAppUpdateInfo() " +
                "with a successful response before requesting a flexible update");

        try {
            manager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                activity,
                FLEXIBLE_UPDATE_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called to process {@link Activity#onActivityResult(int, int, Intent)} results for the
     * in-app updates installStateListener.
     *
     * @param requestCode {@link Activity#onActivityResult(int, int, Intent)}'s request code
     * @param resultCode  {@link Activity#onActivityResult(int, int, Intent)}'s result code
     */
    public void onUpdateStatusResult(int requestCode, int resultCode) {
        if (requestCode == IMMEDIATE_UPDATE_REQUEST_CODE || requestCode == FLEXIBLE_UPDATE_REQUEST_CODE) {
            AppUpdateInstallState state;
            switch (resultCode) {
                case RESULT_CANCELED:
                    if (requestCode == IMMEDIATE_UPDATE_REQUEST_CODE)
                        state = new AppUpdateInstallState(
                            AppUpdateInstallState.Status.DENIED,
                            AppUpdateInstallState.ErrorCode.ERROR_INSTALL_NOT_ALLOWED
                        );
                    else
                        state = new AppUpdateInstallState(
                            AppUpdateInstallState.Status.CANCELED,
                            AppUpdateInstallState.ErrorCode.ERROR_INSTALL_NOT_ALLOWED);
                    break;
                case RESULT_IN_APP_UPDATE_FAILED:
                    // We don't know why the update failed, so return an unknown error
                    state = new AppUpdateInstallState(
                        AppUpdateInstallState.Status.FAILED,
                        AppUpdateInstallState.ErrorCode.ERROR_UNKNOWN
                    );
                    break;
                default:
                    // If everything goes well, check which updates are allowed and use the proper (no) error code
                    boolean areAllUpdateFlowsAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE);

                    state = new AppUpdateInstallState(
                        AppUpdateInstallState.Status.UPDATE_ACCEPTED,
                        areAllUpdateFlowsAllowed
                            ? AppUpdateInstallState.ErrorCode.NO_ERROR
                            : AppUpdateInstallState.ErrorCode.NO_ERROR_PARTIALLY_ALLOWED
                    );
                    break;
            }

            Log.d(TAG, "Update status result: " + state.toString());

            if (installStateListener != null) {
                installStateListener.onInstallStateUpdate(state);
            }
        }
    }

    /**
     * Completes an unfinished installation (for example, when finishing downloading a flexible
     * installation).
     * <p>
     * The method must only be called after calling {@link #startListening(InstallStateListener)}
     * and {@link #getAppUpdateInfo(GetUpdateInfoListener)}.
     */
    public void completeUpdate() throws IllegalStateException {
        if (!isListening)
            throw new IllegalStateException("You must call startListening() " +
                "before completing an update");
        if (appUpdateInfo == null)
            throw new IllegalStateException("You must call getAppUpdateInfo() " +
                "before completing an update");

        manager.completeUpdate();
    }
}
