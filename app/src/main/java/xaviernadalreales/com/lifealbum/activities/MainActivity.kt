package xaviernadalreales.com.lifealbum.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
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

    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageAddElement: ImageView = findViewById(R.id.add_element)

        //https://stackoverflow.com/questions/67886839/how-to-get-requestcode-from-activity-result-api
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (intent != null) {
                        val requestCode: String? = intent.extras?.getString(REQUESTCODE)
                        if (requestCode != null) {
                            Log.d("si o no", intent.getBooleanExtra("noteDeleted" ,false).toString())
                            getNotes(requestCode, intent.getBooleanExtra("noteDeleted", false))
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
        getNotes("SHOW", false)

        //https://stackoverflow.com/questions/40569436/kotlin-addtextchangelistener-lambda
        val inputSearch: EditText = findViewById(R.id.input_search)
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(noteList.size != 0){
                    notesAdapter.search(s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                notesAdapter.cancelTimer()
            }
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    fun getNotes(requestCode: String, noteDeleted: Boolean) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val notes = NotesDatabase.getDatabase(applicationContext)?.noteDao()?.getAllNotes()

            handler.post {
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
                        if (noteDeleted){
                            Log.d("pero", "si")
                            notesAdapter.notifyItemRemoved(noteClickedPosition)
                        } else {
                            if (notes != null) {
                                noteList.add(noteClickedPosition, notes[noteClickedPosition])
                                notesAdapter.notifyItemChanged(noteClickedPosition)
                            }
                        }
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