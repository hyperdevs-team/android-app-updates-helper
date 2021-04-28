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

import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.model.InstallErrorCode;
import com.google.android.play.core.install.model.InstallStatus;

import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * Class that contains information about the app update installation state.
 */
public class AppUpdateInstallState {
    static final long BYTES_UNKNOWN = 0;
    static final long PROGRESS_UNKNOWN = 0;

    @NonNull
    private final Status status;
    @NonNull
    private final ErrorCode errorCode;
    private final long bytesDownloaded;
    private final long totalBytesToDownload;
    private final float downloadProgress;

    AppUpdateInstallState(@NonNull Status status,
                          @NonNull ErrorCode errorCode,
                          long bytesDownloaded,
                          long totalBytesToDownload) {
        this.status = status;
        this.errorCode = errorCode;
        this.bytesDownloaded = bytesDownloaded;
        this.totalBytesToDownload = totalBytesToDownload;
        if (status == Status.DOWNLOADED) {
            this.downloadProgress = 100f;
        } else if (totalBytesToDownload <= BYTES_UNKNOWN) {
            this.downloadProgress = PROGRESS_UNKNOWN;
        } else {
            this.downloadProgress = bytesDownloaded * 100f / totalBytesToDownload;
        }
    }

    AppUpdateInstallState(@NonNull InstallState state) {
        this(Status.from(state),
                ErrorCode.from(state),
                state.bytesDownloaded(),
                state.totalBytesToDownload());
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @NonNull
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public long getBytesDownloaded() {
        return bytesDownloaded;
    }

    public long getTotalBytesToDownload() {
        return totalBytesToDownload;
    }

    public float getDownloadProgress() {
        return downloadProgress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUpdateInstallState that = (AppUpdateInstallState) o;
        return bytesDownloaded == that.bytesDownloaded &&
                totalBytesToDownload == that.totalBytesToDownload &&
                Float.compare(that.downloadProgress, downloadProgress) == 0 &&
                status == that.status &&
                errorCode == that.errorCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, errorCode, bytesDownloaded, totalBytesToDownload, downloadProgress);
    }

    @Override
    public String toString() {
        return "AppUpdateInstallState{" +
                "status=" + status +
                ", errorCode=" + errorCode +
                ", bytesDownloaded=" + bytesDownloaded +
                ", totalBytesToDownload=" + totalBytesToDownload +
                ", downloadProgress=" + downloadProgress +
                '}';
    }

    /**
     * Specific state of the app update installation.
     * <p>
     * IMPORTANT: there's no SUCCESS state because the app is reloaded when the install process
     * finishes, so there's no need to emit that specific state.
     * <p>
     * The enums represent the following states:
     * <p>
     * - UNKNOWN: the update state is not known
     * - DENIED: the update has been denied by the user. It will only happen in immediate installs.
     * - REQUIRES_UI_INTENT: needs an UI Intent
     * - UPDATE_ACCEPTED: the user pressed the "update" button in the in-app update prompt.
     * - PENDING: the update is pending
     * - DOWNLOADING: the update is currently downloading
     * - DOWNLOADED: the update is downloaded, the user can be prompted to install it in this case
     * - INSTALLING: the update is currently being installed
     * - INSTALLED: the update has been installed
     * - FAILED: the update failed
     * - CANCELED: the update has been canceled, but it can be resumed in another moment.
     */
    public enum Status {
        UNKNOWN,
        DENIED,
        REQUIRES_UI_INTENT,
        UPDATE_ACCEPTED,
        PENDING,
        DOWNLOADING,
        DOWNLOADED,
        INSTALLING,
        INSTALLED,
        FAILED,
        CANCELED;

        static Status from(@NonNull InstallState state) {
            switch (state.installStatus()) {
                case InstallStatus.REQUIRES_UI_INTENT:
                    return Status.REQUIRES_UI_INTENT;
                case InstallStatus.PENDING:
                    return Status.PENDING;
                case InstallStatus.DOWNLOADING:
                    return Status.DOWNLOADING;
                case InstallStatus.DOWNLOADED:
                    return Status.DOWNLOADED;
                case InstallStatus.INSTALLING:
                    return Status.INSTALLING;
                case InstallStatus.INSTALLED:
                    return Status.INSTALLED;
                case InstallStatus.FAILED:
                    return Status.FAILED;
                case InstallStatus.CANCELED:
                    return Status.CANCELED;
                case InstallStatus.UNKNOWN:
                default:
                    return Status.UNKNOWN;
            }
        }
    }

