package ru.netology.nework.posts

import androidx.fragment.app.Fragment
import ru.netology.nework.R
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.databinding.FragmentPostsBinding

class PostsFragment : Fragment(R.layout.fragment_posts) {

    private val binding by viewBinding<FragmentPostsBinding>()
}