package xaviernadalreales.com.lifealbum.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import xaviernadalreales.com.lifealbum.entities.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note)

    @Delete
    fun deleteNote(note: Note)
}