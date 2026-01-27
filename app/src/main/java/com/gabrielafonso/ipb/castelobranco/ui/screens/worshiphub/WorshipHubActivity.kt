package com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseActivity
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseViewModelProvider
import com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.views.WorshipHubView
import com.gabrielafonso.ipb.castelobranco.ui.theme.IPBCasteloBrancoTheme

class WorshipHubActivity : BaseActivity() {

    private val viewModel: WorshipHubViewModel by viewModels { BaseViewModelProvider.Factory }

    override fun onPreCreate(savedInstanceState: Bundle?) {
        super.onPreCreate(savedInstanceState)
        // Inicialize ViewModel, leia extras da intent, registre receivers, etc.
        // ex: val id = intent?.getStringExtra("EXTRA_ID")
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Ações que precisam ocorrer após setContent (não dentro do Composable)
    }

    @Composable
    override fun ScreenContent() {
        val navController = rememberNavController()
        WorshipHubNavGraph(
            navController = navController,
            viewModel = viewModel
        )
//        WorshipHubScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWorshipHub() {
    IPBCasteloBrancoTheme {
        WorshipHubView(onTablesClick = {})
    }
}
