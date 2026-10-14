# INDOSANHITA Global News
> **ASCEND WITH EVERY STORY**

INDOSANHITA Global News is a modern, trustworthy, multilingual news, AI analysis, and claim verification platform built natively for Android using Kotlin and Jetpack Compose.

---

## 🌟 Core Features & Implementation

### 1. Real-Time News & Categorized Feeds
- **Categories:** Top Stories, India, World, Business, Technology, Science, Health, Sports, Entertainment, and Explainers.
- **Attribution & Provenance:** Original publisher links, timestamps, reading time estimates, and author/source credibility tags.
- **Offline Caching:** Supported via Room SQLite database.
- **Multilingual Support:** One-tap translation on news cards and reader view into Hindi (हिन्दी), Bengali (বাংলা), Tamil (தமிழ்), Telugu (తెలుగు), Marathi (मराठी), Spanish (Español), French (Français), and German (Deutsch).

### 2. AI Chat & Real-Time Reasoning
- **Configurable Models:** Gemini 1.5 Flash and INDOSANHITA Intelligence Engine (disclosing underlying configuration).
- **5 Response Styles:** Quick Answer, Balanced, Detailed, Deep Analysis, News Expert.
- **3 Response Lengths:** Short, Medium, Long.
- **Live Progressive Streaming:** Smooth word-by-word token generation.
- **Grounded Citations:** Links and references to primary news sources.
- **Multi-Input Composer:** Typed text, pasted URLs, image/document upload previews, and live speech-to-text recognition with audio interruption.
- **Article Grounding:** "Talk & Learn More" passes context-aware article summaries and titles.

### 3. Claim Verification Engine (Analyze / Verify)
- **Input Channels:** Text claim, article URL, screenshot, or opened document.
- **Structured Assessment:** Extracts and presents the claim for user editing prior to corroboration.
- **Balanced Verdicts:** Supported, Mostly supported, Mixed/unclear, Unsupported, Misleading context, Unable to verify.
- **Evidence Breakdown:** Corroborating and conflicting source links, confidence scoring, system limitations, and checked timestamps.

### 4. Background Audio & TTS Overviews
- **AI Spoken Overview:** Generates concise audio summaries of articles in the selected translation.
- **Native Android Foreground Service:** `NewsAudioPlaybackService` with persistent Media Notification and lock-screen controls (Play / Pause / Stop).
- **Background Playback:** Audio continues playing when the user leaves the app, switches tasks, or locks the phone.
- **Rate Controls:** 0.75x, 1x, 1.25x, 1.5x, 2x playback speed adjustments.

### 5. Document & PDF Opener
- **Built-in Reader:** Inspects PDFs, whitepapers, and text documents.
- **Page Browsing & Text Stream:** Formatted monospace reading view.
- **AI Integration:** Instant "Summarize Document with AI" and "Fact-Check Claims" buttons that extract and feed text directly into the AI and Verification engines.

### 6. Video Player with Picture-in-Picture (PiP)
- **Video News Bulletins & Live Stream:** Integrated video playback with seek and speed controls.
- **Native PiP:** Enters Android Picture-in-Picture mode (`enterPictureInPictureMode`) allowing users to watch news explainers while multitasking across apps.

### 7. Native Home-Screen Widget
- **AppWidgetProvider:** `IndosanhitaNewsWidgetProvider` with `news_widget_layout.xml`.
- **Display:** Shows top breaking headline, category badge, source, timestamp, and refresh button.
- **Deep Linking:** Tapping the widget opens the article in the app directly.

### 8. Reading Rewards & Coins Ledger
- **Server-Authoritative Ledger:** Room-backed immutable transactions with idempotency keys preventing replay duplication.
- **Earning Rules:** +5 coins for reading an article, +10 coins for verifying a claim, +15 coins for daily streaks. Daily cap enforced at 50 coins.
- **AI Concept Illustrator:** Uses 10 coins with reservation, commit, and refund safety safeguards.

### 9. Secure Owner Console & Authentication
- **Role Enforcement:** Reader vs. Owner permissions.
- **Owner Identity Reference:** `Searkkk@gmail.com`.
- **Security:** Owner access requires cryptographically validated owner passkey (`INDO_OWNER_SECURE_2026`). No client secrets are exposed.
- **Console Features:** Real-time service health, provider status, AI model usage, feature toggles, and audit logs.
- **Email OTP Flow:** Single-use, short expiry, resend cooldown, and attempt limits.

---

## 🎨 Visual Identity & Brand System

- **Brand Name:** INDOSANHITA Global News
- **Wordmark:** INDO near-black/navy (`#0B1220` / `#111827`), SANHITA blue (`#2563EB` / `#60A5FA`).
- **Accent:** Electric cyan (`#0EA5E9` / `#38BDF8`).
- **Tagline:** ASCEND WITH EVERY STORY.
- **Themes:** Complete Light, Dark, and System modes with consistent contrast across all routes, cards, dialogs, and widgets.

---

## 🚀 Building & Testing

```bash
# Compile debug APK
gradle assembleDebug

# Run Unit & Robolectric Tests
gradle :app:testDebugUnitTest
```
