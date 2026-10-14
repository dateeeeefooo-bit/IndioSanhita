package com.example.data.repository

import kotlinx.coroutines.delay

data class SupportedLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String
)

data class TranslationResult(
    val originalText: String,
    val translatedText: String,
    val targetLanguageCode: String,
    val isTranslated: Boolean
)

class TranslationRepository {

    val supportedLanguages = listOf(
        SupportedLanguage("en", "English", "English"),
        SupportedLanguage("hi", "Hindi", "हिन्दी"),
        SupportedLanguage("bn", "Bengali", "বাংলা"),
        SupportedLanguage("ta", "Tamil", "தமிழ்"),
        SupportedLanguage("te", "Telugu", "తెలుగు"),
        SupportedLanguage("mr", "Marathi", "मराठी"),
        SupportedLanguage("es", "Spanish", "Español"),
        SupportedLanguage("fr", "French", "Français"),
        SupportedLanguage("de", "German", "Deutsch"),
        SupportedLanguage("ar", "Arabic", "العربية"),
        SupportedLanguage("ja", "Japanese", "日本語")
    )

    private val memoryCache = mutableMapOf<String, String>()

    suspend fun translateText(
        text: String,
        targetLang: String
    ): TranslationResult {
        if (targetLang == "en" || text.isBlank()) {
            return TranslationResult(text, text, targetLang, isTranslated = false)
        }

        val cacheKey = "${text.hashCode()}_$targetLang"
        memoryCache[cacheKey]?.let { cached ->
            return TranslationResult(text, cached, targetLang, isTranslated = true)
        }

        delay(300) // Realistic translation latency

        val translated = when (targetLang) {
            "hi" -> translateToHindi(text)
            "es" -> translateToSpanish(text)
            "fr" -> translateToFrench(text)
            "de" -> translateToGerman(text)
            "bn" -> translateToBengali(text)
            "ta" -> translateToTamil(text)
            "te" -> translateToTelugu(text)
            "mr" -> translateToMarathi(text)
            else -> "[Translated to $targetLang] $text"
        }

        memoryCache[cacheKey] = translated
        return TranslationResult(text, translated, targetLang, isTranslated = true)
    }

    private fun translateToHindi(text: String): String {
        return when {
            text.contains("Global Clean Energy Pact") ->
                "नई दिल्ली में 85 देशों द्वारा वैश्विक स्वच्छ ऊर्जा समझौते पर हस्ताक्षर किए गए"
            text.contains("ISRO & NASA") ->
                "इसरो और नासा ने संयुक्त निसार पृथ्वी अवलोकन प्रसंस्करण वास्तुकला को आगे बढ़ाया"
            text.contains("Next-Generation On-Device Multimodal AI") ->
                "अल्ट्रा-लो लेटेंसी के साथ अगली पीढ़ी के ऑन-डिवाइस मल्टीमॉडल एआई मॉडल की घोषणा"
            text.contains("Global Trade Logistics") ->
                "वैश्विक व्यापार रसद उच्च दक्षता वाले मल्टीमॉडल रेल फ्रेट कॉरिडोर की ओर मुड़ा"
            text.contains("CRISPR Epigenetic Therapy") ->
                "CRISPR एपिजेनेटिक थेरेपी ने चरण III परीक्षणों में पुरानी सूजन के मार्करों को उलट दिया"
            text.contains("Explainers: How Verification Engines") ->
                "स्पष्टीकरण: सत्यापन इंजन कैसे दुष्प्रचार को सत्यापित समाचार से अलग करते हैं"
            text.contains("World Athletics Championship") ->
                "विश्व एथलेटिक्स चैम्पियनशिप ने अनुकूलनीय वास्तविक समय बायोमेट्रिक ट्रैकिंग पेश की"
            text.contains("James Webb Space Telescope") ->
                "जेम्स वेब स्पेस टेलीस्कोप ने एक्सोप्लैनेट K2-18b पर मीथेन और ओजोन की पहचान की"
            text.contains("Delegates conclude a landmark multilateral agreement") ->
                "प्रतिनिधियों ने 2035 तक एकीकृत नवीकरणीय ग्रिड इंटरकनेक्शन मानकों और संप्रभु हरित क्रेडिट गारंटी की स्थापना करते हुए एक ऐतिहासिक बहुपक्षीय समझौते को अंतिम रूप दिया।"
            text.contains("Key Takeaway:") ->
                "मुख्य निष्कर्ष: नई दिल्ली समझौते पर 85 देशों ने हस्ताक्षर किए, $120B की क्रेडिट गारंटी जुटाई और 2035 तक स्वच्छ ग्रिड इंटरकनेक्टिविटी स्थापित की।"
            else -> "हिन्दी अनुवाद: $text"
        }
    }

    private fun translateToSpanish(text: String): String {
        return when {
            text.contains("Global Clean Energy Pact") ->
                "Pacto mundial de energía limpia firmado por 85 naciones en Nueva Delhi"
            text.contains("ISRO & NASA") ->
                "ISRO y NASA avanzan en la arquitectura conjunta de procesamiento de radar NISAR"
            text.contains("Next-Generation On-Device Multimodal AI") ->
                "Modelos de IA multimodal en dispositivo de próxima generación anunciados con latencia ultra baja"
            else -> "[Español] $text"
        }
    }

    private fun translateToFrench(text: String): String {
        return when {
            text.contains("Global Clean Energy Pact") ->
                "Pacte mondial pour l'énergie propre signé par 85 nations à New Delhi"
            else -> "[Français] $text"
        }
    }

    private fun translateToGerman(text: String): String {
        return when {
            text.contains("Global Clean Energy Pact") ->
                "Globales Abkommen für saubere Energie von 85 Nationen in Neu-Delhi unterzeichnet"
            else -> "[Deutsch] $text"
        }
    }

    private fun translateToBengali(text: String): String {
        return when {
            text.contains("Global Clean Energy Pact") ->
                "নয়াদিল্লিতে ৮৫টি দেশ বৈশ্বিক পরিচ্ছন্ন শক্তি চুক্তিতে স্বাক্ষর করেছে"
            else -> "[বাংলা] $text"
        }
    }

    private fun translateToTamil(text: String): String {
        return when {
            text.contains("Global Clean Energy Pact") ->
                "புது தில்லியில் 85 நாடுகள் உலகளாவிய தூய எரிசக்தி ஒப்பந்தத்தில் கையெழுத்திட்டன"
            else -> "[தமிழ்] $text"
        }
    }

    private fun translateToTelugu(text: String): String {
        return when {
            text.contains("Global Clean Energy Pact") ->
                "న్యూఢిల్లీలో 85 దేశాలు గ్లోబల్ క్లీన్ ఎనర్జీ ఒప్పందంపై సంతకం చేశాయి"
            else -> "[తెలుగు] $text"
        }
    }

    private fun translateToMarathi(text: String): String {
        return when {
            text.contains("Global Clean Energy Pact") ->
                "नवी दिल्लीत ८५ देशांनी जागतिक स्वच्छ ऊर्जा करारावर स्वाक्षरी केली"
            else -> "[मराठी] $text"
        }
    }
}
