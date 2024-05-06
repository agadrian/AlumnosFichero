import java.io.File
import androidx.compose.runtime.*
interface IStudentsViewModel{
    val newStudent: State<String>
    val studentList: List<String>
    fun changeName(name: String)
    fun addStudent()
}

class StudentsViewModel(
    private val fileManagement: IFileManager,
    private val studentsFile: File
): IStudentsViewModel {

    companion object {
        private const val MAXCHARACTERS = 10
        private const val MAXNUMSTUDENTSVISIBLE = 7
    }

    private var _newStudent = mutableStateOf("")
    override val newStudent: State<String> = _newStudent

    private var _studentList = mutableStateListOf<String>()
    override val studentList: List<String> = _studentList


    override fun changeName(name: String) {
        if (name.length < MAXCHARACTERS) _newStudent.value = name
    }

    override fun addStudent() {
        if (_newStudent.value.isNotBlank()) {
            _studentList.add(_newStudent.value.trim())
            _newStudent.value = ""
        }
    }

}