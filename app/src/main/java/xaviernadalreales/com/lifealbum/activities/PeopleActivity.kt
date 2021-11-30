package xaviernadalreales.com.lifealbum.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.adapters.PeopleAdapter
import xaviernadalreales.com.lifealbum.database.PeopleDatabase
import xaviernadalreales.com.lifealbum.entities.Person
import xaviernadalreales.com.lifealbum.listeners.GenericListener
import java.util.concurrent.Executors

class PeopleActivity : AppCompatActivity(), GenericListener<Person> {

    private lateinit var recyclerViewProfiles: RecyclerView
    private var peopleList: MutableList<Person> = mutableListOf()
    private lateinit var peopleAdapter: PeopleAdapter

    private var profileClickedPosition = -1

    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_people)

        setUpBottomNav()
        activitiesResults()
        setUpAddProfileButton()

        recyclerViewProfiles = findViewById(R.id.recyclerViewProfiles)
        recyclerViewProfiles.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)

        peopleAdapter = PeopleAdapter(peopleList, this)
        recyclerViewProfiles.adapter = peopleAdapter

        getProfiles("SHOW", false)


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
                        val requestCode: String? = intent.extras?.getString("REQUEST_CODE")
                        if (requestCode != null) {
                            getProfiles(
                                requestCode,
                                intent.getBooleanExtra("profileDeleted", false)
                            )
                        }
                    }
                }
            }
    }

    private fun setUpBottomNav() {
        val bnv = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.activity_main_item -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                }

                R.id.activity_create_note -> {
                    val intent = Intent(applicationContext, CreateNoteActivity::class.java)
                    intent.putExtra("ADD_NOTE", true)
                    resultLauncher.launch(intent)
                }
                R.id.layout_people -> recyclerViewProfiles.smoothScrollToPosition(0)
            }
            Log.d("no me jodas", "no me jodas")
            return@setOnItemSelectedListener true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getProfiles(requestCode: String, profileDeleted: Boolean) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            Log.d("A", "a1")
            val people = PeopleDatabase.getDatabase(applicationContext)?.personDao()?.getAllPeople()

            handler.post {
                Log.d("A", "a2")
                when (requestCode) {
                    "SHOW" ->
                        if (people != null) {
                            peopleList.addAll(people)
                            peopleAdapter.notifyDataSetChanged()
                        }
                    "ADD_PROFILE" -> {
                        if (people != null) {
                            peopleList.add(0, people[0])
                        }
                        peopleAdapter.notifyItemInserted(0)
                        recyclerViewProfiles.smoothScrollToPosition(0)
                    }
                    "UPDATE" -> {
                        peopleList.removeAt(profileClickedPosition)
                        if (profileDeleted) {
                            peopleAdapter.notifyItemRemoved(profileClickedPosition)
                        } else {
                            peopleList.add(
                                profileClickedPosition,
                                people?.get(profileClickedPosition)!!
                            )
                            peopleAdapter.notifyItemChanged(profileClickedPosition)
                        }
                    }
                }
            }
        }
    }

    override fun onElementClicked(element: Person, position: Int) {
        profileClickedPosition = position
        //TODO: EXPAND FIRST????
        /// TODO: yes, this below another function
        val intent = Intent(applicationContext, CreateProfileActivity::class.java)
        intent.putExtra("REQUEST_CODE", "UPDATE")
        intent.putExtra("viewOrUpdate", true)
        intent.putExtra("profile", element)
        resultLauncher.launch(intent)
    }
}