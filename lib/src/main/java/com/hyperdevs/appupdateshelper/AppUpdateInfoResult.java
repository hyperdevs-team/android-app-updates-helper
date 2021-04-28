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
    public static final int VERSION_UNKNOWN = -1;
    public static final int VERSION_STALENESS_UNKNOWN = -1;
    public static final int UPDATE_PRIORITY_UNKNOWN = -1;


    private final boolean isSuccessful;
    private final int versionCode;
    private final Availability updateAvailability;
    private final int updatePriority;
    private final boolean canInstallFlexibleUpdate;
    private final boolean canInstallImmediateUpdate;
    private final int clientVersionStalenessDays;
    private final Exception exception;

    AppUpdateInfoResult(@Nullable AppUpdateInfo info,
                        @Nullable Exception exception) {
        this.isSuccessful = info != null && exception == null;
        this.versionCode = (info != null) ? info.availableVersionCode() : VERSION_UNKNOWN;
        this.updateAvailability = Availability.from(
                (info != null) ? info.updateAvailability() : UpdateAvailability.UNKNOWN,
                (info != null) ? info.installStatus() : InstallStatus.UNKNOWN);
        this.updatePriority = info != null ? info.updatePriority() : UPDATE_PRIORITY_UNKNOWN;
        this.canInstallFlexibleUpdate = info != null && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE);
        this.canInstallImmediateUpdate = info != null && info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE);

        Integer infoVersionStalenessDays =
                info != null ? info.clientVersionStalenessDays() : Integer.valueOf(VERSION_STALENESS_UNKNOWN);
        //noinspection ConstantConditions
        this.clientVersionStalenessDays = infoVersionStalenessDays != null ? infoVersionStalenessDays : VERSION_STALENESS_UNKNOWN;

        this.exception = exception;
    }

    @VisibleForTesting
    @SuppressWarnings("checkstyle:ParameterNumber")
    AppUpdateInfoResult(boolean isSuccessful,
                        int versionCode,
                        Availability updateAvailability,
                        int updatePriority,
                        boolean canInstallFlexibleUpdate,
                        boolean canInstallImmediateUpdate,
                        int clientVersionStalenessDays,
                        Exception exception) {
        this.isSuccessful = isSuccessful;
        this.versionCode = versionCode;
        this.updateAvailability = updateAvailability;
        this.updatePriority = updatePriority;
        this.canInstallFlexibleUpdate = canInstallFlexibleUpdate;
        this.canInstallImmediateUpdate = canInstallImmediateUpdate;
        this.clientVersionStalenessDays = clientVersionStalenessDays;
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

    public int getUpdatePriority() {
        return updatePriority;
    }

    public int getClientVersionStalenessDays() {
        return clientVersionStalenessDays;
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
                updatePriority == that.updatePriority &&
                canInstallFlexibleUpdate == that.canInstallFlexibleUpdate &&
                canInstallImmediateUpdate == that.canInstallImmediateUpdate &&
                clientVersionStalenessDays == that.clientVersionStalenessDays &&
                updateAvailability == that.updateAvailability &&
                exception.equals(that.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSuccessful, versionCode, updateAvailability,
                updatePriority, canInstallFlexibleUpdate, canInstallImmediateUpdate,
                clientVersionStalenessDays, exception);
    }

    @Override
    public String toString() {
        return "AppUpdateInfoResult{" +
                "isSuccessful=" + isSuccessful +
                ", versionCode=" + versionCode +
                ", updateAvailability=" + updateAvailability +
                ", updatePriority=" + updatePriority +
                ", canInstallFlexibleUpdate=" + canInstallFlexibleUpdate +
                ", canInstallImmediateUpdate=" + canInstallImmediateUpdate +
                ", clientVersionStalenessDays=" + clientVersionStalenessDays +
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
