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


class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private var notes: List<Note>

    constructor(notes: List<Note>) {
        this.notes = notes
    }

    class NoteViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView = itemView.findViewById(R.id.textTitle)
        var textDate: TextView = itemView.findViewById(R.id.textDate)
        var layoutNote: LinearLayout = itemView.findViewById(R.id.layoutNote)
        var imageNote: ImageView = itemView.findViewById(R.id.imageNote)

        fun setNote(note: Note) {
            textTitle.text = note.title
            if (textTitle.text == "") textTitle.visibility = View.GONE
            textDate.text = note.date


            val gradientDrawable: GradientDrawable = layoutNote.background as GradientDrawable
            if (note.colorNote != "") {
                gradientDrawable.setColor(parseColor(note.colorNote))
            } else {
                //TODO: Also change this default color haha
                gradientDrawable.setColor(parseColor("#333333"))
            }
            Log.d("arribe aqui","si")
            Log.d("i aqui?", "a" + note.imagePath)
            if (note.imagePath != "") {
                Log.d("arribe aqui","jajajajaajja")
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
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}