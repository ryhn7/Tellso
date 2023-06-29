package com.example.tellso.data

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.tellso.adapter.StoriesResponseAdapter
import com.example.tellso.data.local.room.StoryDatabase
import com.example.tellso.data.remote.response.StoriesResponse
import com.example.tellso.data.remote.retrofit.ApiService
import com.example.tellso.utils.DataDummy
import com.example.tellso.utils.MainDispatcherRule
import com.example.tellso.utils.PagedTestDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepoTest {

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyDatabase: StoryDatabase

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var storyRepositoryMock: StoryRepoImpl

    private lateinit var storyRepository: StoryRepoImpl

    private val dummyToken = "authentication_token"
    private val dummyMultipart = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()
    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()

    @Before
    fun setup() {
        storyRepository = StoryRepoImpl( apiService, storyDatabase)
    }

    @Test
    fun `Get stories with pager - successfully`() = runTest {
        val dummyStories = DataDummy.generateDummyListStory()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val expectedResult = flowOf(data)

        Mockito.`when`(storyRepositoryMock.getAllStories(dummyToken)).thenReturn(expectedResult)

        storyRepositoryMock.getAllStories(dummyToken).collect { actualResult ->
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoriesResponseAdapter.StoryDiffCallback,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = mainDispatcherRules.testDispatcher,
                workerDispatcher = mainDispatcherRules.testDispatcher
            )
            differ.submitData(actualResult)

            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(
                dummyStoriesResponse.stories.size,
                differ.snapshot().size
            )
        }

    }

    @Test
    fun `Get stories with location - successfully`() = runTest {
        val expectedResult = flowOf(Result.success(dummyStoriesResponse))

        Mockito.`when`(storyRepositoryMock.getAllStoriesWithLocation(dummyToken)).thenReturn(expectedResult)

        storyRepositoryMock.getAllStoriesWithLocation(dummyToken).collect { result ->
            Assert.assertTrue(result.isSuccess)
            Assert.assertFalse(result.isFailure)

            result.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(dummyStoriesResponse, actualResponse)
            }
        }
    }

    @Test
    fun `Get stories with location - throw exception`() = runTest {
        val expectedResponse = flowOf<Result<StoriesResponse>>(Result.failure(Exception("failed")))

        Mockito.`when`(storyRepositoryMock.getAllStoriesWithLocation(dummyToken)).thenReturn(
            expectedResponse
        )

        storyRepositoryMock.getAllStoriesWithLocation(dummyToken).collect { result ->
            Assert.assertFalse(result.isSuccess)
            Assert.assertTrue(result.isFailure)

            result.onFailure {
                Assert.assertNotNull(it)
            }
        }
    }

    @Test
    fun `Upload image file - successfully`() = runTest {
        val expectedResponse = DataDummy.generateDummyFileUploadResponse()

        Mockito.`when`(
            apiService.uploadStory(
                dummyToken.generateBearerToken(),
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        ).thenReturn(expectedResponse)

        storyRepository.uploadStory(dummyToken, dummyMultipart, dummyDescription, null, null)
            .collect { result ->
                Assert.assertTrue(result.isSuccess)
                Assert.assertFalse(result.isFailure)

                result.onSuccess { actualResponse ->
                    Assert.assertEquals(expectedResponse, actualResponse)
                }
            }

        Mockito.verify(apiService)
            .uploadStory(
                dummyToken.generateBearerToken(),
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        Mockito.verifyNoInteractions(storyDatabase)
    }

    @Test
    fun `Upload image file - throw exception`() = runTest {

        Mockito.`when`(
            apiService.uploadStory(
                dummyToken.generateBearerToken(),
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        ).then { throw Exception() }

        storyRepository.uploadStory(dummyToken, dummyMultipart, dummyDescription, null, null)
            .collect { result ->
                Assert.assertFalse(result.isSuccess)
                Assert.assertTrue(result.isFailure)

                result.onFailure {
                    Assert.assertNotNull(it)
                }
            }

        Mockito.verify(apiService).uploadStory(
            dummyToken.generateBearerToken(),
            dummyMultipart,
            dummyDescription,
            null,
            null
        )
    }

    private fun String.generateBearerToken(): String {
        return "Bearer $this"
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}