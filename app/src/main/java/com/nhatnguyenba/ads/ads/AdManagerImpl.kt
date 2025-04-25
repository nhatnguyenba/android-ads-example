package com.nhatnguyenba.ads.ads

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.nhatnguyenba.ads.R

class AdManagerImpl(
    private val context: Context,
    private val type: AdType,
    private val config: AdConfig
) : AdManager {

    // Region: Banner Ad Implementation
    private var bannerAdView: AdView? = null

    private fun setupBanner() {
        val bannerConfig = config as AdConfig.BannerConfig
        bannerAdView = AdView(context).apply {
            setAdSize(bannerConfig.adSize)
            adUnitId = bannerConfig.adUnitId
            loadAd(AdRequest.Builder().build())
        }
    }

    private fun showBanner() {
        val bannerConfig = config as AdConfig.BannerConfig
        if (bannerAdView == null) {
            setupBanner()
        }
        bannerConfig.container.removeView(bannerAdView)
        bannerConfig.container.addView(bannerAdView)
    }

    private fun hideBanner() {
        val bannerConfig = config as AdConfig.BannerConfig
        bannerAdView?.let {
            bannerConfig.container.removeView(it)
            it.destroy()
            bannerAdView = null
        }
    }
    // End Region

    // Region: Interstitial Ad Implementation
    private var interstitialAd: InterstitialAd? = null

    private fun loadInterstitial() {
        val interstitialConfig = config as AdConfig.InterstitialConfig
        InterstitialAd.load(context, interstitialConfig.adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(loadedAd: InterstitialAd) {
                    interstitialAd = loadedAd
                }
            })
    }

    private fun showInterstitial() {
        interstitialAd?.show(context as Activity)
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                loadInterstitial() // Reload after dismiss
            }
        }
    }

    private fun hideInterstitial() {
        interstitialAd = null
    }
    // End Region

    // Region: Native Ad Implementation
    private var nativeAd: NativeAd? = null
    private var nativeAdView: NativeAdView? = null

    private fun setupNativeAd() {
        val nativeConfig = config as AdConfig.NativeConfig
        val adLoader = AdLoader.Builder(context, nativeConfig.adUnitId)
            .forNativeAd { ad ->
                nativeAd = ad
                inflateNativeAdView(ad, nativeConfig)
            }
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun inflateNativeAdView(ad: NativeAd, config: AdConfig.NativeConfig) {
        nativeAdView = LayoutInflater.from(context)
            .inflate(config.templateId, config.container, false) as NativeAdView

        // Bind ad data to views
        nativeAdView?.headlineView = nativeAdView?.findViewById(R.id.ad_headline)
        nativeAdView?.bodyView = nativeAdView?.findViewById(R.id.ad_body)
        // ... bind other views

        nativeAdView?.setNativeAd(ad)
        config.container.addView(nativeAdView)
    }

    private fun showNative() {
        if (nativeAd == null) {
            setupNativeAd()
        } else {
            nativeAdView?.visibility = View.VISIBLE
        }
    }

    private fun hideNative() {
        nativeAdView?.visibility = View.GONE
        nativeAd?.destroy()
        nativeAd = null
    }
    // End Region

    // Region: Rewarded Ad Implementation
    private var rewardedAd: RewardedAd? = null
    private var isRewarded = false

    private fun loadRewarded() {
        val rewardedConfig = config as AdConfig.RewardedConfig
        RewardedAd.load(context, rewardedConfig.adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
            })
    }

    private fun showRewarded() {
        rewardedAd?.show(context as Activity) { reward ->
            (config as AdConfig.RewardedConfig).onRewardEarned()
            isRewarded = true
        }

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                if (!isRewarded) {
                    // Handle case when user didn't earn reward
                }
                loadRewarded() // Reload after dismiss
            }
        }
    }

    private fun hideRewarded() {
        rewardedAd = null
        isRewarded = false
    }
    // End Region

    // Region: App Open Ad Implementation
    private var appOpenAd: AppOpenAd? = null
    private var isShowingAppOpen = false

    private fun loadAppOpen() {
        val appOpenConfig = config as AdConfig.AppOpenConfig
        AppOpenAd.load(context, appOpenConfig.adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                }
            })
    }

    private fun showAppOpen() {
        if (isShowingAppOpen) return

        appOpenAd?.show(context as Activity)
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                isShowingAppOpen = false
                loadAppOpen() // Reload after dismiss
            }
        }
        isShowingAppOpen = true
    }

    private fun hideAppOpen() {
        appOpenAd = null
        isShowingAppOpen = false
    }
    // End Region

    override fun show() {
        when (type) {
            AdType.BANNER -> showBanner()
            AdType.INTERSTITIAL -> showInterstitial()
            AdType.NATIVE -> showNative()
            AdType.REWARDED -> showRewarded()
            AdType.APP_OPEN -> showAppOpen()
        }
    }

    override fun hide() {
        when (type) {
            AdType.BANNER -> hideBanner()
            AdType.INTERSTITIAL -> hideInterstitial()
            AdType.NATIVE -> hideNative()
            AdType.REWARDED -> hideRewarded()
            AdType.APP_OPEN -> hideAppOpen()
        }
    }
}