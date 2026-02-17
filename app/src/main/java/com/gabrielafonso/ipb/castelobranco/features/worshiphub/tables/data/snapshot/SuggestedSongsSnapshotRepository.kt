package com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.data.snapshot

import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.BaseSnapshotRepository
import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.Logger
import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.RefreshResult
import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.SnapshotCache
import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.SnapshotFetcher
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.data.api.SongsTableApi
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.data.dto.SuggestedSongDto
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.data.mapper.SuggestedSongsMapper
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.model.SuggestedSong
import javax.inject.Inject

class SuggestedSongsSnapshotRepository @Inject constructor(
    private val api: SongsTableApi,
    private val cacheRef: SnapshotCache<List<SuggestedSongDto>>,
    fetcher: SnapshotFetcher<List<SuggestedSongDto>>,
    logger: Logger,
    mapper: SuggestedSongsMapper
) : BaseSnapshotRepository<List<SuggestedSongDto>, List<SuggestedSong>>(
    cache = cacheRef,
    fetcher = fetcher,
    mapper = mapper::map,
    logger = logger,
    tag = "SuggestedSongsSnapshot"
) {

    /**
     * Refresh "do jeito que você quer": sempre sobrescreve o MESMO snapshot
     * (último resultado), usando o parâmetro fixed atual.
     *
     * Observação: aqui a gente FORÇA rede (If-None-Match = null),
     * porque o fixed muda o conteúdo e você não quer depender de ETag antigo.
     */
    suspend fun refresh(fixedByPosition: Map<Int, Int>): RefreshResult {
        return try {
            val fixedParam = fixedByPosition
                .toList()
                .sortedBy { (pos, _) -> pos }
                .joinToString(separator = ",") { (pos, playedId) -> "$pos:$playedId" }

            val response = api.getSuggestedSongs(
                ifNoneMatch = null,
                fixed = fixedParam.ifBlank { null }
            )

            if (!response.isSuccessful) {
                return if (cacheRef.load() != null) RefreshResult.CacheUsed
                else RefreshResult.Error(IllegalStateException("HTTP ${response.code()}"))
            }

            val body = response.body()
                ?: return if (cacheRef.load() != null) RefreshResult.CacheUsed
                else RefreshResult.Error(IllegalStateException("Resposta vazia"))

            val newETag = response.headers()["ETag"]?.trim()
            cacheRef.save(body, newETag)

            RefreshResult.Updated
        } catch (t: Throwable) {
            RefreshResult.Error(t)
        }
    }
}