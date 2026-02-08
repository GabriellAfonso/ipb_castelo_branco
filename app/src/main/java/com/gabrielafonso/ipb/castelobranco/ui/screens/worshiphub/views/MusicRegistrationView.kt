package com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielafonso.ipb.castelobranco.R
import com.gabrielafonso.ipb.castelobranco.domain.model.Song
import com.gabrielafonso.ipb.castelobranco.domain.model.SundayPlayPushItem
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseScreen
import com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.WorshipHubViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

enum class RegistrationType(val label: String) {
    MUSIC("Registrar música"),
    SUNDAY("Registrar domingo")
}

@Immutable
data class SundaySongRowState(
    val position: Int,
    val songQuery: String = "",
    val selectedSongId: Int? = null,
    val tone: String = ""
)

@Immutable
data class MusicRegistrationUiState(
    val registrationType: RegistrationType = RegistrationType.SUNDAY,
    val availableSongs: List<Song> = emptyList(),
    val sundayRows: List<SundaySongRowState> = listOf(
        SundaySongRowState(position = 1),
        SundaySongRowState(position = 2),
        SundaySongRowState(position = 3),
        SundaySongRowState(position = 4),
    ),
    val dateBr: String = "",
    val canSubmit: Boolean = false,
    val isSubmitting: Boolean = false
)

data class MusicRegistrationActions(
    val onBackClick: () -> Unit,
    val onRegistrationTypeChange: (RegistrationType) -> Unit,
    val onOpenDatePicker: () -> Unit,
    val onSundaySongQueryChange: (position: Int, query: String) -> Unit,
    val onSundaySongSelect: (position: Int, song: Song) -> Unit,
    val onSundayToneChange: (position: Int, tone: String) -> Unit,
    val onAddMoreSundayRowsClick: () -> Unit,
    val onRemoveSundayRowClick: (position: Int) -> Unit,
    val onSubmitClick: () -> Unit
)

private val BR_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val ISO_DATE: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

// Tons naturais (sem sustenido/bemol)
private val NATURAL_TONES: List<String> = listOf("C", "D", "E", "F", "G", "A", "B")

