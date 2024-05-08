import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLTimeoutException
import java.sql.SQLException

object Database {
    private const val URL = "jdbc:mysql://localhost:3306/studentdb"
    private const val USER = "studentuser"
    private const val PASSWORD = "password"
    init {
        try {
            // Asegurarse de que el driver JDBC de MySQL esté disponible
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (e: ClassNotFoundException) {
            e.printStackTrace();
        }
    }
    fun getConnection(): Connection =
        try {
            DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (e: SQLTimeoutException) {
            //println("$e : La conexión ha excedido el tiempo de espera permitido.")

        } catch (e: SQLException) {
            //throw SqlErrorException("Error de SQL: ${e.message}")
        }

    fun closeConnection(): Connection{
        try {
            DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (e: SQLTimeoutException) {
    }
}
