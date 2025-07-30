package id.anison.amanogawa.core.database.filter

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import id.anison.amanogawa.core.database.genre.GenreDbo
import id.anison.amanogawa.core.database.teammember.DubberDbo
import id.anison.amanogawa.core.database.teammember.SoundEngineerDbo
import id.anison.amanogawa.core.database.teammember.TranslatorDbo

@Entity(tableName = "filter")
class FilterDbo(

    @PrimaryKey
    val filterType: FilterTypeDbo

)

enum class FilterTypeDbo {
    DISPLAYED,
    SEARCH
}

@Entity(
    tableName = "filter_genre_cross_ref",
    primaryKeys = ["filterType", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = FilterDbo::class,
            parentColumns = ["filterType"],
            childColumns = ["filterType"]
        ),
        ForeignKey(
            entity = GenreDbo::class,
            parentColumns = ["genreId"],
            childColumns = ["genreId"]
        ),
    ],
    indices = [Index("genreId")]
)
data class FilterGenreCrossRef(
    val filterType: FilterTypeDbo,
    val genreId: Long
)


@Entity(
    tableName = "filter_dubber_cross_ref",
    primaryKeys = ["filterType", "dubberId"],
    foreignKeys = [
        ForeignKey(
            entity = FilterDbo::class,
            parentColumns = ["filterType"],
            childColumns = ["filterType"],
        ),
        ForeignKey(
            entity = DubberDbo::class,
            parentColumns = ["dubberId"],
            childColumns = ["dubberId"]
        ),
    ],
)
data class FilterDubberCrossRef(
    val filterType: FilterTypeDbo,
    val dubberId: Long,
)

@Entity(
    tableName = "filter_translator_cross_ref",
    primaryKeys = ["filterType", "translatorId"],
    foreignKeys = [
        ForeignKey(
            entity = FilterDbo::class,
            parentColumns = ["filterType"],
            childColumns = ["filterType"],
        ),
        ForeignKey(
            entity = TranslatorDbo::class,
            parentColumns = ["translatorId"],
            childColumns = ["translatorId"]
        ),
    ],
)
data class FilterTranslatorCrossRef(
    val filterType: FilterTypeDbo,
    val translatorId: Long,
)

@Entity(
    tableName = "filter_sound_engineer_cross_ref",
    primaryKeys = ["filterType", "soundEngineerId"],
    foreignKeys = [
        ForeignKey(
            entity = FilterDbo::class,
            parentColumns = ["filterType"],
            childColumns = ["filterType"],
        ),
        ForeignKey(
            entity = SoundEngineerDbo::class,
            parentColumns = ["soundEngineerId"],
            childColumns = ["soundEngineerId"]
        ),
    ],
)
data class FilterSoundEngineerCrossRef(
    val filterType: FilterTypeDbo,
    val soundEngineerId: Long,
)

data class FilterWithGenres(

    @Embedded
    val filter: FilterDbo,

    @Relation(
        parentColumn = "filterType",
        entityColumn = "genreId",
        associateBy = Junction(FilterGenreCrossRef::class)
    )
    val genres: List<GenreDbo>,

)

data class FilterWithDubbers(

    @Embedded
    val filter: FilterDbo,

    @Relation(
        parentColumn = "filterType",
        entityColumn = "dubberId",
        associateBy = Junction(
            value = FilterDubberCrossRef::class,
            parentColumn = "filterType",
            entityColumn = "dubberId"
        )
    )
    val dubbers: List<DubberDbo>
)

data class FilterWithTranslators(

    @Embedded
    val filter: FilterDbo,

    @Relation(
        parentColumn = "filterType",
        entityColumn = "translatorId",
        associateBy = Junction(
            value = FilterTranslatorCrossRef::class,
            parentColumn = "filterType",
            entityColumn = "translatorId"
        )
    )
    val translators: List<TranslatorDbo>
)

data class FilterWithSoundEngineers(

    @Embedded
    val filter: FilterDbo,

    @Relation(
        parentColumn = "filterType",
        entityColumn = "soundEngineerId",
        associateBy = Junction(
            value = FilterSoundEngineerCrossRef::class,
            parentColumn = "filterType",
            entityColumn = "soundEngineerId"
        )
    )
    val soundEngineers: List<SoundEngineerDbo>
)

@Entity(tableName = "filter_year")
data class FilterYearDbo(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val year: Short,

    val filterType: FilterTypeDbo,

)

data class FilterWithYears(

    @Embedded
    val filter: FilterDbo,

    @Relation(
        parentColumn = "filterType",
        entityColumn = "filterType"
    )
    val years: List<FilterYearDbo>

)
