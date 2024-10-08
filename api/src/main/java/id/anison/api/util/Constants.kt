package id.anison.api.util

enum class Constants(val url: String) {
    BASIC_URL ("https://amanogawa.space/api"),
    TITLES (BASIC_URL.url + "/titles?"),
    CURRENT (BASIC_URL.url + "/titles/current"),
    FILTER (BASIC_URL.url + "/filters"),
    ANIME (BASIC_URL.url + "/title/"),
    ANIME_EPISODES (BASIC_URL.url + "/episodes/"),
    RANDOM_ANIME (BASIC_URL.url + "/random"),
    SEARCH_ANIME(BASIC_URL.url + "/search?s="),
    TEAM(BASIC_URL.url + "/team"),
}
