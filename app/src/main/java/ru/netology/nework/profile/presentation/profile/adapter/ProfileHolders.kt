package ru.netology.nework.profile.presentation.profile.adapter

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.ItemAddJobBinding
import ru.netology.nework.databinding.ItemEmptyJobBinding
import ru.netology.nework.databinding.ItemJobBinding

class ProfileHolder(
    private val binding: ItemJobBinding,
    private val onEditClick: (item: ProfileItems.JobItem) -> Unit,
    private val onDeleteClick: (jobId: Long) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ProfileItems.JobItem) {
        with(binding) {
            tvPosition.text = item.position
            tvCompany.text = item.company
            tvWorkRange.text = item.workRange

            ivDelete.isInvisible = item.deleteInProgress
            progress.isVisible = item.deleteInProgress

            ivEdit.setOnClickListener {
                onEditClick.invoke(item)
            }

            ivDelete.setOnClickListener {
                onDeleteClick.invoke(item.id)
            }
        }
    }

    fun updateJobInfo(
        position: String,
        company: String,
        workRange: String
    ) {
        with(binding) {
            tvPosition.text = position
            tvCompany.text = company
            tvWorkRange.text = workRange
        }
    }

    fun updateDeleteInProgress(deleteInProgress: Boolean) {
        binding.ivDelete.isInvisible = deleteInProgress
        binding.progress.isVisible = deleteInProgress
    }
}

class AddNewJobHolder(
    private val binding: ItemAddJobBinding,
    private val onItemClick: (jobId: Long) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(jobId: Long) {
        binding.btnAddNewJob.setOnClickListener {
            onItemClick.invoke(jobId)
        }
    }
}

class EmptyJobListHolder(
    private val binding: ItemEmptyJobBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ProfileItems.EmptyJobList) {
        binding.tvMessage.text = item.message
    }
}