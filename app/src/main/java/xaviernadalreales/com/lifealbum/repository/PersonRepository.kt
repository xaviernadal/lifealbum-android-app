package xaviernadalreales.com.lifealbum.repository

import android.app.Application
import androidx.lifecycle.LiveData
import xaviernadalreales.com.lifealbum.dao.PersonDao
import xaviernadalreales.com.lifealbum.database.PeopleDatabase
import xaviernadalreales.com.lifealbum.entities.Person

class PersonRepository internal constructor(application: Application) {
    private lateinit var personDao: PersonDao
    val allPeople: LiveData<List<Person>>

    init {
        val database: PeopleDatabase? = PeopleDatabase.getDatabase(application)
        if (database != null) {
            personDao = database.personDao()
        }
        allPeople = personDao.getAllPeople()
    }

    suspend fun insert(person: Person){
        personDao.insertPerson(person)
    }
    suspend fun delete(person: Person){
        personDao.deletePerson(person)
    }
}
