package com.gabrielafonso.ipb.castelobranco.ui.screens.monthschedule

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseActivity
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseViewModelProvider

class MonthScheduleActivity : BaseActivity() {

    private val viewModel: MonthScheduleViewModel by viewModels { BaseViewModelProvider.Factory }

    override fun onPreCreate(savedInstanceState: Bundle?) {
        super.onPreCreate(savedInstanceState)

    }

    @Composable
    override fun ScreenContent() {
        MonthScheduleView(viewModel = viewModel)
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewWorshipHub() {
//    IPBCasteloBrancoTheme {
//        WorshipHubScreen(onTablesClick = {})
//    }
//}

