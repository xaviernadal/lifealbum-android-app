package xaviernadalreales.com.lifealbum.activities

import android.app.Activity
import android.app.Person
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.adapters.PeopleAdapter
import xaviernadalreales.com.lifealbum.listeners.GenericListener

class PeopleActivity : AppCompatActivity(), GenericListener<Person> {

    private lateinit var recyclerViewProfiles: RecyclerView
    private var peopleList: MutableList<Person> = mutableListOf()
    private lateinit var peopleAdapter: PeopleAdapter

    private var profileClickedPosition = -1

    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.layout_people)

        //TODO:REPEATED CODE
        val imageAddElement: ImageView = findViewById(R.id.add_element)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data

                }
            }
        imageAddElement.setOnClickListener {
            val intent = Intent(applicationContext, CreateNoteActivity::class.java)
            intent.putExtra("ADD_NOTE", true)
            resultLauncher.launch(intent)
        }

        recyclerViewProfiles = findViewById(R.id.recyclerViewProfiles)
        recyclerViewProfiles.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)

        peopleAdapter = PeopleAdapter(peopleList, this)
        recyclerViewProfiles.adapter = peopleAdapter
        getProfiles("SHOW", false)


    }


    override fun onElementClicked(element: Person, position: Int) {
        TODO("Not yet implemented")
    }
}