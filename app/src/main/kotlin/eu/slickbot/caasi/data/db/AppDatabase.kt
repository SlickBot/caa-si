package eu.slickbot.caasi.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import eu.slickbot.caasi.data.db.dao.CacheDao
import eu.slickbot.caasi.data.db.entity.LayerEntity
import eu.slickbot.caasi.data.db.entity.LayerFeatureEntity

@Database(
  entities = [LayerEntity::class, LayerFeatureEntity::class],
  version = 1,
  exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun cacheDao(): CacheDao

  companion object {
    fun create(context: Context): AppDatabase = Room.databaseBuilder(
      context.applicationContext,
      AppDatabase::class.java,
      "caasi.db",
    ).build()
  }
}
