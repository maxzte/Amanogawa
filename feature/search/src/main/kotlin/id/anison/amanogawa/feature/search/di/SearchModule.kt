package id.anison.amanogawa.feature.search.di

import id.anison.amanogawa.feature.search.screenmodel.FilterResultScreenModel
import id.anison.amanogawa.feature.search.screenmodel.FilterScreenModel
import id.anison.amanogawa.feature.search.screenmodel.RandomAnimeScreenModel
import id.anison.amanogawa.feature.search.screenmodel.SearchScreenModel
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module

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
