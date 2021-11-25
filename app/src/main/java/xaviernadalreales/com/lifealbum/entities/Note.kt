package xaviernadalreales.com.lifealbum.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "date") var date: String = "",
    @ColumnInfo(name = "noteText") var noteText: String = "",
    @ColumnInfo(name = "image_path") var imagePath: String = "",
    @ColumnInfo(name = "colorNote") var colorNote: String = "",
    @ColumnInfo(name = "webLink") var webLink: String = ""
)
