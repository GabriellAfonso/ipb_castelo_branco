package com.gabrielafonso.ipb.castelobranco.ui.screens.main

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gabrielafonso.ipb.castelobranco.ui.theme.IPBCasteloBrancoTheme
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseActivity

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {


    override fun onPreCreate(savedInstanceState: Bundle?) {
        super.onPreCreate(savedInstanceState)

    }

    @Composable
    override fun ScreenContent() {
        MainScreen()
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    IPBCasteloBrancoTheme {
        MainScreen()
    }
}
