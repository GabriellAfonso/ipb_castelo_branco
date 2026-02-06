package com.gabrielafonso.ipb.castelobranco.ui.screens.profile

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielafonso.ipb.castelobranco.R
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseScreen
import com.yalantis.ucrop.UCrop
import java.io.File

data class ProfileUiState(
    val isUploading: Boolean = false,
    val error: String? = null
)

data class ProfileActions(
    val onBackClick: () -> Unit,
    val onPickImageClick: () -> Unit
)

@Composable
fun ProfileView(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isUploading by viewModel.isUploading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data

        if (result.resultCode != Activity.RESULT_OK) {
            val cropError = data?.let(UCrop::getError)
            viewModel.clearError()
            return@rememberLauncherForActivityResult
        }

        val resultUri = data?.let(UCrop::getOutput)
        if (resultUri == null) {
            viewModel.clearError()
            return@rememberLauncherForActivityResult
        }

        val bytes: ByteArray = context.contentResolver
            .openInputStream(resultUri)
            ?.use { it.readBytes() }
            ?: run {
                viewModel.clearError()
                return@rememberLauncherForActivityResult
            }

        if (bytes.isNotEmpty()) {
            viewModel.uploadProfilePhoto(bytes, "profile.jpg")
        }
    }

    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult

        val destinationUri = Uri.fromFile(
            File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        )

        // --- CONFIGURAÇÃO PARA OS BOTÕES NÃO FICAREM ATRÁS DA STATUSBAR ---
        val options = UCrop.Options().apply {

            // Define a cor da StatusBar (onde fica bateria/hora)
            setStatusBarColor(Color.BLACK)
            // Define a cor da Toolbar do uCrop (onde ficam os botões superior)
            setToolbarColor(Color.BLACK)
            // Cor dos ícones (X e Check)
            setToolbarWidgetColor(Color.WHITE)
            // Título da tela
            setToolbarTitle("Recortar Foto")
            // Garante que a UI não tente ser "fullscreen" total
            setHideBottomControls(false)
        }

        val intent = UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(512, 512)
            .withOptions(options) // Aplica as opções
            .getIntent(context)

        cropLauncher.launch(intent)
    }

    val state = ProfileUiState(isUploading, error)
    val actions = ProfileActions(
        onBackClick = onBackClick,
        onPickImageClick = { pickLauncher.launch("image/*") }
    )

    ProfileScreen(state = state, actions = actions)
}

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    actions: ProfileActions
) {
    BaseScreen(
        tabName = "Perfil",
        showBackArrow = true,
        onBackClick = actions.onBackClick
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_placeholder),
                contentDescription = "Selecionar foto do perfil",
                modifier = Modifier.clickable(enabled = !state.isUploading) {
                    actions.onPickImageClick()
                }
            )

            Spacer(Modifier.height(12.dp))

            if (state.isUploading) {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("A enviar...")
            }

            if (!state.error.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Erro: ${state.error.orEmpty()}")
            }
        }
    }
}