package ru.netology.nework.profile.presentation.profile.adapter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.netology.nework.R
import ru.netology.nework.core.domain.entities.JobData
import ru.netology.nework.core.utils.DAY_MONTH_YEAR_FORMAT
import ru.netology.nework.core.utils.MONTH_YEAR_FORMAT
import ru.netology.nework.core.utils.ResourcesManager
import ru.netology.nework.core.utils.dateFormat

private const val CREATE_JOB_ID = 0L
private const val EMPTY_LIST_ID = -1L

sealed class ProfileItems(open val id: Long) {

    @Parcelize
    data class JobItem(
        override val id: Long,
        val position: String,
        val company: String,
        val workRange: String,
        val deleteInProgress: Boolean,
        val startJob: String,
        val finishJob: String?
    ) : ProfileItems(id), Parcelable

    object AddJobItem : ProfileItems(CREATE_JOB_ID)

    data class EmptyJobList(
        val message: String
    ) : ProfileItems(EMPTY_LIST_ID)
}

fun JobData.toJobItem(resourcesManager: ResourcesManager): ProfileItems.JobItem {
    val start = this.start.dateFormat(outputFormat = MONTH_YEAR_FORMAT)
    val end = this.finish?.dateFormat(outputFormat = MONTH_YEAR_FORMAT) ?: resourcesManager.getString(R.string.present)
    val workRange = resourcesManager.getString(R.string.work_range, start, end)
    return ProfileItems.JobItem(
        id = this.id,
        position = this.position,
        company = this.name,
        workRange = workRange,
        deleteInProgress = false,
        startJob = this.start.dateFormat(outputFormat = DAY_MONTH_YEAR_FORMAT),
        finishJob = this.finish?.dateFormat(outputFormat = DAY_MONTH_YEAR_FORMAT)
    )
}

fun List<JobData>.toJobItemList(resourcesManager: ResourcesManager): List<ProfileItems.JobItem> {
    return this.map { it.toJobItem(resourcesManager) }
}
