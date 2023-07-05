package ru.netology.nework.posts.presentation.posts

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.observeWhenOnStarted
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.databinding.FragmentPostsBinding
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
                    // todo navigate when feature create will be done
                    Toast.makeText(requireContext(), "Navigate to Create", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}