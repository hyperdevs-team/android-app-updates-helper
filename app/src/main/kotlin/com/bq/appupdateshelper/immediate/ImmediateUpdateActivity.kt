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

package com.bq.appupdateshelper.immediate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bq.appupdateshelper.AppUpdateInfoResult
import com.bq.appupdateshelper.AppUpdateInstallState.Status.*
import com.bq.appupdateshelper.AppUpdatesHelper
import com.bq.appupdateshelper.R
import com.bq.appupdateshelper.misc.showToast
import com.google.android.material.snackbar.Snackbar

/**
 * Activity that illustrates the use of the [AppUpdatesHelper] class of the lib start immediate
 * updates.
 */
class ImmediateUpdateActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ImmediateUpdateActivity"
        fun newIntent(context: Context): Intent {
            return Intent(context, ImmediateUpdateActivity::class.java)
        }
    }

    private lateinit var appUpdatesHelper: AppUpdatesHelper

    private val startUpdateButton: Button
        get() = findViewById(R.id.start_update_button)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_immediate_update)

        setTitle(R.string.activity_immediate_update_title)

        // Instantiate the app updates helper and start using it with startListening()
        appUpdatesHelper = AppUpdatesHelper(this)

        appUpdatesHelper.startListening { installState ->
            // The update process is tracked here from the moment the user clicks "Update" until the
            // app is fully installed
            Log.d(TAG, "Update install state: $installState")

            when (installState.status) {
                UNKNOWN -> {
                    showToast("Unknown install state")
                }
                DENIED -> {
                    // This state is only reachable in immediate updates, and it happens when the
                    // user cancels an immediate update. Here you can finish your activity.
                    showToast("The user denied the immediate update!")
                    finish()
                }
                REQUIRES_UI_INTENT -> {
                    // The docs don't really say anything about this state or when it's triggered
                    // so...
                    showToast("The update needs an UI intent!")
                }
                UPDATE_ACCEPTED -> {
                    // The user has accepted the update, so you can notify the user
                    // This is not guaranteed to arrive in immediate updates.
                    showToast("The user accepted the update!")
                }
                PENDING -> {
                    showToast("Waiting for update to start!")
                }
                DOWNLOADING -> {
                    showToast("The update is downloading!")
                }
                DOWNLOADED -> {
                    showToast("The update has been downloaded!")
                }
                INSTALLING -> {
                    showToast("The update is being installed!")
                }
                INSTALLED -> {
                    // Usually you won't get up to this state, since the app closes automatically
                    // in the installation process. Anyway, it's not a bad practice to consider this
                    // case
                    showToast("The update has been installed!")
                }
                FAILED -> {
                    // The installation failed for some reason, here you can retry the update
                    // process if needed
                    showToast("The update failed! Reason: ${installState.errorCode}")

                    Snackbar.make(findViewById(android.R.id.content), "Retry?", Snackbar.LENGTH_LONG)
                        .apply {
                            setAction("Retry") {
                                appUpdatesHelper.startImmediateUpdate(this@ImmediateUpdateActivity)
                            }
                        }
                        .show()
                }
                CANCELED -> {
                    // This state is only reachable in flexible updates.
                    showToast("The user canceled the immediate update!")
                }
            }
        }

        startUpdateButton.setOnClickListener {
            // Start the update flow by first checking if there's any update available for the app
            // in the Play Store using getAppUpdateInfo()
            appUpdatesHelper.getAppUpdateInfo { appUpdateInfoResult ->
                Log.d(TAG, "App update info: $appUpdateInfoResult")

                if (appUpdateInfoResult.isSuccessful) {
                    // If the request went well, you can check the state of the app update
                    when (appUpdateInfoResult.updateAvailability!!) {
                        AppUpdateInfoResult.Availability.UNKNOWN -> {
                            showToast("The state of the update is unknown!")
                        }
                        AppUpdateInfoResult.Availability.UPDATE_NOT_AVAILABLE -> {
                            showToast("No update available!")
                        }
                        AppUpdateInfoResult.Availability.UPDATE_AVAILABLE -> {
                            showToast("Update available!")
                            // When we know that the update is available, we must check if we can
                            // perform the desired type of update
                            if (appUpdateInfoResult.canInstallImmediateUpdate()) {
                                // Start the update flow immediately
                                showToast("Can install immediate update!")
                                appUpdatesHelper.startImmediateUpdate(this)
                            } else {
                                showToast("Can not install immediate update!")
                            }
                        }
                        AppUpdateInfoResult.Availability.UPDATE_IN_PROGRESS -> {
                            // If the update is in progress, we can jump to the immediate flow again
                            showToast("Update in progress!")
                            appUpdatesHelper.startImmediateUpdate(this)
                        }
                        AppUpdateInfoResult.Availability.UPDATE_DOWNLOADED -> {
                            // The app update is downloaded, but the install flow has not started
                            // yet, so complete the update
                            showToast("Update downloaded!")
                            appUpdatesHelper.completeUpdate()
                        }
                    }
                } else {
                    showToast("The update info could not be retrieved, " +
                              "cause: ${appUpdateInfoResult.exception!!.message}")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // We have to bind the helper to the activity results so it can properly dispatch some
        // update events
        appUpdatesHelper.onUpdateStatusResult(requestCode, resultCode)
    }

    override fun onDestroy() {
        // Stop listening for updates with stopListening()
        super.onDestroy()
        appUpdatesHelper.stopListening()
    }
}
