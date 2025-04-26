package com.nhatnguyenba.ads.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
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
        bannerConfig.container.visibility = View.VISIBLE
        bannerConfig.container.removeView(bannerAdView)
        bannerConfig.container.addView(bannerAdView)
    }

    private fun hideBanner() {
        val bannerConfig = config as AdConfig.BannerConfig
        bannerAdView?.let {
            bannerConfig.container.removeView(it)
            bannerConfig.container.visibility = View.GONE
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
                    interstitialAd?.show(context as Activity)
                }
            })
    }

    private fun showInterstitial() {
        loadInterstitial()
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d("NHAT", "Ad dismissed fullscreen content.")
                interstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Log.e("NHAT", "Ad failed to show fullscreen content.")
                interstitialAd = null
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
    private var nativeContainer: ViewGroup? = null

    private fun setupNativeAd() {
        val nativeConfig = config as AdConfig.NativeConfig
        nativeContainer = nativeConfig.container
        val adLoader = AdLoader.Builder(context, nativeConfig.adUnitId)
            .forNativeAd { ad ->
                nativeAd = ad
                inflateNativeAdView(ad, nativeConfig)
            }
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    @SuppressLint("CutPasteId")
    private fun inflateNativeAdView(ad: NativeAd, config: AdConfig.NativeConfig) {
        nativeAdView = LayoutInflater.from(context)
            .inflate(config.templateId, config.container, false) as NativeAdView

        // Bind ad data to views
        nativeAdView?.apply {
            val adAppIcon = findViewById<ImageView>(R.id.ad_app_icon)
            val adHeadline = findViewById<TextView>(R.id.ad_headline)
            val adBody = findViewById<TextView>(R.id.ad_body)
            val adAdvertiser = findViewById<TextView>(R.id.ad_advertiser)
            val adMedia = findViewById<MediaView>(R.id.ad_media)
            val adRating = findViewById<TextView>(R.id.ad_stars)
            val adPrice = findViewById<TextView>(R.id.ad_price)
//            var adStore = findViewById<TextView>(R.id.ad_store)
            val adCallToAction = findViewById<Button>(R.id.ad_call_to_action)
//            var adRatingBar = findViewById<RatingBar>(R.id.ad_rating_bar)

            adHeadline.text = ad.headline
            adBody.text = ad.body
            adAdvertiser.text = ad.advertiser
            adRating.text = ad.starRating.toString()
            adPrice.text = ad.price
//            adStore.text = ad.store
            adCallToAction.text = ad.callToAction
//            adRatingBar.rating = ad.starRating.toFloat()
            adMedia.mediaContent = ad.mediaContent
            adAppIcon.setImageDrawable(ad.icon?.drawable)

            iconView = adAppIcon
            callToActionView = adCallToAction
            mediaView = adMedia
            starRatingView = adRating
            priceView = adPrice
            headlineView = adHeadline
            bodyView = adBody
            advertiserView = adAdvertiser
        }

        nativeAdView?.setNativeAd(ad)
        config.container.removeAllViews()
        config.container.addView(nativeAdView)
    }

    private fun showNative() {
        if (nativeAd == null) {
            setupNativeAd()
        }
        nativeContainer?.visibility = View.VISIBLE
        nativeAdView?.visibility = View.VISIBLE
    }

    private fun hideNative() {
        nativeContainer?.visibility = View.GONE
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
                    rewardedAd?.show(context as Activity) { reward ->
                        config.onRewardEarned()
                        isRewarded = true
                    }
                }
            })
    }

    private fun showRewarded() {
        loadRewarded()
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                if (!isRewarded) {
                    // Handle case when user didn't earn reward
                }
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
                    appOpenAd?.show(context as Activity)
                }
            })
    }

    private fun showAppOpen() {
        if (isShowingAppOpen) return

        loadAppOpen()
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                isShowingAppOpen = false
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