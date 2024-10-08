package id.anison.database.util

import androidx.room.TypeConverter
import id.anison.database.model.DubersDbo
import id.anison.database.model.GenreDbo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromDubersList(dubers: DubersDbo): String {
        return Json.encodeToString(dubers)
    }

    @TypeConverter
    fun toDubersList(dubersString: String): DubersDbo {
        return Json.decodeFromString(dubersString)
    }


    @TypeConverter
    fun fromGenreList(genres: List<GenreDbo>): String {
        return Json.encodeToString(genres)
    }

    @TypeConverter
    fun toGenreList(genreString: String): List<GenreDbo> {
        return Json.decodeFromString(genreString)
    }

    @TypeConverter
    fun fromIntList(yearsList: List<Int>): String {
        return Json.encodeToString(yearsList)
    }

    @TypeConverter
    fun toIntList(yearsString: String): List<Int> {
        return Json.decodeFromString(yearsString)
    }

    //
    /*@TypeConverter
    fun fromListSDbo(value: List<SDbo>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toListSDbo(value: String): List<SDbo> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromListTDbo(value: List<TDbo>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toListTDbo(value: String): List<TDbo> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromListVDbo(value: List<VDbo>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toListVDbo(value: String): List<VDbo> {
        return Json.decodeFromString(value)
    }*/

    /*@TypeConverter
    fun fromListGenreDbo(value: List<GenreDbo>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toListGenreDbo(value: String): List<GenreDbo> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromListInt(value: List<Int>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toListInt(value: String): List<Int> {
        return Json.decodeFromString(value)
    }*/


    /*@TypeConverter
    fun fromList(value: List<T>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toList(value: String): List<T> {
        return Json.decodeFromString(value)
    }*/
}