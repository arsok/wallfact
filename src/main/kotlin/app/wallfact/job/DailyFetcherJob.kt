package app.wallfact.job

interface DailyFetcherJob {
    suspend fun fetch()
}