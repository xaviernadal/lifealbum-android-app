package xaviernadalreales.com.lifealbum.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xaviernadalreales.com.lifealbum.database.PeopleDatabase
import xaviernadalreales.com.lifealbum.entities.Person
import xaviernadalreales.com.lifealbum.repository.PersonRepository

class PersonViewModel(application: Application) : AndroidViewModel(application) {

    val allPeople : LiveData<List<Person>>
    val repository : PersonRepository

    init {
        val dao = PeopleDatabase.getDatabase(application)?.personDao()
        repository = PersonRepository(application)
        allPeople = repository.allPeople
    }
    fun getPeople(): LiveData<List<Person>> {
        return allPeople
    }
    fun insertPerson(person: Person) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(person)
    }

    fun deletePerson (person: Person) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(person)
    }

}