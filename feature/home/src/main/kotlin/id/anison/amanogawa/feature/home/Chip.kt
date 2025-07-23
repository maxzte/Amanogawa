package id.anison.amanogawa.feature.home

enum class Chip(
    val title: String
) {
    ALL(title = "All"),
    ONLY_ALL(title = "Only all"),
    ONLY_CURRENT(title = "Only current"),
}