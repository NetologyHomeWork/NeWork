package ru.netology.nework.core.domain.entities

import android.net.Uri
import java.io.File

data class ImageFileModel(
    val uri: Uri,
    val file: File
)