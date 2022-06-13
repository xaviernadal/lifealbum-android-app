package xaviernadalreales.com.lifealbum.repository

import android.app.Application
import androidx.lifecycle.LiveData
import xaviernadalreales.com.lifealbum.dao.NoteDao
import xaviernadalreales.com.lifealbum.database.NotesDatabase
import xaviernadalreales.com.lifealbum.entities.Note
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class NoteRepository internal constructor(application: Application) {
    private lateinit var noteDao: NoteDao
    val allNotes: LiveData<List<Note>>
    private val executorService: ExecutorService

    init {
        val database: NotesDatabase? = NotesDatabase.getDatabase(application)
        if (database != null) {
            noteDao = database.noteDao()
        }
        allNotes = noteDao.getAllNotes()
        executorService = Executors.newFixedThreadPool(2)
    }
    suspend fun insert(note: Note?){
        if (note != null) {
            noteDao.insertNote(note)
        }
    }
    suspend fun delete(note:Note?){
        if (note != null) {
            noteDao.deleteNote(note)
        }
    }

    /*
    fun insert(note: Note?) {
        executorService.execute {
            if (note != null) {
                noteDao.insertNote(note)
            }
        }
    }

    fun delete(note: Note) {
        executorService.execute{
            noteDao.deleteNote(note)
        }
    }

    companion object {
        private var instance: NoteRepository? = null
        @Synchronized
        fun getInstance(application: Application): NoteRepository? {
            if (instance == null) instance = NoteRepository(application)
            return instance
        }
    }
    */
}