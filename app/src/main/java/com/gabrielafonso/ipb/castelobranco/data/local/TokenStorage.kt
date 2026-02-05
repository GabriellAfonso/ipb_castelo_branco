// app/src/main/java/com/gabrielafonso/ipb/castelobranco/data/local/TokenStorage.kt
package com.gabrielafonso.ipb.castelobranco.data.local

import android.content.Context
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

import com.gabrielafonso.ipb.castelobranco.domain.model.AuthTokens
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) {

    private object Keys {
        val TOKENS = stringPreferencesKey("auth_tokens")
    }

    suspend fun save(tokens: AuthTokens) {
        dataStore.edit { prefs ->
            prefs[Keys.TOKENS] = json.encodeToString(tokens)
        }
    }

    suspend fun loadOrNull(): AuthTokens? {
        val prefs = dataStore.data.first()
        val raw = prefs[Keys.TOKENS] ?: return null
        return runCatching { json.decodeFromString<AuthTokens>(raw) }.getOrNull()
    }

    suspend fun clear() {
        dataStore.edit { it.remove(Keys.TOKENS) }
    }
}