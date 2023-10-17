import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;

public class Main {
    private static Connection connection;
    private static String query;
    private static PreparedStatement statement;
    private static BufferedReader reader;


    public static void main(String[] args) throws SQLException, IOException {
        Properties properties = new Properties();
        properties.setProperty("username", "root");
        properties.setProperty("password", "123max123");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", properties);
        reader = new BufferedReader(new InputStreamReader(System.in));


        //  2. Get Villains’ Names
        //getVillainsNameAndCountOfMinions();


        //  3. Get Minion Names
        //getMinionNamesExercise();

        // 4. Add Minion
        //addMinionExercise();

        // 9. Increase Age Stored Procedure
        //increaseAgeWithStoredProcedure();
    }

    private static void increaseAgeWithStoredProcedure() throws IOException, SQLException {
        System.out.println("Enter minion id:");
        int minionId = Integer.parseInt(reader.readLine());
        query = "CALL usp_get_older(?)";

        CallableStatement callableStatement = connection.prepareCall(query);
        callableStatement.setInt(1, minionId);
        callableStatement.execute();
    }

    private static void addMinionExercise() throws IOException, SQLException {
        System.out.println("Enter minion parameters: ");
        String [] minionParameters = reader.readLine().split("\\s+");
        String minionName = minionParameters[0];
        int minionAge = Integer.parseInt(minionParameters[1]);
        String minionTown = minionParameters[2];

        System.out.println("Enter villain name: ");
        String villainName = reader.readLine();

        if(!checkIfEntityExistsByName(minionTown, "towns")) {
            insertEntityInTown(minionTown);
        }

    }

    private static void insertEntityInTown(String minionTown) throws SQLException {

        query = "INSERT INTO towns (name, country) value(?, ?)";
        statement = connection.prepareStatement(query);
        statement.setString(1, minionTown);
        statement.setString(2, "NULL");
        statement.execute();

    }

    private static boolean checkIfEntityExistsByName(String entityName, String tableName) throws SQLException {

        query = "SELECT * FROM " + tableName + " WHERE name = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, entityName);
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next();
    }

    private static void getMinionNamesExercise() throws IOException, SQLException {
        System.out.println("Enter villain id:");
       int villain_id = Integer.parseInt(reader.readLine());

        if(!checkIfEntityExists(villain_id, "villains")){
            System.out.printf("No villain with ID %d exists in the database.", villain_id);
        } else {
            System.out.printf("Villain: %s%n", getEntityNameById(villain_id, "villains"));
            getMinionNameAndAgeByVillainsId(villain_id);

        }



    }

    private static void getMinionNameAndAgeByVillainsId(int villain_id) throws SQLException {
        query = "SELECT m.name, m.age FROM minions AS m\n" +
                "JOIN minions_villains mv on m.id = mv.minion_id\n" +
                "WHERE mv.villain_id = ?";
        statement = connection.prepareStatement(query);
        statement.setInt(1, villain_id);
        ResultSet resultSet = statement.executeQuery();
        int minionNumber = 0;

        while(resultSet.next()) {
            System.out.printf("%d. %s %d%n", ++minionNumber,
                               resultSet.getString("name"), resultSet.getInt(2));
        }
    }

    private static String getEntityNameById(int entityId, String tableName) throws SQLException {

        query = "SELECT name FROM " + tableName + " WHERE id = ?";
        statement = connection.prepareStatement(query);
        statement.setInt(1, entityId);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next() ? resultSet.getString("name") : null;
    }

    private static boolean checkIfEntityExists(int villainId, String villains) throws SQLException {
        query = "SELECT * FROM " + villains +" WHERE id = ?";

        statement = connection.prepareStatement(query);
        statement.setInt(1, villainId);
        ResultSet resultSet = statement.executeQuery();

        return resultSet.next();
    }


    private static void getVillainsNameAndCountOfMinions() throws SQLException {
        query = "SELECT v.name, count(distinct mv.minion_id) count_of_minions\n" +
                "FROM villains AS v\n" +
                "JOIN minions_villains mv ON v.id = mv.villain_id\n" +
                "GROUP BY v.id\n" +
                "HAVING count_of_minions > 15\n" +
                "ORDER BY count_of_minions DESC";
        statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while(resultSet.next()) {
            System.out.printf("%s %d%n", resultSet.getString(1), resultSet.getInt(2));
        }

    }
}