package xaviernadalreales.com.lifealbum.activities

import android.Manifest
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
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.database.PeopleDatabase
import xaviernadalreales.com.lifealbum.entities.Person
import java.io.InputStream
import java.util.concurrent.Executors

class CreateProfileActivity : AppCompatActivity() {
    private lateinit var inputName: EditText
    private lateinit var inputDescription: EditText
    private lateinit var profileImage: ImageView

    var imagePath: String = ""

    companion object {
        private const val REQUESTCODE_PERMISSION = 1
    }

    private var RETURNCODE = "ADD_PROFILE"

    private var deleteProfileDialog: AlertDialog? = null
    private var alreadyAvailableProfile: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AAAAAAAAAAAAAA", "AAAAAAAAAAAAAAAAAa")
        setContentView(R.layout.activity_create_profile)

        val imageBack: ImageView = findViewById(R.id.back)
        imageBack.setOnClickListener { onBackPressed() }

        inputName = findViewById(R.id.profileNameAdd)
        inputDescription = findViewById(R.id.descriptionProfileAdd)
        profileImage = findViewById(R.id.profileImageAdd)

        val imageSave: ExtendedFloatingActionButton = findViewById(R.id.save_profile_fab)
        imageSave.setOnClickListener { saveProfile() }

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
        if (intent.getBooleanExtra("viewOrUpdate", false)) {
            alreadyAvailableProfile = intent.extras?.get("profile") as Person
            setViewOrUpdate()
            RETURNCODE = "UPDATE"
        }
    }

    private fun setViewOrUpdate() {
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

    val resultLauncher =
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

}