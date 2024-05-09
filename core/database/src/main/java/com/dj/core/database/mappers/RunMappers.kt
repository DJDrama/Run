package com.dj.core.database.mappers

import com.dj.core.database.entity.RunEntity
import com.dj.core.domain.location.Location
import com.dj.core.domain.run.Run
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunEntity.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUTc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(
            lat = latitude,
            long = longitude,
        ),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}

fun Run.toRunEntity(): RunEntity {
    return RunEntity(
        id = id ?: ObjectId().toHexString(),
        durationMillis = duration.inWholeMicroseconds,
        maxSpeedKmh = maxSpeedKmh,
        dateTimeUTc = dateTimeUtc.toInstant().toString(),
        latitude = location.lat,
        longitude = location.long,
        avgSpeedKmh = avgSpeedKmh,
        distanceMeters = distanceMeters,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}