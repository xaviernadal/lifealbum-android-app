package xaviernadalreales.com.lifealbum.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.adapters.NotesAdapter
import xaviernadalreales.com.lifealbum.database.NotesDatabase
import xaviernadalreales.com.lifealbum.entities.Note
import xaviernadalreales.com.lifealbum.listeners.NotesListener
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity(), NotesListener {

    private lateinit var recyclerViewNotes: RecyclerView
    private var noteList: MutableList<Note> = mutableListOf()
    private lateinit var notesAdapter: NotesAdapter

    private var noteClickedPosition = -1

    companion object {
        private var REQUESTCODE = "REQUEST_CODE"
    }

    //https://stackoverflow.com/questions/67886839/how-to-get-requestcode-from-activity-result-api

    lateinit var resultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageAddElement: ImageView = findViewById(R.id.add_element)
        /*
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                //I had a lot of trouble here since I save the notes by pressing the back button, and the
                //resultCode for that is 0 (cancelled), but the RESULT_OK is -1.
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("claro pero aqui qentre ono", "claro claro4")
                    val intent = result.data
                    getNotes()
                }
            }

         */
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                Log.d("IMPORTANT2", "he arribat?")
                Log.d("IMPORTANT2", result.resultCode.toString())
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("IMPORTANT3", "he arribat?")
                    val intent = result.data
                    if (intent != null) {
                        Log.d("IMPORTANT4", "he arribat?")
                        val requestCode: String? = intent.extras?.getString(REQUESTCODE)

                        if (requestCode != null) {
                            Log.d("IMPORTANT5", "he arribat?")
                            getNotes(requestCode)
                        }
                    }

                }
            }
        imageAddElement.setOnClickListener {
            val intent = Intent(applicationContext, CreateNoteActivity::class.java)
            intent.putExtra("ADD_NOTE", true)
            resultLauncher.launch(intent)

        }
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
        recyclerViewNotes.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        notesAdapter = NotesAdapter(noteList, this)
        recyclerViewNotes.adapter = notesAdapter
        getNotes("SHOW")
    }


    @SuppressLint("NotifyDataSetChanged")
    fun getNotes(requestCode: String) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val notes = NotesDatabase.getDatabase(applicationContext)?.noteDao()?.getAllNotes()

            handler.post {
                Log.d("IMPORTANT", requestCode)
                when (requestCode) {
                    "SHOW" ->
                        if (notes != null) {
                            noteList.addAll(notes)
                            notesAdapter.notifyDataSetChanged()
                        }
                    "ADD_NOTE" -> {
                        noteList.add(0, notes?.get(0)!!)
                        notesAdapter.notifyItemInserted(0)
                        recyclerViewNotes.smoothScrollToPosition(0)
                    }
                    "UPDATE" -> {
                        noteList.removeAt(noteClickedPosition)
                        if (notes != null) {
                            noteList.add(noteClickedPosition, notes[noteClickedPosition])
                        }
                        notesAdapter.notifyItemChanged(noteClickedPosition)
                    }
                }
            }
        }
    }


    override fun onNoteClicked(note: Note, position: Int) {
        noteClickedPosition = position
        val intent = Intent(applicationContext, CreateNoteActivity::class.java)
        intent.putExtra(REQUESTCODE, "UPDATE")
        intent.putExtra("viewOrUpdate", true)
        intent.putExtra("note", note)
        resultLauncher.launch(intent)
    }
}