package com.odnovolov.forgetmenot.db

import android.content.Context
import android.database.DatabaseUtils
import androidx.room.*
import com.odnovolov.forgetmenot.db.entity.CardDbEntity
import com.odnovolov.forgetmenot.db.entity.DeckDbEntity
import com.odnovolov.forgetmenot.db.entity.ExerciseCardDbEntity
import com.odnovolov.forgetmenot.db.entity.PronunciationDbEntity
import com.odnovolov.forgetmenot.ui.adddeck.AddDeckDao
import com.odnovolov.forgetmenot.ui.decksettings.DeckSettingsDao
import com.odnovolov.forgetmenot.ui.exercise.ExerciseDao
import com.odnovolov.forgetmenot.ui.exercisecreator.ExerciseCreatorDao
import com.odnovolov.forgetmenot.ui.home.HomeDao
import com.odnovolov.forgetmenot.ui.pronunciation.PronunciationDao

@Database(
    entities = [
        DeckDbEntity::class,
        CardDbEntity::class,
        ExerciseCardDbEntity::class,
        PronunciationDbEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun homeDao(): HomeDao
    abstract fun addDeckDao(): AddDeckDao
    abstract fun exerciseCreatorDao(): ExerciseCreatorDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun deckSettingsDao(): DeckSettingsDao
    abstract fun pronunciationDao(): PronunciationDao

    companion object {
        private const val NAME = "ForgetMeNot.db"
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            //context.deleteDatabase(NAME)
            return Room.databaseBuilder(context, AppDatabase::class.java, NAME)
                .build()
        }

        fun dump(query: String): String {
            return DatabaseUtils.dumpCursorToString(instance?.openHelper?.readableDatabase?.query(query))
        }
    }
}