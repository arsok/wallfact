package app.wallfact.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Fact(@BsonId val id: ObjectId, val content: String, val ext_id: Int)