package com.example.timemanagement.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.timemanagement.MainActivity
import com.example.timemanagement.adapter.ItemReminder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.reflect.Type
import java.time.OffsetDateTime

class DataStoreInstance private constructor() {
    companion object {

        @Volatile
        private var instance: DataStoreInstance? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: DataStoreInstance().also { instance = it }
            }
    }

    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(OffsetDateTime::class.java, object : JsonSerializer<OffsetDateTime>,
            JsonDeserializer<OffsetDateTime> {
            override fun serialize(
                src: OffsetDateTime?,
                typeOfSrc: Type?,
                context: JsonSerializationContext?
            ): JsonElement = JsonPrimitive(src.toString())

            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): OffsetDateTime = OffsetDateTime.parse(json?.asString)
        }).create()

    private val USER_PREFERENCES_NAME = "my_data_store"

    val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    suspend fun saveList(context: Context, list: List<ItemReminder>) {
        val json = gson.toJson(list)
        val key = stringPreferencesKey("reminders_list")
        context.dataStore.edit { preferences ->
            preferences[key] = json
        }
    }

    fun getList(context: Context): Flow<List<ItemReminder>> {
        val key = stringPreferencesKey("reminders_list")
        return context.dataStore.data
            .map { preferences ->
                preferences[key]?.let {
                    try {
                        gson.fromJson(it, object : TypeToken<List<ItemReminder>>() {}.type)
                    } catch (e: Exception) {
                        emptyList()
                    }
                } ?: emptyList()
            }
    }

    suspend fun clearList(context: Context) {
        val json = gson.toJson(listOf<ItemReminder>())
        val key = stringPreferencesKey("reminders_list")
        context.dataStore.edit { preferences ->
            preferences[key] = json
        }
    }
}