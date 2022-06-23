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

            //stmt.executeQuery("DROP TABLE user_root CASCADE CONSTRAINTS");
            //stmt.executeQuery("DROP TABLE user_info CASCADE CONSTRAINTS");
            //stmt.executeQuery("DROP TABLE insurance_providers CASCADE CONSTRAINTS");
            //stmt.executeQuery("DROP TABLE patients CASCADE CONSTRAINTS");
            //stmt.executeQuery("DROP TABLE chart_records CASCADE CONSTRAINTS");
            //stmt.executeQuery("DROP TABLE appointments CASCADE CONSTRAINTS");
            //stmt.executeQuery("DROP TABLE doctor_schedule CASCADE CONSTRAINTS");
            //stmt.executeQuery("DROP TABLE payment_types CASCADE CONSTRAINTS");
            //stmt.executeQuery("DROP TABLE payments CASCADE CONSTRAINTS");
            //stmt.executeQuery("DROP TABLE drug_types CASCADE CONSTRAINTS");
            //stmt.executeQuery("DROP TABLE prescriptions CASCADE CONSTRAINTS");

            stmt.executeQuery("CREATE TABLE user_root(" +
                    "username VARCHAR2(100 CHAR), " +
                    "password_hash CHAR(64), " +
                    "CONSTRAINT users_pk PRIMARY KEY(username) ENABLE" +
                ")");
            stmt.executeQuery("CREATE TABLE user_info(" +
                    "id_num NUMBER, " +
                    "username VARCHAR2(100 CHAR), " +
                    "first_name CHAR(128), " +
                    "first_name_length NUMBER, " +
                    "last_name CHAR(128), " +
                    "last_name_length NUMBER, " +
                    "authorization_level NUMBER, " +
                        "CONSTRAINT user_info_pk PRIMARY KEY(id_num) ENABLE, " +
                        "CONSTRAINT user_info_fk FOREIGN KEY(username) REFERENCES user_root(username) ENABLE" +
                ")");
            stmt.executeQuery("CREATE TABLE insurance_providers(" +
                    "id_num NUMBER, " +
                    "name VARCHAR2(100 CHAR), " +
                        "CONSTRAINT insurance_providers_pk PRIMARY KEY(id_num) ENABLE" +
                ")");
            stmt.executeQuery("CREATE TABLE patients(" +
                    "id_num NUMBER, " +
                    "first_name CHAR(128), " +
                    "first_name_length NUMBER, " +
                    "last_name CHAR(128), " +
                    "last_name_length NUMBER, " +
                    "birthdate CHAR(32), " +
                    "street_address CHAR(256), " +
                    "street_address_length NUMBER, " +
                    "city CHAR(128), " +
                    "city_length NUMBER, " +
                    "us_state CHAR(64), " +
                    "us_state_length NUMBER, " +
                    "zip_code CHAR(32), " +
                    "phone_number CHAR(32), " +
                    "ssn CHAR(32), " +
                    "insurance_provider NUMBER, " +
                    "insurance_policy_num CHAR(128), " +
                    "insurance_policy_num_length NUMBER, " +
                        "CONSTRAINT patients_pk PRIMARY KEY(id_num) ENABLE, " +
                        "CONSTRAINT patients_fk FOREIGN KEY(insurance_provider) REFERENCES insurance_providers(id_num) ENABLE" +
                ")");
            stmt.executeQuery("CREATE TABLE chart_records(" +
                    "id_num NUMBER, " +
                    "patient_id NUMBER, " +
                    "record_day NUMBER, " +
                    "record_month NUMBER, " +
                    "record_year NUMBER, " +
                    "temperature CHAR(32), " + //record as 3-digit number
                    "pulse_rate CHAR(32), " + //record as 3-digit number
                    "breathing_rate CHAR(32), " + //record as 3-digit number
                    "blood_pressure_systolic CHAR(32), " + //record as 3-digit number
                    "blood_pressure_diastolic CHAR(32), " + //record as 3-digit number
                        "CONSTRAINT chart_records_pk PRIMARY KEY(id_num) ENABLE, " +
                        "CONSTRAINT chart_records_fk FOREIGN KEY(patient_id) REFERENCES patients(id_num) ENABLE" +
                ")");
            stmt.executeQuery("CREATE TABLE appointments(" +
                    "id_num NUMBER, " +
                    "patient_id NUMBER, " +
                    "doctor_id NUMBER, " +
                    "appt_day NUMBER, " +
                    "appt_month NUMBER, " +
                    "appt_year NUMBER, " +
                    "appt_hour NUMBER, " +
                    "appt_minute NUMBER, " +
                    "no_show NUMBER, " + //no_show contains 0 for no show or 1 for arrived
                        "CONSTRAINT appointments_pk PRIMARY KEY(id_num) ENABLE, " +
                        "CONSTRAINT appointments_fk1 FOREIGN KEY(patient_id) REFERENCES patients(id_num) ENABLE, " +
                        "CONSTRAINT appointments_fk2 FOREIGN KEY(doctor_id) REFERENCES user_info(id_num) ENABLE" +
                ")");
            stmt.executeQuery("CREATE TABLE doctor_schedule(" +
                    "doctor_id NUMBER, " +
                    "s_day NUMBER, " +
                    "s_month NUMBER, " +
                    "s_year NUMBER, " +
                    "start_hour NUMBER, " +
                    "start_minute NUMBER, " +
                    "end_hour NUMBER, " +
                    "end_minute NUMBER, " +
                        "CONSTRAINT doctor_schedule_fk FOREIGN KEY(doctor_id) REFERENCES user_info(id_num) ENABLE" +
                ")");
            stmt.executeQuery("CREATE TABLE payment_types(" +
                    "id_num NUMBER, " +
                    "payment_type VARCHAR2(40 CHAR), " +
                        "CONSTRAINT payment_types_pk PRIMARY KEY(id_num) ENABLE" +
                ")");
            stmt.executeQuery("CREATE TABLE payments(" +
                    "reference_num NUMBER, " +
                    "patient_id NUMBER, " +
                    "amount CHAR(32), " +
                    "generated_day NUMBER, " +
                    "generated_month NUMBER, " +
                    "generated_year NUMBER, " +
                    "paid_check NUMBER, " + //1 for paid, 0 for unpaid
                    "paid_day NUMBER, " +
                    "paid_month NUMBER, " +
                    "paid_year NUMBER, " +
                    "payment_type NUMBER, " +
                        "CONSTRAINT payments_pk PRIMARY KEY(reference_num) ENABLE, " +
                        "CONSTRAINT payments_fk1 FOREIGN KEY(patient_id) REFERENCES patients(id_num) ENABLE, " +
                        "CONSTRAINT payments_fk2 FOREIGN KEY(payment_type) REFERENCES payment_types(id_num) ENABLE" +
                ")");
            stmt.executeQuery("CREATE TABLE drug_types(" +
                    "id_num NUMBER, " +
                    "drug_name CHAR(128), " +
                    "drug_name_length NUMBER, " +
                        "CONSTRAINT drug_types_pk PRIMARY KEY(id_num) ENABLE" +
                ")");
            stmt.executeQuery("CREATE TABLE prescriptions(" +
                    "id_num NUMBER, " +
                    "patient_id NUMBER, " +
                    "drug_id NUMBER, " +
                    "prescribe_day NUMBER, " +
                    "prescribe_month NUMBER, " +
                    "prescribe_year NUMBER, " +
                    "dose CHAR(128), " +
                    "dose_length NUMBER, " +
                    "amount NUMBER, " +
                    "instructions CHAR(512), " +
                    "instructions_length NUMBER, " +
                        "CONSTRAINT prescriptions_pk PRIMARY KEY(id_num) ENABLE, " +
                        "CONSTRAINT prescriptions_fk1 FOREIGN KEY(patient_id) REFERENCES patients(id_num) ENABLE, " +
                        "CONSTRAINT prescriptions_fk2 FOREIGN KEY(drug_id) REFERENCES drug_types(id_num) ENABLE" +
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
            String efn;
            String eln;

            enc_f_name = DatabaseSecurity.encrypt("Amy", 64);
            enc_l_name = DatabaseSecurity.encrypt("Stephens", 64);
            efn = DatabaseSecurity.byte_array_to_hex_string(enc_f_name);
            eln = DatabaseSecurity.byte_array_to_hex_string(enc_l_name);
            stmt.executeQuery("INSERT INTO user_info VALUES (9427, 'doc_stephens', '" + efn + "', 3, '" + eln + "', 8, 2)");

            enc_f_name = DatabaseSecurity.encrypt("Jose", 64);
            enc_l_name = DatabaseSecurity.encrypt("Gonzalez", 64);
            efn = DatabaseSecurity.byte_array_to_hex_string(enc_f_name);
            eln = DatabaseSecurity.byte_array_to_hex_string(enc_l_name);
            stmt.executeQuery("INSERT INTO user_info VALUES (3852, 'doc_gonzalez', '" + efn + "', 4, '" + eln + "', 8, 2)");

            enc_f_name = DatabaseSecurity.encrypt("Himari", 64);
            enc_l_name = DatabaseSecurity.encrypt("Yamamoto", 64);
            efn = DatabaseSecurity.byte_array_to_hex_string(enc_f_name);
            eln = DatabaseSecurity.byte_array_to_hex_string(enc_l_name);
            stmt.executeQuery("INSERT INTO user_info VALUES (1749, 'nurse_yamamoto', '" + efn + "', 6, '" + eln + "', 8, 1)");

            enc_f_name = DatabaseSecurity.encrypt("Sara", 64);
            enc_l_name = DatabaseSecurity.encrypt("Martinez", 64);
            efn = DatabaseSecurity.byte_array_to_hex_string(enc_f_name);
            eln = DatabaseSecurity.byte_array_to_hex_string(enc_l_name);
            stmt.executeQuery("INSERT INTO user_info VALUES (2870, 'nurse_martinez', '" + efn + "', 4, '" + eln + "', 8, 1)");

            enc_f_name = DatabaseSecurity.encrypt("James", 64);
            enc_l_name = DatabaseSecurity.encrypt("Carlson", 64);
            efn = DatabaseSecurity.byte_array_to_hex_string(enc_f_name);
            eln = DatabaseSecurity.byte_array_to_hex_string(enc_l_name);
            stmt.executeQuery("INSERT INTO user_info VALUES (6478, 'j_carlson', '" + efn + "', 5, '" + eln + "', 7, 0)");

            enc_f_name = DatabaseSecurity.encrypt("Beatrice", 64);
            enc_l_name = DatabaseSecurity.encrypt("Smith", 64);
            efn = DatabaseSecurity.byte_array_to_hex_string(enc_f_name);
            eln = DatabaseSecurity.byte_array_to_hex_string(enc_l_name);
            stmt.executeQuery("INSERT INTO user_info VALUES (4782, 'b_smith', '" + efn + "', 8, '" + eln + "', 5, 0)");

            //This code is for populating the table insurance_providers.
            stmt.executeQuery("INSERT INTO insurance_providers VALUES (0, 'Medicare')");
            stmt.executeQuery("INSERT INTO insurance_providers VALUES (1, 'Aetna')");
            stmt.executeQuery("INSERT INTO insurance_providers VALUES (2, 'UnitedHealth')");
            stmt.executeQuery("INSERT INTO insurance_providers VALUES (3, 'Humana')");

            //This code is for populating the table patients.
            byte[] enc_birth = new byte[16];
            byte[] enc_st_address = new byte[128];
            byte[] enc_city = new byte[64];
            byte[] enc_state = new byte[32];
            byte[] enc_zip = new byte[16];
            byte[] enc_phone = new byte[16];
            byte[] enc_ssn = new byte[16];
            byte[] enc_policy = new byte[64];
            String eb;
            String esa;
            String ec;
            String es;
            String ez;
            String ep;
            String essn;
            String epol;

            enc_f_name = DatabaseSecurity.encrypt("Jia", 64);
            efn = DatabaseSecurity.byte_array_to_hex_string(enc_f_name);
            enc_l_name = DatabaseSecurity.encrypt("Chen", 64);
            eln = DatabaseSecurity.byte_array_to_hex_string(enc_l_name);
            enc_birth = DatabaseSecurity.encrypt("16071982", 16);
            eb = DatabaseSecurity.byte_array_to_hex_string(enc_birth);
            enc_st_address = DatabaseSecurity.encrypt("123 Main St, Apt B", 128);
            esa = DatabaseSecurity.byte_array_to_hex_string(enc_st_address);
            enc_city = DatabaseSecurity.encrypt("Lubbock", 64);
            ec = DatabaseSecurity.byte_array_to_hex_string(enc_city);
            enc_state = DatabaseSecurity.encrypt("Texas", 32);
            es = DatabaseSecurity.byte_array_to_hex_string(enc_state);
            enc_zip = DatabaseSecurity.encrypt("12345", 16);
            ez = DatabaseSecurity.byte_array_to_hex_string(enc_zip);
            enc_phone = DatabaseSecurity.encrypt("5555555555", 16);
            ep = DatabaseSecurity.byte_array_to_hex_string(enc_phone);
            enc_ssn = DatabaseSecurity.encrypt("123456789", 16);
            essn = DatabaseSecurity.byte_array_to_hex_string(enc_ssn);
            enc_policy = DatabaseSecurity.encrypt("A1B2C3D4E5F6", 64);
            epol = DatabaseSecurity.byte_array_to_hex_string(enc_policy);
            stmt.executeQuery("INSERT INTO patients VALUES (1094273, '" + efn + "', 3, '" + eln + "', 4, '" + eb + "', '" + esa + "', 18, '" + ec + "', 7, '" + es + "', 5, '" + ez + "', '" + ep + "', '" + essn + "', 2, '" + epol + "', 12)");

            //This code is for populating the table doctor_schedule.
            stmt.executeQuery("INSERT INTO doctor_schedule VALUES (9427, 5, 7, 2022, 8, 0, 17, 0)");
            stmt.executeQuery("INSERT INTO doctor_schedule VALUES (9427, 6, 7, 2022, 8, 0, 17, 0)");
            stmt.executeQuery("INSERT INTO doctor_schedule VALUES (9427, 7, 7, 2022, 8, 0, 17, 0)");
            stmt.executeQuery("INSERT INTO doctor_schedule VALUES (9427, 8, 7, 2022, 8, 0, 12, 0)");
            stmt.executeQuery("INSERT INTO doctor_schedule VALUES (3852, 6, 7, 2022, 8, 0, 17, 0)");
            stmt.executeQuery("INSERT INTO doctor_schedule VALUES (3852, 8, 7, 2022, 8, 0, 17, 0)");

            //This code is for populating the table payment_types.
            stmt.executeQuery("INSERT INTO payment_types VALUES (0, 'cash')");
            stmt.executeQuery("INSERT INTO payment_types VALUES (1, 'credit card')");
            stmt.executeQuery("INSERT INTO payment_types VALUES (2, 'debit card')");

            //This code is for populating the table drug_types.
            byte[] enc_d_name = new byte[64];
            String edn;

            enc_d_name = DatabaseSecurity.encrypt("Amoxicillin", 64);
            edn = DatabaseSecurity.byte_array_to_hex_string(enc_d_name);
            stmt.executeQuery("INSERT INTO drug_types VALUES (0, '" + edn + "', 11)");

            enc_d_name = DatabaseSecurity.encrypt("Levothyroxin", 64);
            edn = DatabaseSecurity.byte_array_to_hex_string(enc_d_name);
            stmt.executeQuery("INSERT INTO drug_types VALUES (1, '" + edn + "', 12)");

            enc_d_name = DatabaseSecurity.encrypt("Lisinopril", 64);
            edn = DatabaseSecurity.byte_array_to_hex_string(enc_d_name);
            stmt.executeQuery("INSERT INTO drug_types VALUES (2, '" + edn + "', 10)");

            enc_d_name = DatabaseSecurity.encrypt("Amlodipine", 64);
            edn = DatabaseSecurity.byte_array_to_hex_string(enc_d_name);
            stmt.executeQuery("INSERT INTO drug_types VALUES (3, '" + edn + "', 10)");

            enc_d_name = DatabaseSecurity.encrypt("Prednisone", 64);
            edn = DatabaseSecurity.byte_array_to_hex_string(enc_d_name);
            stmt.executeQuery("INSERT INTO drug_types VALUES (4, '" + edn + "', 10)");

            conn.close();
        } catch (Exception SQLException) {
            System.out.println(SQLException);
        }
    }
}