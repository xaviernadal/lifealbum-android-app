package xaviernadalreales.com.lifealbum.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.database.NotesDatabase
import xaviernadalreales.com.lifealbum.entities.Note
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_ADD_NOTE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageAddElement: ImageView = findViewById(R.id.add_element)
        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(), ActivityResultCallback {
            }
        )

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                }
            }

        imageAddElement.setOnClickListener {
            val intent = Intent(applicationContext, CreateNoteActivity::class.java)
            resultLauncher.launch(intent)
        }
        getNotes()
    }

    private fun getNotes() {

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())


        executor.execute {
            val notes = NotesDatabase.getDatabase(applicationContext)?.noteDao()?.getAllNotes()

            handler.post {
                Log.d("MyNOTES", notes.toString())
            }
        }
    }

}