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

package com.hyperdevs.appupdateshelper.app.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hyperdevs.appupdateshelper.AppUpdatesHelper
import com.hyperdevs.appupdateshelper.app.R
import com.hyperdevs.appupdateshelper.app.databinding.FragmentUpdateActivityBinding

/**
 * Activity that illustrates the use of the [AppUpdatesHelper] class of the lib to start flexible
 * updates in a fragment.
 */
class FragmentUpdateActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, FragmentUpdateActivity::class.java)
        }
    }

    private lateinit var binding: FragmentUpdateActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentUpdateActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        setTitle(R.string.activity_fragment_update_title)
    }
}