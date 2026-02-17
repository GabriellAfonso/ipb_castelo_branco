package com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.data.repository

import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.RefreshResult
import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.SnapshotState
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.data.api.SongsTableApi
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.data.mapper.SuggestedSongsMapper
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.data.snapshot.AllSongsSnapshotRepository
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.data.snapshot.SongsBySundaySnapshotRepository
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.data.snapshot.TopSongsSnapshotRepository
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.data.snapshot.TopTonesSnapshotRepository
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.domain.repository.SongsRepository
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.model.Song
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.model.SuggestedSong
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.model.SundaySet
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.model.TopSong
import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.model.TopTone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SongsRepositoryImpl @Inject constructor(
    private val api: SongsTableApi,
    private val songsBySundaySnapshot: SongsBySundaySnapshotRepository,
    private val topSongsSnapshot: TopSongsSnapshotRepository,
    private val topTonesSnapshot: TopTonesSnapshotRepository,
    private val allSongsSnapshot: AllSongsSnapshotRepository,
    private val suggestedSongsMapper: SuggestedSongsMapper
) : SongsRepository {

    private val _suggestedSongsState =
        MutableStateFlow<SnapshotState<List<SuggestedSong>>>(SnapshotState.Loading)

    override fun observeSongsBySunday(): Flow<SnapshotState<List<SundaySet>>> =
        songsBySundaySnapshot.observe()

    override suspend fun refreshSongsBySunday(): RefreshResult =
        songsBySundaySnapshot.refresh()

    override fun observeTopSongs(): Flow<SnapshotState<List<TopSong>>> =
        topSongsSnapshot.observe()

    override suspend fun refreshTopSongs(): RefreshResult =
        topSongsSnapshot.refresh()

    override fun observeTopTones(): Flow<SnapshotState<List<TopTone>>> =
        topTonesSnapshot.observe()

    override suspend fun refreshTopTones(): RefreshResult =
        topTonesSnapshot.refresh()

    override fun observeSuggestedSongs(): Flow<SnapshotState<List<SuggestedSong>>> =
        _suggestedSongsState.asStateFlow()

    override suspend fun refreshSuggestedSongs(): RefreshResult =
        refreshSuggestedSongs(fixedByPosition = emptyMap())

    override suspend fun refreshSuggestedSongs(fixedByPosition: Map<Int, Int>): RefreshResult {
        _suggestedSongsState.value = SnapshotState.Loading

        return runCatching {
            val fixedParam = fixedByPosition
                .toList()
                .sortedBy { (pos, _) -> pos }
                .joinToString(separator = ",") { (pos, playedId) -> "$pos:$playedId" }

            val response = api.getSuggestedSongs(
                ifNoneMatch = null, // sempre rede; sem snapshot/cache/etag
                fixed = fixedParam.ifBlank { null }
            )

            if (!response.isSuccessful) {
                throw IllegalStateException("HTTP ${response.code()}")
            }

            val body = response.body()
                ?: throw IllegalStateException("Resposta vazia")

            val mapped = suggestedSongsMapper.map(body)
            _suggestedSongsState.value = SnapshotState.Data(mapped)

            RefreshResult.Updated
        }.getOrElse { t ->
            _suggestedSongsState.value = SnapshotState.Error(t)
            RefreshResult.Error(t)
        }
    }

    override fun observeAllSongs(): Flow<SnapshotState<List<Song>>> =
        allSongsSnapshot.observe()

    override suspend fun refreshAllSongs(): RefreshResult =
        allSongsSnapshot.refresh()


}