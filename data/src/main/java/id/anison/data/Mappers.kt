package id.anison.data

import id.anison.api.model.Anime
import id.anison.api.model.AnimeEpisodes
import id.anison.api.model.CurrentTitle
import id.anison.api.model.Dubers
import id.anison.api.model.Episode
import id.anison.api.model.Filter
import id.anison.api.model.FoundAnime
import id.anison.api.model.Genre
import id.anison.api.model.GroupF
import id.anison.api.model.S
import id.anison.api.model.T
import id.anison.api.model.TeamItem
import id.anison.api.model.Title
import id.anison.api.model.V
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
import id.anison.database.model.CurrentTitlesDbo
import id.anison.database.model.DubersDbo
import id.anison.database.model.FavoriteDbo
import id.anison.database.model.FilterDbo
import id.anison.database.model.FoundAnimeDbo
import id.anison.database.model.GenreDbo
import id.anison.database.model.SDbo
import id.anison.database.model.TDbo
import id.anison.database.model.TitleDbo
import id.anison.database.model.VDbo
import kotlin.math.round


internal fun TitleDbo.toDataTitle(): DataTitle {
    return DataTitle(
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
        episode_count = episode_count
    )
}

internal fun Title.toDataTitle(): DataTitle {
    return DataTitle(
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
        episode_count = episode_count
    )
}

internal fun Title.toTitleDbo(): TitleDbo {
    return TitleDbo(
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
        episode_count = episode_count
    )
}

internal fun CurrentTitle.toCurrentDataTitle(): DataCurrentTitle {
    return DataCurrentTitle(
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
        new_episode = new_episode,
        is_extra = is_extra,
        is_ova = is_ova,
        time = time,
        time_str = time_str,
    )
}

internal fun CurrentTitlesDbo.toCurrentDataTitle(): DataCurrentTitle {
    return DataCurrentTitle(
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
        new_episode = new_episode,
        is_extra = is_extra,
        is_ova = is_ova,
        time = time,
        time_str = time_str,
    )
}

internal fun CurrentTitle.toCurrentTitleDbo(): CurrentTitlesDbo {
    return CurrentTitlesDbo(
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
        new_episode = new_episode,
        is_extra = is_extra,
        is_ova = is_ova,
        time = time,
        time_str = time_str,
    )
}

internal fun Anime.toDataAnime(): DataAnime {
    return DataAnime(
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
        genres_f = genres_f,
        voices_f = voices_f,
        sounds_f = sounds_f,
        transl_f = transl_f,
        group_f = group_f?.map { it.toDaraGroupF() },
        episode_count = episode_count,
        bot_url = bot_url,
        rating = if (liked + disliked > 0) round(liked / (liked.toDouble() + disliked) * 5).toInt()
        else 0,
    )
}

internal fun GroupF?.toDaraGroupF(): DataGroupF {
    return DataGroupF(
        id = this!!.id,
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

internal fun AnimeEpisodes<Episode>.toDataEpisodes(): DataEpisodes<DataEpisode> {
    return DataEpisodes(
        pages = pages,
        data = data.map { it.toDataEpisode() },
        bot_url = bot_url,
    )
}

internal fun Episode.toDataEpisode(): DataEpisode {
    return DataEpisode(
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

internal fun DataFavorite.toFavoriteDbo(): FavoriteDbo {
    return FavoriteDbo(
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

internal fun FavoriteDbo.toDataFavorite(): DataFavorite {
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

internal fun Filter.toDataFilter(): DataFilter {
    return DataFilter(
        id = id,
        dubers = dubers.toDataDubers(),
        genres = genres.toDataGenres(),
        years = years,
    )
}

internal fun Dubers.toDataDubers(): DataDubers {
    return DataDubers(
        s = s.toDataList { it.toDataS() },
        t = t.toDataList { it.toDataT() },
        v = v.toDataList { it.toDataV() }
    )
}

internal fun S.toDataS(): DataS {
    return DataS(
        id = id,
        name = name,
    )
}

internal fun T.toDataT(): DataT {
    return DataT(
        id = id,
        name = name,
    )
}

internal fun V.toDataV(): DataV {
    return DataV(
        id = id,
        name = name,
    )
}

internal fun <T, R> List<T>.toDataList(transform: (T) -> R): List<R> {
    return this.map(transform)
}

internal fun List<Genre>.toDataGenres(): List<DataGenre> {
    return this.map { it.toDataGenre() }
}

internal fun Genre.toDataGenre(): DataGenre {
    return DataGenre(
        id = id,
        name = name,
        bot_id_name = bot_id_name,
    )
}

internal fun Filter.toFilterDbo(): FilterDbo {
    return FilterDbo(
        id = id,
        dubers = dubers.toDubersDbo(),
        genres = genres.toGenresDbo(),
        years = years,
    )
}

internal fun Dubers.toDubersDbo(): DubersDbo {
    return DubersDbo(
        s = s.map { it.toSDbo() },
        t = t.map { it.toTDbo() },
        v = v.map { it.toVDbo() },
    )
}

internal fun S.toSDbo(): SDbo {
    return SDbo(
        id = id,
        name = name,
    )
}

internal fun T.toTDbo(): TDbo {
    return TDbo(
        id = id,
        name = name,
    )
}

internal fun V.toVDbo(): VDbo {
    return VDbo(
        id = id,
        name = name,
    )
}

internal fun List<Genre>.toGenresDbo(): List<GenreDbo> {
    return this.map { it.toGenreDbo() }
}

internal fun Genre.toGenreDbo(): GenreDbo {
    return GenreDbo(
        id = id,
        name = name,
        bot_id_name = bot_id_name,
    )
}

internal fun FilterDbo.toDataFilter(): DataFilter {
    return DataFilter(
        id = id,
        dubers = dubers.toDataDubers(),
        genres = genres.fromDboToDataGenres(),
        years = years,
    )
}

internal fun DubersDbo.toDataDubers(): DataDubers {
    return DataDubers(
        s = s.toDataList { it.toDataS() },
        t = t.toDataList { it.toDataT() },
        v = v.toDataList { it.toDataV() }
    )
}

internal fun SDbo.toDataS(): DataS {
    return DataS(
        id = id,
        name = name,
    )
}

internal fun TDbo.toDataT(): DataT {
    return DataT(
        id = id,
        name = name,
    )
}

internal fun VDbo.toDataV(): DataV {
    return DataV(
        id = id,
        name = name,
    )
}

internal fun List<GenreDbo>.fromDboToDataGenres(): List<DataGenre> {
    return this.map { it.toDataGenre() }
}

internal fun GenreDbo.toDataGenre(): DataGenre {
    return DataGenre(
        id = id,
        name = name,
        bot_id_name = bot_id_name,
    )
}

internal fun FoundAnime.toDataFoundAnime(): DataFoundAnime {
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

internal fun FoundAnimeDbo.toDataFoundAnime(): DataFoundAnime {
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

internal fun DataFoundAnime.toFoundAnimeDbo(): FoundAnimeDbo {
    return FoundAnimeDbo(
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

internal fun TeamItem.toDataTeamItem(): DataTeamItem {
    return DataTeamItem(
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
