package xaviernadalreales.com.lifealbum.dao

import androidx.room.*
import xaviernadalreales.com.lifealbum.entities.Person

@Dao
interface PersonDao {
    @Query("SELECT * FROM people ORDER BY id DESC")
    fun getAllPeople(): List<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(person: Person)

    @Delete
    fun deletePerson(person: Person)
}