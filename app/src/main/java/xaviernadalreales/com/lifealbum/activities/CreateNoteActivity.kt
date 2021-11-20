package xaviernadalreales.com.lifealbum.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import xaviernadalreales.com.lifealbum.R

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var inputNoteTitle : EditText; private lateinit var inputNoteText : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val imageBack: ImageView = findViewById(R.id.back);
        imageBack.setOnClickListener { onBackPressed() }
    }


}