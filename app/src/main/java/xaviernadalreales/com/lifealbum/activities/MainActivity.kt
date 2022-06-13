package xaviernadalreales.com.lifealbum.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.adapters.NotesAdapter
import xaviernadalreales.com.lifealbum.database.NotesDatabase
import xaviernadalreales.com.lifealbum.entities.Note
import xaviernadalreales.com.lifealbum.listeners.GenericListener
import xaviernadalreales.com.lifealbum.viewModel.NoteViewModel
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity(), GenericListener<Note> {

    private lateinit var recyclerViewNotes: RecyclerView
    private var noteList: MutableList<Note> = mutableListOf()
    private lateinit var notesAdapter: NotesAdapter
    lateinit var viewModel: NoteViewModel
    private var noteClickedPosition = -1

    companion object {
        private var REQUESTCODE = "REQUEST_CODE"
    }

    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activitiesResult()
        setUpBottomNavigation()
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
        setUpRecyclerView()
        recyclerViewListener()
        searchNotes()

        initViewModel()

        getNotes("SHOW", false)
    }

    private fun activitiesResult() {
        //https://stackoverflow.com/questions/67886839/how-to-get-requestcode-from-activity-result-api
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (intent != null) {
                        val requestCode: String? = intent.extras?.getString(REQUESTCODE)
                        if (requestCode != null) {
                            getNotes(requestCode, intent.getBooleanExtra("noteDeleted", false))
                        }
                    }
                }
            }
    }

    private fun setUpBottomNavigation() {
        val bnv = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.activity_main_item -> recyclerViewNotes.smoothScrollToPosition(0)

                R.id.activity_create_note -> {
                    val intent = Intent(applicationContext, CreateNoteActivity::class.java)
                    intent.putExtra("ADD_NOTE", true)
                    resultLauncher.launch(intent)
                }
                R.id.layout_people -> {
                    val intent = Intent(this, PeopleActivity::class.java)
                    startActivity(intent)

                }
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun setUpRecyclerView() {
        recyclerViewNotes.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        notesAdapter = NotesAdapter(noteList, this)
        recyclerViewNotes.adapter = notesAdapter
    }

    private fun recyclerViewListener() {
        val imageColumns: ImageView = findViewById(R.id.imageLayoutColumns)
        val imageRows: ImageView = findViewById(R.id.imageLayoutRows)
        imageColumns.setOnClickListener {
            recyclerViewNotes.layoutManager =
                StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)

            notesAdapter = NotesAdapter(noteList, this, displayLineChange = true)
            recyclerViewNotes.adapter = notesAdapter
            imageColumns.visibility = View.GONE
            imageRows.visibility = View.VISIBLE
        }
        imageRows.setOnClickListener {
            setUpRecyclerView()
            imageRows.visibility = View.GONE
            imageColumns.visibility = View.VISIBLE
        }
    }

    private fun searchNotes() {
        //https://stackoverflow.com/questions/40569436/kotlin-addtextchangelistener-lambda
        val inputSearch: EditText = findViewById(R.id.input_search)
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (noteList.size != 0) {
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

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModel::class.java)
        viewModel.allNotes.observe(this, Observer { list ->
            list.let {
                notesAdapter.updateList(noteList)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getNotes(requestCode: String, noteDeleted: Boolean) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val notes = viewModel.getNotes()
            //LiveData notes
            handler.post {
                when (requestCode) {
                    "ADD_NOTE" -> {
                        notes.value?.let { notesAdapter.updateList(it) }
                        recyclerViewNotes.smoothScrollToPosition(0)
                    }
                    "UPDATE" -> {
                        val note = viewModel.getNotes().value?.get(noteClickedPosition)!!
                        if (noteDeleted) {
                            viewModel.deleteNote(note)
                            Toast.makeText(this, "Note Deleted", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.insertNote(note)
                        }
                        recyclerViewNotes.smoothScrollToPosition(0)
                    }
                }
            }
        }
    }

    override fun onElementClicked(element: Note, position: Int) {
        noteClickedPosition = position
        val intent = Intent(applicationContext, CreateNoteActivity::class.java)
        intent.putExtra(REQUESTCODE, "UPDATE")
        intent.putExtra("viewOrUpdate", true)
        intent.putExtra("note", element)
        resultLauncher.launch(intent)
    }
}
