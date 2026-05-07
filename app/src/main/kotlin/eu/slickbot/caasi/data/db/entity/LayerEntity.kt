package eu.slickbot.caasi.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "layers")
data class LayerEntity(
  @PrimaryKey val id: String,
  val position: Int,
  val json: String,
)
