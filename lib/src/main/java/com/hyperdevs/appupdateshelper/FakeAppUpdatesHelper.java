/*
 * Copyright (C) 2021 HyperDevs
 *
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

package com.hyperdevs.appupdateshelper;

import android.content.Context;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A fake implementation of the {@link AppUpdatesHelper}, backed by a {@link FakeAppUpdateManager}.
 * <p>
 * This implementation is completely self-contained in the library and does not interact with the Play Store. For this reason no UI is
 * being shown and no update is really performed.
 * <p>
 * The developer can simulate the most common user actions, download progress and failure scenarios.
 * <p>
 * It is designed to be used for unit-tests and early development iterations. It is not meant for full stack integration tests:
 * the latter can be performed by uploading your app to Play and sideload an older version (modified so it can trigger an in-app update)
 * onto your test device.
 */
@SuppressWarnings("JavadocReference")
public class FakeAppUpdatesHelper extends AppUpdatesHelper {
    private final FakeAppUpdateManager fakeAppUpdateManager;

    public FakeAppUpdatesHelper(@NonNull Context context) {
        this(new FakeAppUpdateManager(context));
    }

    private FakeAppUpdatesHelper(@NonNull AppUpdateManager appUpdateManager) {
        super(appUpdateManager);
        fakeAppUpdateManager = (FakeAppUpdateManager) appUpdateManager;
    }

    /**
     * Declares that an update is available and provides its version code.
     *
     * @param availableVersionCode the available update's version code
     */
    public void setUpdateAvailable(int availableVersionCode) {
        fakeAppUpdateManager.setUpdateAvailable(availableVersionCode);
    }

    /**
     * Declares that no updates are available.
     */
    public void setUpdateNotAvailable() {
        fakeAppUpdateManager.setUpdateNotAvailable();
    }

    /**
     * Simulates user click on the positive button in the update confirmation dialog. The download is enqueued in
     * {@link AppUpdateInstallState.Status.PENDING PENDING} status.
     * <p>
     * Works only if {@link #isConfirmationDialogVisible()} or {@link #isImmediateFlowVisible()} is true.
     * <p>
     * In the real implementation a {@link android.app.Activity.RESULT_OK} would also be received by the calling activity's
     * {@link android.app.Activity#onActivityResult}.
     */
    public void userAcceptsUpdate() {
        // Since the fake updater does not trigger onActivityResult, we have to manually trigger the ACCEPTED callback.
        int requestCode = getRequestCode();

        fakeAppUpdateManager.userAcceptsUpdate();
        onUpdateStatusResult(requestCode, RESULT_OK);
    }

    /**
     * Returns which type of update is currently in progress or null if no update is in progress.
     */
    @Nullable
    public Integer getTypeForUpdateInProgress() {
        return fakeAppUpdateManager.getTypeForUpdateInProgress();
    }

    /**
     * Simulates user canceling the download via the Play UI.
     * <p>
     * Works only if the download of an update is pending or downloading.
     */
    public void userCancelsDownload() {
        fakeAppUpdateManager.userCancelsDownload();
    }

    /**
     * Simulates user click on the negative button in the currently active update flow.
     * <p>
     * Works only if {@link #isConfirmationDialogVisible()} or {@link #isImmediateFlowVisible()} is true.
     * <p>
     * In the real implementation a {@link android.app.Activity.RESULT_CANCELED} would also be received by the calling activity's
     * {@link android.app.Activity#onActivityResult}.
     */
    public void userRejectsUpdate() {
        // Since the fake updater does not trigger onActivityResult, we first have to check which
        // view is being shown (immediate or flexible) and then trigger the onUpdateStatusResult
        // callback manually
        int requestCode = getRequestCode();

        fakeAppUpdateManager.userRejectsUpdate();
        onUpdateStatusResult(requestCode, RESULT_CANCELED);
    }

    /**
     * Simulates the download completing.
     * <p>
     * Works only after {@link #userAcceptsUpdate()}.
     */
    public void downloadStarts() {
        fakeAppUpdateManager.downloadStarts();
    }

    /**
     * Simulates the download starting.
     * <p>
     * Works only after {@link #downloadStarts()}.
     */
    public void downloadCompletes() {
        fakeAppUpdateManager.downloadCompletes();
    }

    /**
     * Simulates a download failure.
     * <p>
     * Works only if the download of an update is pending or downloading.
     */
    public void downloadFails() {
        fakeAppUpdateManager.downloadFails();
    }

    /**
     * Simulates the download completing.
     * <p>
     * Works only after {@link #completeUpdate()} has been triggered by the application.
     */
    public void installCompletes() {
        fakeAppUpdateManager.installCompletes();
    }

    /**
     * Simulates an install failure.
     * <p>
     * Works only if the update is already installing (after a call to {@link #completeUpdate()}).
     */
    public void installFails() {
        fakeAppUpdateManager.installFails();
    }

    /**
     * Sets an error code which will be returned by the next API calls.
     * <p>
     * The error code is being honored by {@link #getAppUpdateInfo()} and {@link #completeUpdate()}; the error code is not being honored
     * if a more severe workflow error happens, e.g. if {@link #completeUpdate()} is called when the update is not downloaded yet.
     * <p>
     * The error code is persistent: to return to the default behaviour, you can reset the value by calling the same method with
     * {@link AppUpdateInstallState.ErrorCode.NO_ERROR}.
     *
     * @param installErrorCode the install error code
     */
    public void setInstallErrorCode(AppUpdateInstallState.ErrorCode installErrorCode) {
        fakeAppUpdateManager.setInstallErrorCode(installErrorCode.getValue());
    }

    /**
     * Returns whether the user confirmation screen of the immediate update is visible to the user.
     */
    public boolean isConfirmationDialogVisible() {
        return fakeAppUpdateManager.isConfirmationDialogVisible();
    }

    /**
     * Returns whether the user confirmation screen of the immediate update is visible to the user.
     */
    public boolean isImmediateFlowVisible() {
        return fakeAppUpdateManager.isImmediateFlowVisible();
    }

    /**
     * Returns whether the splash screen is visible to the user. This happens during update completion:
     * in the real implementation your app is reloaded with the new version.
     */
    public boolean isInstallSplashScreenVisible() {
        return fakeAppUpdateManager.isInstallSplashScreenVisible();
    }

    private int getRequestCode() {
        if (isImmediateFlowVisible()) {
            return IMMEDIATE_UPDATE_REQUEST_CODE;
        }
        if (isConfirmationDialogVisible()) {
            return FLEXIBLE_UPDATE_REQUEST_CODE;
        }
        return -1;
    }

}
