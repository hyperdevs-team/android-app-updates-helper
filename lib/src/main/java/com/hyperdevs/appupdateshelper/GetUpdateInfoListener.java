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

import androidx.annotation.NonNull;

/**
 * Callback listener to obtain information about a finished update info request.
 */
public interface GetUpdateInfoListener {
    /**
     * Triggered when an app info request has finished.
     *
     * @param result The result of the app info request whether is an error or not
     */
    void onGetUpdateInfoComplete(@NonNull AppUpdateInfoResult result);
}
