package ru.netology.nework.posts.presentation.posts

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.observeWhenOnStarted
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.create.presentation.edit.post.EditPostFragment
import ru.netology.nework.databinding.FragmentPostsBinding
import ru.netology.nework.posts.presentation.detail.PostDetailFragment
import ru.netology.nework.posts.presentation.posts.adapter.PostItem
import ru.netology.nework.posts.presentation.posts.adapter.PostsAdapter

@AndroidEntryPoint
class PostsFragment : Fragment(R.layout.fragment_posts) {

    private val binding by viewBinding<FragmentPostsBinding>()

    private val viewModel by viewModels<PostsViewModel>()

    private var adapter: PostsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.postsFlow.observeWhenOnCreated(viewLifecycleOwner) { posts ->
            adapter?.submitList(posts)
        }

        binding.threeStateView.observeStateView(viewModel.threeStateFlow, viewLifecycleOwner)

        viewModel.swipeRefreshingFlow.observeWhenOnStarted(viewLifecycleOwner) { isRefresh ->
            binding.swipeRefresh.isRefreshing = isRefresh
        }

        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { command ->
            when (command) {
                is PostsViewModel.PostsCommands.ShowSnackbar -> {
                    Snackbar.make(
                        requireContext(),
                        binding.root,
                        command.message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.threeStateView.setErrorButtonClickListener(viewModel::onRetryClick)

        binding.swipeRefresh.setOnRefreshListener(viewModel::onSwipeRefresh)

        setFragmentResultListener(
            EditPostFragment.SAVE_POST_REQ_KEY
        ) { _, bundle ->
            val postId = bundle.getLong(EditPostFragment.SAVE_POST_ID_KEY)
            val content = bundle.getString(EditPostFragment.SAVE_POST_CONTENT_KEY, "")
            viewModel.onPostUpdated(postId, content)
        }

        setFragmentResultListener(PostDetailFragment.LIKE_RESULT_REQ_KEY) { _, bundle ->
            val likeResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(PostDetailFragment.LIKE_RESULT_BUNDLE_KEY, PostDetailFragment.LikeResult::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable(PostDetailFragment.LIKE_RESULT_BUNDLE_KEY)
            } ?: return@setFragmentResultListener
            viewModel.likeUpdated(likeResult.postId, likeResult.isLike, likeResult.likeCount)
        }

        setFragmentResultListener(PostDetailFragment.DELETE_RESULT_REQ_KEY) { _, bundle ->
            val postId = bundle.getLong(PostDetailFragment.DELETE_RESULT_BUNDLE_KEY, 0L)
            viewModel.onPostDeleted(postId)
        }
    }

    private fun setupView() {
        adapter = initAdapter()
        binding.rvPosts.adapter = adapter
    }

    private fun initAdapter(): PostsAdapter {
        return PostsAdapter(
            listener = object : PostsAdapter.PostEventListener {

                override fun onLikeClick(postId: Long, isLike: Boolean) {
                    viewModel.likePost(postId, isLike)
                }

                override fun onPostClick(postId: Long) {
                    val direction = PostsFragmentDirections.actionPostsFragmentToPostDetailFragment(postId)
                    findNavController().navigate(direction)
                }

                override fun onDeleteClick(postId: Long) {
                    viewModel.onDeletePostClick(postId)
                }

                override fun onEditClick(post: PostItem) {
                    val direction = PostsFragmentDirections.actionPostsFragmentToEditPostFragment(
                        postId = post.id,
                        content = post.content
                    )
                    findNavController().navigate(direction)
                }
            }
        )
    }
}