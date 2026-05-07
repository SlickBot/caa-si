package eu.slickbot.caasi.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "features",
  indices = [Index("layerId")],
)
data class LayerFeatureEntity(
  @PrimaryKey(autoGenerate = true) val rowId: Long = 0,
  val layerId: String,
  val featureId: Int,
  val json: String,
)
