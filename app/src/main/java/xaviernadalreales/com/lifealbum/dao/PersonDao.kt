package xaviernadalreales.com.lifealbum.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import xaviernadalreales.com.lifealbum.entities.Person

@Dao
interface PersonDao {
    @Query("SELECT * FROM people ORDER BY id DESC")
    fun getAllPeople(): LiveData<List<Person>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(person: Person)

    @Delete
    fun deletePerson(person: Person)
}