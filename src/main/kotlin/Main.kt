import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sun.tools.javac.Main
import java.io.File
import javax.swing.Icon

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
        Surface {
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

    LaunchedEffect(studentsFile) {
        studentsFile?.let { file ->
            val loadedStudents = fileManager.loadStudents(file)
            studentsList = loadedStudents
        }
    }
    Row (
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = newStudent,
                onValueChange = { newStudent = it },
                label = { Text("Nuevo estudiante") },
                modifier = Modifier.padding(15.dp)
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
                Text("Añadir estudiante")
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            studentsList.forEach { student ->
                Text(student)
            }
        }
    }

}

