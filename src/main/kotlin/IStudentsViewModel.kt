import androidx.compose.runtime.State

interface IStudentsViewModel{
    val newStudent: State<String>
    val studentList: List<String>
    val infoMessage: State<String>
    val showInfoMessage: State<Boolean>
    val selectedIndex: State<Int>
    fun changeName(name: String)
    fun addStudent()
    fun saveStudents()
    fun clearStudents()
    fun loadStudents()
    fun deleteStudent(index: Int)
    fun showInfoMessage(show: Boolean)
    fun studentSelected(index: Int)
}