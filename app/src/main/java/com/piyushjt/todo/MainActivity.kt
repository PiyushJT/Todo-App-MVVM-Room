package com.piyushjt.todo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.piyushjt.todo.ui.theme.Background
import com.piyushjt.todo.ui.theme.CanceledText
import com.piyushjt.todo.ui.theme.LightCanceledText
import com.piyushjt.todo.ui.theme.LightText
import com.piyushjt.todo.ui.theme.Line
import com.piyushjt.todo.ui.theme.Purple
import com.piyushjt.todo.ui.theme.TextColor
import com.piyushjt.todo.ui.theme.TextFieldBorder
import com.piyushjt.todo.ui.theme.TodoTheme
import com.piyushjt.todo.ui.theme.Transparent
import com.piyushjt.todo.ui.theme.Typography
import com.piyushjt.todo.ui.theme.White
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {

    // Initializing Database
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todos.db"
        ).build()
    }

    // Defining ViewModel and dao
    private val viewModel by viewModels<TodoViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoViewModel(db.dao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        Background.toArgb().also { window.navigationBarColor = it }

        setContent {
            TodoTheme() {

                // Todo State
                val state by viewModel.state.collectAsState()

                // Navigation (Main Screen and Add a Todo Screen)
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = MainScreen
                ) {
                    // Main Screen
                    composable<MainScreen> {
                        MainScreen(state = state, onEvent = viewModel::onEvent, navigate = {
                            navController.navigate(AddTodoScreen)
                        })
                    }
                    // Add a Todo Screen
                    composable<AddTodoScreen> {
                        AddTodo(state = state, onEvent = viewModel::onEvent, navigate = {
                            navController.navigate(MainScreen) {
                                popUpTo(AddTodoScreen) {
                                    inclusive = true
                                }
                            }
                        })
                    }

                }
            }
        }
    }

}

@Serializable
object MainScreen

@Serializable
object AddTodoScreen


// Declaring an external font family
val inter = FontFamily(
    Font(R.font.inter, FontWeight.SemiBold)
)


// Main Screen Composable
@Composable
fun MainScreen(
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    navigate: () -> Unit
) {
    BG()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            // Current Date
            Header(SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(java.util.Date()))

            // Todo List
            MyTodoList(
                state = state,
                onEvent = onEvent
            )

        }

        // Button to add a new Todo
        BottomButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            onEvent,
            navigate,
            "Add New Task"
        )

    }
}


// Add Todo Screen Composable
@Composable
fun AddTodo(
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    navigate: () -> Unit
) {
    BG()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {

            Header("Add New Task")

            // New Task Input Fields
            NewTask(
                state= state,
                onEvent = onEvent
            )

        }

        // Button to save the Todo
        BottomButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            onEvent,
            navigate,
            "Save"
        )

    }
}


