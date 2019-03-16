package com.paulinasadowska.rxworkmanagerobservers.base

import android.os.Looper

internal fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()