package id.anison.amanogawa.core.database.util

import androidx.room.TypeConverter
import id.anison.amanogawa.core.database.teammember.TeamMemberTypeDbo
import id.anison.amanogawa.core.database.filter.FilterTypeDbo

class EnumConverter {

    @TypeConverter
    fun fromDubberType(value: TeamMemberTypeDbo): String = value.name

    @TypeConverter
    fun toDubberType(value: String): TeamMemberTypeDbo = TeamMemberTypeDbo.valueOf(value)

    @TypeConverter
    fun fromFilterType(type: FilterTypeDbo): String = type.name

    @TypeConverter
    fun toFilterType(type: String): FilterTypeDbo = FilterTypeDbo.valueOf(type)

}
