package za.co.habittracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val emoji: String = "✓",
    val color: Long = 0xFF6750A4,
    val createdAt: Long = System.currentTimeMillis(),
    val archived: Boolean = false
)

@Entity(tableName = "habit_completions", primaryKeys = ["habitId", "day"])
data class HabitCompletion(val habitId: String, val day: String, val completedAt: Long = System.currentTimeMillis())

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val parentId: String? = null,
    val priority: Int = 1,
    val dueAt: Long? = null,
    val tags: String = "",
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
