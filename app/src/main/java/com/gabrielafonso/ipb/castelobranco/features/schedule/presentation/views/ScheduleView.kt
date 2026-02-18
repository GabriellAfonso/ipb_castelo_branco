package com.gabrielafonso.ipb.castelobranco.features.schedule.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielafonso.ipb.castelobranco.R
import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.SnapshotState
import com.gabrielafonso.ipb.castelobranco.core.ui.base.BaseScreen
import com.gabrielafonso.ipb.castelobranco.features.schedule.domain.formatter.MonthScheduleWhatsappFormatter
import com.gabrielafonso.ipb.castelobranco.features.schedule.domain.model.MonthSchedule
import com.gabrielafonso.ipb.castelobranco.features.schedule.presentation.components.MonthScheduleTable
import com.gabrielafonso.ipb.castelobranco.features.schedule.presentation.components.ScheduleRowUi
import com.gabrielafonso.ipb.castelobranco.features.schedule.presentation.components.ScheduleSectionUi
import com.gabrielafonso.ipb.castelobranco.features.schedule.presentation.viewmodel.ScheduleViewModel
import java.util.Locale

data class MonthScheduleUiState(
    val monthScheduleState: SnapshotState<MonthSchedule> = SnapshotState.Loading,
    val isLoading: Boolean = false
)

data class MonthScheduleActions(
    val onShare: (String) -> Unit,
    val onGenerateNewSchedule: () -> Unit = {}
)

@Composable
fun MonthScheduleView(
    viewModel: ScheduleViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onShare: (String) -> Unit
) {
    val monthScheduleState by viewModel.monthScheduleState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isRefreshingMonthSchedule.collectAsStateWithLifecycle()

    val state = MonthScheduleUiState(
        monthScheduleState = monthScheduleState,
        isLoading = isLoading
    )

    val actions = MonthScheduleActions(
        onShare = onShare,
        onGenerateNewSchedule = { /* TODO: gerar nova escala depois */ }
    )

    MonthScheduleScreen(
        state = state,
        actions = actions,
        onBackClick = onBackClick
    )
}

@Composable
fun MonthScheduleScreen(
    state: MonthScheduleUiState,
    actions: MonthScheduleActions,
    onBackClick: () -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val canViewSchedule by viewModel.canViewSchedule.collectAsStateWithLifecycle()
    val cachedMonthSchedule by viewModel.cachedMonthSchedule.collectAsStateWithLifecycle()

    BaseScreen(
        tabName = "Escala",
        logoRes = R.drawable.ic_schedule,
        showBackArrow = true,
        onBackClick = onBackClick,
    ) { innerPadding ->

        if (!canViewSchedule) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Acesso negado",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Escala disponível apenas para membros.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            return@BaseScreen
        }

        val monthScheduleFromSnapshot = (state.monthScheduleState as? SnapshotState.Data)?.value
        val monthSchedule = cachedMonthSchedule ?: monthScheduleFromSnapshot

        val sections = remember(monthSchedule) {
            monthSchedule?.toSectionsUi().orEmpty()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título do mês no topo (novo)
            monthSchedule?.let { s ->
                Text(
                    text = "Escala de ${MonthScheduleWhatsappFormatter.monthPtBr(s.month)}",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.isLoading && monthSchedule == null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Carregando escala...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    MonthScheduleTable(sections = sections)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = actions.onGenerateNewSchedule,
                    enabled = false,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                    )
                ) {
                    Text(text = "Nova Escala")
                }

                Button(
                    onClick = {
                        val s = monthSchedule ?: return@Button
                        val text = MonthScheduleWhatsappFormatter.format(
                            schedule = s,
                            locale = Locale.forLanguageTag("pt-BR")
                        )
                        actions.onShare(text)
                    },
                    enabled = monthSchedule != null,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                    )
                ) {
                    Text(text = "Compartilhar")
                }
            }
        }
    }
}

private fun MonthSchedule.toSectionsUi(): List<ScheduleSectionUi> {
    val order = listOf("terça", "terca", "quinta", "domingo")
    val locale = Locale.forLanguageTag("pt-BR")

    return schedule.entries
        .sortedWith(
            compareBy<Map.Entry<String, com.gabrielafonso.ipb.castelobranco.features.schedule.domain.model.ScheduleEntry>> { entry ->
                val t = entry.key.trim().lowercase(locale)
                order.indexOfFirst { t.startsWith(it) }
                    .let { if (it == -1) Int.MAX_VALUE else it }
            }.thenBy { it.key.lowercase(locale) }
        )
        .map { (title, entry) ->
            ScheduleSectionUi(
                title = title,
                time = entry.time,
                rows = entry.items
                    .sortedBy { it.day }
                    .map { item ->
                        ScheduleRowUi(
                            day = item.day,
                            member = item.member
                        )
                    }
            )
        }
}