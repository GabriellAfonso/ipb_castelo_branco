package com.gabrielafonso.ipb.castelobranco.core.domain.snapshot

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
abstract class BaseSnapshotRepository<Dto, Domain>(
    private val cache: SnapshotCache<Dto>,
    private val fetcher: SnapshotFetcher<Dto>,
    private val mapper: (Dto) -> Domain,
    private val logger: Logger = Logger.Noop,
    private val tag: String
) {

    fun observe(): Flow<SnapshotState<Domain>> = flow {
        emit(SnapshotState.Loading)

        val cached = cache.load()
        if (cached != null) {
            emit(SnapshotState.Data(mapper(cached)))
        }

        when (val result = fetcher.fetch(cache.loadETag())) {

            is NetworkResult.NotModified -> Unit

            is NetworkResult.Success -> {
                cache.save(result.body, result.etag)
                emit(SnapshotState.Data(mapper(result.body)))
            }

            is NetworkResult.Failure -> {
                emit(SnapshotState.Error(result.throwable))
            }
        }
    }

    suspend fun refresh(): RefreshResult {
        return try {
            when (val result = fetcher.fetch(cache.loadETag())) {

                is NetworkResult.NotModified ->
                    RefreshResult.NotModified

                is NetworkResult.Success -> {
                    cache.save(result.body, result.etag)
                    RefreshResult.Updated
                }

                is NetworkResult.Failure -> {
                    if (cache.load() != null) RefreshResult.CacheUsed
                    else RefreshResult.Error(result.throwable)
                }
            }
        } catch (t: Throwable) {
            logger.warn(tag, "Snapshot refresh failed", t)
            RefreshResult.Error(t)
        }
    }
}
