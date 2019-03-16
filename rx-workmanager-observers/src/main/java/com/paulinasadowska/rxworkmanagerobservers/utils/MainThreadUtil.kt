package com.paulinasadowska.rxworkmanagerobservers.utils

import android.os.Looper

internal fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()