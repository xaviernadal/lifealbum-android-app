package xaviernadalreales.com.lifealbum.activities

import android.Manifest.*
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
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
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import xaviernadalreales.com.lifealbum.database.NotesDatabase
import java.io.InputStream
import java.util.concurrent.Executors


class CreateNoteActivity : AppCompatActivity() {

    private lateinit var inputNoteTitle: EditText
    private lateinit var inputNoteText: EditText
    private lateinit var date: TextView
    private lateinit var imageNote: ImageView

    private lateinit var selectedNoteColor: String
    private lateinit var selectedImagePath: String

    companion object {
        private const val REQUESTCODE_PERMISSION = 1
    }

    private var RETURNCODE = "ADD_NOTE"

    private var deleteNoteDialog: AlertDialog? = null

    private var alreadyAvailableNote: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)


        val imageBack: ImageView = findViewById(R.id.back)
        imageBack.setOnClickListener { onBackPressed() }

        inputNoteTitle = findViewById(R.id.noteTitle)
        inputNoteText = findViewById(R.id.note)
        date = findViewById(R.id.date)
        date.text =
            SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())
        imageNote = findViewById(R.id.imageNote)

        val imageSave: ExtendedFloatingActionButton = findViewById(R.id.saveNote)
        imageSave.setOnClickListener { saveNote() }

        //TODO: Canviar pq es fake
        selectedNoteColor = "#333333"
        selectedImagePath = ""


        if (intent.getBooleanExtra("viewOrUpdate", false)) {
            alreadyAvailableNote = intent.extras?.get("note") as Note
            setViewOfUpdate()
            RETURNCODE = "UPDATE"
        }

        findViewById<ImageView>(R.id.removeImage).setOnClickListener {
            imageNote.setImageBitmap(null)
            imageNote.visibility = View.GONE
            findViewById<ImageView>(R.id.removeImage).visibility = View.GONE
            selectedImagePath = ""
        }

        initColors()
    }

    private fun setViewOfUpdate() {
        inputNoteTitle.setText(alreadyAvailableNote!!.title)
        inputNoteText.setText(alreadyAvailableNote!!.noteText)
        date.text = alreadyAvailableNote!!.date
        if (alreadyAvailableNote!!.imagePath != "") {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote!!.imagePath))
            imageNote.visibility = View.VISIBLE
            selectedImagePath = alreadyAvailableNote!!.imagePath
            findViewById<ImageView>(R.id.removeImage).visibility = View.VISIBLE
        }

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


        //Manually setting an existing id to a new note, to replace the old one. In NoteDAO, the
        //onConflictStrategy is set to REPLACE
        if (alreadyAvailableNote != null) {
            note.id = alreadyAvailableNote!!.id
        }


        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            NotesDatabase.getDatabase(applicationContext)?.noteDao()?.insertNote(note)
        }
        handler.post {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("REQUEST_CODE", RETURNCODE)
            setResult(RESULT_OK, intent)
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

        if (alreadyAvailableNote != null && alreadyAvailableNote!!.colorNote != "") {
            when (alreadyAvailableNote!!.colorNote) {
                "#333333" -> layoutColors.findViewById<View>(R.id.viewColor1).performClick()
                "#FDBE3B" -> layoutColors.findViewById<View>(R.id.viewColor2).performClick()
                "#FF4842" -> layoutColors.findViewById<View>(R.id.viewColor3).performClick()
                "#3A52FC" -> layoutColors.findViewById<View>(R.id.viewColor4).performClick()
                "#000000" -> layoutColors.findViewById<View>(R.id.viewColor5).performClick()
            }
        }

        layoutColors.findViewById<TextView>(R.id.textColor).setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

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
            imageColor1.setImageResource(0); imageColor2.setImageResource(0)
            imageColor3.setImageResource(R.drawable.ic_done); imageColor4.setImageResource(0); imageColor5.setImageResource(
            0
        )
        }
        layoutColors.findViewById<View>(R.id.viewColor4).setOnClickListener {
            selectedNoteColor = "#3A52FC"
            imageColor1.setImageResource(0); imageColor2.setImageResource(0)
            imageColor3.setImageResource(0); imageColor4.setImageResource(R.drawable.ic_done); imageColor5.setImageResource(
            0
        )
        }
        layoutColors.findViewById<View>(R.id.viewColor5).setOnClickListener {
            selectedNoteColor = "#000000"
            imageColor1.setImageResource(0); imageColor2.setImageResource(0)
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
        if (alreadyAvailableNote != null) {
            layoutColors.findViewById<LinearLayout>(R.id.layoutDeleteNote).visibility = View.VISIBLE
            layoutColors.findViewById<LinearLayout>(R.id.layoutDeleteNote).setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                showDeleteDialog()
            }
        }
    }

    private fun showDeleteDialog() {
        if (deleteNoteDialog == null) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@CreateNoteActivity)
            val view: View = LayoutInflater.from(this)
                .inflate(
                    R.layout.layout_delete_note,
                    findViewById(R.id.layoutDeleteNoteContainer)
                )
            builder.setView(view)
            deleteNoteDialog = builder.create()
            if (deleteNoteDialog != null) {
                if (deleteNoteDialog!!.window != null) {
                    deleteNoteDialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
                }
            }
            view.findViewById<TextView>(R.id.deleteButtonNote).setOnClickListener {
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())

                executor.execute {
                    NotesDatabase.getDatabase(applicationContext)?.noteDao()
                        ?.deleteNote(alreadyAvailableNote)
                }
                handler.post {
                    deleteNoteDialog?.dismiss()
                    val intent = Intent()
                    intent.putExtra("REQUEST_CODE", "UPDATE")
                    intent.putExtra("noteDeleted", true)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
            view.findViewById<TextView>(R.id.cancelButtonNote).setOnClickListener {
                deleteNoteDialog?.dismiss()
            }
        }
        deleteNoteDialog?.show()
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
                            findViewById<ImageView>(R.id.removeImage).visibility = View.VISIBLE

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