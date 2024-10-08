package id.anison.feature.model

import id.anison.data.model.DataAnime
import id.anison.data.model.DataCurrentTitle
import id.anison.data.model.DataDubers
import id.anison.data.model.DataEpisode
import id.anison.data.model.DataEpisodes
import id.anison.data.model.DataFavorite
import id.anison.data.model.DataFilter
import id.anison.data.model.DataFoundAnime
import id.anison.data.model.DataGenre
import id.anison.data.model.DataGroupF
import id.anison.data.model.DataS
import id.anison.data.model.DataT
import id.anison.data.model.DataTeamItem
import id.anison.data.model.DataTitle
import id.anison.data.model.DataV


internal fun DataTitle.toTitleUI(): TitleUI {
    return TitleUI(
        id = id,
        name = name,
        is_current = is_current,
        en_jp_name = en_jp_name,
        dubed_episodes = dubed_episodes,
        episodes_custom = episodes_custom,
        episodes_total = episodes_total,
        extra_total = extra_total,
        ova_total = ova_total,
        year = year,
        season = season,
        part = part,
        views = views,
        poster_thumb = poster_thumb,
        poster_mini = poster_mini,
        schedule = schedule,
        episode_count = episode_count,
        time_str = null,
    )
}

internal fun DataCurrentTitle.toCurrentTitleUI(): CurrentTitleUI {
    return CurrentTitleUI(
        id = id,
        name = name,
        is_current = is_current,
        en_jp_name = en_jp_name,
        dubed_episodes = dubed_episodes,
        episodes_custom = episodes_custom,
        episodes_total = episodes_total,
        extra_total = extra_total,
        ova_total = ova_total,
        year = year,
        season = season,
        part = part,
        views = views,
        poster_thumb = poster_thumb,
        poster_mini = poster_mini,
        schedule = schedule,
        episode_count = null,
        time_str = time_str,
    )
}

internal fun DataAnime.toAnimeUI(): AnimeUI {
    return AnimeUI(
        id = id,
        name = name,
        en_jp_name = en_jp_name,
        year = year,
        duration = duration,
        company = company,
        director = director,
        description = description,
        bot_id = bot_id,
        season = season,
        part = part,
        views = views,
        post_date = post_date,
        post_update = post_update,
        episodes_total = episodes_total,
        episodes_custom = episodes_custom,
        dubed_episodes = dubed_episodes,
        extra_total = extra_total,
        ova_total = ova_total,
        liked = liked,
        disliked = disliked,
        is_current = is_current,
        is_movie = is_movie,
        is_dubed = is_dubed,
        is_haveSubs = is_haveSubs,
        torrent_url = torrent_url,
        torrent_4k_url = torrent_4k_url,
        poster_maker = poster_maker,
        poster = poster,
        poster_thumb = poster_thumb,
        poster_mini = poster_mini,
        screens_f = screens_f,
        genres_f = genres_f.map { GenreUI(id = it[0].toInt(), name = it[1]) },
        voices_f = voices_f.map { VUI(id = it[0].toInt(), name = it[1]) },
        sounds_f = sounds_f.map { SUI(id = it[0].toInt(), name = it[1]) },
        transl_f = transl_f.map { TUI(id = it[0].toInt(), name = it[1]) },
        group_f = group_f?.map { it.toGroupFUI() },
        episode_count = episode_count,
        bot_url = bot_url,
        rating = rating,
    )
}

internal fun DataGroupF.toGroupFUI(): GroupFUI {
    return GroupFUI(
        id = id,
        name = name,
        is_current = is_current,
        en_jp_name = en_jp_name,
        dubed_episodes = dubed_episodes,
        episodes_custom = episodes_custom,
        episodes_total = episodes_total,
        extra_total = extra_total,
        ova_total = ova_total,
        year = year,
        season = season,
        part = part,
        views = views,
        poster_thumb = poster_thumb,
        poster_mini = poster_mini,
        schedule = schedule,
        episode_count = episode_count,
    )
}

internal fun DataEpisodes<DataEpisode>.toEpisodesUI(): EpisodesUI<EpisodeUI> {
    return EpisodesUI(
        bot_url = bot_url,
        data = data.map { it.toListEpisodeUI() },
        pages = pages,
    )
}

internal fun DataEpisode.toListEpisodeUI(): EpisodeUI {
    return EpisodeUI(
        bot_id = bot_id,
        id = id,
        is_extra = is_extra,
        is_ova = is_ova,
        name = name,
        number = number,
        post_date = post_date,
        post_update = post_update,
        screen = screen,
        screen_original = screen_original,
        title_id = title_id,
    )
}

