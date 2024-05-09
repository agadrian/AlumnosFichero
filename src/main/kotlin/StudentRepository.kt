import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

/**
 * Sin usar .use
 */
class StudentRepository: IStudentRepository {

    override fun getAllStudents(): Result<List<String>> {
        val students = mutableListOf<String>()
        var connectionDb: Connection? = null
        var stmt: Statement? = null

        try {
            connectionDb = Database.getConnection()
            stmt = connectionDb.createStatement()

            val query = "SELECT name FROM students"
            val resultSet = stmt.executeQuery(query)

            while (resultSet.next()) {
                students.add(resultSet.getString("name"))
            }

            // Cerramos conexion antes de retornar
            stmt?.close()
            connectionDb.close()

            return Result.success(students)

        } catch (e: SQLException) {
            return Result.failure(e)
        }
    }


    override fun updateStudents(students: List<String>): Result<Unit> {
        var connectionDb: Connection? = null
        var stmt: Statement? = null
        var error: Exception? = null

        try {
            connectionDb = Database.getConnection()
            connectionDb.autoCommit = false

            stmt = connectionDb.createStatement()
            val query = " DELETE FROM students"
            stmt.execute(query)


            stmt = connectionDb.prepareStatement("INSERT INTO students (name) VALUES (?)")
            for (student in students) {
                stmt.setString(1, student)
                stmt.executeUpdate()
            }


            connectionDb.commit()
            return Result.success(Unit)

        } catch (e: Exception) {
            error = e
            try {
                connectionDb?.rollback()
            } catch (e: SQLException) {
                error = e
            }

        } finally {
            try {
                connectionDb?.autoCommit = true
                stmt?.close()
                connectionDb?.close()
            }catch (e: SQLException){
                error = e
            }


            // Retorno aquí fuera del try-catch-finally
            return if (error != null) {
                Result.failure(error)
            } else {
                Result.success(Unit)
            }
        }
    }
}