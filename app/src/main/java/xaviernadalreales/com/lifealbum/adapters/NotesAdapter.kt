package xaviernadalreales.com.lifealbum.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.entities.Note


class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private var notes: List<Note>

    constructor(notes: List<Note>) {
        this.notes = notes
    }

    class NoteViewHolder(@NonNull itemView : View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView = itemView.findViewById(R.id.textTitle)
        var textDate: TextView = itemView.findViewById(R.id.textDate)

        fun setNote(note: Note) {
            textTitle.text = note.title
            if (textTitle.text == "") textTitle.visibility = View.GONE
            textDate.text = note.date
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
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}