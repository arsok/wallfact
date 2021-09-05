package app.wallfact.fact

import app.wallfact.model.Fact
import com.mongodb.client.model.Aggregates
import org.apache.commons.lang3.StringUtils.EMPTY
import org.litote.kmongo.coroutine.CoroutineDatabase

class FactService(private val database: CoroutineDatabase) {

    suspend fun getRandomFact(): String {
        return database.getCollection<Fact>("facts")
            .aggregate<Fact>(listOf(Aggregates.sample(1)))
            .first()
            ?.content
            ?: EMPTY
    }
}