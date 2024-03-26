import java.sql.*;

public class Database {
    private Connection connection;
    private Statement statement;

    public Database(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_mahasiswa", "root", "");
            statement = connection.createStatement();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public ResultSet selectQuery(String sql){
        try {
            statement.executeQuery(sql);
            return  statement.getResultSet();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public int insertUpdateDeleteQuery(String sql){
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public int executeCountQuery(String sql) {
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next(); // Pindahkan cursor ke baris pertama
            int count = resultSet.getInt(1); // Ambil nilai COUNT(*) dari baris pertama
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Statement getStatement() {
        return statement;
    }
}
