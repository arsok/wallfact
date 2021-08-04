package app.wallfact.integration.pixabay.impl

import app.wallfact.integration.pixabay.api.PixabayRepoApi

class PixabayCachedRepo : PixabayRepoApi {
    override suspend fun getImages(count: Int, query: String) {
        TODO("get image from our db, if missing, go to real repo")
    }

    override suspend fun getImage(query: String) {
        if (query.isBlank()) return getRandomImage()
    }

    override suspend fun getRandomImage() {
        TODO("Not yet implemented")
    }
}