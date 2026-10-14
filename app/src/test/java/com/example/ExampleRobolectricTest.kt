package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.repository.NewsRepository
import com.example.data.repository.TranslationRepository
import com.example.data.repository.VerificationRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `verify brand app name string`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("INDOSANHITA", appName)
    }

    @Test
    fun `verify news repository categories and articles`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val repo = NewsRepository(db.appDao())

        val topStories = repo.getArticles("Top Stories")
        assertTrue(topStories.isNotEmpty())
        assertEquals("Top Stories", topStories.first().category)

        val techStories = repo.getArticles("Technology")
        assertTrue(techStories.isNotEmpty())
        assertEquals("Technology", techStories.first().category)
    }

    @Test
    fun `verify claim verification engine returns structured verdict`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val verifyRepo = VerificationRepository(db.appDao())

        val report = verifyRepo.verifyClaim("85 nations signed clean energy accord", "text")
        assertNotNull(report)
        assertEquals("Supported", report.verdict)
        assertTrue(report.evidenceFor.isNotEmpty())
    }

    @Test
    fun `verify translation dictionary functionality`() = runBlocking {
        val transRepo = TranslationRepository()
        val result = transRepo.translateText("Global Clean Energy Pact Signed by 85 Nations in New Delhi", "hi")
        assertTrue(result.isTranslated)
        assertTrue(result.translatedText.contains("नई दिल्ली"))
    }

    @Test
    fun `verify owner login accepts Searkkk and Krieshna_14`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val authRepo = com.example.data.repository.AuthAndOwnerRepository(db.appDao())

        val result = authRepo.loginAsOwner("Searkkk@gmail.com", "Krieshna_14")
        assertTrue("Owner login should succeed with Searkkk@gmail.com and Krieshna_14", result.isSuccess)
        val profile = result.getOrNull()
        assertNotNull(profile)
        assertEquals(com.example.data.model.UserRole.OWNER, profile?.role)
        assertEquals("Searkkk@gmail.com", profile?.email)
    }

    @Test
    fun `verify reader signup generates OTP and verifies successfully`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val authRepo = com.example.data.repository.AuthAndOwnerRepository(db.appDao())

        val otpRequest = authRepo.requestEmailOtp("newreader@indosanhita.com")
        assertTrue(otpRequest.isSuccess)

        val verifyResult = authRepo.verifyEmailOtp("newreader@indosanhita.com", "123456")
        assertTrue("OTP verification should succeed with code or demo 123456", verifyResult.isSuccess)
        val user = verifyResult.getOrNull()
        assertNotNull(user)
        assertEquals("newreader@indosanhita.com", user?.email)
        assertEquals(com.example.data.model.UserRole.READER, user?.role)
    }
}
