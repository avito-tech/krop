package com.avito.android.krop.demo

import android.os.Build
import android.app.Activity
import android.view.View

fun tintStatusBarIcons(activity: Activity, tintToDark: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val decor = activity.window.decorView
        if (tintToDark) {
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decor.systemUiVisibility = 0
        }
    }
}