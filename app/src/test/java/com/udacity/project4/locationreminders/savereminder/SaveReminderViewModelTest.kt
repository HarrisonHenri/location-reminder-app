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

    //TODO: provide testing to the SaveReminderView and its live data objects
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
        val reminderDataItem = ReminderDataItem(
                "Philippines",
                "Location of Philippines",
                "Thailand",
                12.8797,
                121.7740,
                "1"
        )
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminderDataItem)
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
        val reminderDataItem = ReminderDataItem(
                "Singapore",
                "Singapore",
                null,
                1.3521,
                103.8198,
                "2"
        )
        assertThat(saveReminderViewModel.validateEnteredData(reminderDataItem), `is`(false))
        assertThat(
                saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
                `is`(R.string.err_select_location)
        )
    }

}