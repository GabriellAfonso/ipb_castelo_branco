package com.gabrielafonso.ipb.castelobranco.utils

import android.content.Context
import java.io.File
import com.google.gson.Gson

data class CacheWrapper(
    val timestamp: Long, // hora em milissegundos
    val data: String
)

object DiskCache {

    private val gson = Gson()

    fun save(context: Context, key: String, data: String) {
        val file = File(context.cacheDir, key)
        val wrapper = CacheWrapper(System.currentTimeMillis(), data)
        file.writeText(gson.toJson(wrapper))
    }

    fun load(context: Context, key: String): String? {
        val file = File(context.cacheDir, key)
        if (!file.exists()) return null

        val wrapper = gson.fromJson(file.readText(), CacheWrapper::class.java)
        return wrapper.data
    }

    fun isCacheValidToday(context: Context, key: String): Boolean {
        val file = File(context.cacheDir, key)
        if (!file.exists()) return false

        val wrapper = gson.fromJson(file.readText(), CacheWrapper::class.java)
        val cacheDay = java.util.Calendar.getInstance().apply { timeInMillis = wrapper.timestamp }
        val today = java.util.Calendar.getInstance()

        return cacheDay.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR)
                && cacheDay.get(java.util.Calendar.DAY_OF_YEAR) == today.get(java.util.Calendar.DAY_OF_YEAR)
    }
    fun isCacheValidWithinMinutes(context: Context, key: String, minutes: Int): Boolean {
        val file = File(context.cacheDir, key)
        if (!file.exists()) return false

        val wrapper = gson.fromJson(file.readText(), CacheWrapper::class.java)
        val now = System.currentTimeMillis()
        val diffMillis = now - wrapper.timestamp
        val diffMinutes = diffMillis / (1000 * 60)

        return diffMinutes < minutes
    }
    fun clear(context: Context, key: String) {
        val file = File(context.cacheDir, key)
        if (file.exists()) file.delete()
    }
}