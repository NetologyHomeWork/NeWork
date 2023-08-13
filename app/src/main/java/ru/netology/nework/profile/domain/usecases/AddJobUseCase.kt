package ru.netology.nework.profile.domain.usecases

import ru.netology.nework.core.domain.entities.JobData
import ru.netology.nework.core.utils.Resource
import ru.netology.nework.core.utils.compareDate
import ru.netology.nework.core.utils.toIso8601
import ru.netology.nework.profile.domain.entity.AddJobField
import ru.netology.nework.profile.domain.exceptions.CurrentJobException
import ru.netology.nework.profile.domain.exceptions.EmptyFieldException
import ru.netology.nework.profile.domain.exceptions.WrongRangeDateException
import ru.netology.nework.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class AddJobUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend fun execute(
        name: String,
        position: String,
        start: String,
        finish: String?,
        isCurrentJob: Boolean
    ): Resource<JobData> {
        if (name.isBlank()) return Resource.Error(EmptyFieldException(AddJobField.COMPANY))
        if (position.isBlank()) return Resource.Error(EmptyFieldException(AddJobField.POSITION))
        if (start.isBlank()) return Resource.Error(EmptyFieldException(AddJobField.START_DATE))
        if (isCurrentJob.not() && finish.isNullOrBlank()) return Resource.Error(CurrentJobException())
        if (!finish.isNullOrBlank() && start.compareDate(finish)) {
            return Resource.Error(WrongRangeDateException())
        }
        val finishDate = if (finish.isNullOrBlank()) null else finish
        return profileRepository.addJob(
            JobData(
                id = ID_FOR_CREATE_JOB,
                name = name,
                position = position,
                start = start.toIso8601(),
                finish = finishDate?.toIso8601(),
                link = null
            )
        )
    }

    private companion object {
        private const val ID_FOR_CREATE_JOB = 0L
    }
}