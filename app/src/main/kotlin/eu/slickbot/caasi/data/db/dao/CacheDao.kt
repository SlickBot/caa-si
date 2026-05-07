package eu.slickbot.caasi.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import eu.slickbot.caasi.data.db.entity.LayerEntity
import eu.slickbot.caasi.data.db.entity.LayerFeatureEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CacheDao {

  @Query("SELECT * FROM layers ORDER BY position ASC")
  abstract fun flowLayers(): Flow<List<LayerEntity>>

  @Query("SELECT * FROM features")
  abstract fun flowFeatures(): Flow<List<LayerFeatureEntity>>

  @Query("SELECT * FROM layers ORDER BY position ASC")
  abstract suspend fun getLayers(): List<LayerEntity>

  @Query("DELETE FROM layers")
  protected abstract suspend fun clearLayers()

  @Query("DELETE FROM features")
  protected abstract suspend fun clearFeatures()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  protected abstract suspend fun insertLayers(layers: List<LayerEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  protected abstract suspend fun insertFeatures(features: List<LayerFeatureEntity>)

  @Transaction
  open suspend fun replaceAll(
    layers: List<LayerEntity>,
    features: List<LayerFeatureEntity>,
  ) {
    clearFeatures()
    clearLayers()
    insertLayers(layers)
    insertFeatures(features)
  }
}
