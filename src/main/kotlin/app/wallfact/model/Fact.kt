package app.wallfact.model

import org.bson.codecs.pojo.annotations.BsonId

data class Fact(@BsonId val id: Int, val content: String, val ext_id: Int)