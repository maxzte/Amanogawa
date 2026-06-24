package id.anison.amanogawa.feature.search.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("id.anison.amanogawa.feature.search")
class SearchModule

/*
val searchModule = module {
    single<SearchScreenModel>()
    factory<RandomAnimeScreenModel>()
    factory<FilterScreenModel>()
    factory<FilterResultScreenModel>()
}
*/
