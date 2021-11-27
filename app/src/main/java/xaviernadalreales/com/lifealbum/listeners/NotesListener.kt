package xaviernadalreales.com.lifealbum.listeners

import xaviernadalreales.com.lifealbum.entities.Note

interface NotesListener {
    fun onNoteClicked(note : Note, position: Int)
}