package xaviernadalreales.com.lifealbum.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.entities.Note
import java.text.SimpleDateFormat
import java.util.*
import android.os.Looper
import android.util.Log
import xaviernadalreales.com.lifealbum.database.NotesDatabase
import java.util.concurrent.Executors


class CreateNoteActivity : AppCompatActivity() {

    private lateinit var inputNoteTitle: EditText;
    private lateinit var inputNoteText: EditText
    private lateinit var date: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val imageBack: ImageView = findViewById(R.id.back);
        imageBack.setOnClickListener { saveNote(); onBackPressed() }

        inputNoteTitle = findViewById(R.id.noteTitle)
        inputNoteText = findViewById(R.id.note)
        date = findViewById(R.id.date)
        date.text =
            SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())
    }

    private fun saveNote() {
        if (inputNoteTitle.text.toString().trim().isEmpty() && inputNoteText.text.toString().trim()
                .isEmpty()
        ) {
            Toast.makeText(this, "Note is empty.", Toast.LENGTH_SHORT).show()
            return
        }


        val note = Note(0)
        note.title = inputNoteTitle.text.toString()
        note.noteText = inputNoteText.text.toString()
        note.date = date.text.toString()

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            NotesDatabase.getDatabase(applicationContext)?.noteDao()?.insertNote(note)
        }
        handler.post {
            setResult(RESULT_OK, Intent())
            finish()
        }
    }

}