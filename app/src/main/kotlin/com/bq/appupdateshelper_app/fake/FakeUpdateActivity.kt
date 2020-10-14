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

package com.bq.appupdateshelper_app.fake

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bq.appupdateshelper.AppUpdateInfoResult
import com.bq.appupdateshelper.AppUpdateInstallState.Status.*
import com.bq.appupdateshelper.FakeAppUpdatesHelper
import com.bq.appupdateshelper_app.databinding.FakeUpdateActivityBinding
import com.bq.appupdateshelper_app.R
import com.bq.appupdateshelper_app.misc.showToast
import com.google.android.material.snackbar.Snackbar

/**
 * Activity that illustrates the use of the [FakeAppUpdatesHelper] class of the lib to simulate
 * app updates in test environments.
 *
 * In this class we'll simulate a flexible update scenario. In order to understand how flexible
 * updates work, please review [com.bq.appupdateshelper.flexible.FlexibleUpdateActivity].
 */
class FakeUpdateActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "FakeUpdateActivity"
        fun newIntent(context: Context): Intent {
            return Intent(context, FakeUpdateActivity::class.java)
        }
    }

    private lateinit var fakeAppUpdatesHelper: FakeAppUpdatesHelper

    private lateinit var binding: FakeUpdateActivityBinding

    private val startUpdateButton: Button
        get() = binding.startUpdateButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FakeUpdateActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        setTitle(R.string.activity_fake_update_title)

        fakeAppUpdatesHelper = FakeAppUpdatesHelper(this)

        fakeAppUpdatesHelper.startListening { installState ->

            Log.d(TAG, "Update install state: $installState")

            when (installState.status) {
                UNKNOWN -> {
                    showToast("Unknown install state")
                }
                DENIED -> {
                    showToast("The user denied the flexible update!")
                    finish()
                }
                REQUIRES_UI_INTENT -> {
                    showToast("The update needs an UI intent!")
                }
                UPDATE_ACCEPTED -> {
                    showToast("The user accepted the update!")
                }
                PENDING -> {
                    showToast("Waiting for update to start!")
                }
                DOWNLOADING -> {
                    showToast("The update is downloading!")
                }
                DOWNLOADED -> {
                    showToast("The update is downloading! Progress: ${installState.downloadProgress}")

                    Snackbar.make(binding.root, "Install the update?", Snackbar.LENGTH_INDEFINITE)
                            .apply {
                                setAction("Install") {
                                    fakeAppUpdatesHelper.completeUpdate()

                                    // Fake installation process
                                    fakeAppUpdatesHelper.completeFakeUpdate()
                                }
                            }
                            .show()
                }
                INSTALLING -> {
                    showToast("The update is being installed!")
                }
                INSTALLED -> {
                    showToast("The update has been installed!")
                }
                FAILED -> {
                    showToast("The update failed! Reason: ${installState.errorCode}")

                    Snackbar.make(binding.root, "Retry?", Snackbar.LENGTH_LONG)
                            .apply {
                                setAction("Retry") {
                                    fakeAppUpdatesHelper.startImmediateUpdate(this@FakeUpdateActivity)
                                }
                            }
                            .show()
                }
                CANCELED -> {
                    showToast("The user canceled the flexible update!")
                }
            }
        }

        startUpdateButton.setOnClickListener {
            // We're going to set-up the fake in order to simulate an available update
            fakeAppUpdatesHelper.configAvailableUpdate()

            fakeAppUpdatesHelper.getAppUpdateInfo { appUpdateInfoResult ->
                Log.d(TAG, "App update info: $appUpdateInfoResult")

                if (appUpdateInfoResult.isSuccessful) {
                    when (appUpdateInfoResult.updateAvailability!!) {
                        AppUpdateInfoResult.Availability.UNKNOWN -> {
                            showToast("The state of the update is unknown!")
                        }
                        AppUpdateInfoResult.Availability.UPDATE_NOT_AVAILABLE -> {
                            showToast("No update available!")
                        }
                        AppUpdateInfoResult.Availability.UPDATE_AVAILABLE -> {
                            showToast("Update available!")
                            if (appUpdateInfoResult.canInstallFlexibleUpdate()) {
                                // Start the update flow
                                showToast("Can install flexible update!")
                                fakeAppUpdatesHelper.startFlexibleUpdate(this)

                                // Start faking the update flow
                                fakeAppUpdatesHelper.startFakeInstallFlow()
                            } else {
                                showToast("Can not install flexible update!")
                            }
                        }
                        AppUpdateInfoResult.Availability.UPDATE_IN_PROGRESS -> {
                            showToast("Update in progress!")
                        }
                        AppUpdateInfoResult.Availability.UPDATE_DOWNLOADED -> {
                            showToast("Update downloaded!")
                            fakeAppUpdatesHelper.completeUpdate()

                            // Start faking installation process
                            fakeAppUpdatesHelper.completeFakeUpdate()
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

        // We bind this but the fake will not launch an external activity, so it won't effect
        // the flow
        fakeAppUpdatesHelper.onUpdateStatusResult(requestCode, resultCode)
    }

    override fun onDestroy() {
        super.onDestroy()
        fakeAppUpdatesHelper.stopListening()
    }
}

private fun FakeAppUpdatesHelper.configAvailableUpdate() {
    setUpdateAvailable(12345)
}

private fun FakeAppUpdatesHelper.startFakeInstallFlow() {
    val handler = Handler()

    with(handler) {
        postDelayed({ userAcceptsUpdate() }, 0)
        postDelayed({ downloadStarts() }, 1000)
        postDelayed({ downloadCompletes() }, 4000)
    }
}

private fun FakeAppUpdatesHelper.completeFakeUpdate() {
    val handler = Handler()

    with(handler) {
        postDelayed({ installCompletes() }, 4000)
    }
}
