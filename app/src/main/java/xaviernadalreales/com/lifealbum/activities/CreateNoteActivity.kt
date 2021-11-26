package xaviernadalreales.com.lifealbum.activities

import android.Manifest.*
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.entities.Note
import java.text.SimpleDateFormat
import java.util.*
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.w3c.dom.Text
import xaviernadalreales.com.lifealbum.database.NotesDatabase
import java.io.InputStream
import java.util.concurrent.Executors
import java.util.jar.Manifest


class CreateNoteActivity : AppCompatActivity() {

    private lateinit var inputNoteTitle: EditText;
    private lateinit var inputNoteText: EditText
    private lateinit var date: TextView
    private lateinit var imageNote: ImageView

    private lateinit var selectedNoteColor: String
    private lateinit var selectedImagePath: String

    companion object {
        private val REQUESTCODE_PERMISSION = 1
        private val REQUESTCODE_IMAGE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val imageBack: ImageView = findViewById(R.id.back);
        imageBack.setOnClickListener { onBackPressed() }

        inputNoteTitle = findViewById(R.id.noteTitle)
        inputNoteText = findViewById(R.id.note)
        date = findViewById(R.id.date)
        date.text =
            SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())
        imageNote = findViewById(R.id.imageNote)

        val imageSave: ImageView = findViewById(R.id.saveNote)
        imageSave.setOnClickListener { saveNote(); }

        //TODO: Canviar pq es fake
        selectedNoteColor = "#333333"
        selectedImagePath = ""

        initColors()
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
        note.colorNote = selectedNoteColor
        note.imagePath = selectedImagePath

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

    private fun initColors() {

        val layoutColors: LinearLayout = findViewById(R.id.layoutColors)

        val imageColor1: ImageView = layoutColors.findViewById(R.id.imageColor1)
        val imageColor2: ImageView = layoutColors.findViewById(R.id.imageColor2)
        val imageColor3: ImageView = layoutColors.findViewById(R.id.imageColor3)
        val imageColor4: ImageView = layoutColors.findViewById(R.id.imageColor4)
        val imageColor5: ImageView = layoutColors.findViewById(R.id.imageColor5)

        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> =
            BottomSheetBehavior.from(layoutColors)

        layoutColors.findViewById<TextView>(R.id.textColor).setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        //TODO: Improve this because my eyes are hurting man
        //Colors according to res/values/colors.xml
        layoutColors.findViewById<View>(R.id.viewColor1).setOnClickListener {
            selectedNoteColor = "#333333"
            imageColor1.setImageResource(R.drawable.ic_done); imageColor2.setImageResource(0)
            imageColor3.setImageResource(0); imageColor4.setImageResource(0); imageColor5.setImageResource(
            0
        )
        }
        layoutColors.findViewById<View>(R.id.viewColor2).setOnClickListener {
            selectedNoteColor = "#FDBE3B"
            imageColor1.setImageResource(0); imageColor2.setImageResource(R.drawable.ic_done)
            imageColor3.setImageResource(0); imageColor4.setImageResource(0); imageColor5.setImageResource(
            0
        )
        }
        layoutColors.findViewById<View>(R.id.viewColor3).setOnClickListener {
            selectedNoteColor = "#FF4842"
            imageColor1.setImageResource(0); imageColor2.setImageResource(0);
            imageColor3.setImageResource(R.drawable.ic_done); imageColor4.setImageResource(0); imageColor5.setImageResource(
            0
        )
        }
        layoutColors.findViewById<View>(R.id.viewColor4).setOnClickListener {
            selectedNoteColor = "#3A52FC"
            imageColor1.setImageResource(0); imageColor2.setImageResource(0);
            imageColor3.setImageResource(0); imageColor4.setImageResource(R.drawable.ic_done); imageColor5.setImageResource(
            0
        )
        }
        layoutColors.findViewById<View>(R.id.viewColor5).setOnClickListener {
            selectedNoteColor = "#000000"
            imageColor1.setImageResource(0); imageColor2.setImageResource(0);
            imageColor3.setImageResource(0); imageColor4.setImageResource(0); imageColor5.setImageResource(
            R.drawable.ic_done
        )
        }

        layoutColors.findViewById<LinearLayout>(R.id.addImage).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            when {
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    selectImage()
                }
                shouldShowRequestPermissionRationale("") -> {

                }
                else -> {
                    requestPermissions(
                        this, arrayOf(
                            permission.READ_EXTERNAL_STORAGE
                        ), REQUESTCODE_PERMISSION
                    )
                }
            }
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
                            imageNote.setImageBitmap(bitmap)
                            imageNote.visibility = View.VISIBLE

                            selectedImagePath = getPathFromUri(selectedImageUri)

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
    private fun getPathFromUri(contentUri : Uri): String {
        val filePath : String
        val cursor : Cursor? = contentResolver.query(contentUri, null, null,null, null, null)
        if (cursor == null) {
            filePath = contentUri.path.toString()
        } else {
            cursor.moveToFirst()
            val index : Int = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }
}