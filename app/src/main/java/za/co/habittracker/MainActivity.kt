package za.co.habittracker
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import za.co.habittracker.data.*
import java.time.LocalDate
class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{HabitApp()}}}
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HabitApp(vm:MainViewModel=viewModel()){
 var dark by remember{mutableStateOf(false)};var tab by remember{mutableIntStateOf(0)}
 MaterialTheme(colorScheme=if(dark)darkColorScheme()else lightColorScheme()){Scaffold(
 topBar={TopAppBar(title={Text(listOf("Today","Habits","Tasks","Progress")[tab],fontWeight=FontWeight.Bold)},actions={IconButton({dark=!dark}){Icon(if(dark)Icons.Default.LightMode else Icons.Default.DarkMode,null)}})},
 bottomBar={NavigationBar{listOf("Today" to Icons.Default.Today,"Habits" to Icons.Default.CheckCircle,"Tasks" to Icons.Default.List,"Progress" to Icons.Default.BarChart).forEachIndexed{i,p->NavigationBarItem(selected=tab==i,onClick={tab=i},icon={Icon(p.second,null)},label={Text(p.first)})}}}
 ){pad->Box(Modifier.padding(pad)){when(tab){0->Today(vm);1->Habits(vm);2->Tasks(vm);else->Progress(vm)}}}}
}
@Composable fun Today(vm:MainViewModel){
 val habits by vm.habits.collectAsState();val comp by vm.completions.collectAsState();val tasks by vm.tasks.collectAsState();val today=LocalDate.now().toString();val done=comp.count{it.day==today};val progress=if(habits.isEmpty())0f else done.toFloat()/habits.size
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
 item{Text("Today",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold);Text("Small steps, consistently.",color=MaterialTheme.colorScheme.onSurfaceVariant)}
 item{Card{Column(Modifier.padding(20.dp)){Text("DAILY PROGRESS",style=MaterialTheme.typography.labelMedium);Spacer(Modifier.height(10.dp));Text(((progress*100).toInt()).toString()+"%",style=MaterialTheme.typography.displaySmall,fontWeight=FontWeight.Bold);LinearProgressIndicator({progress},Modifier.fillMaxWidth());Spacer(Modifier.height(8.dp));Text(done.toString()+" of "+habits.size+" habits complete")}}}
 item{Text("Habits",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)}
 items(habits){h->val checked=comp.any{it.habitId==h.id&&it.day==today};Card{Row(Modifier.fillMaxWidth().clickable{vm.toggleHabit(h.id,checked)}.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Checkbox(checked,{vm.toggleHabit(h.id,checked)});Text(h.title,Modifier.weight(1f));Text("🔥 "+vm.streak(h.id,comp))}}}
 item{Text("Tasks",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)};items(tasks.filter{!it.completed}.take(5)){t->TaskRow(t,vm)}
 }}
@Composable fun Habits(vm:MainViewModel){val habits by vm.habits.collectAsState();val comp by vm.completions.collectAsState();var title by remember{mutableStateOf("")};Column(Modifier.fillMaxSize().padding(20.dp)){Row{OutlinedTextField(title,{title=it},Modifier.weight(1f),placeholder={Text("New habit")},singleLine=true);Spacer(Modifier.width(8.dp));FilledIconButton({vm.addHabit(title);title=""}){Icon(Icons.Default.Add,null)}};Spacer(Modifier.height(16.dp));LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)){items(habits){h->Card{Column(Modifier.padding(16.dp)){Row(verticalAlignment=Alignment.CenterVertically){Text(h.title,Modifier.weight(1f),fontWeight=FontWeight.SemiBold);Text("🔥 "+vm.streak(h.id,comp))};Spacer(Modifier.height(10.dp));Heatmap(h.id,comp)}}}}}}
@Composable fun Heatmap(id:String,comp:List<HabitCompletion>){Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){(13 downTo 0).forEach{n->val day=LocalDate.now().minusDays(n.toLong()).toString();val on=comp.any{it.habitId==id&&it.day==day};Box(Modifier.size(16.dp).background(if(on)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,RoundedCornerShape(4.dp)))}}}
@Composable fun Tasks(vm:MainViewModel){val tasks by vm.tasks.collectAsState();var q by remember{mutableStateOf("")};var title by remember{mutableStateOf("")};val shown=tasks.filter{q.isBlank()||it.title.contains(q,true)||it.tags.contains(q,true)};Column(Modifier.fillMaxSize().padding(20.dp)){OutlinedTextField(q,{q=it},Modifier.fillMaxWidth(),leadingIcon={Icon(Icons.Default.Search,null)},placeholder={Text("Search tasks or tags")},singleLine=true);Spacer(Modifier.height(10.dp));Row{OutlinedTextField(title,{title=it},Modifier.weight(1f),placeholder={Text("Add a task")},singleLine=true);Spacer(Modifier.width(8.dp));FilledIconButton({vm.addTask(title);title=""}){Icon(Icons.Default.Add,null)}};Spacer(Modifier.height(12.dp));LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)){items(shown){TaskRow(it,vm)}}}}
@Composable fun TaskRow(t:Task,vm:MainViewModel){Card{Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically){Checkbox(t.completed,{vm.toggleTask(t)});Column(Modifier.weight(1f)){Text(t.title,fontWeight=FontWeight.Medium);if(t.tags.isNotBlank())Text(t.tags,style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary)};AssistChip(onClick={}, label={Text(listOf("Low","Medium","High","Urgent").getOrElse(t.priority){"Medium"})})}}}
@Composable fun Progress(vm:MainViewModel){val habits by vm.habits.collectAsState();val comp by vm.completions.collectAsState();LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){item{Text("Your progress",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold);Text("Last 7 days",color=MaterialTheme.colorScheme.onSurfaceVariant)};items(habits){h->val count=(0..6).count{n->comp.any{it.habitId==h.id&&it.day==LocalDate.now().minusDays(n.toLong()).toString()}};Card{Column(Modifier.padding(16.dp)){Row{Text(h.title,Modifier.weight(1f),fontWeight=FontWeight.SemiBold);Text(count.toString()+" / 7")};Spacer(Modifier.height(10.dp));LinearProgressIndicator({count/7f},Modifier.fillMaxWidth());Spacer(Modifier.height(12.dp));Heatmap(h.id,comp)}}}}}
