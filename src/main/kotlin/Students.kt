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
import androidx.compose.ui.focus.onFocusChanged
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
    fileManager: IFiles,
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

        val repository = StudentRepository()

        MaterialTheme {
            Surface(
                color = colorWindowBackground,
                modifier = Modifier.fillMaxSize()
            ) {
                StudentScreen(StudentsViewModelDb(fileManager, studentsFile!!, repository))
                //StudentScreen(StudentsViewModelFile(fileManager, studentsFile!!))

            }
        }
    }
}




@Composable
fun StudentScreen(
    viewModel: IStudentsViewModel
){
    val newStudent by viewModel.newStudent
    val studentsList = viewModel.studentList

    //DEJAR AQUI
    val scrollState = rememberScrollState()
    val scrollVerticalState = rememberLazyListState()

    //DEJAR AQUI
    val newStudentFocusRequester = remember { FocusRequester() }
    val studentListFocusRequester = remember { FocusRequester() }

    val infoMessage by viewModel.infoMessage
    val showInfoMessage by viewModel.showInfoMessage

    val selectedIndex by viewModel.selectedIndex

    LaunchedEffect(key1 = true) {  // key1 = true asegura que esto se ejecute solo una vez
        viewModel.loadStudents()
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
                newStudentFocusRequester = newStudentFocusRequester,
                onNewStudentChange = {name ->  viewModel.changeName(name) },
                onButtonAddStudent = { viewModel.addStudent() }
            )



            // COLUMNA LISTA
            StudentListColumn(
                studentsList = studentsList,
                scrollState = scrollState,
                scrollVerticalState = scrollVerticalState,
                studentListFocusRequester = studentListFocusRequester,
                onDelete = { index -> viewModel.deleteStudent(index) },
                onButtonClearStudentsClick = { viewModel.clearStudents() },
                onStudentSelected = { index -> viewModel.studentSelected(index) },
                selectedIndex = selectedIndex

            )
        }

        SaveChangesButton(
            onSaveChanges = {
                viewModel.saveStudents()
                newStudentFocusRequester.requestFocus()
            }
        )
    }

    // Gestión de la visibilidad del mensaje informativo
    if (showInfoMessage) {
        InfoMessage(
            message = infoMessage,
            onCloseInfoMessage = {
                viewModel.showInfoMessage(false)
                newStudentFocusRequester.requestFocus()
            }
        )
    }

    // Automáticamente oculta el mensaje después de un retraso
    LaunchedEffect(showInfoMessage) {
        if (showInfoMessage) {
            delay(1000)
            viewModel.showInfoMessage(false)
        }
    }

}


@Composable
fun StudentFormColumn(
    newStudent: String,
    newStudentFocusRequester: FocusRequester,
    onNewStudentChange: (String) -> Unit,
    onButtonAddStudent: () -> Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = newStudent,
            onValueChange =  onNewStudentChange ,
            label = { Text("New student name") },
            modifier = Modifier
                .padding(15.dp)
                .focusRequester(newStudentFocusRequester)
        )

        Button(
            onClick = {
                if (newStudent.isNotBlank()) onButtonAddStudent()

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
    onButtonClearStudentsClick: () -> Unit,
    onStudentSelected: (Int) -> Unit,
    selectedIndex: Int
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
                .background(colorFocusComponentsBackground)
                .height(300.dp)
                .border(2.dp, colorBorder)
                .padding(10.dp)
                .width(200.dp)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(10.dp)
                    .focusRequester(studentListFocusRequester)
                    .focusable()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused && selectedIndex >= 0){
                            onStudentSelected(selectedIndex)
                        }
                    },
                scrollVerticalState

            ) {
                items(studentsList.size) { index ->
                    StudentRow(
                        student = studentsList[index],
                        onDelete = { onDelete(index) },
                        index = index,
                        selectedIndex = selectedIndex
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
            onClick = { onButtonClearStudentsClick() },
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
    onDelete: () -> Unit,
    index: Int,
    selectedIndex: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = student,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .widthIn(min = 120.dp)
                .background(if (index == selectedIndex) colorSelected else colorUnselected)
        )

        IconButton(
            onClick = onDelete
        ) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete Student")
        }
    }
}

@Composable
fun InfoMessage(
    message: String,
    onCloseInfoMessage: () -> Unit
) {
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