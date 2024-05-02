import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File

fun main() {
    val title = "AdriAG"
    val icon = BitmapPainter(useResource("icon.png", ::loadImageBitmap))
    val fileManager: IFileManager = FileManager()
    val studentsFile = fileManager.getFile("src/main/resources/students.txt")

    application {
        Window(
            title = title,
            icon = icon,
            onCloseRequest = { exitApplication() }
        ) {
            MainWindow(
                fileManager = fileManager,
                studentsFile = studentsFile
            )
        }
    }
}


@Composable
@Preview
fun MainWindow(
    fileManager: IFileManager,
    studentsFile: File?
) {
    MaterialTheme {
        Surface(
            color = Color.LightGray
        ) {
            StudentScreen(
                fileManager = fileManager,
                studentsFile = studentsFile
            )
        }
    }
}

@Composable
fun StudentScreen(
    fileManager: IFileManager,
    studentsFile: File?
){
    var newStudent by remember { mutableStateOf("") }
    var studentsList by remember { mutableStateOf<List<String>>(emptyList()) }
    val scrollState = rememberScrollState()
    val scrollVerticalState = rememberLazyListState()

    LaunchedEffect(studentsFile) {
        studentsFile?.let { file ->
            val loadedStudents = fileManager.loadStudents(file)
            studentsList = loadedStudents
        }
    }

    Box {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // COLUMNA FORMULARIO
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = newStudent,
                    onValueChange = { newStudent = it },
                    label = { Text("New student name") },
                    modifier = Modifier
                        .padding(15.dp)
                )

                Button(
                    onClick = {
                        if (newStudent.isNotBlank()) {
                            studentsList = studentsList + newStudent
                            newStudent = ""
                        }
                    },
                    modifier = Modifier.padding(15.dp)
                ) {
                    Text("Add student")
                }
            }


            // COLUMNA LISTA
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
            ) {
                Text(
                    "Students: ${studentsList.size}",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)

                )

                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .height(300.dp)
                        .border(2.dp, Color.Black)
                        .padding(10.dp)
                        .width(200.dp)
                ) {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                            .padding(10.dp),
                        scrollVerticalState

                    ) {

                        items(studentsList.size) { index ->
                            StudentRow(
                                student = studentsList[index],
                                onDelete = { studentsList = studentsList.filterIndexed { i, _ -> i != index } }
                            )
                        }
                    }

                    VerticalScrollbar(
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(
                            scrollState = scrollVerticalState
                        )
                    )
                }

                Button(
                    onClick = { studentsList = emptyList() },
                    modifier = Modifier
                        .padding(15.dp),
                ) {
                    Text("Clear all")
                }
            }
        }


        Button(
            onClick = {
                studentsFile?.let { file ->
                    fileManager.saveStudents(file, studentsList)
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text("Save changes")
        }
    }

}

@Composable
fun StudentRow(student: String, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = student,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .widthIn(min = 120.dp)
        )

        IconButton(
            onClick = onDelete
        ) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete Student")
        }
    }
}
