package app.wallfact.fact

import app.wallfact.model.Fact
import com.mongodb.client.model.Aggregates
import org.apache.commons.lang3.StringUtils.EMPTY
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.LoggerFactory

class FactService(private val database: CoroutineDatabase) {
    private val log = LoggerFactory.getLogger(this::javaClass.get())

    suspend fun getRandomFact(): String {
        log.info("Retrieving random fact from database")

        return database.getCollection<Fact>("facts")
            .aggregate<Fact>(listOf(Aggregates.sample(1)))
            .first()
            ?.content
            ?: EMPTY
    }
}