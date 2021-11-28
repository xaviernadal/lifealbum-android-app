package xaviernadalreales.com.lifealbum.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import xaviernadalreales.com.lifealbum.dao.PersonDao
import xaviernadalreales.com.lifealbum.entities.Person

@Database(entities = [Person::class], version = 1, exportSchema = false)
abstract class PeopleDatabase : RoomDatabase() {
    companion object {
        private var peopleDatabase: PeopleDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): PeopleDatabase? {
            if (peopleDatabase == null) {
                peopleDatabase = Room.databaseBuilder(
                    context,
                    PeopleDatabase::class.java,
                    "people_db"
                ).build()
            }
            return peopleDatabase
        }

    }

    abstract fun personDao(): PersonDao


}
