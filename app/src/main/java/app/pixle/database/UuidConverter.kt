package app.pixle.database

import androidx.room.TypeConverter
import java.util.UUID

class UuidConverter {
    @TypeConverter
    fun uuidToString(uuid: UUID ): String  {
        return uuid .toString()
    }

    @TypeConverter
    fun stringToUuid(string: String ): UUID  {
        return string .let { UUID.fromString(string) }
    }
}