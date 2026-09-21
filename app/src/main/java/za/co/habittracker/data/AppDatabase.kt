package za.co.habittracker.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY createdAt")
    fun habits(): Flow<List<Habit>>

    @Query("SELECT * FROM habit_completions")
    fun completions(): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM tasks ORDER BY completed, priority DESC, createdAt DESC")
    fun tasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertHabit(habit: Habit)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertCompletion(item: HabitCompletion)
    @Query("DELETE FROM habit_completions WHERE habitId=:habitId AND day=:day") suspend fun removeCompletion(habitId: String, day: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertTask(task: Task)
}

@Database(entities = [Habit::class, HabitCompletion::class, Task::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "habit-tracker.db").build().also { INSTANCE = it }
        }
    }
}
