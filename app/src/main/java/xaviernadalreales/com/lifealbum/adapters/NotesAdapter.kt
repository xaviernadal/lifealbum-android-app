package xaviernadalreales.com.lifealbum.adapters


import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color.parseColor
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.entities.Note
import xaviernadalreales.com.lifealbum.listeners.GenericListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask


class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private var displayLine: Boolean
    private var notes: List<Note>
    private var notesListener: GenericListener<Note>
    private var timer: Timer? = null
    private var totalNotes: List<Note>

    companion object {
        var displayLine: Boolean = false
    }

    constructor(
        notes: List<Note>,
        notesListener: GenericListener<Note>,
        displayLineChange: Boolean = false
    ) {
        this.notes = notes
        this.notesListener = notesListener
        totalNotes = notes
        displayLine = displayLineChange
    }

    class NoteViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView = itemView.findViewById(R.id.textTitle)
        var textDate: TextView = itemView.findViewById(R.id.textDate)
        var textNote: TextView = itemView.findViewById(R.id.textNote)
        var layoutNote = if (displayLine) {
            itemView.findViewById<ConstraintLayout>(R.id.layoutNote)
        } else {
            itemView.findViewById<LinearLayout>(R.id.layoutNote)
        }
        var imageNote: ImageView = itemView.findViewById(R.id.imageNote)

        fun setNote(note: Note) {
            textTitle.text = note.title
            if (textTitle.text == "") textTitle.visibility = View.GONE
            textDate.text = note.date
            textNote.text = note.noteText
            if (textNote.text == "") textNote.visibility = View.GONE

            val gradientDrawable: GradientDrawable = layoutNote.background as GradientDrawable
            if (note.colorNote != "") {
                gradientDrawable.setColor(parseColor(note.colorNote))
            } else {
                gradientDrawable.setColor(parseColor("#ECEFF1"))
            }

            if (note.imagePath != "") {
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                imageNote.visibility = View.VISIBLE
            } else {
                imageNote.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        if (displayLine) {
            return NoteViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_container_note_line, parent, false
                )
            )
        }
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_container_note, parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.setNote(notes[position])
        holder.layoutNote.setOnClickListener {
            notesListener.onElementClicked(notes[position], position)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    //Using Timer since Handler is deprecated
    @SuppressLint("NotifyDataSetChanged")
    fun search(keyword: String) {
        timer = Timer()
        timer!!.schedule(
            timerTask
            {
                if (keyword.trim().isEmpty()) {
                    notes = totalNotes
                } else {
                    val temp: ArrayList<Note> = ArrayList()
                    for (note: Note in totalNotes) {
                        if (note.title.lowercase()
                                .contains(keyword.lowercase()) || note.noteText.lowercase()
                                .contains(keyword.lowercase())
                        ) {
                            temp.add(note)
                        }
                    }
                    notes = temp
                    Handler(Looper.getMainLooper()).post {
                        notifyDataSetChanged()
                    }
                }
            },
            //To give time to write the keyword
            500,
        )
    }

    fun cancelTimer() {
        timer?.cancel()
    }
}