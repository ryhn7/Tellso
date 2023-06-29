package com.example.tellso.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.tellso.adapter.StoriesResponseAdapter
import com.example.tellso.data.StoryRepoImpl
import com.example.tellso.domain.entity.Story
import com.example.tellso.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class HomeVMTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()


    private lateinit var homeViewModel: HomeViewModel
    @Mock
    private lateinit var storyRepoImpl: StoryRepoImpl



    private val dummyToken = "authentication_token"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        homeViewModel = HomeViewModel(storyRepoImpl)
    }

    @Test
    fun `Get all stories successfully`() = runTest {
        val dummyStories = DataDummy.generateDummyListStory()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val stories = flowOf(data)

        Mockito.`when`(storyRepoImpl.getAllStories(dummyToken)).thenReturn(stories)

        val actualStories = homeViewModel.getAllStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesResponseAdapter.StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatcherRules.testDispatcher,
            workerDispatcher = mainDispatcherRules.testDispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        Mockito.verify(storyRepoImpl).getAllStories(dummyToken)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])

    }

    @Test
    fun `Get all stories is Empty should return no data`() = runTest {
        val emptyList = emptyList<Story>()
        val data = PagedTestDataSource.snapshot(emptyList)

        val stories = flowOf(data)

        Mockito.`when`(storyRepoImpl.getAllStories(dummyToken)).thenReturn(stories)

        val actualStories = homeViewModel.getAllStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesResponseAdapter.StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatcherRules.testDispatcher,
            workerDispatcher = mainDispatcherRules.testDispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        Mockito.verify(storyRepoImpl).getAllStories(dummyToken)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(0, differ.snapshot().size)
    }


    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}