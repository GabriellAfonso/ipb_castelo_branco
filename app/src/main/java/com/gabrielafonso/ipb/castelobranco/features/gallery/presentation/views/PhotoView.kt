package com.gabrielafonso.ipb.castelobranco.features.gallery.presentation.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gabrielafonso.ipb.castelobranco.R
import com.gabrielafonso.ipb.castelobranco.core.ui.base.BaseScreen
import com.gabrielafonso.ipb.castelobranco.features.gallery.presentation.navigation.GalleryNav
import com.gabrielafonso.ipb.castelobranco.features.gallery.presentation.viewmodel.GalleryViewModel
import java.io.File


@Composable
fun PhotoView(
    albumId: Long,
    photoIndex: Int,
    viewModel: GalleryViewModel,
    nav: GalleryNav
) {
    PhotoScreen(
        albumId = albumId,
        initialIndex = photoIndex,
        viewModel = viewModel,
        actions = nav
    )
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoScreen(
    albumId: Long,
    initialIndex: Int,
    viewModel: GalleryViewModel,
    actions: GalleryNav
) {
    var photos by remember { mutableStateOf<List<File>>(emptyList()) }
    val pagerState = rememberPagerState(initialPage = initialIndex) { photos.size }
    val context = LocalContext.current

    var isZoomed by remember { mutableStateOf(false) }
// Cria o título dinâmico baseado na página atual do Pager
    val currentTitle = remember(pagerState.currentPage, photos) {
        if (photos.isNotEmpty()) {
            photos.getOrNull(pagerState.currentPage)?.name ?: "Foto"
        } else {
            "Carregando..."
        }
    }
    LaunchedEffect(albumId) {
        photos = viewModel.getLocalPhotos(albumId)
    }

    BaseScreen(
        tabName = currentTitle,
        logoRes = R.drawable.ic_galery,
        showBackArrow = true,
        onBackClick = actions.back
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (photos.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    userScrollEnabled = !isZoomed
                ) { index ->
                    var scale by remember { mutableFloatStateOf(1f) }
                    var offset by remember { mutableStateOf(Offset.Zero) }

                    LaunchedEffect(pagerState.currentPage) {
                        scale = 1f
                        offset = Offset.Zero
                        isZoomed = false
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                // Primeiro detector: Apenas para o Double Tap
                                detectTapGestures(
                                    onDoubleTap = {
                                        scale = 1f
                                        offset = Offset.Zero
                                        isZoomed = false
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                // Segundo detector: Zoom e Pan
                                awaitEachGesture {
                                    awaitFirstDown(requireUnconsumed = false)
                                    do {
                                        val event = awaitPointerEvent()
                                        val zoom = event.calculateZoom()
                                        val pan = event.calculatePan()

                                        // Só atualizamos e consumimos se houver mudança real (zoom ou arrasto com zoom)
                                        if (zoom != 1f || (scale > 1f && pan != Offset.Zero)) {
                                            scale = (scale * zoom).coerceIn(1f, 5f)
                                            isZoomed = scale > 1f

                                            if (isZoomed) {
                                                offset += pan
                                                // Consome os eventos para o Pager não rodar
                                                event.changes.forEach { it.consume() }
                                            }
                                        }

                                        // Se resetarmos pro 1.0f manualmente ou na pinça
                                        if (scale <= 1f) {
                                            isZoomed = false
                                            offset = Offset.Zero
                                        }

                                    } while (event.changes.any { it.pressed })
                                }
                            }
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                            }
                    ) {
                        AsyncImage(
                            model = photos[index],
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { sharePhoto(context, photos[pagerState.currentPage]) }) {
                        Text("Compartilhar")
                    }
                    Button(onClick = { copyToDownloads(context, photos[pagerState.currentPage]) }) {
                        Text("Baixar")
                    }
                }
            }
        }
    }
}

private fun sharePhoto(context: Context, file: File) {
    val uri = Uri.fromFile(file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
    }
    context.startActivity(Intent.createChooser(intent, "Compartilhar imagem"))
}

private fun copyToDownloads(context: Context, file: File) {
    val downloadsDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val destFile = File(downloadsDir, file.name)
    file.copyTo(destFile, overwrite = true)
    // Note: In real app, handle permissions and MediaScanner if needed
}