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

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import static com.google.android.play.core.install.model.InstallStatus.DOWNLOADED;

/**
 * Class that represents the result of
 * {@link AppUpdatesHelper#getAppUpdateInfo(GetUpdateInfoListener)}.
 */
public class AppUpdateInfoResult {
    private final boolean isSuccessful;
    private final int versionCode;
    private final Availability updateAvailability;
    private final boolean canInstallFlexibleUpdate;
    private final boolean canInstallImmediateUpdate;
    private final Exception exception;

    AppUpdateInfoResult(@Nullable AppUpdateInfo info,
                        @Nullable Exception exception) {
        this.isSuccessful = info != null && exception == null;
        this.versionCode = (info != null) ? info.availableVersionCode() : -1;
        this.updateAvailability = Availability.from(
            (info != null) ? info.updateAvailability() : -1,
            (info != null) ? info.installStatus() : -1);
        this.canInstallFlexibleUpdate = info != null && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE);
        this.canInstallImmediateUpdate = info != null && info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE);

        this.exception = exception;
    }

    @VisibleForTesting AppUpdateInfoResult(boolean isSuccessful,
                                           int versionCode,
                                           Availability updateAvailability,
                                           boolean canInstallFlexibleUpdate,
                                           boolean canInstallImmediateUpdate, Exception exception) {
        this.isSuccessful = isSuccessful;
        this.versionCode = versionCode;
        this.updateAvailability = updateAvailability;
        this.canInstallFlexibleUpdate = canInstallFlexibleUpdate;
        this.canInstallImmediateUpdate = canInstallImmediateUpdate;
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public Availability getUpdateAvailability() {
        return updateAvailability;
    }

    /**
     * @return if the update can be flexible
     */
    public boolean canInstallFlexibleUpdate() {
        return canInstallFlexibleUpdate;
    }

    /**
     * @return if the update can be immediate
     */
    public boolean canInstallImmediateUpdate() {
        return canInstallImmediateUpdate;
    }

    @Nullable
    public Exception getException() {
        return exception;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUpdateInfoResult that = (AppUpdateInfoResult) o;
        return isSuccessful == that.isSuccessful &&
            versionCode == that.versionCode &&
            canInstallFlexibleUpdate == that.canInstallFlexibleUpdate &&
            canInstallImmediateUpdate == that.canInstallImmediateUpdate &&
            updateAvailability == that.updateAvailability &&
            Objects.equals(exception, that.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccessful, versionCode, updateAvailability, canInstallFlexibleUpdate, canInstallImmediateUpdate, exception);
    }

    @Override public String toString() {
        return "AppUpdateInfoResult{" +
            "isSuccessful=" + isSuccessful +
            ", versionCode=" + versionCode +
            ", updateAvailability=" + updateAvailability +
            ", canInstallFlexibleUpdate=" + canInstallFlexibleUpdate +
            ", canInstallImmediateUpdate=" + canInstallImmediateUpdate +
            ", exception=" + exception +
            '}';
    }

    /**
     * Enum that represents the availability of the update.
     */
    public enum Availability {
        UNKNOWN,
        UPDATE_NOT_AVAILABLE,
        UPDATE_AVAILABLE,
        UPDATE_IN_PROGRESS,
        UPDATE_DOWNLOADED;

        static Availability from(@UpdateAvailability int availability, @InstallStatus int installStatus) {
            switch (availability) {
                case UpdateAvailability.UPDATE_NOT_AVAILABLE:
                    return UPDATE_NOT_AVAILABLE;
                case UpdateAvailability.UPDATE_AVAILABLE:
                    return UPDATE_AVAILABLE;
                case UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS:
                    if (installStatus == DOWNLOADED) {
                        return UPDATE_DOWNLOADED;
                    } else {
                        return UPDATE_IN_PROGRESS;
                    }
                case UpdateAvailability.UNKNOWN:
                default:
                    return UNKNOWN;
            }
        }
    }
}
