package com.trclay34

import android.content.Context
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import com.lagradost.cloudstream3.APIHolder

@CloudstreamPlugin
class FilmKutusuPlugin: Plugin() {
    override fun load(context: Context) {
        APIHolder.allProviders.add(FilmKutusu())
    }
}
