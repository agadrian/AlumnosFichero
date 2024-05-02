import org.jetbrains.skia.Path
import java.io.File

interface IFileManager{
    fun getFile(path: String): File?
    fun loadStudents(file: File): List<String>
}

class FileManager: IFileManager {
    override fun getFile(path: String): File? {
        val file = File(path)
        return if (file.isFile) file else null
    }

    override fun loadStudents(file: File): List<String> {
        val studentsList = mutableListOf<String>()
        if (file.isFile) {
            file.forEachLine { line ->
                val students = line.split(",").map { it.trim() }
                students.forEach { studentsList.add(it) }
            }
        }
        return studentsList
    }


}