package io.github.rsookram.rss

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.*
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.rsookram.rss.data.Repository
import io.github.rsookram.rss.data.RssService
import io.github.rsookram.rss.data.SyncWorker
import kotlinx.coroutines.Dispatchers
import me.toptas.rssconverter.RssConverterFactory
import retrofit2.Retrofit
import retrofit2.create
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

private const val DATABASE_NAME = "rss.db"

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        WorkManager.getInstance(this)
            .enqueue(
                PeriodicWorkRequestBuilder<SyncWorker>(Duration.ofHours(12))
                    .setConstraints(
                        Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
                    )
                    .build()
            )
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideRepository(@ApplicationContext context: Context, service: RssService): Repository {
        return Repository(
            Database(
                AndroidSqliteDriver(
                    Database.Schema,
                    context,
                    name = DATABASE_NAME,
                    callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            db.execSQL("PRAGMA foreign_keys=ON;")
                        }
                    }
                )
            ),
            service,
            Dispatchers.IO,
        )
    }

    @Provides
    @Singleton
    fun provideRssService(): RssService =
        Retrofit.Builder()
            .baseUrl("https://www.example.test/")
            .addConverterFactory(RssConverterFactory.create())
            .build()
            .create()
}
