package com.dj.run.domain

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.dj.core.domain.location.Location
import com.dj.core.domain.location.LocationWithAltitude
import com.dj.test.LocationObserverFake
import com.dj.test.MainCoroutineExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class RunningTrackerTest {

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    private lateinit var runningTracker: RunningTracker
    private lateinit var locationObserverFake: LocationObserverFake

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: CoroutineScope

    @BeforeEach
    fun setup() {
        locationObserverFake = LocationObserverFake()

        // testDispatcher = StandardTestDispatcher() // for fine-grained control
        // testDispatcher = UnconfinedTestDispatcher() // no need to write advanceUntilIdle, runCurrent, and etc*/

        testDispatcher = mainCoroutineExtension.testDispatcher

        testScope = CoroutineScope(testDispatcher)

        runningTracker = RunningTracker(
            locationObserver = locationObserverFake,
            applicationScope = testScope
        )
    }

    @Test
    fun testCombiningRunData() = runTest {
        runningTracker.runData.test {
            skipItems(count = 1)

            runningTracker.startObservingLocation()
            runningTracker.setIsTracking(isTracking = true)

            // testDispatcher.scheduler.runCurrent() // run all pending coroutines (for standard test dispatcher)

            val location1 = LocationWithAltitude(location = Location(1.0, 1.0), altitude = 1.0)
            locationObserverFake.trackLocation(location = location1)
            val emission1 = awaitItem()

            val location2 = LocationWithAltitude(location = Location(2.0, 2.0), altitude = 2.0)
            locationObserverFake.trackLocation(location = location2)
            val emission2 = awaitItem()

            testScope.cancel()

            assertThat(emission1.locations[0][0].location).isEqualTo(expected = location1)
            assertThat(emission2.locations[0][1].location).isEqualTo(expected = location2)


        }
    }
}