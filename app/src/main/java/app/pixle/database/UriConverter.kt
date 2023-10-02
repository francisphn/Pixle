package app.pixle.database

import android.net.Uri
import androidx.room.TypeConverter

class UriConverter {
    @TypeConverter
    fun uriToString(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun stringToUri(string: String): Uri {
        return string.let { Uri.parse(string) }
    }


}