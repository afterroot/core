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

package com.afterroot.core.extensions

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.webkit.MimeTypeMap
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.preference.PreferenceManager
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afterroot.core.R

/**
 * Sets visibility of view with optional [transition]
 * @param value view should visible or not
 * @param transition [Transition] apply to view while changing visibility
 * @param viewGroup [View] of which visibility will change
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 * @since v0.1.0
 */
fun View.visible(
    value: Boolean,
    transition: Transition? = Fade(if (value) Fade.MODE_IN else Fade.MODE_OUT),
    viewGroup: ViewGroup = parent as ViewGroup
) {
    if (transition != null) {
        TransitionManager.beginDelayedTransition(viewGroup, transition)
    }
    visibility = if (value) View.VISIBLE else View.GONE
}

/**
 * animate property of Any Object
 * @param property [Float] property to animate
 * @param from start of [property]
 * @param to end of [property]
 * @param duration duration of animation
 * @param interpolator [Interpolator] to use for animation
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 * @since v0.1.0
 */
fun Any.animateProperty(
    property: String,
    from: Float,
    to: Float,
    duration: Long = 400,
    interpolator: Interpolator = AccelerateDecelerateInterpolator()
) {
    ObjectAnimator.ofFloat(this, property, from, to).apply {
        this.interpolator = interpolator
        this.duration = duration
        start()
    }
}

/**
 * Extension function for animating progress of [DrawerArrowDrawable]
 * @param from start progress
 * @param to end progress
 * @param duration duration of animation
 * @param interpolator [Interpolator] for Animation. Default is [AccelerateDecelerateInterpolator]
 * @since v0.2.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 */
fun DrawerArrowDrawable.progress(
    from: Float,
    to: Float,
    duration: Long = 400,
    interpolator: Interpolator = AccelerateDecelerateInterpolator()
) {
    animateProperty("progress", from, to, duration, interpolator)
}

/**
 * Helper function for checking app is installed or not
 * @param pName package name of target
 * @throws [PackageManager.NameNotFoundException] throws if package not found - no need to handle exception
 * @since v0.1.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 * @return true if package installed otherwise false
 */
fun Context.isAppInstalled(pName: String): Boolean {
    return try {
        this.packageManager.getApplicationInfo(pName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

/**
 * Helper function for checking internet connection availability. May not work on API 29 or higher
 * @since v0.1.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 * @return true if connection available otherwise false
 */
@Deprecated("Does not work on Android 10", replaceWith = ReplaceWith("NetworkStateMonitor", "com.afterroot.core.network"))
fun Context.isNetworkAvailable(): Boolean { //TODO compatibility with android 10
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo?.isConnectedOrConnecting == true
}

/**
 * Get default [SharedPreferences] instance
 * @since v0.1.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 * @return [SharedPreferences] instance
 */
fun Context.getPrefs(): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}

/**
 * Helper function for get drawable using Compat Method
 * @param id Resource id of [Drawable]
 * @param tint tint to apply
 * @since v0.1.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 * @return [Drawable] with optional tint
 */
fun Context.getDrawableExt(@DrawableRes id: Int, @ColorRes tint: Int? = null): Drawable? {
    var drawable: Drawable? = null
    try {
        drawable = ContextCompat.getDrawable(this, id)!!
        if (tint != null) {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this, tint))
        }
    } catch (e: Exception) {
        Log.e("Extensions", "getDrawableExt: ${e.stackTrace}")
    }
    return drawable
}

/**
 * Helper function for requiring extension of file
 * @param fileName fileName name of file
 * @return extension of [fileName]
 * @since v0.1.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 */
fun getFileExt(fileName: String): String {
    return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length)
}

/**
 * @fileName fileName name of file
 * @return mime type of fileName
 * @since v0.1.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 */
fun getMimeType(fileName: String): String? {
    var type: String? = null
    try {
        val extension = getFileExt(fileName)
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return type
}

/**
 * [Intent] Helper for [Intent.ACTION_VIEW]
 * @param context Context
 * @param filename filename with extension
 * @param uri Data
 * @since v0.1.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 */
fun openFile(context: Context, filename: String, uri: Uri) {
    context.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(uri, getMimeType(filename)))
}

/**
 * Extension Function for Inflating Layout to [ViewGroup]
 * @param layoutId Resource id of layout to inflate
 * @param attachToRoot Whether the inflated hierarchy should be attached to the root parameter?
 * If false, root is only used to create the correct subclass of
 * LayoutParams for the root view in the XML.
 * @since v0.1.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 */
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

/**
 * Get given view as [Bitmap] image
 * @return [Bitmap] image of view
 * @since v0.2.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 */
fun View.getAsBitmap(): Bitmap? {
    if (width == 0 || height == 0) {
        return null
    }
    val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    run {
        layout(left, top, right, bottom)
        draw(c)
    }
    return b
}

/**
 * Shows indeterminate progress dialog
 * @param progressText text to show in Dialog
 * @return [MaterialDialog] with progress layout
 * @since v0.1.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 */
fun Context.showStaticProgressDialog(progressText: String): MaterialDialog {
    val dialog = MaterialDialog(this).show {
        customView(R.layout.dialog_progress)
        cornerRadius(16f)
        cancelable(false)
    }
    dialog.updateProgressText(progressText)
    return dialog
}

/**
 * Update progress of dialog shown by [showStaticProgressDialog]
 * @param progressText text to show in Dialog
 * @since v0.2.0
 * @author [Sandip Vaghela](http://github.com/thesandipv)
 */
fun MaterialDialog.updateProgressText(progressText: String) {
    getCustomView().findViewById<AppCompatTextView>(R.id.text_progress).text = progressText
}