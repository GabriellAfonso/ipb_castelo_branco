package com.gabrielafonso.ipb.castelobranco.ui.screens.monthschedule

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.gabrielafonso.ipb.castelobranco.MyApp
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonthScheduleActivity : BaseActivity() {


    override fun onPreCreate(savedInstanceState: Bundle?) {
        super.onPreCreate(savedInstanceState)

    }
    private fun shareText(text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(sendIntent, "Compartilhar"))
    }

    @Composable
    override fun ScreenContent() {
        MonthScheduleView(
            onBackClick = { finish() },
            onShare = { text -> shareText(text) }
        )
    }

}

//@Preview(showBackground = true)
//@Composable
//fun PreviewWorshipHub() {
//    IPBCasteloBrancoTheme {
//        WorshipHubScreen(onTablesClick = {})
//    }
//}

