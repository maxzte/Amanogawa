package id.anison.feature.search.event

import id.anison.feature.model.FilterUI
import id.anison.feature.model.GenreUI
import id.anison.feature.model.SUI
import id.anison.feature.model.TUI
import id.anison.feature.model.VUI

interface ExtendedSearchScreenEvents {
    data object PopBack: ExtendedSearchScreenEvents
    data class AddGenre(val genre: GenreUI): ExtendedSearchScreenEvents
    data class AddTranslator(val translator: TUI): ExtendedSearchScreenEvents
    data class AddVoice(val voice: VUI): ExtendedSearchScreenEvents
    data class AddSound(val sound: SUI): ExtendedSearchScreenEvents
    data class AddYear(val year: Int): ExtendedSearchScreenEvents
    data class RemoveYear(val year: Int): ExtendedSearchScreenEvents
    data class Search(val filter: FilterUI) : ExtendedSearchScreenEvents
    data object Refresh: ExtendedSearchScreenEvents
}