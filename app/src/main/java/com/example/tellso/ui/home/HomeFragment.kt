package com.example.tellso.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tellso.adapter.StoriesResponseAdapter
import com.example.tellso.data.remote.response.Story
import com.example.tellso.databinding.FragmentHomeBinding
import com.example.tellso.utils.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var token: String = ""

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: StoriesResponseAdapter

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        arguments?.let {
            token = it.getString(ARG_TOKEN) ?: ""
        }

        if (token.isEmpty()) {
            println("token is empty")
        } else {
            println("HomeFragment token: $token")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSwipeRefreshLayout()
        setRecyclerView()
        getAllStories()
    }

    private fun getAllStories() {
        binding.progressBar.animateVisibility(true)
        binding.swipeRefresh.isRefreshing = true

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {

                viewModel.getAllStories(token).collect { result ->
                    result.onSuccess { response ->
                        updateRecyclerViewData(response.stories)

                        binding.apply {
                            tvNotFound.animateVisibility(response.stories.isEmpty())
                            ivNotFound.animateVisibility(response.stories.isEmpty())
                            rvStories.animateVisibility(response.stories.isNotEmpty())
                            progressBar.animateVisibility(false)
                            swipeRefresh.isRefreshing = false
                        }
                    }

                    result.onFailure {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()

                        binding.apply {
                            tvNotFound.animateVisibility(true)
                            ivNotFound.animateVisibility(true)
                            rvStories.animateVisibility(false)
                            progressBar.animateVisibility(false)
                            swipeRefresh.isRefreshing = false
                        }
                    }
                }
            }
        }

    }

    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        listAdapter = StoriesResponseAdapter()

        recyclerView = binding.rvStories.apply {
            layoutManager = linearLayoutManager
            adapter = listAdapter
        }

    }

    private fun setSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            getAllStories()
            binding.progressBar.animateVisibility(false)
        }

    }

    private fun updateRecyclerViewData(stories: List<Story>) {
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        listAdapter.submitList(stories)
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_TOKEN = "arg_token"
    }
}