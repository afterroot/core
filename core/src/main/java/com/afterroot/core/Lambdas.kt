/*
 * Copyright (C) 2019-2020 Sandip Vaghela
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.afterroot.core

import android.os.Build
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(parameter = 0, lambda = 1)
fun onVersionGreaterThanEqualTo(targetVersion: Int, trueFun: () -> Unit, falseFun: (() -> Unit)? = null) {
    if (Build.VERSION.SDK_INT >= targetVersion) {
        trueFun()
    } else {
        if (falseFun != null) {
            falseFun()
        }
    }
}

fun onVersionLessThan(targetVersion: Int, trueFun: () -> Unit, falseFun: (() -> Unit)? = null) {
    if (Build.VERSION.SDK_INT < targetVersion) {
        trueFun()
    } else {
        if (falseFun != null) {
            falseFun()
        }
    }
}

/**
 * just like runCatching but without result
 * @see runCatching
 */
inline fun <T> T.safeRun(TAG: String = "", block: T.() -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        //ignore but log it
        Log.e(TAG, e.toString())
    }
}