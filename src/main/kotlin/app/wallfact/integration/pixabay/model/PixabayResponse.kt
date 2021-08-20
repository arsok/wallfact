package app.wallfact.integration.pixabay.model

import kotlinx.serialization.Serializable

@Serializable
data class PixabayResponse(val total: Int, val totalHits: Int, val hits: Set<Hit>)