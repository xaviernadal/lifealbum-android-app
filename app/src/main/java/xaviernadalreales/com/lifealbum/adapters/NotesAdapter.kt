package xaviernadalreales.com.lifealbum.adapters


import android.graphics.BitmapFactory
import android.graphics.Color.parseColor
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.entities.Note
import xaviernadalreales.com.lifealbum.listeners.NotesListener


class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private var notes: List<Note>
    private var notesListener: NotesListener

    constructor(notes: List<Note>, notesListener: NotesListener) {
        this.notes = notes
        this.notesListener = notesListener
    }

    class NoteViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView = itemView.findViewById(R.id.textTitle)
        var textDate: TextView = itemView.findViewById(R.id.textDate)
        var textNote: TextView = itemView.findViewById(R.id.textNote)
        var layoutNote: LinearLayout = itemView.findViewById(R.id.layoutNote)
        var imageNote: ImageView = itemView.findViewById(R.id.imageNote)

        fun setNote(note: Note) {
            textTitle.text = note.title
            if (textTitle.text == "") textTitle.visibility = View.GONE
            textDate.text = note.date
            textNote.text = note.noteText
            if(textNote.text == "") textNote.visibility = View.GONE

            val gradientDrawable: GradientDrawable = layoutNote.background as GradientDrawable
            if (note.colorNote != "") {
                gradientDrawable.setColor(parseColor(note.colorNote))
            } else {
                //TODO: Also change this default color haha
                gradientDrawable.setColor(parseColor("#333333"))
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
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_container_note, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.setNote(notes[position])
        holder.layoutNote.setOnClickListener {
            notesListener.onNoteClicked(notes.get(position), position)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}