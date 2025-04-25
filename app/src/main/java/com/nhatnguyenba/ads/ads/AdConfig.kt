package com.nhatnguyenba.ads.ads

import android.view.ViewGroup
import com.google.android.gms.ads.AdSize

sealed class AdConfig {
    data class BannerConfig(
        val container: ViewGroup,
        val adUnitId: String,
        val adSize: AdSize
    ) : AdConfig()

    data class InterstitialConfig(
        val adUnitId: String
    ) : AdConfig()

    data class NativeConfig(
        val container: ViewGroup,
        val adUnitId: String,
        val templateId: Int
    ) : AdConfig()

    data class RewardedConfig(
        val adUnitId: String,
        val onRewardEarned: () -> Unit
    ) : AdConfig()

    data class AppOpenConfig(
        val adUnitId: String
    ) : AdConfig()
}