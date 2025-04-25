package com.nhatnguyenba.ads.ads

import android.content.Context

object AdFactory {
    fun create(
        context: Context,
        type: AdType,
        config: AdConfig
    ): AdManager = AdManagerImpl(context, type, config)
}