    /**
     * Specific enum related to an app install error code. It will always be present in {@link AppUpdateInstallState}
     * (even in non-error cases, then the value of this field will be NO_ERROR)
     * <p>
     * The enums represent the following states:
     * <p>
     * - NO_ERROR: No error occurred; all types of update flow are allowed.
     * - ERROR_UNKNOWN: An unknown error occurred.
     * - ERROR_API_NOT_AVAILABLE: The API is not available on this device (such as when the device is not supported).
     * - ERROR_INVALID_REQUEST: The request that was sent by the app is malformed.
     * - ERROR_INSTALL_UNAVAILABLE: The install is unavailable to this user or device.
     * - ERROR_INSTALL_NOT_ALLOWED: The download/install is not allowed, due to the current device state (e.g. low battery, low disk space…)
     * - ERROR_DOWNLOAD_NOT_PRESENT: The install/update has not been (fully) downloaded yet.
     * - ERROR_APP_NOT_OWNED: The user hasn't acquired the app via Play
     * - ERROR_PLAY_STORE_NOT_FOUND: The Play Store app is either not installed or not the official version.
     * - ERROR_INTERNAL_ERROR: An internal error happened in the Play Store.
     */
    public enum ErrorCode {
        NO_ERROR,
        ERROR_UNKNOWN,
        ERROR_API_NOT_AVAILABLE,
        ERROR_INVALID_REQUEST,
        ERROR_INSTALL_UNAVAILABLE,
        ERROR_INSTALL_NOT_ALLOWED,
        ERROR_DOWNLOAD_NOT_PRESENT,
        ERROR_APP_NOT_OWNED,
        ERROR_PLAY_STORE_NOT_FOUND,
        ERROR_INTERNAL_ERROR;

        static ErrorCode from(InstallState state) {
            switch (state.installErrorCode()) {
                case InstallErrorCode.NO_ERROR:
                    return ErrorCode.NO_ERROR;
                case InstallErrorCode.ERROR_API_NOT_AVAILABLE:
                    return ErrorCode.ERROR_API_NOT_AVAILABLE;
                case InstallErrorCode.ERROR_INVALID_REQUEST:
                    return ErrorCode.ERROR_INVALID_REQUEST;
                case InstallErrorCode.ERROR_INSTALL_UNAVAILABLE:
                    return ErrorCode.ERROR_INSTALL_UNAVAILABLE;
                case InstallErrorCode.ERROR_INSTALL_NOT_ALLOWED:
                    return ErrorCode.ERROR_INSTALL_NOT_ALLOWED;
                case InstallErrorCode.ERROR_DOWNLOAD_NOT_PRESENT:
                    return ErrorCode.ERROR_DOWNLOAD_NOT_PRESENT;
                case InstallErrorCode.ERROR_APP_NOT_OWNED:
                    return ErrorCode.ERROR_APP_NOT_OWNED;
                case InstallErrorCode.ERROR_PLAY_STORE_NOT_FOUND:
                    return ErrorCode.ERROR_PLAY_STORE_NOT_FOUND;
                case InstallErrorCode.ERROR_INTERNAL_ERROR:
                    return ErrorCode.ERROR_INTERNAL_ERROR;
                case InstallErrorCode.ERROR_UNKNOWN:
                    //noinspection deprecation
                case InstallErrorCode.NO_ERROR_PARTIALLY_ALLOWED:
                default:
                    return ErrorCode.ERROR_UNKNOWN;
            }
        }

        @InstallErrorCode
        int getValue() {
            switch (this) {
                case NO_ERROR:
                    return InstallErrorCode.NO_ERROR;
                case ERROR_API_NOT_AVAILABLE:
                    return InstallErrorCode.ERROR_API_NOT_AVAILABLE;
                case ERROR_INVALID_REQUEST:
                    return InstallErrorCode.ERROR_INVALID_REQUEST;
                case ERROR_INSTALL_UNAVAILABLE:
                    return InstallErrorCode.ERROR_INSTALL_UNAVAILABLE;
                case ERROR_INSTALL_NOT_ALLOWED:
                    return InstallErrorCode.ERROR_INSTALL_NOT_ALLOWED;
                case ERROR_DOWNLOAD_NOT_PRESENT:
                    return InstallErrorCode.ERROR_DOWNLOAD_NOT_PRESENT;
                case ERROR_APP_NOT_OWNED:
                    return InstallErrorCode.ERROR_APP_NOT_OWNED;
                case ERROR_PLAY_STORE_NOT_FOUND:
                    return InstallErrorCode.ERROR_PLAY_STORE_NOT_FOUND;
                case ERROR_INTERNAL_ERROR:
                    return InstallErrorCode.ERROR_INTERNAL_ERROR;
                case ERROR_UNKNOWN:
                default:
                    return InstallErrorCode.ERROR_UNKNOWN;
            }
        }
    }
}
