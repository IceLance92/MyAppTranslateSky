package com.example.myapptranclatesky.di

import android.content.Context
import androidx.room.Room
import com.example.myapptranclatesky.data.TranslationRepository
import com.example.myapptranclatesky.data.local.AppDatabase
import com.example.myapptranclatesky.data.remote.SkyengService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ServiceLocator {
    private var db: AppDatabase? = null
    private var repo: TranslationRepository? = null

    fun provideRepository(context: Context): TranslationRepository {
        return repo ?: synchronized(this) {
            repo ?: run {
                val client = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }).build()

                val service = Retrofit.Builder()
                    .baseUrl("https://dictionary.skyeng.ru/doc/api/external/")
                    .client(client)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(SkyengService::class.java)

                val database = db ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "translations.db"
                ).fallbackToDestructiveMigration().build().also { db = it }

                TranslationRepository(service, database.translations()).also { repo = it }
            }
        }
    }
}