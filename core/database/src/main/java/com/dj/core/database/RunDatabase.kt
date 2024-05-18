package com.dj.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dj.core.database.dao.RunDao
import com.dj.core.database.dao.RunPendingSyncDao
import com.dj.core.database.entity.DeletedRunSyncEntity
import com.dj.core.database.entity.RunEntity
import com.dj.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [RunEntity::class, RunPendingSyncEntity::class, DeletedRunSyncEntity::class],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao
    abstract val runPendingSyncDao: RunPendingSyncDao
}