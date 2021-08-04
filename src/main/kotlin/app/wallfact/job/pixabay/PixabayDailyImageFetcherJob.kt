package app.wallfact.job.pixabay

import app.wallfact.job.DailyFetcherJob

class PixabayDailyImageFetcherJob: DailyFetcherJob {
    override fun fetch() {
        TODO("Every day we fetch fixed amount of wallpapers to db") // think about cleanup of old wallpapers
    }
}