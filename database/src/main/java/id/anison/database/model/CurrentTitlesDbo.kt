package id.anison.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currentTitles")
class CurrentTitlesDbo (
    @PrimaryKey val id: Long,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("is_current") val is_current: Boolean,
    @ColumnInfo("en_jp_name") val en_jp_name: String,
    @ColumnInfo("dubed_episodes") val dubed_episodes: Int,
    @ColumnInfo("episodes_custom") val episodes_custom: String?,
    @ColumnInfo("episodes_total") val episodes_total: String,
    @ColumnInfo("extra_total") val extra_total:Int,
    @ColumnInfo("ova_total") val ova_total:Int,
    @ColumnInfo("year") val year: Int,
    @ColumnInfo("season") val season: Int,
    @ColumnInfo("part") val part: Int,
    @ColumnInfo("views") val views: Int,
    @ColumnInfo("poster_thumb") val poster_thumb: String,
    @ColumnInfo("poster_mini") val poster_mini:String,
    @ColumnInfo("schedule") val schedule: String?,
    @ColumnInfo("new_episode") val new_episode: Int,
    @ColumnInfo("is_extra") val is_extra: Boolean,
    @ColumnInfo("is_ova") val is_ova: Boolean,
    @ColumnInfo("time") val time: Double,
    @ColumnInfo("time_str") val time_str: String,
)
