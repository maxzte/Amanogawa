package id.anison.feature.profile.event


interface AboutUsScreenEvents {
    data object PopBack: AboutUsScreenEvents
    data class NavigateToTitlesByTeammateName(val teammateName: String): AboutUsScreenEvents
}