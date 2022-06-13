package xaviernadalreales.com.lifealbum.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.adapters.PeopleAdapter
import xaviernadalreales.com.lifealbum.database.NotesDatabase
import xaviernadalreales.com.lifealbum.database.PeopleDatabase
import xaviernadalreales.com.lifealbum.entities.Person
import xaviernadalreales.com.lifealbum.listeners.GenericListener
import xaviernadalreales.com.lifealbum.viewModel.PersonViewModel
import java.util.concurrent.Executors

class SelectProfile : AppCompatActivity(), GenericListener<Person> {
    private lateinit var recyclerViewProfiles: RecyclerView
    private var peopleList: MutableList<Person> = mutableListOf()
    private lateinit var peopleAdapter: PeopleAdapter

    private var profileClickedPosition = -1
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var personViewModel: PersonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_profile)


        activitiesResults()
        setUpAddProfileButton()

        recyclerViewProfiles = findViewById(R.id.recyclerViewProfiles)
        recyclerViewProfiles.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)

        peopleAdapter = PeopleAdapter(peopleList, this)
        recyclerViewProfiles.adapter = peopleAdapter

        personViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(PersonViewModel::class.java)

        getProfiles(false)
    }

    private fun setUpAddProfileButton() {
        val buttonAddProfile = findViewById<ExtendedFloatingActionButton>(R.id.add_profile_fab)
        buttonAddProfile.setOnClickListener {
            val intent = Intent(this, CreateProfileActivity::class.java)
            intent.putExtra("ADD_PROFILE", false)
            resultLauncher.launch(intent)
        }
    }

    private fun activitiesResults() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (intent != null) {
                        val added = intent.extras?.get("REQUEST_CODE")
                        if (added == "ADD_PROFILE") {
                            getProfiles(true)
                        }
                    }
                }
            }
    }


    override fun onElementClicked(element: Person, position: Int) {
        profileClickedPosition = position
        val intent = Intent()
        intent.putExtra("SELECT_PROFILE", element)
        setResult(RESULT_OK, intent)
        finish()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getProfiles(added: Boolean) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val people = personViewModel.getPeople().value
            handler.post {
                if (people != null) {
                    if (added) {
                        peopleList.add(0, people[0])
                        peopleAdapter.notifyItemInserted(0)
                        recyclerViewProfiles.smoothScrollToPosition(0)
                    } else {
                        peopleList.addAll(people)
                        peopleAdapter.notifyDataSetChanged()

                    }
                }
            }
        }
    }
}

