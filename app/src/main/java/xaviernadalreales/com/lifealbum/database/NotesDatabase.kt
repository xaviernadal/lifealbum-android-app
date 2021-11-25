package xaviernadalreales.com.lifealbum.database

import android.content.Context
import androidx.room.Database
import xaviernadalreales.com.lifealbum.entities.Note
import androidx.room.Room
import androidx.room.RoomDatabase
import xaviernadalreales.com.lifealbum.dao.NoteDao


@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {


    companion object {

        private var notesDatabase: NotesDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): NotesDatabase? {
            if (notesDatabase == null) {
                notesDatabase = Room.databaseBuilder(
                    context,
                    NotesDatabase::class.java,
                    "notes_db"
                ).build()
            }
            return notesDatabase
        }

    }

    abstract fun noteDao(): NoteDao


}