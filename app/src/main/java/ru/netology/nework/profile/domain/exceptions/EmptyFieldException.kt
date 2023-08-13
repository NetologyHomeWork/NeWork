package ru.netology.nework.profile.domain.exceptions

import ru.netology.nework.core.domain.AppExceptions
import ru.netology.nework.profile.domain.entity.AddJobField

class EmptyFieldException(
    val field: AddJobField
) : AppExceptions()