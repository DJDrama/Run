package com.dj.run.domain

import com.dj.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt

object LocationDataCalculator {
    fun getTotalDistanceMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations
            .sumOf {
                it.zipWithNext { a, b ->
                    a.location.location.distanceTo(b.location.location)
                }.sum()
                    .roundToInt()
            }
    }
}