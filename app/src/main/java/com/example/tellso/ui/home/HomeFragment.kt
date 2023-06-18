package com.example.tellso.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tellso.R
import com.example.tellso.adapter.LoadingStateAdapter
import com.example.tellso.adapter.StoriesResponseAdapter
import com.example.tellso.data.local.entity.Story
import com.example.tellso.data.remote.response.StoryItem
import com.example.tellso.databinding.FragmentHomeBinding
import com.example.tellso.utils.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
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
        (activity as AppCompatActivity).setSupportActionBar(binding.appBarMain.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
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
        viewModel.getAllStories(token).observe(viewLifecycleOwner) { result ->
            updateRecyclerViewData(result)
        }
    }


    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        listAdapter = StoriesResponseAdapter()

        listAdapter.addLoadStateListener { loadState ->
            if ((loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && listAdapter.itemCount < 1) || loadState.source.refresh is LoadState.Error) {
                binding.apply {
                    ivNotFound.animateVisibility(true)
                    tvNotFound.animateVisibility(true)
                    rvStories.animateVisibility(false)
                }
            } else {
                binding.apply {
                    ivNotFound.animateVisibility(false)
                    tvNotFound.animateVisibility(false)
                    rvStories.animateVisibility(true)
                }
            }
            binding.swipeRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading
        }

        try {
            recyclerView = binding.rvStories
            recyclerView.apply {
                adapter = listAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        listAdapter.retry()
                    }
                )
                layoutManager = linearLayoutManager
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }

    private fun setSwipeRefreshLayout() {
        //            change color of refresh icon
        binding.swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue
            )
        )

        binding.swipeRefresh.setOnRefreshListener {
            getAllStories()
            binding.viewLoading.animateVisibility(false)
        }

    }

    private fun updateRecyclerViewData(stories: PagingData<Story>) {
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        listAdapter.submitData(lifecycle, stories)
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