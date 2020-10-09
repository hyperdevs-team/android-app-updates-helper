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

package com.bq.appupdateshelper_app

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bq.appupdateshelper_app.databinding.MainActivityBinding
import com.bq.appupdateshelper_app.fake.FakeUpdateActivity
import com.bq.appupdateshelper_app.flexible.FlexibleUpdateActivity
import com.bq.appupdateshelper_app.immediate.ImmediateUpdateActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    private val immediateButton: Button
        get() = binding.immediateButton

    private val flexibleButton: Button
        get() = binding.flexibleButton

    private val fakeButton: Button
        get() = binding.fakeButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        immediateButton.setOnClickListener {
            startActivity(ImmediateUpdateActivity.newIntent(this))
        }

        flexibleButton.setOnClickListener {
            startActivity(FlexibleUpdateActivity.newIntent(this))
        }

        fakeButton.setOnClickListener {
            startActivity(FakeUpdateActivity.newIntent(this))
        }
    }
}
