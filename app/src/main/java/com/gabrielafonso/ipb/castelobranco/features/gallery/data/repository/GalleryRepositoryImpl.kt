package com.gabrielafonso.ipb.castelobranco.features.gallery.data.repository

import com.gabrielafonso.ipb.castelobranco.features.gallery.data.api.GalleryApi
import com.gabrielafonso.ipb.castelobranco.features.gallery.data.dto.GalleryPhotoDto
import com.gabrielafonso.ipb.castelobranco.features.gallery.data.local.GalleryPhotoStorage
import com.gabrielafonso.ipb.castelobranco.features.gallery.domain.repository.DownloadProgress
import com.gabrielafonso.ipb.castelobranco.features.gallery.domain.repository.GalleryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.emitAll

data class Album(val id: Long, val name: String)

@Singleton
class GalleryRepositoryImpl @Inject constructor(
    private val api: GalleryApi,
    private val storage: GalleryPhotoStorage,
) : GalleryRepository {

    // 1. ESTADOS QUENTES: Moram aqui no Singleton e são alimentados pelo preload
    private val _albumsFlow = MutableStateFlow<List<Album>>(emptyList())
    override val albumsFlow: StateFlow<List<Album>> = _albumsFlow.asStateFlow()

    /**
     * Chamado pelo MainViewModel. Lê o disco e popula os Flows acima.
     */
    override suspend fun preload() = withContext(Dispatchers.IO) {
        // Carrega álbuns
        val albums = storage.listAlbums().map { Album(it.first, it.second) }
        _albumsFlow.value = albums
    }
    override fun downloadAlbum(albumId: Long): Flow<DownloadProgress> = flow {
        val photos = api.getAlbumPhotos(albumId)
        emitAll(processDownload(photos))
    }

    override fun downloadAllPhotos(): Flow<DownloadProgress> = flow {
        val photos = api.getAllPhotos().body()
            ?: throw Exception("Lista de fotos vazia")
        emitAll(processDownload(photos))
        // IMPORTANTE: Atualiza o Flow assim que o download acaba
        preload()
    }

    /**
     * Lógica centralizada para processar o download de uma lista de fotos
     */
    private fun processDownload(photos: List<GalleryPhotoDto>): Flow<DownloadProgress> = flow {
        if (photos.isEmpty()) {
            emit(DownloadProgress(0, 0))
            return@flow
        }
        val total = photos.size
        var downloaded = 0

        emit(DownloadProgress(downloaded, total))

        for (photo in photos) {
            val albumId = photo.albumId
            val photoId = photo.id
            val photoName = photo.name

            if (!storage.exists(albumId, photoId)) {
                val response = api.downloadFile(photo.imageUrl)

                if (!response.isSuccessful) {
                    throw Exception("Erro ao baixar imagem ${photo.id} (HTTP ${response.code()})")
                }

                val body = response.body()
                    ?: throw Exception("ResponseBody nulo para imagem ${photo.id}")

                storage.save(
                    albumId = albumId,
                    photoId = photoId,
                    photoName = photoName,
                    ext = photo.fileExtension(),
                    input = body.byteStream()
                )
                storage.savePhotoMetadata(albumId, photoId, photo)
            }

            downloaded++
            emit(DownloadProgress(downloaded, total))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getLocalPhotos(
        albumId: Long
    ): List<File> =
        withContext(Dispatchers.IO) {
            storage.listPhotos(albumId)
        }

    override suspend fun clearAlbum(
        albumId: Long
    ) =
        withContext(Dispatchers.IO) {
            storage.clearAlbum(albumId)
        }

    override suspend fun getAllLocalPhotos(): List<File> =
        withContext(Dispatchers.IO) {
            storage.listAllPhotos()
        }

    override suspend fun clearAllPhotos() =
        withContext(Dispatchers.IO) {
            storage.clearAll()
            preload()
        }

    override suspend fun getLocalAlbums(): List<Album> = withContext(Dispatchers.IO) {
        storage.listAlbums().map { Album(it.first, it.second) }
    }

   override suspend fun getThumbnailForAlbum(albumId: Long): File? = withContext(Dispatchers.IO) {
        storage.getThumbnailFile(albumId)
    }
}