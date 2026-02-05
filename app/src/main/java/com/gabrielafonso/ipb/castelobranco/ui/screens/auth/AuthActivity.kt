package com.gabrielafonso.ipb.castelobranco.ui.screens.auth

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseActivity

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {


    override fun onPreCreate(savedInstanceState: Bundle?) {
        super.onPreCreate(savedInstanceState)

    }

    @Composable
    override fun ScreenContent() {
        val navController = rememberNavController()
        AuthNavGraph(
            navController = navController,
            onFinish = { finish() }
        )
    }
}



