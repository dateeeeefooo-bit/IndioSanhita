package com.example.data.repository

import com.example.data.db.AppDao
import com.example.data.db.SavedArticleEntity
import com.example.data.model.NewsArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewsRepository(private val appDao: AppDao) {

    private val masterArticles = listOf(
        NewsArticle(
            id = "art-101",
            title = "Global Clean Energy Pact Signed by 85 Nations in New Delhi",
            summary = "Delegates conclude a landmark multilateral agreement setting unified renewable grid interconnection standards and sovereign green credit guarantees through 2035.",
            content = "NEW DELHI — In a historic milestone for international environmental diplomacy, representatives from 85 countries concluded the International Renewable Grid Summit today by signing the New Delhi Clean Energy Accord.\n\nThe framework establishes synchronized cross-border grid protocols, accelerates solar and offshore wind financing mechanisms, and guarantees $120 billion in pooled credit enhancements for developing nation micro-grids.\n\nEnergy ministers emphasized that the pact addresses transmission bottlenecks while prioritizing equitable energy access across South Asia, Africa, and Latin America. Implementation oversight will be coordinated by a joint technical secretariat with quarterly public compliance audits.",
            category = "Top Stories",
            sourceName = "INDOSANHITA Wire Service",
            sourceDomain = "indosanhita.com",
            sourceUrl = "https://indosanhita.com/world/clean-energy-accord-2026",
            publishedAt = System.currentTimeMillis() - 1000 * 60 * 35, // 35m ago
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 10,
            imageUrl = "https://images.unsplash.com/photo-1509391365360-2e959784a276?w=800&auto=format&fit=crop&q=80",
            imageCredit = "INDOSANHITA / Creative Commons / Clean Energy Forum",
            readingTimeMinutes = 3,
            language = "en",
            aiOverview = "Key Takeaway: 85 nations signed the New Delhi Accord, mobilizing $120B in credit backing and establishing standardized clean grid interconnectivity by 2035.",
            whatIsClear = listOf(
                "85 nations ratified the final summit framework document.",
                "$120 billion in sovereign credit guarantees allocated for developing economies.",
                "Grid synchronization standards take effect across participating transmission corridors by Q3 2027."
            ),
            whatRemainsUnclear = listOf(
                "Final dispute arbitration mechanism between private consortiums and state grid operators.",
                "Exact carbon tariff equalization timeline for non-signatory trade flows."
            ),
            relatedStories = listOf(
                "Solar manufacturing capacity surges 42% across Southeast Asia",
                "High-voltage DC lines: How intercontinental power transfer works"
            ),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        ),
        NewsArticle(
            id = "art-102",
            title = "ISRO & NASA Advance Joint NISAR Earth Observation Processing Architecture",
            summary = "Space agencies validate dual-frequency synthetic aperture radar calibration routines ahead of expanded climate and seismic displacement data sharing.",
            content = "BENGALURU / PASADENA — Engineers from the Indian Space Research Organisation (ISRO) and NASA's Jet Propulsion Laboratory have completed phase-two verification of the NISAR (NASA-ISRO Synthetic Aperture Radar) downlink pipeline.\n\nThe dual L-band and S-band radar system provides unprecedented centimeter-scale resolution for tracking tectonic deformation, glacier retreat, and soil moisture changes globally every 12 days.\n\nData streams will be distributed openly through synchronized regional earth-science nodes, giving agronomists and disaster response teams real-time predictive hazard modeling.",
            category = "India",
            sourceName = "Press Trust of India / Space Desk",
            sourceDomain = "isro.gov.in",
            sourceUrl = "https://isro.gov.in/missions/nisar-update-2026",
            publishedAt = System.currentTimeMillis() - 1000 * 60 * 85, // 1h 25m ago
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 20,
            imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&auto=format&fit=crop&q=80",
            imageCredit = "ISRO-NASA Joint Science Team",
            readingTimeMinutes = 4,
            language = "en",
            aiOverview = "Key Takeaway: The joint NISAR Earth observation data pipeline achieved verification, delivering open 12-day radar scans of Earth's surface for disaster monitoring.",
            whatIsClear = listOf(
                "Both L-band (NASA) and S-band (ISRO) radar processing pipelines are fully calibrated.",
                "Data products will be publicly accessible to research institutions and humanitarian agencies."
            ),
            whatRemainsUnclear = listOf(
                "Scheduled commercial constellation integration dates for marine traffic monitoring."
            ),
            relatedStories = listOf(
                "Next-gen launch vehicles target monthly orbital deployment rhythm",
                "Earth's cryosphere: What dual radar frequencies reveal underneath ice sheets"
            ),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        ),
        NewsArticle(
            id = "art-103",
            title = "Next-Generation On-Device Multimodal AI Models Announced with Ultra-Low Latency",
            summary = "Semiconductor and AI labs introduce 4-bit quantized neural weights that operate complex contextual reasoning entirely within mobile edge silicon.",
            content = "SAN FRANCISCO / TOKYO — Breakthroughs in sub-byte weight quantization and sparse attention architectures have enabled high-accuracy multimodal foundation models to execute on standard consumer smartphones without cloud network offloading.\n\nDemonstrated benchmark tests showed response latency below 80 milliseconds for image-claim analysis and continuous live translation, all while consuming 65% less battery power compared to prior generation cloud-tethered assistants.\n\nPrivacy advocates praised the architectural shift, noting that sensitive audio and document processing remains strictly confined to on-device secure enclaves.",
            category = "Technology",
            sourceName = "Global Tech Review",
            sourceDomain = "techreview.org",
            sourceUrl = "https://techreview.org/mobile-silicon-ai-2026",
            publishedAt = System.currentTimeMillis() - 1000 * 60 * 140,
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 60,
            imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&auto=format&fit=crop&q=80",
            imageCredit = "Neural Hardware Consortium",
            readingTimeMinutes = 3,
            language = "en",
            aiOverview = "Key Takeaway: 4-bit edge multimodal AI executes within 80ms on mobile silicon, drastically cutting latency and power while protecting user data privacy on-device.",
            whatIsClear = listOf(
                "Quantization techniques enable full local parameter execution under 4GB RAM footprint.",
                "Zero data transmission required for core speech transcription and OCR validation."
            ),
            whatRemainsUnclear = listOf(
                "Licensing terms for independent open-source developers versus hardware OEMs."
            ),
            relatedStories = listOf(
                "How NPU silicon differs from conventional GPUs in edge mobile devices",
                "The evolution of offline privacy-first AI assistants"
            )
        ),
        NewsArticle(
            id = "art-104",
            title = "Global Trade Logistics Pivot Toward High-Efficiency Multimodal Rail Freight Corridors",
            summary = "Intercontinental freight volumes shift toward electrified railway arteries connecting Central Asia, Europe, and maritime shipping terminals.",
            content = "VIENNA / SINGAPORE — Commercial cargo operators reported a 28% year-over-year surge in intermodal container freight moving through dedicated electrified rail networks.\n\nShorter transit times—averaging 14 days between East Asia and European hubs compared to 36 days via marine sea routes—along with automated customs clearance protocols have spurred manufacturing supply chains to diversify away from maritime choke points.\n\nPort authorities in Rotterdam and Mumbai are accelerating terminal rail integration to handle dual-stacked freight trains seamlessly.",
            category = "Business",
            sourceName = "Financial Times & Commerce Wire",
            sourceDomain = "ftcommerce.com",
            sourceUrl = "https://ftcommerce.com/logistics-rail-2026",
            publishedAt = System.currentTimeMillis() - 1000 * 60 * 210,
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 90,
            imageUrl = "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=800&auto=format&fit=crop&q=80",
            imageCredit = "Intermodal Freight Federation",
            readingTimeMinutes = 4,
            language = "en",
            aiOverview = "Key Takeaway: Electrified rail corridors saw a 28% jump in freight cargo, halving traditional sea transit times between major industrial hubs.",
            whatIsClear = listOf(
                "Average transit times slashed to 14 days.",
                "Customs automation platforms reduced border inspection delays by 70%."
            ),
            whatRemainsUnclear = listOf(
                "Standardization of track gauges across legacy Eastern European rail segments."
            ),
            relatedStories = listOf(
                "Digital freight bills of lading achieve worldwide legal recognition",
                "Decarbonizing heavy transport: Hydrogen vs Battery electric locomotives"
            )
        ),
        NewsArticle(
            id = "art-105",
            title = "CRISPR Epigenetic Therapy Reverses Chronic Inflammatory Markers in Phase III Trials",
            summary = "Clinical researchers present verified multi-center data demonstrating targeted gene silencing without altering primary DNA genomic sequences.",
            content = "GENEVA — In clinical trials across 14 hospital centers, a novel epigenetic medicine designed to temporarily down-regulate cytokine hyperactivity showed an 82% remission rate in refractory autoimmune conditions.\n\nUnlike traditional gene-editing treatments that introduce double-stranded DNA breaks, this epigenetic approach applies chemical methylation caps that can be adjusted or reversed, significantly mitigating off-target risk profiles.\n\nRegulatory authorities in five nations have granted priority review status following safety monitoring that reported zero genomic rearrangements.",
            category = "Health",
            sourceName = "Medical Science Chronicle",
            sourceDomain = "medchronicle.org",
            sourceUrl = "https://medchronicle.org/crispr-epigenetic-2026",
            publishedAt = System.currentTimeMillis() - 1000 * 60 * 300,
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 120,
            imageUrl = "https://images.unsplash.com/photo-1532187863486-abf9dbad1b69?w=800&auto=format&fit=crop&q=80",
            imageCredit = "Gene Regulation Laboratory / WHO Bioethics",
            readingTimeMinutes = 5,
            language = "en",
            aiOverview = "Key Takeaway: Phase III epigenetic therapy achieved an 82% remission rate without altering primary DNA codes, opening safer therapies for autoimmune disorders.",
            whatIsClear = listOf(
                "Zero off-target genomic cleavage observed in 1,200 patient cohort.",
                "Therapy relies on reversible methylation instead of permanent gene cutting."
            ),
            whatRemainsUnclear = listOf(
                "Duration of remission beyond the initial 36-month monitoring window."
            ),
            relatedStories = listOf(
                "How epigenetic switches regulate cellular aging and regeneration",
                "Global health access initiatives for advanced cellular medicines"
            )
        ),
        NewsArticle(
            id = "art-106",
            title = "Explainers: How Verification Engines Separate Disinformation from Verified News",
            summary = "A deep look into multi-perspective cross-referencing, cryptographic content provenance, and real-time citation analysis.",
            content = "In an era of rapid information diffusion and synthetic media generation, newsrooms and readers require rigorous verification standards.\n\nModern verification methodologies rely on three core pillars:\n1. Cryptographic Provenance: Verifying C2PA content credentials embedded at the sensor level to confirm capture location and camera settings.\n2. Multi-Perspective Corroboration: Comparing primary reporting against regional and independent on-the-ground journalists rather than algorithmic popularity.\n3. Transparent Uncertainty Reporting: Clearly communicating what remains unproven rather than forcing an authoritative binary verdict.\n\nINDOSANHITA Global News applies these principles directly in its verification engine to ensure readers ascend with truth and clarity.",
            category = "Explainers",
            sourceName = "INDOSANHITA Research Desk",
            sourceDomain = "indosanhita.com",
            sourceUrl = "https://indosanhita.com/explainers/how-verification-works",
            publishedAt = System.currentTimeMillis() - 1000 * 60 * 420,
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 180,
            imageUrl = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=800&auto=format&fit=crop&q=80",
            imageCredit = "INDOSANHITA Standards & Fact Check Unit",
            readingTimeMinutes = 4,
            language = "en",
            aiOverview = "Key Takeaway: Verification requires cryptographic sensor provenance, diverse multi-source corroboration, and honest uncertainty reporting.",
            whatIsClear = listOf(
                "Sensor-level C2PA credentials provide tamper-evident media origin.",
                "Binary true/false models are insufficient for complex geopolitical reporting."
            ),
            whatRemainsUnclear = listOf(
                "Adoption rate of provenance standards among older camera models and legacy devices."
            ),
            relatedStories = listOf(
                "Detecting synthetic voice clones: Acoustic and temporal spectrogram analysis",
                "Open source intelligence (OSINT) tools for citizen researchers"
            )
        ),
        NewsArticle(
            id = "art-107",
            title = "World Athletics Championship Introduces Adaptive Real-Time Biometric Tracking",
            summary = "Sensory telemetry tracks hydration, core temperature, and stride kinematics to optimize runner safety during endurance marathon events.",
            content = "DOHA / NAIROBI — The World Athletics Federation has ratified the use of non-invasive epidermal sensors during long-distance road races.\n\nMedical teams stationed along marathon routes received instantaneous alerts regarding athletes exhibiting critical heat strain index spikes, preventing severe dehydration episodes before symptomatic collapse.\n\nAthletes also gained post-race telemetry indicating biomechanical asymmetries, enabling tailored physical therapy and injury prevention programs.",
            category = "Sports",
            sourceName = "Global Sports Bureau",
            sourceDomain = "worldathletics.org",
            sourceUrl = "https://worldathletics.org/biometrics-marathon-2026",
            publishedAt = System.currentTimeMillis() - 1000 * 60 * 500,
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 200,
            imageUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=800&auto=format&fit=crop&q=80",
            imageCredit = "Sports Medicine & Telemetry Board",
            readingTimeMinutes = 3,
            language = "en",
            aiOverview = "Key Takeaway: Epidermal telemetry sensors kept runners safe from heat strain during endurance marathons while delivering rich kinematic analytics.",
            whatIsClear = listOf(
                "Non-invasive sensor deployment approved for all official road race categories.",
                "Zero athlete collapses occurred during the initial test marathon."
            ),
            whatRemainsUnclear = listOf(
                "Guidelines on whether live biometric streams can be incorporated into broadcast graphics."
            ),
            relatedStories = listOf(
                "Carbon-plated shoe evolution and physiological impact on joints",
                "High-altitude training facilities and red blood cell adaptations"
            )
        ),
        NewsArticle(
            id = "art-108",
            title = "James Webb Space Telescope Identifies Atmospheric Methane and Ozone on Exoplanet K2-18b",
            summary = "Spectroscopic transmission readings confirm photochemical signatures indicative of water ocean vapor and volatile atmospheric dynamics.",
            content = "BALTIMORE — The Space Telescope Science Institute revealed high-significance transmission spectroscopy data captured across five separate orbital transits of exoplanet K2-18b.\n\nThe observations identified consistent absorption features corresponding to methane, carbon dioxide, and ozone-analog photolytic molecules in the planet's upper envelope.\n\nAstronomers stressed that while these chemical imbalances resemble terrestrial organic cycles, non-biological geologic equilibrium models are currently being evaluated with upcoming deep spectroscopic observations.",
            category = "Science",
            sourceName = "Astrophysical Journal Press",
            sourceDomain = "nasa.gov",
            sourceUrl = "https://nasa.gov/webb/k2-18b-atmospheric-analysis",
            publishedAt = System.currentTimeMillis() - 1000 * 60 * 620,
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 300,
            imageUrl = "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=800&auto=format&fit=crop&q=80",
            imageCredit = "NASA / ESA / CSA / STScI Spectroscopy Team",
            readingTimeMinutes = 5,
            language = "en",
            aiOverview = "Key Takeaway: JWST detected strong methane and ozone-analog spectroscopic lines on exoplanet K2-18b, prompting rigorous planetary modeling.",
            whatIsClear = listOf(
                "Methane and carbon dioxide absorption peaks confirmed at greater than 5-sigma certainty.",
                "Sub-Neptune Hycean atmospheric model supported by transmission metrics."
            ),
            whatRemainsUnclear = listOf(
                "Definitive confirmation of dimethyl sulfide (DMS) trace components."
            ),
            relatedStories = listOf(
                "Next space observatories aimed specifically at direct habitable zone imaging",
                "How stellar flares from red dwarf stars impact exoplanet atmospheres"
            )
        )
    )

    fun getArticles(category: String? = null): List<NewsArticle> {
        return if (category.isNullOrBlank() || category == "Top" || category == "Top Stories") {
            masterArticles
        } else {
            masterArticles.filter { it.category.equals(category, ignoreCase = true) }
        }
    }

    fun getArticleById(id: String): NewsArticle? {
        return masterArticles.find { it.id == id }
    }

    fun searchArticles(query: String, category: String? = null): List<NewsArticle> {
        val q = query.trim().lowercase()
        return masterArticles.filter { article ->
            val matchesQuery = q.isEmpty() ||
                article.title.lowercase().contains(q) ||
                article.summary.lowercase().contains(q) ||
                article.content.lowercase().contains(q) ||
                article.sourceName.lowercase().contains(q)

            val matchesCat = category == null || category == "All" ||
                article.category.equals(category, ignoreCase = true)

            matchesQuery && matchesCat
        }
    }

    suspend fun saveArticle(article: NewsArticle) {
        val entity = SavedArticleEntity(
            id = article.id,
            title = article.title,
            summary = article.summary,
            content = article.content,
            category = article.category,
            sourceName = article.sourceName,
            sourceDomain = article.sourceDomain,
            sourceUrl = article.sourceUrl,
            publishedAt = article.publishedAt,
            imageUrl = article.imageUrl
        )
        appDao.insertSavedArticle(entity)
    }

    suspend fun removeSavedArticle(id: String) {
        appDao.deleteSavedArticle(id)
    }

    suspend fun isArticleSaved(id: String): Boolean {
        return appDao.isArticleSaved(id)
    }

    fun getAllSavedArticles(): Flow<List<NewsArticle>> {
        return appDao.getAllSavedArticles().map { list ->
            list.map { entity ->
                NewsArticle(
                    id = entity.id,
                    title = entity.title,
                    summary = entity.summary,
                    content = entity.content,
                    category = entity.category,
                    sourceName = entity.sourceName,
                    sourceDomain = entity.sourceDomain,
                    sourceUrl = entity.sourceUrl,
                    publishedAt = entity.publishedAt,
                    updatedAt = entity.publishedAt,
                    imageUrl = entity.imageUrl,
                    imageCredit = "Saved Item",
                    readingTimeMinutes = 3,
                    isBookmarked = true
                )
            }
        }
    }
}
