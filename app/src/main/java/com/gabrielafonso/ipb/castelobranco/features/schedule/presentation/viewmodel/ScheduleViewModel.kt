package com.gabrielafonso.ipb.castelobranco.features.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.SnapshotState
import com.gabrielafonso.ipb.castelobranco.features.schedule.domain.model.MonthSchedule
import com.gabrielafonso.ipb.castelobranco.features.schedule.domain.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _monthScheduleState =
        MutableStateFlow<SnapshotState<MonthSchedule>>(SnapshotState.Loading)

    val monthScheduleState: StateFlow<SnapshotState<MonthSchedule>> =
        _monthScheduleState.asStateFlow()

    private val _isRefreshingMonthSchedule = MutableStateFlow(false)
    val isRefreshingMonthSchedule: StateFlow<Boolean> = _isRefreshingMonthSchedule.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeMonthSchedule()
                .collect { snapshotState ->
                    _monthScheduleState.value = snapshotState
                }
        }
    }

    fun refreshMonthSchedule(minDurationMs: Long = 1_000L) {
        viewModelScope.launch {
            if (_isRefreshingMonthSchedule.value) return@launch
            _isRefreshingMonthSchedule.value = true

            try {
                val refreshJob = async { repository.refreshMonthSchedule() }
                val minTimeJob = async { delay(minDurationMs) }

                refreshJob.await()
                minTimeJob.await()
            } finally {
                _isRefreshingMonthSchedule.value = false
            }
        }
    }
}