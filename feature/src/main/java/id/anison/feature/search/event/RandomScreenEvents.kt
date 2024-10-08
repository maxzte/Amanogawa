package id.anison.feature.search.event

import id.anison.feature.model.FilterUI

interface RandomScreenEvents {
    data object PopBack: RandomScreenEvents
    data object Next: RandomScreenEvents
    data object IsLast: RandomScreenEvents
    data class OnLinkClick(val filter: FilterUI, val filterName: String): RandomScreenEvents
}