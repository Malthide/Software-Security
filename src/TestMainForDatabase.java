/* TestMainForDatabase.java
   Created by Christopher Walker.
   Created 14 June 2022.
   Last modified 22 June 2022.
   This program can be run to test the database_access package. It also provides a template for how methods
   in that package should be accessed. Especially note the import statements and how the connection to the
   database is opened and closed. Note that the database address, username, and password are currently set
   according to my PC's specifications and may need to be changed based on the specifics of your database
   management system.
   Also note that the file "ojdbc6.jar" has been added to the classpath of this program, which allows the
   OracleDriver to be included. This is important to java.sql.DriverManager since I'm using Oracle Database
   as my database management system. If you are using a different database system, you may need to add its
   driver to the classpath instead.
   Please contact me with any questions.
 */


import database_access.DatabaseAccess;
import database_access.UserInfo;

import java.sql.Connection;
import java.sql.DriverManager;


public class TestMainForDatabase {
    public static void main(String[] args) {
        try {
            //The database address, username, and password should be modified when run on a different system.
            String database_address = "jdbc:oracle:thin:@localhost:1521:xe";
            String database_username = "system";
            String database_password = "YellowGreen27";
            Connection conn = DriverManager.getConnection(database_address, database_username, database_password);

            int num = DatabaseAccess.verify_user_pass(conn, "nurse_martinez", "password3");
            System.out.println("Verify password result: " + num);

            UserInfo u_info = DatabaseAccess.pull_user_info(conn, "nurse_martinez");

            System.out.println("System ID number: " + u_info.id_num);
            System.out.println("First name: " + u_info.first_name);
            System.out.println("Last name: " + u_info.last_name);
            System.out.println("Authorization level: " + u_info.authorization_level);

            conn.close();
        } catch (Exception SQLException) {
            System.out.println(SQLException);
        }
    }
}