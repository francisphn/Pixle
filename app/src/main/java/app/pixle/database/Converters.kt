package app.pixle.database

import android.net.Uri
import androidx.room.TypeConverter
import java.time.LocalDate
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.let { uri.toString() }
    }

    @TypeConverter
    fun toUri(string: String?): Uri? {
        return string?.let { Uri.parse(string) }
    }

    @TypeConverter
    fun fromDate(date: LocalDate?): String? {
        return date?.let { date.toString() }
    }

    @TypeConverter
    fun toDate(string: String?): LocalDate? {
        return string?.let { LocalDate.parse(string) }
    }

    @TypeConverter
    fun fromUuid(uuid: UUID?): String? {
        return uuid?.let { uuid.toString() }
    }

    @TypeConverter
    fun toUuid(string: String?): UUID? {
        return string?.let { UUID.fromString(string) }
    }
}