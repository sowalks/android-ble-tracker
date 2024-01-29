package com.example.bletracker.fake

import com.example.bletracker.rules.TestDispatcherRule
import com.example.bletracker.ui.screens.MarsUiState
import com.example.bletracker.ui.screens.MarsViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MarsViewModelTest {
    @get:Rule
    val testDispatcher = TestDispatcherRule()
    @Test
    fun marsViewModel_getMarsPhotos_verifyMarsUiStateSuccess() =
        runTest{
            val marsViewModel = MarsViewModel(
               locatorRepository = FakeNetworkLocatorRepository()
            )
            assertEquals(
                MarsUiState.Success("Success: ${FakeDataSource.locatorEntries.entries.size} Mars " +
                        "photos retrieved"),
                marsViewModel.marsUiState
            )

        }

}