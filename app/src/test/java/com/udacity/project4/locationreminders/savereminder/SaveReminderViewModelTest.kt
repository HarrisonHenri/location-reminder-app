package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SaveReminderViewModelTest {

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val fakeDataSource = FakeDataSource()

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun createViewModel() {
        stopKoin()
        saveReminderViewModel =
                SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }


    @Test
    fun saveReminderViewModelTest_whenSavingReminder_shouldShowLoadingAndNavigateBack() {
        val reminder = ReminderDataItem(
                "Salvador",
                "Any local at Salvador",
                "Salvador",
                32.8797,
                -121.0,
                "1"
        )
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminder)
        var showLoading = saveReminderViewModel.showLoading.getOrAwaitValue()
        val navigate = saveReminderViewModel.navigationCommand.getOrAwaitValue()
        assertThat(showLoading, `is`(true))
        mainCoroutineRule.resumeDispatcher()
        showLoading = saveReminderViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoading, `is`(false))
        assertThat(navigate, instanceOf(NavigationCommand.Back::class.java))
    }

    @Test
    fun saveReminderViewModelTest_whenSavingReminderWithNullLocation_shouldShowLoadingAndNavigateBack() {
        val reminder = ReminderDataItem(
                "Salvador",
                "Any local at Salvador",
                "Salvador",
                32.8797,
                -121.0,
                "1"
        )
        assertThat(saveReminderViewModel.validateEnteredData(reminder), `is`(false))
        assertThat(
                saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
                `is`(R.string.err_select_location)
        )
    }

}