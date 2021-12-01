package xaviernadalreales.com.lifealbum.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.adapters.NotesAdapter
import xaviernadalreales.com.lifealbum.adapters.PeopleAdapter
import xaviernadalreales.com.lifealbum.database.NotesDatabase
import xaviernadalreales.com.lifealbum.database.PeopleDatabase
import xaviernadalreales.com.lifealbum.entities.Note
import xaviernadalreales.com.lifealbum.entities.Person
import xaviernadalreales.com.lifealbum.listeners.GenericListener
import java.io.InputStream
import java.util.concurrent.Executors

class CreateProfileActivity : AppCompatActivity(), GenericListener<Note> {
    private lateinit var inputName: EditText
    private lateinit var inputDescription: EditText
    private lateinit var profileImage: ImageView

    private lateinit var recyclerViewNotes: RecyclerView
    private var notesList: MutableList<Note> = mutableListOf()
    private lateinit var notesAdapter: NotesAdapter

    private var profileClickedPosition = -1
    var imagePath: String = ""

    companion object {
        private const val REQUESTCODE_PERMISSION = 1
    }

    private var RETURNCODE = "ADD_PROFILE"

    private var deleteProfileDialog: AlertDialog? = null
    private var alreadyAvailableProfile: Person? = null

    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        activitiesResults()
        val imageBack: ImageView = findViewById(R.id.back)
        imageBack.setOnClickListener { onBackPressed() }

        inputName = findViewById(R.id.profileNameAdd)
        inputDescription = findViewById(R.id.descriptionProfileAdd)
        profileImage = findViewById(R.id.profileImageAdd)

        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
        recyclerViewNotes.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        notesAdapter = NotesAdapter(notesList, this)
        recyclerViewNotes.adapter = notesAdapter

        val imageSave: ExtendedFloatingActionButton = findViewById(R.id.save_profile_fab)
        imageSave.setOnClickListener { saveProfile() }
        setUpProfileImage()

        if (intent.getBooleanExtra("viewOrUpdate", false)) {
            alreadyAvailableProfile = intent.extras?.get("profile") as Person
            setViewOrUpdate()
            getNotes()
            RETURNCODE = "UPDATE"
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getNotes() {
        val notesIds = alreadyAvailableProfile?.notesInside
        if (notesIds != null) {
            Log.d("NOTES", notesIds)
        }
        val validNotes: MutableList<Note> = mutableListOf()
        val allNotes = notesIds?.dropLast(1)
        if (allNotes != "") {
            val stringNotes = allNotes?.split(",")
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            executor.execute {
                val notes = NotesDatabase.getDatabase(applicationContext)?.noteDao()
                    ?.getAllNotes()
                handler.post {
                    if (stringNotes != null) {
                        for (id in stringNotes) {
                            if (notes != null) {
                                for (note in notes) {
                                    if (id.toInt() == note.id) {
                                        validNotes.add(note)
                                    }
                                }
                            }
                        }
                    }
                    notesList.addAll(validNotes)
                    notesAdapter.notifyDataSetChanged()
                }
            }
        }


    }

    private fun activitiesResults() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null) {
                        val selectedImageUri: Uri? = data.data
                        if (selectedImageUri != null) {
                            try {
                                val inputStream: InputStream? =
                                    contentResolver.openInputStream(selectedImageUri)
                                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                                profileImage.setImageBitmap(bitmap)
                                imagePath = getPathFromUri(selectedImageUri)

                            } catch (e: Exception) {
                                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
    }

    private fun setUpProfileImage() {
        profileImage.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    selectImage()
                }
                shouldShowRequestPermissionRationale("") -> {

                }
                else -> {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), REQUESTCODE_PERMISSION
                    )
                }
            }
        }
    }

    private fun setViewOrUpdate() {
        Log.d("UDPATE", alreadyAvailableProfile!!.notesInside + "A")
        inputName.setText(alreadyAvailableProfile!!.name)
        inputDescription.setText(alreadyAvailableProfile!!.descriptionText)
        if (alreadyAvailableProfile!!.profilePicture != "") {
            profileImage.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableProfile!!.profilePicture))
            imagePath = alreadyAvailableProfile!!.profilePicture
        }

    }

    private fun saveProfile() {
        if (inputName.text.toString().isEmpty() && inputDescription.text.toString().isEmpty()) {
            Toast.makeText(this, "Profile is empty.", Toast.LENGTH_SHORT).show()
            return
        }
        val profile = Person(0)
        profile.name = inputName.text.toString()
        profile.descriptionText = inputDescription.text.toString()
        profile.profilePicture = imagePath

        if (alreadyAvailableProfile != null) {
            profile.id = alreadyAvailableProfile!!.id
        }

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            PeopleDatabase.getDatabase(applicationContext)?.personDao()?.insertPerson(profile)
        }
        handler.post {
            val intent = Intent(applicationContext, PeopleActivity::class.java)
            intent.putExtra("REQUEST_CODE", RETURNCODE)
            setResult(RESULT_OK, intent)
            finish()
        }
    }


    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUESTCODE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPathFromUri(contentUri: Uri): String {
        val filePath: String
        val cursor: Cursor? = contentResolver.query(contentUri, null, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path.toString()
        } else {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onElementClicked(element: Note, position: Int) {
        TODO("Not yet implemented")
    }

}