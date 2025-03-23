package id.anison.database.util

import androidx.room.TypeConverter
import id.anison.database.anime.entity.AnimeDbo
import id.anison.database.filter.DubersDbo
import id.anison.database.genre.GenreDbo
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromDubers(dubers: DubersDbo): String = Json.encodeToString(dubers)

    @TypeConverter
    fun toDubers(dubersString: String): DubersDbo = Json.decodeFromString(dubersString)

    @TypeConverter
    fun fromGenres(genres: List<GenreDbo>): String = Json.encodeToString(genres)

    @TypeConverter
    fun toGenres(genreString: String): List<GenreDbo> = Json.decodeFromString(genreString)

    @TypeConverter
    fun fromYears(yearsList: List<Short>): String = Json.encodeToString(yearsList)

    @TypeConverter
    fun toYears(yearsString: String): List<Short> = Json.decodeFromString(yearsString)

    @TypeConverter
    fun fromGroupF(groupF: List<AnimeDbo>): String = Json.encodeToString(groupF)

    @TypeConverter
    fun toGroupF(groupFString: String): List<AnimeDbo> = Json.decodeFromString(groupFString)

    @TypeConverter
    fun fromScreenF(screenF: List<List<String>>): String = Json.encodeToString(screenF)

    @TypeConverter
    fun toScreenF(screenFString: String): List<List<String>> = Json.decodeFromString(screenFString)
}