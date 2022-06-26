/* DatabaseAccess.java
   Created by Christopher Walker.
   Created 14 June 2022.
   Last modified 25 June 2022.
   This package provides access functions to a specified SQL database connection. The methods provided here
   are designed for ease of database access and according to the design specifications given in the project
   description.
   Development Update: At this time, the only methods completed are the verify_user_pass() function, the
   pull_user_info function(), the pull_doctor_schedule() function, the pull_patient_info() function, the
   pull_patient_ssn() function, the pull_appt_schedule() function, the add_appt_to_schedule() function, and
   the pull_chart_records() function. Verify_user_pass() should be used in the "Login" use case found in the
   project description. Pull_user_info() should be used to find the user's ID number and authorization level
   (to determine access permissions). Pull_doctor_schedule(), pull_patient_info(), pull_appt_schedule(), and
   add_appt_to_schedule() should be used in the "Make Appointment" use case found in the project description.
   Pull_patient_info(), pull_appt_schedule(), pull_patient_ssn(), and pull_chart_records() should be used in
   the "Check-in Patient" use case.
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
            found within the database_access package). If an SQL exception occurs, returns a UserInfo object
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
            int first_name_length = rs.getInt("first_name_length");
            String eln = rs.getString("last_name");
            int last_name_length = rs.getInt("last_name_length");
            int authorization_level = rs.getInt("authorization_level");

            byte[] enc_first_name = DatabaseSecurity.hex_string_to_byte_array(efn);
            byte[] enc_last_name = DatabaseSecurity.hex_string_to_byte_array(eln);

            String first_name = DatabaseSecurity.decrypt(enc_first_name);
            String last_name = DatabaseSecurity.decrypt(enc_last_name);

            first_name = first_name.substring(0, first_name_length);
            last_name = last_name.substring(0, last_name_length);

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
        int day;
        int month;
        int year;
        int start_hour;
        int start_minute;
        int end_hour;
        int end_minute;

        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = "SELECT * FROM doctor_schedule WHERE doctor_id = " + doctor_id;
            ResultSet rs = stmt.executeQuery(query); //rs now contains all rows that correspond to the given doctor_id

            rs.last();
            schedule_length = rs.getRow(); //This determines how many rows are in rs
            rs.first();

            for (int i = 0; i < schedule_length; i++) {
                day = rs.getInt("s_day");
                month = rs.getInt("s_month");
                year = rs.getInt("s_year");
                start_hour = rs.getInt("start_hour");
                start_minute = rs.getInt("start_minute");
                end_hour = rs.getInt("end_hour");
                end_minute = rs.getInt("end_minute");

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


    /* pull_patient_info: Takes a database connection conn, a first_name as a string, a last_name as a string,
            and a birth_date as a Calendar object. Returns data from the patients table of the database as a
            PatientInfo object (which is defined in the file PatientInfo.java found within the database_access
            package). If no patient is found with matching name and birth_date, returns a PatientInfo object
            with id_num -3. If more than one patient is found with the given name and birth_date, returns a
            PatientInfo object with id_num -2 (at this time, this implementation does not have a way to deal
            with more than one patient having the same name and birthdate). If an SQL exception occurs,
            returns a PatientInfo object with id_num -1.
     */
    static public PatientInfo pull_patient_info(Connection conn, String first_name, String last_name, Calendar birth_date) {
        try {
            //This block of code converts the given birth_date from a Calendar object into integer values and a string b_date_str in form DDMMYYYY.
            int b_day = birth_date.get(Calendar.DATE);
            int b_month = birth_date.get(Calendar.MONTH);
            int b_year = birth_date.get(Calendar.YEAR);
            Integer b_day_ob = b_day;
            Integer b_month_ob = b_month;
            Integer b_year_ob = b_year;
            String b_date_str;
            String b_day_str = b_day_ob.toString();
            String b_month_str = b_month_ob.toString();
            String b_year_str = b_year_ob.toString();
            if (b_day_str.length() == 1)
                b_day_str = "0" + b_day_str;
            if (b_month_str.length() == 1)
                b_month_str = "0" + b_month_str;
            b_date_str = b_day_str + b_month_str + b_year_str;

            byte[] enc_f_name = new byte[64];
            byte[] enc_l_name = new byte[64];
            byte[] enc_b_date = new byte[16];
            enc_f_name = DatabaseSecurity.encrypt(first_name, 64);
            String efn = DatabaseSecurity.byte_array_to_hex_string(enc_f_name);
            enc_l_name = DatabaseSecurity.encrypt(last_name, 64);
            String eln = DatabaseSecurity.byte_array_to_hex_string(enc_l_name);
            enc_b_date = DatabaseSecurity.encrypt(b_date_str, 16);
            String ebd = DatabaseSecurity.byte_array_to_hex_string(enc_b_date);

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = "SELECT * FROM patients WHERE first_name = '" + efn + "' AND last_name = '" + eln + "' AND birthdate = '" + ebd + "'";
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() != true) {
                Calendar e3 = Calendar.getInstance();
                PatientInfo e3_info = new PatientInfo(-3, "00", "00", e3, "00", "00", "00", "00", "00", -3, "00");
                return e3_info;  //returns the object e3_info if no patient info found matching the given name and birthdate
            }

            rs.last();
            int num_results = rs.getRow(); //This determines how many rows are in rs
            rs.first();

            if (num_results > 1) {
                Calendar e2 = Calendar.getInstance();
                PatientInfo e2_info = new PatientInfo(-2, "00", "00", e2, "00", "00", "00", "00", "00", -2, "00");
                return e2_info;  //returns the object e2_info if more than one patient is found matching the given name and birthdate
            }

            int id_num = rs.getInt("id_num");
            String esa = rs.getString("street_address");
            int street_address_length = rs.getInt("street_address_length");
            String ecity = rs.getString("city");
            int city_length = rs.getInt("city_length");
            String estate = rs.getString("us_state");
            int us_state_length = rs.getInt("us_state_length");
            String ezip = rs.getString("zip_code");
            String ephone = rs.getString("phone_number");
            int insurance_provider = rs.getInt("insurance_provider");
            String epol = rs.getString("insurance_policy_num");
            int insurance_policy_num_length = rs.getInt("insurance_policy_num_length");

            byte[] enc_street_address = DatabaseSecurity.hex_string_to_byte_array(esa);
            byte[] enc_city = DatabaseSecurity.hex_string_to_byte_array(ecity);
            byte[] enc_us_state = DatabaseSecurity.hex_string_to_byte_array(estate);
            byte[] enc_zip_code = DatabaseSecurity.hex_string_to_byte_array(ezip);
            byte[] enc_phone_num = DatabaseSecurity.hex_string_to_byte_array(ephone);
            byte[] enc_insurance_policy_num = DatabaseSecurity.hex_string_to_byte_array(epol);

            String street_address = DatabaseSecurity.decrypt(enc_street_address);
            String city = DatabaseSecurity.decrypt(enc_city);
            String us_state = DatabaseSecurity.decrypt(enc_us_state);
            String zip_code = DatabaseSecurity.decrypt(enc_zip_code);
            String phone_num = DatabaseSecurity.decrypt(enc_phone_num);
            String insurance_policy_num = DatabaseSecurity.decrypt(enc_insurance_policy_num);

            street_address = street_address.substring(0, street_address_length);
            city = city.substring(0, city_length);
            us_state = us_state.substring(0, us_state_length);
            zip_code = zip_code.substring(0, 6);
            phone_num = phone_num.substring(0, 11);
            insurance_policy_num = insurance_policy_num.substring(0, insurance_policy_num_length);

            PatientInfo p_info = new PatientInfo(id_num, first_name, last_name, birth_date, street_address, city, us_state, zip_code, phone_num, insurance_provider, insurance_policy_num);
            return p_info;
        } catch (Exception SQLException) {
            System.out.println(SQLException);
            Calendar e1 = Calendar.getInstance();
            PatientInfo e1_info = new PatientInfo(-1, "00", "00", e1, "00", "00", "00", "00", "00", -1, "00");
            return e1_info;
        }
    }


    public static int update_patient_info(Connection conn, PatientInfo p_info) {
        //Changes the info for the given patient in the database. Returns an int if successful or error.
        return 0;
    }


    /* pull_patient_ssn: Takes a database connection conn and a PatientInfo object p_info already created from
            the method pull_patient_info() defined above. Returns the social security number for the patient
            described in p_info as a string. Returns the string "AAAAAAAAA" if the patient was not found in
            the database. Returns the string "SSSSSSSSS" if an SQL Exception occurs.
     */
    public static String pull_patient_ssn(Connection conn, PatientInfo p_info) {
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT ssn FROM patients WHERE id_num = " + p_info.id_num;
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() != true)
                return "AAAAAAAAA";

            String e_ssn = rs.getString("ssn");
            byte[] enc_ssn = DatabaseSecurity.hex_string_to_byte_array(e_ssn);
            String ssn = DatabaseSecurity.decrypt(enc_ssn);
            ssn = ssn.substring(0, 10);

            return ssn;
        } catch (Exception SQLException) {
            return "SSSSSSSSS";
        }
    }


    public static int update_patient_ssn(Connection conn, PatientInfo p_info, String new_ssn) {
        //Changes the ssn in the database for the given patient_id to the new ssn. Returns an into for success or failure.
        return 0;
    }


    /* pull_appt_schedule: Takes a database connection conn. Returns data from the appointments table of the
            database as an ApptSchedule object (which is defined in the file ApptSchedule.java found within
            the database_access package). If no appointments are found, returns an ApptSchedule object with
            no records in appt_list. If an SQL exception occurs, returns an ApptSchedule object with an
            Appointment in appt_list with appt_id -1.
     */
    static public ApptSchedule pull_appt_schedule(Connection conn) {
        ApptSchedule appt_s = new ApptSchedule();
        int appt_id;
        int patient_id;
        int doctor_id;
        int appt_day;
        int appt_month;
        int appt_year;
        int appt_hour;
        int appt_minute;
        Calendar date_time;
        int no_show;

        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = "SELECT * FROM appointments";
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() != true) {
                return appt_s;
            }

            rs.last();
            int num_results = rs.getRow(); //This determines how many rows are in rs
            rs.first();

            for (int i = 0; i < num_results; i++) {
                appt_id = rs.getInt("id_num");
                patient_id = rs.getInt("patient_id");
                doctor_id = rs.getInt("doctor_id");
                appt_day = rs.getInt("appt_day");
                appt_month = rs.getInt("appt_month");
                appt_year = rs.getInt("appt_year");
                appt_hour = rs.getInt("appt_hour");
                appt_minute = rs.getInt("appt_hour");
                no_show = rs.getInt("no_show");

                date_time = Calendar.getInstance();
                date_time.set(appt_year, appt_month, appt_day, appt_hour, appt_minute);

                appt_s.add_appointment(appt_id, patient_id, doctor_id, date_time, no_show);
            }

            return appt_s;
        } catch (Exception SQLException) {
            date_time = Calendar.getInstance();
            appt_s.add_appointment(-1, -1, -1, date_time, -1);
            return appt_s;
        }
    }


    /* add_appt_to_schedule: Takes a database connection conn, an ApptSchedule object appt_s, a patient_id,
            doctor_id, and Calendar object date_time. Inserts this appointment information into the database
            and adds a new appointment object to the appt_list of appt_s. Returns 0 for successful execution
            or 2 for an SQL exception.
     */
    static public int add_appt_to_schedule(Connection conn, ApptSchedule appt_s, int patient_id, int doctor_id, Calendar date_time) {
        int new_appt_id = appt_s.appt_list_length + 1;

        try {
            Statement stmt = conn.createStatement();
            String query = "INSERT INTO appointments VALUES (" + new_appt_id + ", " + patient_id + ", " + doctor_id + ", " + date_time.get(Calendar.DATE) + ", " + date_time.get(Calendar.MONTH) + ", " + date_time.get(Calendar.YEAR) + ", " + date_time.get(Calendar.HOUR) + ", " + date_time.get(Calendar.MINUTE) + ", " + "0)";
            stmt.executeQuery(query);

            appt_s.add_appointment(new_appt_id, patient_id, doctor_id, date_time, 0);

            return 0;
        } catch (Exception SQLException) {
            return 2;
        }
    }


    /* pull_chart_records: Takes a database Connection conn and a patient_id. Returns a PatientChart object,
            defined in PatientChart.java, containing a list of ChartRecord objects that correspond to
            patient_id. If no records are found for the patient, returns a PatientChart object with an
            empty list. If an SQLException occurs, returns a PatientChart object with a ChartRecord with
            record_num -1.
     */
    static public PatientChart pull_chart_records(Connection conn, int patient_id) {
        PatientChart p_chart = new PatientChart();
        int record_num;
        int record_day;
        int record_month;
        int record_year;
        Calendar record_date;
        String e_temperature;
        byte[] enc_temperature;
        int temperature;
        String e_p_rate;
        byte[] enc_p_rate;
        int pulse_rate;
        String e_b_rate;
        byte[] enc_b_rate;
        int breathing_rate;
        String e_bps;
        byte[] enc_bps;
        int blood_pressure_systolic;
        String e_bpd;
        byte[] enc_bpd;
        int blood_pressure_diastolic;

        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = "SELECT * FROM chart_records WHERE patient_id = " + patient_id;
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() != true) {
                return p_chart;
            }

            rs.last();
            int num_results = rs.getRow(); //This determines how many rows are in rs
            rs.first();

            for (int i = 0; i < num_results; i++) {
                record_num = rs.getInt("id_num");
                record_day = rs.getInt("record_day");
                record_month = rs.getInt("record_month");
                record_year = rs.getInt("record_year");
                e_temperature = rs.getString("temperature");
                e_p_rate = rs.getString("pulse_rate");
                e_b_rate = rs.getString("breathing_rate");
                e_bps = rs.getString("blood_pressure_systolic");
                e_bpd = rs.getString("blood_pressure_diastolic");

                enc_temperature = DatabaseSecurity.hex_string_to_byte_array(e_temperature);
                enc_p_rate = DatabaseSecurity.hex_string_to_byte_array(e_p_rate);
                enc_b_rate = DatabaseSecurity.hex_string_to_byte_array(e_b_rate);
                enc_bps = DatabaseSecurity.hex_string_to_byte_array(e_bps);
                enc_bpd = DatabaseSecurity.hex_string_to_byte_array(e_bpd);

                temperature = Integer.parseInt((DatabaseSecurity.decrypt(enc_temperature)).substring(0, 4));
                pulse_rate = Integer.parseInt((DatabaseSecurity.decrypt(enc_p_rate)).substring(0, 4));
                breathing_rate = Integer.parseInt((DatabaseSecurity.decrypt(enc_b_rate)).substring(0,4));
                blood_pressure_systolic = Integer.parseInt((DatabaseSecurity.decrypt(enc_bps)).substring(0, 5));
                blood_pressure_diastolic = Integer.parseInt((DatabaseSecurity.decrypt(enc_bpd)).substring(0, 5));

                record_date = Calendar.getInstance();
                record_date.set(record_year, record_month, record_day);

                p_chart.add_record(record_num, patient_id, record_date, temperature, pulse_rate, breathing_rate, blood_pressure_systolic, blood_pressure_diastolic);
            }

            return p_chart;
        } catch (Exception SQLException) {
            record_date = Calendar.getInstance();
            p_chart.add_record(-1, -1, record_date, -1, -1, -1, -1, -1);
            return p_chart;
        }
    }


    static public void add_new_chart_record() {
        //Takes a chart record and saves it to the database
    }


    static public void update_chart_record() {
        //Takes a chart record object and replaces its old info in the database
    }


    static public void pull_payment_records(int patient_id) {
        //returns a list of payment record objects corresponding to the patient_id
    }


    static public void add_new_payment_record() {
        //Takes a payment record object and stores its data in the database
    }


    static public void update_payment_record() {
        //Takes a payment record object and replaces its old data in the database
    }


    static public int pull_drug_id(String drug_name) {
        //Returns the id_num of the given drug in the database or an error flag (-1?) if not found
        return -1;
    }


    static public void add_new_prescription(int patient_id, int drug_id, String dose, int amount, String instructions) {
        //Saves the given information to the database.
    }
}