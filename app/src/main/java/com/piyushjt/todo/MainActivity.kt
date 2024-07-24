package com.piyushjt.todo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    BG()
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column {
                            Header(innerPadding)
                            MyTodoList()
                        }
                        BottomButton(
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                }
            }
        }
    }
}

val inter = FontFamily(
    Font(R.font.inter, FontWeight.SemiBold)
)

@Composable
fun TaskList(
    tasks: List<Todo>
) {
    Card(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(0.9f)
            .aspectRatio(4.475f / tasks.size)
            .background(Transparent), shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (task in tasks) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .aspectRatio(4.475f),
                ) {

                    if (tasks[0] != task) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Line)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            if (task.isChecked) {
                                Text(
                                    text = task.title,
                                    style = Typography.titleLarge,
                                    color = CanceledText,
                                    fontFamily = inter,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = TextDecoration.LineThrough
                                )
                                task.description?.let {
                                    Text(
                                        text = it,
                                        style = Typography.titleMedium,
                                        color = LightCanceledText,
                                        fontFamily = inter,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }
                            } else {
                                Text(
                                    text = task.title,
                                    style = Typography.titleLarge,
                                    color = TextColor,
                                    fontFamily = inter,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                task.description?.let {
                                    Text(
                                        text = it,
                                        style = Typography.titleMedium,
                                        color = LightText,
                                        fontFamily = inter,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                }
                            }

                        }
                        Checkbox(
                            checked = task.isChecked, onCheckedChange = {},
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

@Composable
fun BottomButton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4.25f)
            .background(Background),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { },
            modifier = modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(6.392857f)
                .background(Transparent),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple
            ),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = "Add New Task",
                style = Typography.titleMedium,
                fontFamily = inter,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

}

@SuppressLint("Range")
@Composable
fun MyTodoList(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Transparent)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "My Todo List",
            style = Typography.titleLarge,
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold
        )

        TaskList(
            tasks = listOf(
                Todo(
                    "Study", "8.5 Hrs", true
                ),
                Todo(
                    "Run", "5 Km", false
                ),
                Todo(
                    "Study", "8.5 Hrs", true
                ),
                Todo(
                    "Run", "5 Km", false
                ),

                Todo(
                    "Go to Party", null, false
                ),
                Todo(
                    "Study", "8.5 Hrs", true
                ),
                Todo(
                    "Run", "5 Km", false
                ),
                Todo(
                    "Go to Party", null, false
                ),
                Todo(
                    "Study", "8.5 Hrs", true
                ),
                Todo(
                    "Go to Party", null, false
                ),

                )
        )


    }
}

@Composable
fun NewTask(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Background)
            .padding(20.dp)
    ) {
        Text(
            text = "Task Title",
            style = Typography.titleMedium,
            fontFamily = inter,
            color = TextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 28.dp)
        )

        OutlinedTextField(value = "",
            onValueChange = {},
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

        Text(
            text = "Task Description",
            style = Typography.titleMedium,
            fontFamily = inter,
            color = TextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 28.dp)
        )

        OutlinedTextField(value = "",
            onValueChange = {},
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

@Composable
fun BG(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.header),
        contentDescription = "Background Image",
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.7567568f)
    )
}

@Composable
fun Header(
    paddingValues: PaddingValues
) {
    Row(
        modifier = Modifier
            .padding(paddingValues)
            .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CircularButton()

        Text(
            text = "October 20, 2022",
            style = Typography.titleMedium,
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold
        )

        CircularButton()

    }
}

@Composable
fun CircularButton(
    image: Int? = null, bg: Color = Transparent
) {
    IconButton(
        onClick = {},
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .background(bg, shape = CircleShape)
    ) {
        if (image != null) {
            Icon(
                painter = painterResource(id = image), contentDescription = "Back Button"
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreen() {
    TodoTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            BG()
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    Header(innerPadding)
                    MyTodoList()
                }
                BottomButton(
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

        }
    }
}