// List of All Todos
@Composable
fun TaskList(
    state: TodoState,
    onEvent: (TodoEvent) -> Unit
) {

    // Curved Cornered White Card
    Card(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.8f)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .background(Transparent), shape = RoundedCornerShape(16.dp)
    ) {

        // Container to arrange all todos
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Loop to show all Todos
            for (todo in state.todos) {

                // Column to show divider line if needed
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .aspectRatio(4.475f),
                ) {

                    // Show divider line for all todos after first todo
                    if (state.todos[0] != todo) {
                        // The line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Line)
                        )
                    }

                    // Row of Todo-text and CheckBox
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        // Column of Title and Description
                        Column {

                            // Modifying text styles for checked and unchecked todos
                            // . For Checked
                            if (todo.isChecked) {

                                // if the todo is checked -> cut the text with line through
                                Text(
                                    text = todo.title,
                                    style = Typography.titleLarge,
                                    color = CanceledText,
                                    fontFamily = inter,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = TextDecoration.LineThrough
                                )
                                if(!todo.description.isNullOrEmpty()){
                                    Text(
                                        text = todo.description,
                                        style = Typography.titleMedium,
                                        color = LightCanceledText,
                                        fontFamily = inter,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }
                            }
                            // . For Unchecked
                            else {
                                Text(
                                    text = todo.title,
                                    style = Typography.titleLarge,
                                    color = TextColor,
                                    fontFamily = inter,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if(!todo.description.isNullOrEmpty()){
                                    Text(
                                        text = todo.description,
                                        style = Typography.titleMedium,
                                        color = LightText,
                                        fontFamily = inter,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Checkbox to show completion status
                        Checkbox(
                            checked = todo.isChecked,

                            // Change completion status
                            onCheckedChange = { isChecked ->
                                onEvent(TodoEvent.SetChecked(todo, isChecked))
                            },

                            colors = CheckboxDefaults.colors(
                                checkedColor = Purple,
                                uncheckedColor = Purple,
                                checkmarkColor = White
                            )
                        )
                    }
                }
            }
        }
    }
}


// Bottom Button Composable
@Composable
fun BottomButton(
    modifier: Modifier = Modifier,
    onEvent: (TodoEvent) -> Unit,
    navigate : () -> Unit,
    text : String
) {
    val navigationBarsPadding = if(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().value <= 16){
        0.dp
    } else {
        (WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().value.toInt() -16).dp
    }
    Log.d("padding", navigationBarsPadding.toString())

    Column(
        modifier = modifier
            .padding(bottom = navigationBarsPadding)
            .fillMaxWidth()
            .aspectRatio(4.25f)
            .background(Background),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (text == "Save") {
                    onEvent(TodoEvent.SaveTodo)
                    onEvent(TodoEvent.HideAddTodo)
                    navigate()
                } else {
                    onEvent(TodoEvent.ShowAddTodo)
                    navigate()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(6.392857f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple
            ),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = text,
                style = Typography.titleMedium,
                fontFamily = inter,
                color = White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// List of all Todos
@SuppressLint("Range")
@Composable
fun MyTodoList(
    modifier: Modifier = Modifier,
    state: TodoState,
    onEvent: (TodoEvent) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Heading
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "My Todo List",
            style = Typography.titleLarge,
            fontFamily = inter,
            color = White,
            fontWeight = FontWeight.SemiBold
        )

        // The card of all todos
        TaskList(
            state = state,
            onEvent = onEvent
        )
    }
}


// New todo composable
@Composable
fun NewTask(
    modifier: Modifier = Modifier,
    state: TodoState,
    onEvent: (TodoEvent) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Background)
            .padding(20.dp)
    ) {

        // Todo title TextField
        Text(
            text = "Task Title",
            style = Typography.titleMedium,
            fontFamily = inter,
            color = TextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 28.dp)
        )

        OutlinedTextField(value = state.title,
            onValueChange = {
                onEvent(TodoEvent.SetTitle(it))
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextColor,
                unfocusedTextColor = TextColor,
                focusedPlaceholderColor = LightText,
                unfocusedPlaceholderColor = LightText,
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedBorderColor = TextFieldBorder,
                unfocusedBorderColor = TextFieldBorder,
            ),
            shape = RoundedCornerShape(6.dp),
            placeholder = {
                Text(text = "Task Title")
            })


        // Todo description TextField
        Text(
            text = "Task Description",
            style = Typography.titleMedium,
            fontFamily = inter,
            color = TextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 28.dp)
        )

        OutlinedTextField(value = state.description,
            onValueChange = {
                onEvent(TodoEvent.SetDescription(it))
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextColor,
                unfocusedTextColor = TextColor,
                focusedPlaceholderColor = LightText,
                unfocusedPlaceholderColor = LightText,
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedBorderColor = TextFieldBorder,
                unfocusedBorderColor = TextFieldBorder,
            ),
            shape = RoundedCornerShape(6.dp),
            placeholder = {
                Text(text = "Task Description (Optional)")
            })
    }
}


// Background of the app (Top image and color)
@Composable
fun BG(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .background(Background)
            .fillMaxSize()
    )
    Image(
        painter = painterResource(id = R.drawable.header),
        contentDescription = "Background Image",
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.7567568f)
    )
}


// Header of the app
@Composable
fun Header(
    text: String
) {
    Row(
        modifier = Modifier
            .padding(top = 50.dp)
            .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            text = text,
            style = Typography.titleMedium,
            fontFamily = inter,
            color = White,
            fontWeight = FontWeight.SemiBold
        )

    }
}