apply plugin: "com.android.library"
apply plugin: "kotlin-android"
apply plugin: "com.lagradost.cloudstream3.gradle"

cloudstream {
    setRepo(System.getenv("GITHUB_REPOSITORY") ?: "trclay34/TRclay")
    authors = listOf("trclay34")
    description = "FilmKutusu.com.tr Eklentisi"
    language = "tr"
    tvTypes = listOf("Movie")
    iconUrl = "https://filmkutusu.com.tr/wp-content/uploads/2026/03/filmkutusu-logo.png"
}