@Composable
fun MusicRegistrationView(
    onBack: () -> Unit,
    viewModel: WorshipHubViewModel
) {
    val allSongs by viewModel.allSongs.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refreshAllSongs() }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isSubmitting by remember { mutableStateOf(false) }
    var registrationType by remember { mutableStateOf(RegistrationType.SUNDAY) }

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    var sundayRows by remember {
        mutableStateOf(
            listOf(
                SundaySongRowState(position = 1),
                SundaySongRowState(position = 2),
                SundaySongRowState(position = 3),
                SundaySongRowState(position = 4),
            )
        )
    }

    fun isRowValid(row: SundaySongRowState): Boolean {
        val songOk = row.selectedSongId != null && allSongs.any { it.id == row.selectedSongId }
        val toneOk = row.tone.isNotBlank()
        return songOk && toneOk
    }

    val canSubmit = remember(registrationType, sundayRows, allSongs, selectedDate) {
        when (registrationType) {
            RegistrationType.SUNDAY -> {
                val dateOk = selectedDate != null
                val hasAtLeastOneValid = sundayRows.any(::isRowValid)
                dateOk && hasAtLeastOneValid
            }

            RegistrationType.MUSIC -> false
        }
    }

    if (showDatePicker && registrationType == RegistrationType.SUNDAY) {
        SundayDatePickerDialog(
            initialDate = selectedDate,
            onDismiss = { showDatePicker = false },
            onConfirm = { picked ->
                selectedDate = picked
                showDatePicker = false
            }
        )
    }

    val actions = remember(onBack, allSongs, sundayRows, registrationType, canSubmit, selectedDate, isSubmitting) {
        MusicRegistrationActions(
            onBackClick = onBack,
            onRegistrationTypeChange = { newType ->
                registrationType = newType
                if (newType == RegistrationType.MUSIC) {
                    selectedDate = null
                    showDatePicker = false
                }
            },
            onOpenDatePicker = {
                if (registrationType == RegistrationType.SUNDAY) showDatePicker = true
            },
            onSundaySongQueryChange = { position, query ->
                sundayRows = sundayRows.map { row ->
                    if (row.position == position) row.copy(songQuery = query, selectedSongId = null) else row
                }
            },
            onSundaySongSelect = { position, song ->
                val label = buildSongLabel(song)
                sundayRows = sundayRows.map { row ->
                    if (row.position == position) row.copy(songQuery = label, selectedSongId = song.id) else row
                }
            },
            onSundayToneChange = { position, tone ->
                sundayRows = sundayRows.map { row ->
                    if (row.position == position) row.copy(tone = tone) else row
                }
            },
            onAddMoreSundayRowsClick = {
                val nextPos = (sundayRows.maxOfOrNull { it.position } ?: 0) + 1
                sundayRows = sundayRows + SundaySongRowState(position = nextPos)
            },
            onRemoveSundayRowClick = { position ->
                if (position <= 4) return@MusicRegistrationActions
                sundayRows = sundayRows.filterNot { it.position == position }
            },
            onSubmitClick = {
                if (!canSubmit || isSubmitting) return@MusicRegistrationActions

                when (registrationType) {
                    RegistrationType.SUNDAY -> {
                        val dateToSend = selectedDate?.format(ISO_DATE)?.trim().orEmpty()

                        val plays = sundayRows.mapNotNull { row ->
                            if (!isRowValid(row)) return@mapNotNull null
                            val sid = row.selectedSongId ?: return@mapNotNull null
                            SundayPlayPushItem(
                                songId = sid,
                                position = row.position,
                                tone = row.tone.trim()
                            )
                        }

                        isSubmitting = true
                        viewModel.submitSundayPlays(
                            date = dateToSend,
                            rows = plays,
                            onResult = { result ->
                                isSubmitting = false
                                when (result) {
                                    is WorshipHubViewModel.SubmitResult.Success -> {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Enviado com sucesso.")
                                        }
                                    }

                                    is WorshipHubViewModel.SubmitResult.Error -> {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(result.message)
                                        }
                                    }
                                }
                            }
                        )
                    }

                    RegistrationType.MUSIC -> {
                        scope.launch {
                            snackbarHostState.showSnackbar("Registro de música ainda não implementado.")
                        }
                    }
                }
            }
        )
    }

    val dateBr = if (registrationType == RegistrationType.SUNDAY) {
        selectedDate?.format(BR_DATE).orEmpty()
    } else {
        ""
    }

    val state = MusicRegistrationUiState(
        registrationType = registrationType,
        availableSongs = allSongs,
        sundayRows = sundayRows,
        dateBr = dateBr,
        canSubmit = canSubmit,
        isSubmitting = isSubmitting
    )

    BaseScreen(
        tabName = "Registrar",
        logoRes = R.drawable.ic_register,
        showBackArrow = true,
        onBackClick = actions.onBackClick
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Conteúdo normal (scroll)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "REGISTRAR",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                RegistrationTypeSelect(
                    value = state.registrationType,
                    onChange = actions.onRegistrationTypeChange,
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.registrationType == RegistrationType.SUNDAY) {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.dateBr,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Data (dd/MM/yyyy)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            TextButton(onClick = actions.onOpenDatePicker) { Text("Selecionar") }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                when (state.registrationType) {
                    RegistrationType.SUNDAY -> SundayRegistrationForm(
                        availableSongs = state.availableSongs,
                        rows = state.sundayRows,
                        onSongQueryChange = actions.onSundaySongQueryChange,
                        onSongSelect = actions.onSundaySongSelect,
                        onToneChange = actions.onSundayToneChange,
                        onAddMoreClick = actions.onAddMoreSundayRowsClick,
                        onRemoveRowClick = actions.onRemoveSundayRowClick
                    )

                    RegistrationType.MUSIC -> {
                        Text(
                            text = "Ainda não implementado.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = actions.onSubmitClick,
                    enabled = state.canSubmit && !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (state.isSubmitting) "A enviar..." else "Enviar ao servidor")
                }
            }

            // Snackbar por cima (overlay), sem empurrar layout
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )
        }
    }
}