internal fun FavoriteUI.toDataFavorite(): DataFavorite {
    return DataFavorite(
        isFavorite = isFavorite,
        isCurrent = isCurrent,
        id = id,
        name = name,
        en_jap_name = en_jap_name,
        season = season,
        year = year,
        poster = poster,
        views = views,
        liked = liked,
        disliked = disliked,
        episodes = episodes,
        isNewEpisode = isNewEpisode,
        schedule = schedule,
        timestamp = timestamp,
    )
}

internal fun DataFavorite.toFavoriteUI(): FavoriteUI {
    return FavoriteUI(
        isFavorite = isFavorite,
        isCurrent = isCurrent,
        id = id,
        name = name,
        en_jap_name = en_jap_name,
        season = season,
        year = year,
        poster = poster,
        views = views,
        liked = liked,
        disliked = disliked,
        episodes = episodes,
        isNewEpisode = isNewEpisode,
        schedule = schedule,
        timestamp = timestamp,
    )
}

internal fun AnimeUI.toFavoriteUI() = FavoriteUI(
    isFavorite = false,
    isCurrent = this.is_current,
    id = this.id,
    name = this.name,
    en_jap_name = this.en_jp_name,
    season = this.season,
    year = this.year,
    poster = this.poster.toString(),
    views = this.views,
    liked = this.liked,
    disliked = this.disliked,
    episodes = this.episode_count.toInt(),
    isNewEpisode = false,
    timestamp = System.currentTimeMillis(),
)

internal fun DataFilter.toFilterUI(): FilterUI {
    return FilterUI(
        id = id,
        dubers = dubers.toDubersUI(),
        genres = genres.toGenreUIList(),
        years = years
    )
}

internal fun FilterUI.toDataFilter(): DataFilter {
    return DataFilter(
        id = id,
        dubers = dubers.toDataDubers(),
        genres = genres.map { it.toDataGenre() },
        years = years
    )
}

internal fun DataDubers.toDubersUI(): DubersUI {
    return DubersUI(
        s = s.map { it.toSUI() },
        t = t.map { it.toTUI() },
        v = v.map { it.toVUI() },
    )
}

internal fun DubersUI.toDataDubers(): DataDubers {
    return DataDubers(
        s = s.map { it.toDataS() },
        t = t.map { it.toDataT() },
        v = v.map { it.toDataV() },
    )
}

internal fun SUI.toDataS(): DataS {
    return DataS(
        id = id,
        name = name
    )
}

internal fun TUI.toDataT(): DataT {
    return DataT(
        id = id,
        name = name
    )
}

internal fun VUI.toDataV(): DataV {
    return DataV(
        id = id,
        name = name
    )
}

internal fun DataS.toSUI(): SUI {
    return SUI(
        id = id,
        name = name
    )
}

internal fun DataT.toTUI(): TUI {
    return TUI(
        id = id,
        name = name
    )
}

internal fun DataV.toVUI(): VUI {
    return VUI(
        id = id,
        name = name
    )
}

internal fun DataGenre.toGenreUI(): GenreUI {
    return GenreUI(
        id = id,
        name = name,
        bot_id_name = bot_id_name
    )
}

internal fun List<DataGenre>.toGenreUIList(): List<GenreUI> {
    return this.map { it.toGenreUI() }
}

internal fun GenreUI.toDataGenre(): DataGenre {
    return DataGenre(
        id = id,
        name = name,
        bot_id_name = bot_id_name
    )
}

internal fun DataFoundAnime.toFoundAnimeUI(): FoundAnimeUI {
    return FoundAnimeUI(
        id = id,
        name = name,
        en_jp_name = en_jp_name,
        year = year,
        season = season,
        part = part,
        poster_mini = poster_mini,
        timestamp = timestamp,
    )
}

internal fun FoundAnimeUI.toDataFoundAnime(): DataFoundAnime {
    return DataFoundAnime(
        id = id,
        name = name,
        en_jp_name = en_jp_name,
        year = year,
        season = season,
        part = part,
        poster_mini = poster_mini,
        timestamp = timestamp,
    )
}

internal fun DataTeamItem.toTeamItemUI(): TeamItemUI {
    return TeamItemUI(
        id = id,
        is_current = is_current,
        is_other = is_other,
        is_sound = is_sound,
        is_transl = is_transl,
        is_voice = is_voice,
        name = name,
        role_text = role_text,
        sound = sound,
        titles = titles,
        translator = translator,
        user_order = user_order,
        voice = voice,
    )
}
