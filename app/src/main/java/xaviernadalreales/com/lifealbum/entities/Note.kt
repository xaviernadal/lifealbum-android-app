package xaviernadalreales.com.lifealbum.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name="title") val title: String?,
    @ColumnInfo(name="date") val date: String?,
    @ColumnInfo(name="noteText") val noteText : String?,
    @ColumnInfo(name="image_path") val imagePath : String?,
    @ColumnInfo(name="color") val colorNote : String?,
    @ColumnInfo(name="webLink") val webLink : String?
)