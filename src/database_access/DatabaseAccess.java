/* DatabaseAccess.java
   Created by Christopher Walker.
   Created 14 June 2022.
   Last modified 22 June 2022.
   This package provides access functions to a specified SQL database connection. The methods provided here
   are designed for ease of database access and according to the design specifications given in the project
   description.
   Development Update: At this time, the only methods completed are the verify_user_pass() function, the
   pull_user_info function, and the pull_doctor_schedule function. Verify_user_pass should be used in the
   "Login" use case found in the project description. Pull_user_info should be used to find the user's ID
   number and authorization level (to determine access permissions). Pull_doctor_schedule should be used
   in the "Make Appointment" use case found in the project description.
 */


package database_access;

import java.sql.*;
import java.util.Calendar;


public class DatabaseAccess {
    /* verify_user_pass: Takes a database connection conn, a username as a string, and a plaintext password
            as a string. Returns 1 if username and password combo are valid. Returns 0 if username was
            found but password is incorrect. Returns 3 if username is not found in database. Returns 2 for
            SQLException.
     */
    static public int verify_user_pass(Connection conn, String username, String password) {
        try {
            String safe_username = DatabaseSecurity.replace_illegal_SQL(username);

            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM user_root WHERE username = '" + safe_username + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next() != true)
                return 3;
            String db_hash = rs.getString("password_hash");

            String pass_hash = new String(DatabaseSecurity.hashing(password));
            pass_hash = DatabaseSecurity.replace_illegal_SQL(pass_hash); //This is necessary because the original hash also had to pass through modification before database storage.

            //Check that each char in pass_hash matches db_hash. If there's a mismatch, return 0.
            for (int i = 0; i < pass_hash.length(); i++) {
                if (pass_hash.charAt(i) != db_hash.charAt(i))
                    return 0;
            }
            return 1;
        } catch (Exception SQLException) {
            System.out.println(SQLException);
            return 2;
        }
    }


    /* pull_user_info: Takes a database connection conn and a username as a string. Returns data from the
            user_info table of the database as a UserInfo object (which is defined in the file UserInfo.java
            found within the database_access package). If a SQL exception occurs, returns a UserInfo object
            with id_num -1 and authorization_level -1.
     */
    static public UserInfo pull_user_info(Connection conn, String username) {
        try {
            String safe_username = DatabaseSecurity.replace_illegal_SQL(username);

            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM user_info WHERE username = '" + safe_username + "'";
            ResultSet rs = stmt.executeQuery(query);

            rs.next();
            int id_num = rs.getInt("id_num");
            String efn = rs.getString("first_name");
            byte[] enc_first_name = DatabaseSecurity.hex_string_to_byte_array(efn);
            int first_name_illegal_index = rs.getInt("first_name_illegal_index");
            String eln = rs.getString("last_name");
            byte[] enc_last_name = DatabaseSecurity.hex_string_to_byte_array(eln);
            int last_name_illegal_index = rs.getInt("last_name_illegal_index");
            int authorization_level = rs.getInt("authorization_level");

            //enc_first_name = DatabaseSecurity.put_apostrophes_back_in(enc_first_name, first_name_illegal_index);
            String first_name = DatabaseSecurity.decrypt(enc_first_name);
            //enc_last_name = DatabaseSecurity.put_apostrophes_back_in(enc_last_name, last_name_illegal_index);
            String last_name = DatabaseSecurity.decrypt(enc_last_name);

            UserInfo u_info = new UserInfo(id_num, username, first_name, last_name, authorization_level);
            return u_info;
        } catch (Exception SQLException) {
            System.out.println(SQLException);
            UserInfo e_info = new UserInfo(-1, "00", "00", "00", -1);
            return e_info;
        }
    }


    /* pull_doctor_schedule: Takes a database connection conn and a doctor_id. Returns data from the
            doctor_schedule table of the database as a DoctorSchedule object (which is defined in the file
            DoctorSchedule.java found within the database_access package).
     */
    static public DoctorSchedule pull_doctor_schedule(Connection conn, int doctor_id) {
        DoctorSchedule ds = new DoctorSchedule(doctor_id);
        int schedule_length;
        byte[] enc_day;
        byte[] enc_month;
        byte[] enc_year;
        byte[] enc_start_hour;
        byte[] enc_start_minute;
        byte[] enc_end_hour;
        byte[] enc_end_minute;
        int day;
        int month;
        int year;
        int start_hour;
        int start_minute;
        int end_hour;
        int end_minute;

        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM doctor_schedule WHERE doctor_id = " + doctor_id;
            ResultSet rs = stmt.executeQuery(query); //rs now contains all rows that correspond to the given doctor_id

            rs.last();
            schedule_length = rs.getRow(); //This determines how many rows are in rs
            rs.first();

            for (int i = 0; i < schedule_length; i++) {
                enc_day = rs.getBytes("s_day");
                enc_month = rs.getBytes("s_month");
                enc_year = rs.getBytes("s_year");
                enc_start_hour = rs.getBytes("start_hour");
                enc_start_minute = rs.getBytes("start_minute");
                enc_end_hour = rs.getBytes("end_hour");
                enc_end_minute = rs.getBytes("end_minute");

                day = Integer.parseInt(DatabaseSecurity.decrypt(enc_day));
                month = Integer.parseInt(DatabaseSecurity.decrypt(enc_month));
                year = Integer.parseInt(DatabaseSecurity.decrypt(enc_year));
                start_hour = Integer.parseInt(DatabaseSecurity.decrypt(enc_start_hour));
                start_minute = Integer.parseInt(DatabaseSecurity.decrypt(enc_start_minute));
                end_hour = Integer.parseInt(DatabaseSecurity.decrypt(enc_end_hour));
                end_minute = Integer.parseInt(DatabaseSecurity.decrypt(enc_end_minute));

                Calendar start_time = Calendar.getInstance();
                start_time.set(year, month, day, start_hour, start_minute);
                Calendar end_time = Calendar.getInstance();
                end_time.set(year, month, day, end_hour, end_minute);

                ds.add_time_slot(start_time, end_time);

                rs.next();
            }

            return ds;
        } catch (Exception SQLException) {
            System.out.println(SQLException);
            return ds;
        }
    }
}