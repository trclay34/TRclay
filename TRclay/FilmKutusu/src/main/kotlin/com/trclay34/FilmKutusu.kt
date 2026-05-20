package com.trclay34

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class FilmKutusu : MainAPI() {
    override var mainUrl = "https://filmkutusu.com.tr"
    override var name = "FilmKutusu"
    override var lang = "tr"
    override val hasMainPage = true
    override val hasSearch = true
    override val supportedTypes = setOf(TvType.Movie)

    override val mainPage = mainPageOf(
        "$mainUrl/kategori/aksiyon/" to "Aksiyon",
        "$mainUrl/kategori/komedi/" to "Komedi",
        "$mainUrl/kategori/dram/" to "Dram",
        "$mainUrl/kategori/gerilim/" to "Gerilim",
        "$mainUrl/kategori/korku/" to "Korku",
        "$mainUrl/kategori/bilim-kurgu/" to "Bilim Kurgu",
        "$mainUrl/kategori/animasyon/" to "Animasyon",
    )

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("h4")?.text()
            ?.replace(Regex("\\d{4} izle"), "")?.trim() ?: return null
        val href = this.selectFirst("a")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("img")?.attr("src")
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = if (page == 1) request.data else "${request.data}page/$page/"
        val document = app.get(url).document
        val home = document.select("article").mapNotNull { it.toSearchResult() }
        return newHomePageResponse(request.name, home)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("$mainUrl/?s=$query").document
        return document.select("article").mapNotNull { it.toSearchResult() }
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.selectFirst("h1.entry-title, h1, .film-title")?.text()
            ?.replace(Regex("\\d{4} izle", RegexOption.IGNORE_CASE), "")?.trim() ?: "Film"
        val poster = document.selectFirst(".poster img, .film-poster img, article img")?.attr("src")
        val plot = document.selectFirst(".entry-content p, .description, .ozet")?.text()
        val year = Regex("(\\d{4})").find(document.title())?.value?.toIntOrNull()

        val videoUrl = document.selectFirst(
            "iframe[src*=player], iframe[src*=video], iframe[data-src]"
        )?.let {
            it.attr("src").ifEmpty { it.attr("data-src") }
        } ?: ""

        return newMovieLoadResponse(title, url, TvType.Movie, videoUrl) {
            this.posterUrl = poster
            this.plot = plot
            this.year = year
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        if (data.isEmpty()) return false
        loadExtractor(data, mainUrl, subtitleCallback, callback)
        return true
    }
}
