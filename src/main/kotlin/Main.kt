
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application

fun main() = application{

    val title = "AdriAG"
    val icon = BitmapPainter(useResource("icon.png", ::loadImageBitmap))
    val windowState = GetWindowState(
        windowWidth = 800.dp,
        windowHeight = 800.dp
    )
    val fileManager: IFileManager = FileManager()
    val studentsFile = fileManager.getFile("src/main/resources/students.txt")


    MainWindow(
        title = title,
        icon = icon,
        windowState = windowState,
        resizable = false,
        fileManager = fileManager,
        studentsFile = studentsFile,
        onCloseMainWindow = { exitApplication() }
    )

}




