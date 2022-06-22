/* PopulateDatabase.java
   Created by Christopher Walker.
   Created 15 June 2022.
   Last modified 22 June 2022.
   This file should only be run once. This program creates a table in the SQL database for usernames and
   passwords. It then puts initial hash values of passwords, along with their corresponding usernames, into
   that table. The code has been commented out in order to prevent runtime errors. To run this program,
   remove the comment markers surrounding main().
 */


import database_access.DatabaseSecurity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


public class PopulateDatabase {
    public static void main(String[] args) {
        try {
            String database_address = "jdbc:oracle:thin:@localhost:1521:xe";
            String database_username = "system";
            String database_password = "YellowGreen27";
            Connection conn = DriverManager.getConnection(database_address, database_username, database_password);
            Statement stmt = conn.createStatement();

            stmt.executeQuery("DROP TABLE user_root CASCADE CONSTRAINTS");
            stmt.executeQuery("DROP TABLE user_info CASCADE CONSTRAINTS");
            stmt.executeQuery("DROP TABLE doctor_schedule CASCADE CONSTRAINTS");

            stmt.executeQuery("CREATE TABLE user_root(" +
                        "username VARCHAR2(100 CHAR), " +
                        "password_hash CHAR(64), " +
                            "CONSTRAINT users_pk PRIMARY KEY(username) ENABLE" +
                    ")");
            stmt.executeQuery("CREATE TABLE user_info(" +
                        "id_num NUMBER, " +
                        "username VARCHAR2(100 CHAR), " +
                        "first_name CHAR(64), " +
                        "first_name_illegal_index NUMBER, " +
                        "last_name CHAR(64), " +
                        "last_name_illegal_index NUMBER, " +
                        "authorization_level NUMBER, " +
                            "CONSTRAINT user_info_pk PRIMARY KEY(id_num) ENABLE, " +
                            "CONSTRAINT user_info_fk FOREIGN KEY(username) REFERENCES user_root(username) ENABLE" +
                    ")");
            stmt.executeQuery("CREATE TABLE doctor_schedule(" +
                        "doctor_id NUMBER, " +
                        "s_day CHAR(16), " +
                        "s_month CHAR(16), " +
                        "s_year CHAR(16), " +
                        "start_hour CHAR(16), " +
                        "start_minute CHAR(16), " +
                        "end_hour CHAR(16), " +
                        "end_minute CHAR(16), " +
                            "CONSTRAINT doctor_schedule_fk FOREIGN KEY(doctor_id) REFERENCES user_info(id_num) ENABLE\n" +
                    ")");

            //This code is for populating the table user_root.
            String temp_pass0 = new String(DatabaseSecurity.hashing("password0"));
            temp_pass0 = DatabaseSecurity.replace_illegal_SQL(temp_pass0);
            String temp_pass1 = new String(DatabaseSecurity.hashing("password1"));
            temp_pass1 = DatabaseSecurity.replace_illegal_SQL(temp_pass1);
            String temp_pass2 = new String(DatabaseSecurity.hashing("password2"));
            temp_pass2 = DatabaseSecurity.replace_illegal_SQL(temp_pass2);
            String temp_pass3 = new String(DatabaseSecurity.hashing("password3"));
            temp_pass3 = DatabaseSecurity.replace_illegal_SQL(temp_pass3);
            String temp_pass4 = new String(DatabaseSecurity.hashing("password4"));
            temp_pass4 = DatabaseSecurity.replace_illegal_SQL(temp_pass4);
            String temp_pass5 = new String(DatabaseSecurity.hashing("password5"));
            temp_pass5 = DatabaseSecurity.replace_illegal_SQL(temp_pass5);

            stmt.executeQuery("INSERT INTO user_root VALUES ('doc_stephens', '" + temp_pass0 + "')");
            stmt.executeQuery("INSERT INTO user_root VALUES ('doc_gonzalez', '" + temp_pass1 + "')");
            stmt.executeQuery("INSERT INTO user_root VALUES ('nurse_yamamoto', '" + temp_pass2 + "')");
            stmt.executeQuery("INSERT INTO user_root VALUES ('nurse_martinez', '" + temp_pass3 + "')");
            stmt.executeQuery("INSERT INTO user_root VALUES ('j_carlson', '" + temp_pass4 + "')");
            stmt.executeQuery("INSERT INTO user_root VALUES ('b_smith', '" + temp_pass5 + "')");

            //This code is for populating the table user_info.
            byte[] enc_f_name = new byte[64];
            byte[] enc_l_name = new byte[64];
            long illegal_f_name = 0;
            long illegal_l_name = 0;
            int illegal_flag = 0;

            enc_f_name = DatabaseSecurity.encrypt("Amy", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_f_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index0 = DatabaseSecurity.indicate_illegal_SQL(enc_f_name);
                illegal_f_name = DatabaseSecurity.SQLarray_to_int(illegal_index0);
                enc_f_name = DatabaseSecurity.replace_illegal_SQL2(enc_f_name);
            }
            enc_l_name = DatabaseSecurity.encrypt("Stephens", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_l_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index1 = DatabaseSecurity.indicate_illegal_SQL(enc_l_name);
                illegal_l_name = DatabaseSecurity.SQLarray_to_int(illegal_index1);
                enc_l_name = DatabaseSecurity.replace_illegal_SQL2(enc_l_name);
            }
            if (illegal_flag == 0) {
                stmt.executeQuery("INSERT INTO user_info VALUES (9427, 'doc_stephens', '" + enc_f_name + "', 0, '" + enc_l_name + "', 0, 2)");
            } else {
                stmt.executeQuery("INSERT INTO user_info VALUES (9427, 'doc_stephens', '" + enc_f_name + "', " + illegal_f_name + ", '" + enc_l_name + "', " + illegal_l_name + ", 2)");
            }

            illegal_flag = 0;
            enc_f_name = DatabaseSecurity.encrypt("Jose", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_f_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index2 = DatabaseSecurity.indicate_illegal_SQL(enc_f_name);
                illegal_f_name = DatabaseSecurity.SQLarray_to_int(illegal_index2);
                enc_f_name = DatabaseSecurity.replace_illegal_SQL2(enc_f_name);
            }
            enc_l_name = DatabaseSecurity.encrypt("Gonzalez", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_l_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index3 = DatabaseSecurity.indicate_illegal_SQL(enc_l_name);
                illegal_l_name = DatabaseSecurity.SQLarray_to_int(illegal_index3);
                enc_l_name = DatabaseSecurity.replace_illegal_SQL2(enc_l_name);
            }
            if (illegal_flag == 0) {
                stmt.executeQuery("INSERT INTO user_info VALUES (3852, 'doc_gonzalez', '" + enc_f_name + "', 0, '" + enc_l_name + "', 0, 2)");
            } else {
                stmt.executeQuery("INSERT INTO user_info VALUES (3852, 'doc_gonzalez', '" + enc_f_name + "', " + illegal_f_name + ", '" + enc_l_name + "', " + illegal_l_name + ", 2)");
            }

            illegal_flag = 0;
            enc_f_name = DatabaseSecurity.encrypt("Himari", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_f_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index4 = DatabaseSecurity.indicate_illegal_SQL(enc_f_name);
                illegal_f_name = DatabaseSecurity.SQLarray_to_int(illegal_index4);
                enc_f_name = DatabaseSecurity.replace_illegal_SQL2(enc_f_name);
            }
            enc_l_name = DatabaseSecurity.encrypt("Yamamoto", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_l_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index5 = DatabaseSecurity.indicate_illegal_SQL(enc_l_name);
                illegal_l_name = DatabaseSecurity.SQLarray_to_int(illegal_index5);
                enc_l_name = DatabaseSecurity.replace_illegal_SQL2(enc_l_name);
            }
            if (illegal_flag == 0) {
                stmt.executeQuery("INSERT INTO user_info VALUES (1749, 'nurse_yamamoto', '" + enc_f_name + "', 0, '" + enc_l_name + "', 0, 1)");
            } else {
                stmt.executeQuery("INSERT INTO user_info VALUES (1749, 'nurse_yamamoto', '" + enc_f_name + "', " + illegal_f_name + ", '" + enc_l_name + "', " + illegal_l_name + ", 1)");
            }

            illegal_flag = 0;
            enc_f_name = DatabaseSecurity.encrypt("Sara", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_f_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index6 = DatabaseSecurity.indicate_illegal_SQL(enc_f_name);
                illegal_f_name = DatabaseSecurity.SQLarray_to_int(illegal_index6);
                enc_f_name = DatabaseSecurity.replace_illegal_SQL2(enc_f_name);
            }
            enc_l_name = DatabaseSecurity.encrypt("Martinez", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_l_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index7 = DatabaseSecurity.indicate_illegal_SQL(enc_l_name);
                illegal_l_name = DatabaseSecurity.SQLarray_to_int(illegal_index7);
                enc_l_name = DatabaseSecurity.replace_illegal_SQL2(enc_l_name);
            }
            if (illegal_flag == 0) {
                stmt.executeQuery("INSERT INTO user_info VALUES (2870, 'nurse_martinez', '" + enc_f_name + "', 0, '" + enc_l_name + "', 0, 1)");
            } else {
                stmt.executeQuery("INSERT INTO user_info VALUES (2870, 'nurse_martinez', '" + enc_f_name + "', " + illegal_f_name + ", '" + enc_l_name + "', " + illegal_l_name + ", 1)");
            }

            illegal_flag = 0;
            enc_f_name = DatabaseSecurity.encrypt("James", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_f_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index8 = DatabaseSecurity.indicate_illegal_SQL(enc_f_name);
                illegal_f_name = DatabaseSecurity.SQLarray_to_int(illegal_index8);
                enc_f_name = DatabaseSecurity.replace_illegal_SQL2(enc_f_name);
            }
            enc_l_name = DatabaseSecurity.encrypt("Carlson", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_l_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index9 = DatabaseSecurity.indicate_illegal_SQL(enc_l_name);
                illegal_l_name = DatabaseSecurity.SQLarray_to_int(illegal_index9);
                enc_l_name = DatabaseSecurity.replace_illegal_SQL2(enc_l_name);
            }
            if (illegal_flag == 0) {
                stmt.executeQuery("INSERT INTO user_info VALUES (6478, 'j_carlson', '" + enc_f_name + "', 0, '" + enc_l_name + "', 0, 0)");
            } else {
                stmt.executeQuery("INSERT INTO user_info VALUES (6478, 'j_carlson', '" + enc_f_name + "', " + illegal_f_name + ", '" + enc_l_name + "', " + illegal_l_name + ", 0)");
            }

            illegal_flag = 0;
            enc_f_name = DatabaseSecurity.encrypt("Beatrice", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_f_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index10 = DatabaseSecurity.indicate_illegal_SQL(enc_f_name);
                illegal_f_name = DatabaseSecurity.SQLarray_to_int(illegal_index10);
                enc_f_name = DatabaseSecurity.replace_illegal_SQL2(enc_f_name);
            }
            enc_l_name = DatabaseSecurity.encrypt("Smith", 64);
            if (DatabaseSecurity.check_for_illegal_SQL(enc_l_name) == 1) {
                illegal_flag = 1;
                int[] illegal_index11 = DatabaseSecurity.indicate_illegal_SQL(enc_l_name);
                illegal_l_name = DatabaseSecurity.SQLarray_to_int(illegal_index11);
                enc_l_name = DatabaseSecurity.replace_illegal_SQL2(enc_l_name);
            }
            if (illegal_flag == 0) {
                stmt.executeQuery("INSERT INTO user_info VALUES (4782, 'b_smith', '" + enc_f_name + "', 0, '" + enc_l_name + "', 0, 0)");
            } else {
                stmt.executeQuery("INSERT INTO user_info VALUES (4782, 'b_smith', '" + enc_f_name + "', " + illegal_f_name + ", '" + enc_l_name + "', " + illegal_l_name + ", 0)");
            }

            conn.close();
        } catch (Exception SQLException) {
            System.out.println(SQLException);
        }
    }
}