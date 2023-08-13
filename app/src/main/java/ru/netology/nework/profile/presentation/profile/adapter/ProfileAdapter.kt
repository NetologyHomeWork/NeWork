package ru.netology.nework.profile.presentation.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.ItemAddJobBinding
import ru.netology.nework.databinding.ItemEmptyJobBinding
import ru.netology.nework.databinding.ItemJobBinding

private const val ADD_NEW_JOB_ITEM_POSITION = 0
private const val JOB_ITEM_POSITION = 1
private const val EMPTY_JOB_ITEM_POSITION = 2

class ProfileAdapter(
    private val onEditClick: (item: ProfileItems.JobItem) -> Unit,
    private val onDeleteClick: (jobId: Long) -> Unit,
    private val onItemClick: (jobId: Long) -> Unit
) : ListAdapter<ProfileItems, RecyclerView.ViewHolder>(ProfileDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            JOB_ITEM_POSITION -> {
                ProfileHolder(
                    binding = ItemJobBinding.inflate(inflater, parent, false),
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick
                )
            }

            ADD_NEW_JOB_ITEM_POSITION -> {
                AddNewJobHolder(
                    binding = ItemAddJobBinding.inflate(inflater, parent, false),
                    onItemClick = onItemClick
                )
            }

            EMPTY_JOB_ITEM_POSITION -> {
                EmptyJobListHolder(
                    binding = ItemEmptyJobBinding.inflate(inflater, parent, false)
                )
            }

            else -> throw IllegalStateException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return onBindViewHolder(holder, position, mutableListOf())
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when (val item = getItem(position)) {

            is ProfileItems.JobItem -> {
                val profileHolder = holder as? ProfileHolder
                if (payloads.isNotEmpty()) {
                    payloads.forEach { payload ->
                        when (payload) {
                            is JobInfo -> {
                                profileHolder?.updateJobInfo(
                                    payload.position,
                                    payload.company,
                                    payload.workRange
                                )
                            }

                            is DeleteInProgress -> {
                                profileHolder?.updateDeleteInProgress(payload.deleteInProgress)
                            }

                            else -> {
                                profileHolder?.bind(item)
                            }
                        }
                    }
                } else {
                    profileHolder?.bind(item)
                }
            }

            ProfileItems.AddJobItem -> (holder as? AddNewJobHolder)?.bind(item.id)
            is ProfileItems.EmptyJobList -> (holder as? EmptyJobListHolder)?.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ProfileItems.JobItem -> JOB_ITEM_POSITION
            ProfileItems.AddJobItem -> ADD_NEW_JOB_ITEM_POSITION
            is ProfileItems.EmptyJobList -> EMPTY_JOB_ITEM_POSITION
        }
    }

    class ProfileDiffUtil : DiffUtil.ItemCallback<ProfileItems>() {
        override fun areItemsTheSame(oldItem: ProfileItems, newItem: ProfileItems): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProfileItems, newItem: ProfileItems): Boolean {
            return when (oldItem) {
                is ProfileItems.JobItem -> {
                    newItem is ProfileItems.JobItem && oldItem == newItem
                }

                ProfileItems.AddJobItem -> true
                is ProfileItems.EmptyJobList -> true
            }
        }

        override fun getChangePayload(oldItem: ProfileItems, newItem: ProfileItems): Any? {
            return when {
                oldItem is ProfileItems.JobItem && newItem is ProfileItems.JobItem
                        && (oldItem.position != newItem.position
                        && oldItem.company != newItem.company
                        && oldItem.workRange != newItem.workRange) -> {
                    JobInfo(newItem.position, newItem.company, newItem.workRange)
                }

                oldItem is ProfileItems.JobItem && newItem is ProfileItems.JobItem
                        && oldItem.deleteInProgress != newItem.deleteInProgress -> {
                    DeleteInProgress(newItem.deleteInProgress)
                }

                else -> super.getChangePayload(oldItem, newItem)
            }
        }
    }
}

private data class DeleteInProgress(val deleteInProgress: Boolean)

private data class JobInfo(
    val position: String,
    val company: String,
    val workRange: String
)