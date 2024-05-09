import java.io.File
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StudentsViewModelFile(
    private val fileManagement: IFiles,
    private val studentsFile: File
): IStudentsViewModel {

    companion object {
        private const val MAXCHARACTERS = 10
        //private const val MAXNUMSTUDENTSVISIBLE = 7
    }

    private var _newStudent = mutableStateOf("")
    override val newStudent: State<String> = _newStudent

    private val _studentList = mutableStateListOf<String>()
    override val studentList: List<String> = _studentList

    private val _infoMessage = mutableStateOf("")
    override val infoMessage: State<String> = _infoMessage

    private val _showInfoMessage = mutableStateOf(false)
    override val showInfoMessage: State<Boolean> = _showInfoMessage

    private val _selectedIndex = mutableStateOf(-1) // -1 significa que no hay selección
    override val selectedIndex: State<Int> = _selectedIndex




    override fun loadStudents() {
        val loadedStudents = fileManagement.leer(studentsFile)
        if (loadedStudents != null) {
            _studentList.addAll(loadedStudents)
        } else {
            updateInfoMessage("No se pudieron cargar los datos de los estudiantes.")
        }
    }

    private fun updateInfoMessage(message: String) {
        _infoMessage.value = message
        _showInfoMessage.value = true
        CoroutineScope(Dispatchers.Default).launch {
            delay(2000)
            _showInfoMessage.value = false
            _infoMessage.value = ""
        }
    }


    override fun changeName(name: String) {
        if (name.length < MAXCHARACTERS) _newStudent.value = name
    }

    override fun addStudent() {
        if (_newStudent.value.isNotBlank()) {
            _studentList.add(_newStudent.value.trim())
            _newStudent.value = ""
        }
    }

    override fun saveStudents() {
        var error = ""
        val newStudentsFile = fileManagement.crearFic(studentsFile.absolutePath)
        if (newStudentsFile != null) {
            for (student in studentList) {
                error = fileManagement.escribir(studentsFile, "$student\n")
                if (error.isNotEmpty()) {
                    break
                }
            }
            if (error.isNotEmpty()) {
                updateInfoMessage(error)
            } else {
                updateInfoMessage("Fichero guardado correctamente")
            }
        } else {
            updateInfoMessage("No se pudo generar el fichero studentList.txt")
        }
    }

    override fun clearStudents(){
        _studentList.clear()
    }

    override fun deleteStudent(index: Int) {
       if (index in _studentList.indices){
           _studentList.removeAt(index)
       }
    }

    override fun showInfoMessage(show: Boolean) {
        _showInfoMessage.value = show
    }

    override fun studentSelected(index: Int) {
        _selectedIndex.value = index
    }

}