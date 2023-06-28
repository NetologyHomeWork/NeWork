package ru.netology.nework.login.domain.exceptions

import ru.netology.nework.core.domain.AppExceptions
import ru.netology.nework.login.domain.entities.AuthField

class EmptyFieldException(
    val field: AuthField
) : AppExceptions()