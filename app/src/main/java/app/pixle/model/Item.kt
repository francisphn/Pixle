package app.pixle.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Item(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("icon")
    val icon: String,

    @field:SerializedName("category")
    val category: String,

    @field:SerializedName("difficulty")
    val difficulty: Long
)