package com.gabrielafonso.ipb.castelobranco.ui.screens.auth

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gabrielafonso.ipb.castelobranco.ui.theme.IPBCasteloBrancoTheme
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseActivity

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {


    override fun onPreCreate(savedInstanceState: Bundle?) {
        super.onPreCreate(savedInstanceState)

    }

    @Composable
    override fun ScreenContent() {
        LoginView(
            onBackClick = { finish() }
        )
    }
}



