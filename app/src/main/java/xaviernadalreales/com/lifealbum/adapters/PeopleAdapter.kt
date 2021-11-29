package xaviernadalreales.com.lifealbum.adapters

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import xaviernadalreales.com.lifealbum.R
import xaviernadalreales.com.lifealbum.activities.PeopleActivity
import xaviernadalreales.com.lifealbum.entities.Person
import xaviernadalreales.com.lifealbum.listeners.GenericListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

class PeopleAdapter : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder> {
    private var people: List<Person>
    private var peopleListener: GenericListener<Person>
    private lateinit var timer: Timer
    private var totalPeople: List<Person>

    constructor(people: List<Person>, peopleListener: GenericListener<Person>) {
        this.people = people
        this.peopleListener = peopleListener
        this.totalPeople = people
    }

    class PeopleViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layoutProfile: LinearLayout = itemView.findViewById(R.id.layoutProfile)
        fun setPerson(person: Person) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        return PeopleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.person_profile, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        holder.setPerson(people[position])
        holder.layoutProfile.setOnClickListener {
            peopleListener.onElementClicked(people[position], position)
        }
    }

    override fun getItemCount(): Int {
        return people.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    //Using Timer since Handler is deprecated
    @SuppressLint("NotifyDataSetChanged")
    fun search(keyword: String) {
        timer = Timer()
        timer.schedule(
            timerTask
            {
                if (keyword.trim().isEmpty()) {
                    people = totalPeople
                } else {
                    val temp: ArrayList<Person> = ArrayList()
                    for (note: Person in totalPeople) {
                        if (note.name.lowercase().contains(keyword.lowercase())) {
                            temp.add(note)
                        }
                    }
                    people = temp
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
        timer.cancel()
    }

}