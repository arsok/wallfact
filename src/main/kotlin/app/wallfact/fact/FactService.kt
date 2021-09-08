package app.wallfact.fact

import app.wallfact.model.Fact
import com.mongodb.client.model.Aggregates
import org.apache.commons.lang3.StringUtils.EMPTY
import org.litote.kmongo.coroutine.CoroutineDatabase

class FactService(private val database: CoroutineDatabase) {
    private val randomPipeline = listOf(Aggregates.sample(1))

    suspend fun getRandomFact(): String = database.getCollection<Fact>("facts")
        .aggregate<Fact>(randomPipeline)
        .first()
        ?.content
        ?: EMPTY
}