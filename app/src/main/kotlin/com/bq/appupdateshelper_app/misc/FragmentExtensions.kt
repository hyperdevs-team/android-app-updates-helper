/*
 * Copyright (C) 2020 BQ
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

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * Displays a text as a toast in the current fragment.
 *
 * @param text Text to display in the toast
 * @param duration Duration, one of [Toast.LENGTH_SHORT] or [Toast.LENGTH_LONG]
 */
fun Fragment.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, text, duration).show()
}

/**
 * Displays a text as a toast in the current fragment.
 *
 * @param stringResId Text to display in the toast as a string resource ID
 * @param duration Duration, one of [Toast.LENGTH_SHORT] or [Toast.LENGTH_LONG]
 */
fun Fragment.showToast(@StringRes stringResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, stringResId, duration).show()
}