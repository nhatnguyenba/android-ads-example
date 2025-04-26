# Android Ads Example 📱🤑

A professional implementation of various ad formats in Android with clean architecture and best practices.

<div align="center">
    <img src="screenshots/ads-demo.gif" width="30%">
</div>

## Features ✨

- **5 Ad Formats Supported**  
  ✅ Banner Ads | ✅ Interstitial Ads | ✅ Native Ads  
  ✅ Rewarded Ads | ✅ App Open Ads
- **SDK Agnostic Architecture**  
  🔄 Easy to switch between AdMob, Facebook Audience Network, etc.
- **Modern Tech Stack**  
  🚀 Kotlin | 🧩 Clean Architecture |
  🌐 Material Design

## Prerequisites 📋

- Android Studio Giraffe+
- Android SDK 33+
- Kotlin 1.8+
- Google AdMob Account
- (Optional) Facebook Audience Network Account

## Installation 🛠️

1. **Clone Repository**
   ```bash
   git clone https://github.com/nhatnguyenba/android-ads-example.git

2. **Add Configuration**

    Create `app/keystore.properties`:

    ```bash
    ADMOB_APP_ID="ca-app-pub-3940256099942544~3347511713"
    BANNER_AD_UNIT_ID="ca-app-pub-3940256099942544/9214589741"
    INTERSTITIAL_AD_UNIT_ID="ca-app-pub-3940256099942544/1033173712"
    // Other IDs ...
3. **Build & Run**

   🏃♂️ Run the app on emulator/device

## Architecture 🏛️
📦 android-ads-example

┣ 📂 ui

┃ ┣ 📂 screens # Activities/Fragments

┃ ┣ 📂 components # Custom ad views

┃ ┗ 📂 state # Ad visibility/state management

┗ 📂 ads

┃ ┣ 📂 contracts # Interfaces & enums

┃ ┣ 📂 adapters # SDK implementations

┃ ┗ 📂 factories # Ad creation logic

### Key Components:
- **`AdManager`:** Base interface for all ad types

- **`AdConfig`**: Type-safe configuration sealed classes

- **`AdFactory`**: Central ad creation point

## Ad Placement Strategy 📊
| Ad Type      | Recommended Placement                           | Frequency & Usage                                     | Why It Matters                                                         |
|--------------|-------------------------------------------------|-------------------------------------------------------|------------------------------------------------------------------------|
| Banner       | Bottom/Top of screens                           | Persistent visibility                                 | Non-intrusive, maintains user flow while generating steady impressions |
| Interstitial | Between natural breaks (e.g., level completion) | 1-2 times per screen transition                       | Avoids interrupting critical user journeys while maximizing impact     |
| Native       | Blended with content feeds                      | Every 5-6 content items (e.g., after 5 news articles) | Feels organic - users perceive ads as natural content                  |
| Rewarded     | Optional rewards section                        | User-initiated (only when users choose rewards)       | Higher completion rates since users actively opt-in                    |
| App Open     | App launch/return from background               | Limited to 3 times/day (even with 10+ app opens)      | Prevents ad fatigue from frequent app usage                            |



