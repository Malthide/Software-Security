/* TestMainForDatabase.java
   Created by Christopher Walker.
   Created 14 June 2022.
   Last modified 23 June 2022.
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
import database_access.DoctorSchedule;
import database_access.TimeSlot;
import database_access.PatientInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Calendar;
import java.util.ListIterator;


public class TestMainForDatabase {
    public static void main(String[] args) {
        try {
            //The database address, username, and password should be modified when run on a different system.
            String database_address = "jdbc:oracle:thin:@localhost:1521:xe";
            String database_username = "system";
            String database_password = "YellowGreen27";
            Connection conn = DriverManager.getConnection(database_address, database_username, database_password);

            //Test DatabaseAccess.verify_user_pass()
            int num = DatabaseAccess.verify_user_pass(conn, "nurse_martinez", "password3");
            System.out.println("Verify password result: " + num);
            System.out.println();

            //Test DatabaseAccess.pull_user_info()
            UserInfo u_info = DatabaseAccess.pull_user_info(conn, "nurse_martinez");
            System.out.println("System ID number: " + u_info.id_num);
            System.out.println("First name: " + u_info.first_name);
            System.out.println("Last name: " + u_info.last_name);
            System.out.println("Authorization level: " + u_info.authorization_level);
            System.out.println();

            //Test DatabaseAccess.pull_doctor_schedule()
            DoctorSchedule ds = DatabaseAccess.pull_doctor_schedule(conn, 9427); //9427 is the id_num of Doctor Stephens
            ListIterator<TimeSlot> litr = ds.time_slot_list.listIterator();
            while (litr.hasNext()) {
                System.out.print("Availability start (DD/MM/YYYY): " + litr.next().start_time.get(Calendar.DATE));
                litr.previous();
                System.out.print("/" + litr.next().start_time.get(Calendar.MONTH));
                litr.previous();
                System.out.print("/" + litr.next().start_time.get(Calendar.YEAR));
                litr.previous();
                System.out.print(" " + litr.next().start_time.get(Calendar.HOUR));
                litr.previous();
                System.out.println(":" + litr.next().start_time.get(Calendar.MINUTE));
                litr.previous();
                System.out.print("Availability end (DD/MM/YYYY): " + litr.next().end_time.get(Calendar.DATE));
                litr.previous();
                System.out.print("/" + litr.next().end_time.get(Calendar.MONTH));
                litr.previous();
                System.out.print("/" + litr.next().end_time.get(Calendar.YEAR));
                litr.previous();
                System.out.print(" " + litr.next().end_time.get(Calendar.HOUR));
                litr.previous();
                System.out.println(":" + litr.next().end_time.get(Calendar.MINUTE));
            }
            System.out.println();

            //Test DatabaseAccess.pull_patient_info()
            Calendar calendar = Calendar.getInstance();
            calendar.set(1982, 7, 16);
            PatientInfo p_info = DatabaseAccess.pull_patient_info(conn, "Jia", "Chen", calendar);
            System.out.println("Patient ID number: " + p_info.id_num);
            System.out.println("Street address: " + p_info.street_address);
            System.out.println("City: " + p_info.city);
            System.out.println("State: " + p_info.us_state);
            System.out.println("ZIP Code: " + p_info.zip_code);
            System.out.println("Insurance provider id number: " + p_info.insurance_provider);
            System.out.println("Insurance policy number: " + p_info.policy_num);
            System.out.println();

            //Test DatabaseAccess.pull_patient_ssn() for the same patient
            String ssn = DatabaseAccess.pull_patient_ssn(conn, p_info);
            System.out.println("Patient ssn: " + ssn);

            //Test DatabaseAccess.pull_appt_schedule()

            //Test DatabaseAccess.add_appt_to_schedule()

            //Test DatabaseAccess.pull_chart_records()

            conn.close();
        } catch (Exception SQLException) {
            System.out.println(SQLException);
        }
    }
}