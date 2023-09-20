package app.pixle.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Answer(
    val item: List<Item>,
    val difficulty: String,
    val date: LocalDate
)