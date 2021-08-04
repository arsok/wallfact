package app.wallfact.job.fact

import app.wallfact.job.DailyFetcherJob

class FactDailyFetcherJob: DailyFetcherJob {
    override fun fetch() {
        TODO("If we have less than, for example, 10_000 facts, go to fact provider & fetch another 10_000 to db")
    }
}