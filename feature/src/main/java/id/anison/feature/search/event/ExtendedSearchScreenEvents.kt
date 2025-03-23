package id.anison.feature.search.event

import id.anison.feature.search.model.FilterUI
import id.anison.feature.search.model.GenreUI


interface ExtendedSearchScreenEvents {
    data object PopBack: ExtendedSearchScreenEvents
    data class AddGenre(val genre: GenreUI): ExtendedSearchScreenEvents
    data class AddTranslator(val t: Map.Entry<Long, String>): ExtendedSearchScreenEvents
    data class AddVoice(val v: Map.Entry<Long, String>): ExtendedSearchScreenEvents
    data class AddSound(val s: Map.Entry<Long, String>): ExtendedSearchScreenEvents
    data class AddYear(val year: Short): ExtendedSearchScreenEvents
    data class RemoveYear(val year: Short): ExtendedSearchScreenEvents
    data class Search(val filter: FilterUI) : ExtendedSearchScreenEvents
    data object Refresh: ExtendedSearchScreenEvents
}