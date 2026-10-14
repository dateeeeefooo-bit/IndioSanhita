package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun isRealKeyConfigured(): Boolean {
        val key = getApiKey()
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY" && !key.startsWith("YOUR_")
    }

    fun getApiKey(): String {
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            (field.get(null) as? String) ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    /**
     * Executes real-time live content generation using Gemini 3.5 Flash
     */
    suspend fun generateContent(
        prompt: String,
        systemInstruction: String? = null
    ): String? = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No valid live Gemini API key detected, using grounded intelligence engine.")
            return@withContext null
        }

        try {
            val endpoint = "$BASE_URL/$MODEL:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                if (!systemInstruction.isNullOrBlank()) {
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", systemInstruction) })
                        })
                    })
                }

                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                    put("topK", 40)
                })
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                Log.w(TAG, "Gemini API HTTP ${response.code}: $errBody")
                return@withContext null
            }

            val respBody = response.body?.string() ?: return@withContext null
            val respJson = JSONObject(respBody)
            val candidates = respJson.optJSONArray("candidates") ?: return@withContext null
            if (candidates.length() == 0) return@withContext null

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return@withContext null
            val parts = content.optJSONArray("parts") ?: return@withContext null
            if (parts.length() == 0) return@withContext null

            val text = parts.getJSONObject(0).optString("text")
            if (text.isNotBlank()) text else null
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API call failed", e)
            null
        }
    }
}
