package id.anison.amanogawa.core.domain.repository

interface RemoteKeyRepo {

    suspend fun cleanUpCache()

}
