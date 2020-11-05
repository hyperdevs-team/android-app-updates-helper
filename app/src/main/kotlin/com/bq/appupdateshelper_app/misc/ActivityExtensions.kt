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

package com.bq.appupdateshelper_app.misc

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * Displays a text as a toast in the current activity.
 *
 * @param text Text to display in the toast
 * @param duration Duration, one of [Toast.LENGTH_SHORT] or [Toast.LENGTH_LONG]
 */
fun Activity.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

/**
 * Displays a text as a toast in the current activity.
 *
 * @param stringResId Text to display in the toast as a string resource ID
 * @param duration Duration, one of [Toast.LENGTH_SHORT] or [Toast.LENGTH_LONG]
 */
fun Activity.showToast(@StringRes stringResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, stringResId, duration).show()
}
