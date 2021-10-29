package io.github.rsookram.rss.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PruneWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: Repository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        repository.prune()
        return Result.success()
    }
}
