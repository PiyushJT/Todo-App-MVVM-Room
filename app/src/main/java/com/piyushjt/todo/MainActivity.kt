package com.piyushjt.todo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.piyushjt.todo.ui.theme.Background
import com.piyushjt.todo.ui.theme.CanceledText
import com.piyushjt.todo.ui.theme.CrimsonRed
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
import java.text.SimpleDateFormat
import java.util.Locale

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

        // Setting status bar & navigation bar colors
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

                NavHost(navController = navController, startDestination = Screen.MainScreen.route) {

                    // Main Screen
                    composable(
                        route = Screen.MainScreen.route
                    ) {
                        MainScreen(
                            navController = navController,
                            state = state,
                            onEvent = viewModel::onEvent
                        )
                    }
                    // Add a Todo Screen
                    composable(
                        route = Screen.AddTodoScreen.route + "/{id}",
                        arguments = listOf(
                            navArgument("id"){
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) { entry ->
                        AddTodoScreen(
                            id = entry.arguments?.getInt("id") ?: -1, // using -1 at null
                            state = state,
                            onEvent = viewModel::onEvent,
                            navController = navController
                        )
                    }

                }
            }
        }
    }

}


// Declaring an external font family
val inter = FontFamily(
    Font(R.font.inter, FontWeight.SemiBold)
)


// Main Screen Composable
@Composable
fun MainScreen(
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    navController: NavController
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
                navController = navController,
                onEvent = onEvent
            )

        }

        // Button to add a new Todo
        BottomButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            onEvent = onEvent,
            state = state,
            text = "Add New Task",
            navController = navController,
            id = -1
        )

    }
}


// Add Todo Screen Composable
@Composable
fun AddTodoScreen(
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    navController: NavController,
    id : Int
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
                id = id,
                onEvent = onEvent
            )

        }

        // Button to save the Todo
        BottomButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            onEvent = onEvent,
            state = state,
            text = "Save",
            navController = navController,
            id = id
        )

    }
}


// List of All Todos
@Composable
fun TaskList(
    state: TodoState,
    navController: NavController,
    onEvent: (TodoEvent) -> Unit
) {

    // Change title and description back to nulls
    LaunchedEffect("") {
        onEvent(TodoEvent.SetTitle(""))
        onEvent(TodoEvent.SetDescription(""))
    }

    // Curved Cornered White Card
    Card(
        modifier = Modifier
            .padding(top = 14.dp)
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.8f)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .background(Transparent),
        shape = RoundedCornerShape(16.dp)
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

                        // Opening Add Todo Screen to update or delete
                        .clickable {
                            onEvent(TodoEvent.ShowAddTodo)
                            navController.navigate(Screen.AddTodoScreen.withargs(todo.id))
                        }
                        .aspectRatio(4.475f)
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
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    id : Int,
    navController : NavController,
    text : String
) {

    Column(
        modifier = modifier
            .padding(
                bottom = if (WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding() == 0.dp
                ) {
                    16.dp
                } else {
                    WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                }
            )
            .fillMaxWidth()
            .background(Background),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(id != -1){
            Button(
                onClick = {
                    // Deleting Todo
                    onEvent(TodoEvent.DeleteTodo(todo = state.todos.find { it.id == id }!!))
                    onEvent(TodoEvent.HideAddTodo)

                    // Navigating back to main screen
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.MainScreen.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 16.dp)
                    .aspectRatio(6.392857f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonRed
                ),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "Delete Todo",
                    style = Typography.titleMedium,
                    fontFamily = inter,
                    color = White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Button(
            onClick = {

                // Saving a new todo
                if (id == -1) {
                    if (text == "Save") {
                        onEvent(TodoEvent.SaveTodo)

                        onEvent(TodoEvent.HideAddTodo)
                        navController.navigate(Screen.MainScreen.route) {
                            popUpTo(Screen.MainScreen.route) { inclusive = true }
                        }
                    } else {
                        onEvent(TodoEvent.ShowAddTodo)
                        navController.navigate(Screen.AddTodoScreen.withargs(-1))
                    }
                }
                // Updating an Existing todo
                else {
                    onEvent(TodoEvent.SetID(id))

                    onEvent(TodoEvent.SaveTodo)
                    onEvent(TodoEvent.HideAddTodo)
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.MainScreen.route) { inclusive = true }
                    }
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
    navController: NavController,
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
            modifier = Modifier,
            text = "My Todo List",
            style = Typography.titleLarge,
            fontFamily = inter,
            color = White,
            fontWeight = FontWeight.SemiBold
        )

        // The card of all todos
        TaskList(
            state = state,
            navController = navController,
            onEvent = onEvent
        )
    }
}


// New todo composable
@Composable
fun NewTask(
    modifier: Modifier = Modifier,
    state: TodoState,
    id : Int,
    onEvent: (TodoEvent) -> Unit
) {

    // Setting Values in State if user is updating the todo
    LaunchedEffect(id) {
        if (id != -1) {
            val todo = state.todos.find { it.id == id }
            if (todo != null) {
                onEvent(TodoEvent.SetTitle(todo.title))
                onEvent(TodoEvent.SetDescription(todo.description ?: ""))
            }
        }
    }

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
            .padding(top = 50.dp, bottom = 10.dp)
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