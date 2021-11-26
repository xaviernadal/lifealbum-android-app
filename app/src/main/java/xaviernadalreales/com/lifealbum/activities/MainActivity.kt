package xaviernadalreales.com.lifealbum.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.adapters.NotesAdapter
import xaviernadalreales.com.lifealbum.database.NotesDatabase
import xaviernadalreales.com.lifealbum.entities.Note
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewNotes: RecyclerView
    private var noteList: MutableList<Note> = mutableListOf()
    private lateinit var notesAdapter: NotesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageAddElement: ImageView = findViewById(R.id.add_element)

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("claro pero aqui qentre ono","claro claro")
                    val intent = result.data
                    getNotes()
                }
            }
        imageAddElement.setOnClickListener {
            resultLauncher.launch(Intent(applicationContext, CreateNoteActivity::class.java))
        }
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
        recyclerViewNotes.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        notesAdapter = NotesAdapter(noteList)
        recyclerViewNotes.adapter = notesAdapter
        getNotes()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getNotes() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val notes = NotesDatabase.getDatabase(applicationContext)?.noteDao()?.getAllNotes()

            handler.post {

                /*
            Check if the list is empty -> app just started
            -> add all notes from the database to the note list
            -> notify the adapter

            List is not empty -> notes already loaded -> adding last note
            -> notify adapter -> Scroll to the top
             */
                //Log.d("MyNotes", notes.toString())

                if (noteList.isEmpty()) {
                    if (notes != null) {
                        noteList.addAll(notes)
                    }
                    notesAdapter.notifyDataSetChanged()

                } else {
                    if (notes != null) {
                        noteList.add(0, notes[0])
                    }
                    notesAdapter.notifyDataSetChanged()
                }
                //Log.d("MyNotesList", noteList.toString())
                recyclerViewNotes.smoothScrollToPosition(0)
            }
        }
    }
}