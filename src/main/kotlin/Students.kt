import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.delay
import java.awt.Toolkit
import java.io.File



@Composable
fun GetWindowState(
    windowWidth: Dp,
    windowHeight: Dp
): WindowState{
    // Obtener las dimensiones de la pantalla
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val screenWidth = screenSize.width
    val screenHeight = screenSize.height

    // Calcular la posición para centrar la ventana
    val positionX = (screenWidth / 2 - windowWidth.value.toInt() / 2)
    val positionY = (screenHeight / 2 - windowHeight.value.toInt() / 2)

    return rememberWindowState(
        size = DpSize(windowWidth, windowHeight),
        position = WindowPosition(positionX.dp, positionY.dp)
    )
}



@Composable
@Preview
fun MainWindow(
    title: String,
    icon: BitmapPainter,
    windowState: WindowState,
    resizable: Boolean,
    fileManager: IFileManager,
    studentsFile: File?,
    onCloseMainWindow : () -> Unit
) {
    Window(
        onCloseRequest = onCloseMainWindow,
        title = title,
        icon = icon,
        resizable = resizable,
        state = windowState
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


    val newStudentFocusRequester = remember { FocusRequester() }
    val studentListFocusRequester = remember { FocusRequester() }

    val maxCharacters = 10
    val maxNumStudentsVisible = 7

    var infoMessage by remember { mutableStateOf("") }
    var showInfoMessage by remember { mutableStateOf(false) }


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
            StudentFormColumn(
                newStudent = newStudent,
                onNewStudentChange = { newStudent = it},
                newStudentFocusRequester = newStudentFocusRequester,
                studentsList = studentsList,
                onStudentListChange = { studentsList = it }

            )



            // COLUMNA LISTA
            StudentListColumn(
                studentsList = studentsList,
                scrollState = scrollState,
                scrollVerticalState = scrollVerticalState,
                studentListFocusRequester = studentListFocusRequester,
                onDelete = { index -> studentsList = studentsList.filterIndexed { i, _ -> i != index } },
                onClearAll = { studentsList = emptyList() }
            )
        }

        SaveChangesButton(
            onSaveChanges = {
                studentsFile?.let { file ->
                    fileManager.saveStudents(file, studentsList)
                }
                infoMessage = "Fichero guardado."
                showInfoMessage = true
            }

        )



    }

    // Gestión de la visibilidad del mensaje informativo
    if (showInfoMessage) {
        InfoMessage(
            message = infoMessage,
            onCloseInfoMessage = {
                showInfoMessage = false
                infoMessage = ""
            }
        )
    }

    // Automáticamente oculta el mensaje después de un retraso
    LaunchedEffect(showInfoMessage) {
        if (showInfoMessage) {
            delay(2000)
            showInfoMessage = false
            infoMessage = ""
        }
    }

}


@Composable
fun StudentFormColumn(
    newStudent: String,
    onNewStudentChange: (String) -> Unit,
    newStudentFocusRequester: FocusRequester,
    studentsList: List<String>,
    onStudentListChange: (List<String>) -> Unit

){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = newStudent,
            onValueChange = { onNewStudentChange(it) },
            label = { Text("New student name") },
            modifier = Modifier
                .padding(15.dp)
                .focusRequester(newStudentFocusRequester)
        )

        Button(
            onClick = {
                if (newStudent.isNotBlank()) {
                    onStudentListChange(studentsList + newStudent)
                    onNewStudentChange("")

                }
                newStudentFocusRequester.requestFocus()
            },
            modifier = Modifier.padding(15.dp)
        ) {
            Text("Add student")
        }
    }
}


@Composable
fun StudentListColumn(
    studentsList: List<String>,
    scrollState: ScrollState,
    scrollVerticalState: LazyListState,
    studentListFocusRequester: FocusRequester,
    onDelete: (Int) -> Unit,
    onClearAll: () -> Unit
){
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
                    .padding(10.dp)
                    .focusRequester(studentListFocusRequester),
                scrollVerticalState

            ) {

                items(studentsList.size) { index ->
                    StudentRow(
                        student = studentsList[index],
                        onDelete = { onDelete(index) }
                        //onDelete = { studentsList = studentsList.filterIndexed { i, _ -> i != index } }
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
            onClick = { onClearAll() },
            modifier = Modifier
                .padding(15.dp),
        ) {
            Text("Clear all")
        }
    }
}


@Composable
fun SaveChangesButton(
    onSaveChanges: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Button(
                onClick = onSaveChanges
            ) {
                Text("Save changes")
            }
        }
    }
}



@Composable
fun StudentRow(
    student: String,
    onDelete: () -> Unit
) {
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

@Composable
fun InfoMessage(message: String, onCloseInfoMessage: () -> Unit) {
    Dialog(
        icon = painterResource("icon.png"),
        title = "Atención",
        resizable = false,
        onCloseRequest = onCloseInfoMessage
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(message)
        }
    }
}