private fun buildSongLabel(song: Song): String =
    if (song.artist.isBlank()) song.title else "${song.title} [${song.artist}]"

@Composable
private fun SundayRegistrationForm(
    availableSongs: List<Song>,
    rows: List<SundaySongRowState>,
    onSongQueryChange: (position: Int, query: String) -> Unit,
    onSongSelect: (position: Int, song: Song) -> Unit,
    onToneChange: (position: Int, tone: String) -> Unit,
    onAddMoreClick: () -> Unit,
    onRemoveRowClick: (position: Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        rows.forEach { row ->
            SundaySongRow(
                availableSongs = availableSongs,
                state = row,
                onSongQueryChange = { q -> onSongQueryChange(row.position, q) },
                onSongSelect = { s -> onSongSelect(row.position, s) },
                onToneChange = { t -> onToneChange(row.position, t) },
                onRemoveClick = if (row.position > 4) ({ onRemoveRowClick(row.position) }) else null
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onAddMoreClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "+")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SundaySongRow(
    availableSongs: List<Song>,
    state: SundaySongRowState,
    onSongQueryChange: (String) -> Unit,
    onSongSelect: (Song) -> Unit,
    onToneChange: (String) -> Unit,
    onRemoveClick: (() -> Unit)?
) {
    var expandedSong by remember { mutableStateOf(false) }
    var expandedTone by remember { mutableStateOf(false) }

    val filtered = remember(state.songQuery, availableSongs) {
        val q = state.songQuery.trim()
        if (q.isBlank()) availableSongs
        else availableSongs.filter {
            it.title.contains(q, ignoreCase = true) || it.artist.contains(q, ignoreCase = true)
        }
    }

    LaunchedEffect(state.selectedSongId) {
        if (state.selectedSongId != null) expandedSong = false
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "${state.position}.",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.width(18.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            ExposedDropdownMenuBox(
                expanded = expandedSong,
                onExpandedChange = { expandedSong = !expandedSong }
            ) {
                OutlinedTextField(
                    value = state.songQuery,
                    onValueChange = {
                        onSongQueryChange(it)
                        expandedSong = true
                    },
                    label = { Text("Música") },
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSong) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedSong,
                    onDismissRequest = { expandedSong = false }
                ) {
                    filtered.forEach { song ->
                        DropdownMenuItem(
                            text = { Text(buildSongLabel(song)) },
                            onClick = { onSongSelect(song) }
                        )
                    }
                }
            }

            val isValidSelection =
                state.selectedSongId != null && availableSongs.any { it.id == state.selectedSongId }

            if (!isValidSelection && state.songQuery.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Selecione uma música existente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // TOM: dropdown estático (sem digitar)
        ExposedDropdownMenuBox(
            expanded = expandedTone,
            onExpandedChange = { expandedTone = !expandedTone },
            modifier = Modifier.width(130.dp)
        ) {
            OutlinedTextField(
                value = state.tone,
                onValueChange = { },
                readOnly = true,
                label = { Text("Tom") },
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTone) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedTone,
                onDismissRequest = { expandedTone = false }
            ) {
                NATURAL_TONES.forEach { tone ->
                    DropdownMenuItem(
                        text = { Text(tone) },
                        onClick = {
                            onToneChange(tone)
                            expandedTone = false
                        }
                    )
                }
            }
        }

        if (onRemoveClick != null) {
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remover",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SundayDatePickerDialog(
    initialDate: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    val initialMillis = initialDate
        ?.atStartOfDay(ZoneId.systemDefault())
        ?.toInstant()
        ?.toEpochMilli()

    val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = pickerState.selectedDateMillis ?: return@TextButton
                    val date = Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    onConfirm(date)
                }
            ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    ) {
        DatePicker(state = pickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationTypeSelect(
    value: RegistrationType,
    onChange: (RegistrationType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de registro") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            RegistrationType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.label) },
                    onClick = {
                        onChange(type)
                        expanded = false
                    }
                )
            }
        }
    }
}