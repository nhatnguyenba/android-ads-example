package com.nhatnguyenba.ads.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdSize
import com.google.android.material.button.MaterialButton
import com.nhatnguyenba.ads.R
import com.nhatnguyenba.ads.ads.AdConfig
import com.nhatnguyenba.ads.ads.AdFactory
import com.nhatnguyenba.ads.ads.AdManager
import com.nhatnguyenba.ads.ads.AdType

class MainActivity : AppCompatActivity() {
    private lateinit var btnShowBanner: MaterialButton
    private lateinit var btnHideBanner: MaterialButton
    private lateinit var btnShowInterstitial: MaterialButton
    private lateinit var btnShowNative: MaterialButton
    private lateinit var btnHideNative: MaterialButton
    private lateinit var btnShowRewarded: MaterialButton
    private lateinit var btnShowAppOpen: MaterialButton

    private lateinit var adManagers: Map<AdType, AdManager>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        btnShowBanner = findViewById(R.id.btn_show_banner)
        btnHideBanner = findViewById(R.id.btn_hide_banner)
        btnShowInterstitial = findViewById(R.id.btn_show_interstitial)
        btnShowNative = findViewById(R.id.btn_show_native)
        btnHideNative = findViewById(R.id.btn_hide_native)
        btnShowRewarded = findViewById(R.id.btn_show_rewarded)
        btnShowAppOpen = findViewById(R.id.btn_show_app_open)

        // Initialize all ad managers
        adManagers = mapOf(
            AdType.BANNER to AdFactory.create(
                this,
                AdType.BANNER,
                AdConfig.BannerConfig(
                    container = findViewById(R.id.banner_container),
                    adUnitId = "ca-app-pub-3940256099942544/9214589741",
                    adSize = AdSize.BANNER
                )
            ),

            AdType.INTERSTITIAL to AdFactory.create(
                this,
                AdType.INTERSTITIAL,
                AdConfig.InterstitialConfig(
                    adUnitId = "ca-app-pub-3940256099942544/1033173712"
                )
            ),

            AdType.NATIVE to AdFactory.create(
                this,
                AdType.NATIVE,
                AdConfig.NativeConfig(
                    container = findViewById(R.id.native_container),
                    adUnitId = "ca-app-pub-3940256099942544/2247696110",
                    templateId = R.layout.native_ad_template
                )
            ),

            AdType.REWARDED to AdFactory.create(
                this,
                AdType.REWARDED,
                AdConfig.RewardedConfig(
                    adUnitId = "ca-app-pub-3940256099942544/5224354917",
                    onRewardEarned = { handleReward() }
                )
            ),

            AdType.APP_OPEN to AdFactory.create(
                this,
                AdType.APP_OPEN,
                AdConfig.AppOpenConfig(
                    adUnitId = "ca-app-pub-3940256099942544/9257395921"
                )
            )
        )

        // Setup click listeners
        btnShowBanner.setOnClickListener { adManagers[AdType.BANNER]?.show() }
        btnHideBanner.setOnClickListener { adManagers[AdType.BANNER]?.hide() }
        btnShowInterstitial.setOnClickListener { adManagers[AdType.INTERSTITIAL]?.show() }
        btnShowNative.setOnClickListener { adManagers[AdType.NATIVE]?.show() }
        btnHideNative.setOnClickListener { adManagers[AdType.NATIVE]?.hide() }
        btnShowRewarded.setOnClickListener { adManagers[AdType.REWARDED]?.show() }
        btnShowAppOpen.setOnClickListener { adManagers[AdType.APP_OPEN]?.show() }
    }

    private fun handleReward() {
        // Handle reward logic
    }

    override fun onResume() {
        super.onResume()
        adManagers[AdType.APP_OPEN]?.show()
    }

    override fun onPause() {
        super.onPause()
        adManagers[AdType.APP_OPEN]?.hide()
    }

    fun onShowAdClick(view: View) {
        when (view.id) {
            R.id.btn_show_banner -> adManagers[AdType.BANNER]?.show()
            R.id.btn_hide_banner -> adManagers[AdType.BANNER]?.hide()
            R.id.btn_show_interstitial -> adManagers[AdType.INTERSTITIAL]?.show()
            R.id.btn_show_native -> adManagers[AdType.NATIVE]?.show()
            R.id.btn_hide_native -> adManagers[AdType.NATIVE]?.hide()
            R.id.btn_show_rewarded -> adManagers[AdType.REWARDED]?.show()
            else -> adManagers[AdType.APP_OPEN]?.show()
        }
    }
}