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
import io.github.rsookram.rss.data.PruneWorker
import io.github.rsookram.rss.data.Repository
import io.github.rsookram.rss.data.RssService
import io.github.rsookram.rss.data.SyncWorker
import io.github.rsookram.rss.data.parser.RssConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.create
import java.time.Clock
import java.time.Duration
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

private const val DATABASE_NAME = "rss.db"

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "sync",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<SyncWorker>(Duration.ofDays(1))
                    .setConstraints(
                        Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
                    )
                    .build()
            )

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "prune",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<PruneWorker>(Duration.ofDays(7))
                    .setConstraints(
                        Constraints.Builder().setRequiresCharging(true).build()
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
            Clock.systemUTC(),
            Dispatchers.IO,
        )
    }

    @Provides
    @Singleton
    fun provideRssService(): RssService =
        Retrofit.Builder()
            .baseUrl("https://www.example.test/")
            .addConverterFactory(RssConverterFactory())
            .build()
            .create()

    @Singleton
    @ApplicationScope
    @Provides
    fun providesCoroutineScope() = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
