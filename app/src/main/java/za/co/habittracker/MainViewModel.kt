package za.co.habittracker
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import za.co.habittracker.data.*
import java.time.LocalDate
class MainViewModel(app: Application):AndroidViewModel(app){
 private val dao=AppDatabase.get(app).dao()
 val habits=dao.habits().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val completions=dao.completions().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val tasks=dao.tasks().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 fun addHabit(title:String, description:String="", frequency:String="Daily")=viewModelScope.launch{if(title.isNotBlank())dao.upsertHabit(Habit(title=title.trim(),description=description,frequency=frequency))}
 fun toggleHabit(id:String,done:Boolean)=viewModelScope.launch{val day=LocalDate.now().toString();if(done)dao.removeCompletion(id,day)else dao.upsertCompletion(HabitCompletion(id,day))}
 fun addTask(title:String,parentId:String?=null,priority:Int=1,tags:String="")=viewModelScope.launch{if(title.isNotBlank())dao.upsertTask(Task(title=title.trim(),parentId=parentId,priority=priority,tags=tags))}
 fun toggleTask(task:Task)=viewModelScope.launch{dao.upsertTask(task.copy(completed=!task.completed))}
 fun saveTask(task:Task)=viewModelScope.launch{dao.upsertTask(task)}
 fun streak(id:String,items:List<HabitCompletion>):Int{val days=items.filter{it.habitId==id}.map{LocalDate.parse(it.day)}.toSet();var d=LocalDate.now();if(d !in days)d=d.minusDays(1);var n=0;while(d in days){n++;d=d.minusDays(1)};return n}
}