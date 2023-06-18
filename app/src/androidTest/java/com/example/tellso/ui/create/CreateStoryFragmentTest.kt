package com.example.tellso.ui.create

import android.app.Activity
import android.app.Instrumentation
import android.provider.MediaStore
import androidx.paging.ExperimentalPagingApi
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.tellso.R
import com.example.tellso.ui.main.MainActivity
import com.example.tellso.utils.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class CreateStoryFragmentTest {


    @OptIn(ExperimentalPagingApi::class)
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    fun cleanup() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testCreateStory() {
        // Launch the fragment
        onView(withId(R.id.create_story)).perform(click())

        // Perform actions on the fragment views
        onView(withId(R.id.et_description)).perform(typeText("This is my story to be senior mobile developer"))
        onView(withId(R.id.cameraButton)).perform(click())

        // Wait for the camera intent to finish
        Intents.intended(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
        Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        )

        // Perform assertions or additional actions on the fragment views
        onView(withId(R.id.postButton)).perform(click())

        // Wait for the API call or background process to complete
        // Use appropriate idling resources to wait for the process to finish

        // Perform assertions on the result or navigate to another screen
    }

}