/* DatabaseAccess.java
   Created by Christopher Walker.
   Created 14 June 2022.
   Last modified 28 June 2022.
   This package provides access functions to a specified SQL database connection. The methods provided here
   are designed for ease of database access and according to the design specifications given in the project
   description.
   This package heavily uses the Calendar class, found in java.util.Calendar, for recording date and time
   information. If this class is unfamiliar to a future developer, I would advise them to take some time to
   study the documentation. Of special note: in the Calendar class, months are indexed beginning with 0 (thus
   January = 0, February = 1, etc.). Take note how I modify the month integer by one unit when converting to
   and from Calendar objects.

   Where to find each method in this file (DatabaseAccess.java), including function specifications:
        verify_user_pass()  . . . . . . . . line   52
        pull_user_info()  . . . . . . . . . line   84
        pull_doctor_id_list() . . . . . . . line  124
        find_doctor_id()  . . . . . . . . . line  173
        find_doctor_name()  . . . . . . . . line  200
        pull_doctor_schedule()  . . . . . . line  234
        pull_patient_info() . . . . . . . . line  285
        find_patient_id() . . . . . . . . . line  388
        update_patient_info() . . . . . . . line  442
        pull_patient_ssn()  . . . . . . . . line  487
        update_patient_ssn()  . . . . . . . line  513
        pull_appt_schedule()  . . . . . . . line  536
        add_appt_to_schedule()  . . . . . . line  596
        change_no_show_status() . . . . . . line  630
        pull_chart_records()  . . . . . . . line  656
        add_new_chart_record()  . . . . . . line  738
        update_chart_record() . . . . . . . line  816
        pull_payment_records()  . . . . . . line  812
        add_new_payment_record()  . . . . . line  956
        update_payment_record() . . . . . . line 1016
        find_drug_id()  . . . . . . . . . . line 1046
        add_new_prescription()  . . . . . . line 1072
        pull_doctor_schedule_as_str_dates() line 1113
        pull_doctor_schedule_as_str_times() line 1151
        find_patient_id_from_name_only()  . line 1201
        does_patient_have_appt_today()  . . line 1235
 */


package database_access;

import java.sql.*;
import java.util.Calendar;
import java.util.Random;


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


    /* pull_doctor_id_list: Takes a database Connection conn. Returns a DoctorIDList object (defined in
            DoctorIDList.java) containing a list of all doctors' names and ID numbers. If an SQL exception
            occurs, returns an empty DoctorIDList object.
     */
    static public DoctorIDList pull_doctor_id_list(Connection conn) {
        DoctorIDList id_list = new DoctorIDList();
        int id_num;
        byte[] enc_f_name;
        byte[] enc_l_name;
        String efn;
        String eln;
        String first_name;
        String last_name;
        int f_name_length;
        int l_name_length;

        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = "SELECT * FROM user_info WHERE authorization_level = 2";
            ResultSet rs = stmt.executeQuery(query);

            rs.last();
            int num_results = rs.getRow(); //This determines how many rows are in rs
            rs.first();

            for (int i = 0; i < num_results; i++) {
                id_num = rs.getInt("id_num");
                efn = rs.getString("first_name");
                f_name_length = rs.getInt("first_name_length");
                eln = rs.getString("last_name");
                l_name_length = rs.getInt("last_name_length");

                enc_f_name = DatabaseSecurity.hex_string_to_byte_array(efn);
                enc_l_name = DatabaseSecurity.hex_string_to_byte_array(eln);

                first_name = (DatabaseSecurity.decrypt(enc_f_name)).substring(0, f_name_length);
                last_name = (DatabaseSecurity.decrypt(enc_l_name)).substring(0, l_name_length);

                id_list.add_item(id_num, first_name, last_name);
                rs.next();
            }

            return id_list;
        } catch (Exception SQLException) {
            return id_list;
        }
    }


    /* find_doctor_id: Takes a database Connection conn, a first_name, and a last_name. Returns the id_num of
            the corresponding doctor. If no id is found, returns -3. If an SQLException occurs, returns -1.
     */
    static public int find_doctor_id(Connection conn, String first_name, String last_name) {
        byte[] enc_f_name = new byte[64];
        byte[] enc_l_name = new byte[64];

        try {
            enc_f_name = DatabaseSecurity.encrypt(first_name, 64);
            String efn = DatabaseSecurity.byte_array_to_hex_string(enc_f_name);
            enc_l_name = DatabaseSecurity.encrypt(last_name, 64);
            String eln = DatabaseSecurity.byte_array_to_hex_string(enc_l_name);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user_info WHERE first_name = '" + efn + "' AND last_name = '" + eln + "'");

            if (rs.next() != true) {
                return -3;
            }

            return rs.getInt("id_num");
        } catch (Exception SQLException) {
            return -1;
        }
    }


    /* find_doctor_name: Takes a database Connection conn and a doctor_id. Returns a string of the doctor's
            full name. If the doctor_id is not found, returns the string "-3". If an SQLException occurs,
            returns the string "-1".
     */
    static public String find_doctor_name(Connection conn, int doctor_id) {
        byte[] enc_f_name = new byte[64];
        byte[] enc_l_name = new byte[64];

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user_info WHERE id_num = " + doctor_id);

            if (rs.next() != true) {
                return "-3";
            }

            String efn = rs.getString("first_name");
            int first_name_length = rs.getInt("first_name_length");
            String eln = rs.getString("last_name");
            int last_name_length = rs.getInt("last_name_length");

            enc_f_name = DatabaseSecurity.hex_string_to_byte_array(efn);
            enc_l_name = DatabaseSecurity.hex_string_to_byte_array(eln);

            String first_name = (DatabaseSecurity.decrypt(enc_f_name)).substring(0, first_name_length);
            String last_name = (DatabaseSecurity.decrypt(enc_l_name)).substring(0, last_name_length);

            return first_name + " " + last_name;
        } catch (Exception SQLException) {
            return "-1";
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
                start_time.set(year, (month - 1), day, start_hour, start_minute);
                Calendar end_time = Calendar.getInstance();
                end_time.set(year, (month - 1), day, end_hour, end_minute);

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
            int b_month = (birth_date.get(Calendar.MONTH)) + 1;
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
            zip_code = zip_code.substring(0, 5);
            phone_num = phone_num.substring(0, 10);
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


    /* find_patient_id: Takes a database Connection conn, a first_name, a last_name, and a Calendar birthdate.
            Returns the id_num of the corresponding patient. If no id is found, returns -3. If more than one
            patient was found with the same name and birthdate, returns -2. If an SQLException occurs, returns -1.
     */
    static public int find_patient_id(Connection conn, String first_name, String last_name, Calendar birthdate) {
        byte[] enc_f_name = new byte[64];
        byte[] enc_l_name = new byte[64];
        byte[] enc_b_date = new byte[16];

        //This block of code converts the given birthdate from a Calendar object into integer values and a string b_date_str in form DDMMYYYY.
        int b_day = birthdate.get(Calendar.DATE);
        int b_month = (birthdate.get(Calendar.MONTH)) + 1;
        int b_year = birthdate.get(Calendar.YEAR);
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

        try {
            enc_f_name = DatabaseSecurity.encrypt(first_name, 64);
            String efn = DatabaseSecurity.byte_array_to_hex_string(enc_f_name);
            enc_l_name = DatabaseSecurity.encrypt(last_name, 64);
            String eln = DatabaseSecurity.byte_array_to_hex_string(enc_l_name);
            enc_b_date = DatabaseSecurity.encrypt(b_date_str, 16);
            String ebd = DatabaseSecurity.byte_array_to_hex_string(enc_b_date);

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("SELECT * FROM patients WHERE first_name = '" + efn + "' AND last_name = '" + eln + "' AND birthdate = '" + ebd + "'");

            if (rs.next() != true)
                return -3;

            rs.last();
            int num_results = rs.getRow(); //This determines how many rows are in rs
            rs.first();

            if (num_results > 1)
                return -2;

            return rs.getInt("id_num");
        } catch (Exception SQLException) {
            return -1;
        }
    }


    /* update_patient_info: Takes a database Connection conn and a PatientInfo object p_info. Updates the
            address, phone, and insurance information in the database according to the information stored in
            p_info. Returns 0 for successful update. Returns 1 if an SQLException occurs.
     */
    public static int update_patient_info(Connection conn, PatientInfo p_info) {
        byte[] enc_st_address = new byte[128];
        byte[] enc_city = new byte[64];
        byte[] enc_state = new byte[32];
        byte[] enc_zip = new byte[16];
        byte[] enc_phone = new byte[16];
        byte[] enc_policy = new byte[64];

        enc_st_address = DatabaseSecurity.encrypt(p_info.street_address, 128);
        String esa = DatabaseSecurity.byte_array_to_hex_string(enc_st_address);
        enc_city = DatabaseSecurity.encrypt(p_info.city, 64);
        String ec = DatabaseSecurity.byte_array_to_hex_string(enc_city);
        enc_state = DatabaseSecurity.encrypt(p_info.us_state, 32);
        String es = DatabaseSecurity.byte_array_to_hex_string(enc_state);
        enc_zip = DatabaseSecurity.encrypt(p_info.zip_code, 16);
        String ez = DatabaseSecurity.byte_array_to_hex_string(enc_zip);
        enc_phone = DatabaseSecurity.encrypt(p_info.phone_number, 16);
        String ep = DatabaseSecurity.byte_array_to_hex_string(enc_phone);
        enc_policy = DatabaseSecurity.encrypt(p_info.policy_num, 64);
        String epol = DatabaseSecurity.byte_array_to_hex_string(enc_policy);

        try {
            Statement stmt = conn.createStatement();
            String query = "UPDATE patients " +
                "SET street_address = '" + esa + "', street_address_length = " + p_info.street_address.length() + ", " +
                "city = '" + ec + "', city_length = " + p_info.city.length() + ", " +
                "us_state = '" + es + "', us_state_length = " + p_info.us_state.length() + ", " +
                "zip_code = '" + ez + "', " +
                "phone_number = '" + ep + "', " +
                "insurance_provider = " + p_info.insurance_provider + ", " +
                "insurance_policy_num = '" + epol + "', insurance_policy_num_length = " + p_info.policy_num.length() + " " +
                "WHERE id_num = " + p_info.id_num;
            stmt.executeQuery(query);

            return 0;
        } catch (Exception SQLException) {
            return 1;
        }
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
            ssn = ssn.substring(0, 9);

            return ssn;
        } catch (Exception SQLException) {
            return "SSSSSSSSS";
        }
    }


    /* update_patient_ssn: Takes a database Connection conn, a PatientInfo object p_info, and a new social
            security number as a string new_ssn. Updates the row in the database corresponding to
            p_info.id_num with the new_ssn. Returns 0 if update is successful. Returns 1 if an SQLException
            occurs.
     */
    public static int update_patient_ssn(Connection conn, PatientInfo p_info, String new_ssn) {
        byte[] enc_ssn = new byte[16];

        enc_ssn = DatabaseSecurity.encrypt(new_ssn, 16);
        String essn = DatabaseSecurity.byte_array_to_hex_string(enc_ssn);

        try {
            Statement stmt = conn.createStatement();
            String query = "UPDATE patients SET ssn = '" + essn + "' WHERE id_num = " + p_info.id_num;
            stmt.executeQuery(query);

            return 0;
        } catch (Exception SQLException) {
            return 1;
        }
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
                appt_minute = rs.getInt("appt_minute");
                no_show = rs.getInt("no_show");

                date_time = Calendar.getInstance();
                date_time.set(appt_year, (appt_month - 1), appt_day, appt_hour, appt_minute);

                appt_s.add_appointment(appt_id, patient_id, doctor_id, date_time, no_show);

                rs.next();
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
            and adds a new appointment object to the appt_list of appt_s. Returns 3 if an appointment is
            already scheduled for the given doctor at the given time and date. Returns 0 for successful
            execution or 2 for an SQL exception.
     */
    static public int add_appt_to_schedule(Connection conn, ApptSchedule appt_s, int patient_id, int doctor_id, Calendar date_time) {
        int new_appt_id = appt_s.appt_list_length + 1;
        int month = (date_time.get(Calendar.MONTH)) + 1;

        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT id_num FROM appointments WHERE doctor_id = " + doctor_id + " AND appt_day = " +
                date_time.get(Calendar.DATE) + " AND appt_month = " + month + " AND appt_year = " + date_time.get(Calendar.YEAR) +
                " AND appt_hour = " + date_time.get(Calendar.HOUR_OF_DAY) + " AND appt_minute = " + date_time.get(Calendar.MINUTE);
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() == true)  //if an appointment with matching date, time, and doctor is found, return 3
                return 3;

            query = "INSERT INTO appointments VALUES (" + new_appt_id + ", " + patient_id + ", " + doctor_id + ", " +
                date_time.get(Calendar.DATE) + ", " + month + ", " + date_time.get(Calendar.YEAR) + ", " +
                date_time.get(Calendar.HOUR_OF_DAY) + ", " +  date_time.get(Calendar.MINUTE) + ", " + "0)";
            stmt.executeQuery(query);

            appt_s.add_appointment(new_appt_id, patient_id, doctor_id, date_time, 0);

            return 0;
        } catch (Exception SQLException) {
            return 2;
        }
    }


    /* change_no_show_status(): Takes a database Connection conn, an appt_id number, and no_show. Appt_id
            is the unique identifier associated with each appointment; in an Appointment object, it is
            stored in the variable appt_id. No_show should equal 0 if the patient has not arrived or 1 if
            they have arrived. Updates the database with the new no_show value. Returns 0 if successful.
            Returns 3 if no appointment with the given appt_id was found in the database. If an
            SQLException occurs, returns 1.
     */
    static public int change_no_show_status(Connection conn, int appt_id, int no_show) {
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT doctor_id FROM appointments WHERE id_num = " + appt_id;
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() != true)
                return 3;

            query = "UPDATE appointments SET no_show = " + no_show + " WHERE id_num = " + appt_id;
            stmt.executeQuery(query);

            return 0;
        } catch (Exception SQLException) {
            return 1;
        }
    }


    /* pull_chart_records: Takes a database Connection conn and a patient_id. Returns a PatientChart object,
            defined in PatientChart.java, containing a list of ChartRecord objects that correspond to
            patient_id. If no records are found for the patient, returns a PatientChart object with an
            empty list. If an SQLException occurs, returns a PatientChart object with a ChartRecord with
            record_num -1.
     */
    static public PatientChart pull_chart_records(Connection conn, int patient_id) {
        PatientChart p_chart = new PatientChart(patient_id);
        long record_num;
        int record_day;
        int record_month;
        int record_year;
        Calendar record_date;
        String e_temperature;
        byte[] enc_temperature;
        double temperature;
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
        int doctor_id;

        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = "SELECT * FROM chart_records WHERE patient_id = " + patient_id;
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() != true)
                return p_chart;

            rs.last();
            int num_results = rs.getRow(); //This determines how many rows are in rs
            rs.first();

            for (int i = 0; i < num_results; i++) {
                record_num = rs.getLong("id_num");
                record_day = rs.getInt("record_day");
                record_month = rs.getInt("record_month");
                record_year = rs.getInt("record_year");
                e_temperature = rs.getString("temperature");
                e_p_rate = rs.getString("pulse_rate");
                e_b_rate = rs.getString("breathing_rate");
                e_bps = rs.getString("blood_pressure_systolic");
                e_bpd = rs.getString("blood_pressure_diastolic");
                doctor_id = rs.getInt("doctor_visited");

                enc_temperature = DatabaseSecurity.hex_string_to_byte_array(e_temperature);
                enc_p_rate = DatabaseSecurity.hex_string_to_byte_array(e_p_rate);
                enc_b_rate = DatabaseSecurity.hex_string_to_byte_array(e_b_rate);
                enc_bps = DatabaseSecurity.hex_string_to_byte_array(e_bps);
                enc_bpd = DatabaseSecurity.hex_string_to_byte_array(e_bpd);

                temperature = Double.parseDouble((DatabaseSecurity.decrypt(enc_temperature)).substring(0, 5));
                pulse_rate = Integer.parseInt((DatabaseSecurity.decrypt(enc_p_rate)).substring(0, 3));
                breathing_rate = Integer.parseInt((DatabaseSecurity.decrypt(enc_b_rate)).substring(0,3));
                blood_pressure_systolic = Integer.parseInt((DatabaseSecurity.decrypt(enc_bps)).substring(0, 4));
                blood_pressure_diastolic = Integer.parseInt((DatabaseSecurity.decrypt(enc_bpd)).substring(0, 4));

                record_date = Calendar.getInstance();
                record_date.set(record_year, (record_month - 1), record_day);

                p_chart.add_record(record_num, patient_id, record_date, temperature, pulse_rate, breathing_rate, blood_pressure_systolic, blood_pressure_diastolic, doctor_id);
                rs.next();
            }

            return p_chart;
        } catch (Exception SQLException) {
            record_date = Calendar.getInstance();
            p_chart.add_record(-1, -1, record_date, -1, -1, -1, -1, -1, -1);
            return p_chart;
        }
    }


    /* add_new_chart_record: This method adds a new record to the table chart_records in the SQL database.
            Takes a database Connection conn and a ChartRecord object cr. Verifies that cr.record_num is
            not contained in the database; if it is, changes cr.record_num to a new value. Inserts a new
            row into chart_records containing information from cr. Returns 0 if successful. Returns 1 if
            an SQLException occurs.
     */
    static public int add_new_chart_record(Connection conn, ChartRecord cr) {
        Random rand = new Random();
        long record_num = cr.record_num;
        int month = (cr.record_date.get(Calendar.MONTH)) + 1;

        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT doctor_visited FROM chart_records WHERE id_num = " + record_num;
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() == true) {    //if an id_num matching the number in cr.record_num is found
                while (true) {  //this will continue generating prescription id numbers until a unique one is found
                    record_num = rand.nextInt(1000000000);    //this will generate values between 0 and 999999999 inclusive; work on ways to return larger random numbers
                    query = "SELECT doctor_visited FROM chart_records WHERE id_num = " + record_num;
                    rs = stmt.executeQuery(query);
                    if (rs.next() != true) {
                        cr.record_num = record_num;
                        break;
                    }
                }
            }

            //Modify temperature to be in the correct string format
            String temperature_str = Double.toString(cr.temperature);
            int decimal_index = temperature_str.indexOf('.');
            while (decimal_index < 3) {
                temperature_str = "0" + temperature_str;
                decimal_index++;
            }
            //Modify other values in cr to be in the correct string format
            String pulse_rate_str = Integer.toString(cr.pulse_rate);
            while (pulse_rate_str.length() < 3)
                pulse_rate_str = "0" + pulse_rate_str;
            String breathing_rate_str = Integer.toString(cr.breathing_rate);
            while (breathing_rate_str.length() < 3)
                breathing_rate_str = "0" + breathing_rate_str;
            String bp_systolic_str = Integer.toString(cr.blood_pressure_systolic);
            while (bp_systolic_str.length() < 4)
                bp_systolic_str = "0" + bp_systolic_str;
            String bp_diastolic_str = Integer.toString(cr.blood_pressure_diastolic);
            while (bp_diastolic_str.length() < 4)
                bp_diastolic_str = "0" + bp_diastolic_str;

            byte[] enc_temp = new byte[16];
            byte[] enc_pr = new byte[16];
            byte[] enc_br = new byte[16];
            byte[] enc_bps = new byte[16];
            byte[] enc_bpd = new byte[16];

            enc_temp = DatabaseSecurity.encrypt(temperature_str, 16);
            String et = DatabaseSecurity.byte_array_to_hex_string(enc_temp);
            enc_pr = DatabaseSecurity.encrypt(pulse_rate_str, 16);
            String epr = DatabaseSecurity.byte_array_to_hex_string(enc_pr);
            enc_br = DatabaseSecurity.encrypt(breathing_rate_str, 16);
            String ebr = DatabaseSecurity.byte_array_to_hex_string(enc_br);
            enc_bps = DatabaseSecurity.encrypt(bp_systolic_str, 16);
            String ebps = DatabaseSecurity.byte_array_to_hex_string(enc_bps);
            enc_bpd = DatabaseSecurity.encrypt(bp_diastolic_str, 16);
            String ebpd = DatabaseSecurity.byte_array_to_hex_string(enc_bpd);

            query = "INSERT INTO chart_records VALUES (" + record_num + ", " + cr.patient_id + ", " + cr.record_date.get(Calendar.DATE) +
                ", " + month + ", " + cr.record_date.get(Calendar.YEAR) + ", '" + et + "', '" + epr + "', '" + ebr + "', '" +
                ebps + "', '" + ebpd + "', " + cr.doctor_visited + ")";
            stmt.executeQuery(query);

            return 0;
        } catch (Exception SQLException) {
            return 1;
        }
    }


    /* update_chart_record: This method updates a chart record that already exists in the SQL database.
            Takes a database Connection conn and a ChartRecord object cr. Overwrites the previous
            information in the database associated with cr.record_num with the current information in cr.
            Returns 0 if overwrite is successful. If a record with id_num equal to cr.record_num is not
            found in the database, returns 3. If an SQLException occurs, returns 1.
     */
    static public int update_chart_record(Connection conn, ChartRecord cr) {
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT doctor_visited FROM chart_records WHERE id_num = " + cr.record_num;
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() != true)
                return 3;

            //Modify temperature to be in the correct string format
            String temperature_str = Double.toString(cr.temperature);
            int decimal_index = temperature_str.indexOf('.');
            while (decimal_index < 3) {
                temperature_str = "0" + temperature_str;
                decimal_index++;
            }
            //Modify other values in cr to be in the correct string format
            String pulse_rate_str = Integer.toString(cr.pulse_rate);
            while (pulse_rate_str.length() < 3)
                pulse_rate_str = "0" + pulse_rate_str;
            String breathing_rate_str = Integer.toString(cr.breathing_rate);
            while (breathing_rate_str.length() < 3)
                breathing_rate_str = "0" + breathing_rate_str;
            String bp_systolic_str = Integer.toString(cr.blood_pressure_systolic);
            while (bp_systolic_str.length() < 4)
                bp_systolic_str = "0" + bp_systolic_str;
            String bp_diastolic_str = Integer.toString(cr.blood_pressure_diastolic);
            while (bp_diastolic_str.length() < 4)
                bp_diastolic_str = "0" + bp_diastolic_str;

            byte[] enc_temp = new byte[16];
            byte[] enc_pr = new byte[16];
            byte[] enc_br = new byte[16];
            byte[] enc_bps = new byte[16];
            byte[] enc_bpd = new byte[16];

            enc_temp = DatabaseSecurity.encrypt(temperature_str, 16);
            String et = DatabaseSecurity.byte_array_to_hex_string(enc_temp);
            enc_pr = DatabaseSecurity.encrypt(pulse_rate_str, 16);
            String epr = DatabaseSecurity.byte_array_to_hex_string(enc_pr);
            enc_br = DatabaseSecurity.encrypt(breathing_rate_str, 16);
            String ebr = DatabaseSecurity.byte_array_to_hex_string(enc_br);
            enc_bps = DatabaseSecurity.encrypt(bp_systolic_str, 16);
            String ebps = DatabaseSecurity.byte_array_to_hex_string(enc_bps);
            enc_bpd = DatabaseSecurity.encrypt(bp_diastolic_str, 16);
            String ebpd = DatabaseSecurity.byte_array_to_hex_string(enc_bpd);

            query = "UPDATE chart_records SET temperature = '" + et + "', pulse_rate = '" + epr + "', breathing_rate = '" +
                ebr + "', blood_pressure_systolic = '" + ebps + "', blood_pressure_diastolic = '" + ebpd +
                "', doctor_visited = " + cr.doctor_visited + " WHERE id_num = " + cr.record_num;

            stmt.executeQuery(query);

            return 0;
        } catch (Exception SQLException) {
            return 1;
        }
    }


    /* pull_payment_records: Takes a database Connection conn and a patient_id. Returns a Payments object
            containing a list of all PaymentRecords associated with the given patient_id in the database. If
            no records are found, returns a Payments object with an empty payments_list. If an SQLException
            occurs, returns a Payments object containing a Payment record with reference_num -1. Note that
            some Payment records will have not been paid yet and will thus contain an empty Calendar object
            for paid_date; paid_check should be consulted before attempting to draw data from paid_date.
     */
    static public Payments pull_payment_records(Connection conn, int patient_id) {
        Payments payments = new Payments(patient_id);
        long reference_num;
        String ea;
        byte[] enc_amount = new byte[16];
        double amount;
        int generated_day;
        int generated_month;
        int generated_year;
        Calendar generated_date;
        int paid_check;
        int paid_day = 0;
        int paid_month = 0;
        int paid_year = 0;
        Calendar paid_date;
        int payment_type_id = -2; //If payment has not yet been made, this will continue to be -2.

        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = "SELECT * FROM payments WHERE patient_id = " + patient_id;
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() != true)
                return payments;

            rs.last();
            int num_results = rs.getRow(); //This determines how many rows are in rs
            rs.first();

            for (int i = 0; i < num_results; i++) {
                reference_num = rs.getLong("reference_num");
                ea = rs.getString("amount");
                generated_day = rs.getInt("generated_day");
                generated_month = rs.getInt("generated_month");
                generated_year = rs.getInt("generated_year");
                paid_check = rs.getInt("paid_check");
                if (paid_check == 1) {  //if the payment has been made, pull the appropriate values
                    paid_day = rs.getInt("paid_day");
                    paid_month = rs.getInt("paid_month");
                    paid_year = rs.getInt("paid_year");
                    payment_type_id = rs.getInt("payment_type");
                }

                enc_amount = DatabaseSecurity.hex_string_to_byte_array(ea);
                amount = Double.parseDouble((DatabaseSecurity.decrypt(enc_amount)).substring(0, 11));

                generated_date = Calendar.getInstance();
                generated_date.set(generated_year, (generated_month - 1), generated_day);
                paid_date = Calendar.getInstance();
                if (paid_check == 1)
                    paid_date.set(paid_year, (paid_month - 1), paid_day);

                payments.add_payment_record(reference_num, patient_id, amount, generated_date, paid_check, paid_date, payment_type_id);

                rs.next();
            }

            return payments;
        } catch (Exception SQLException) {
            generated_date = Calendar.getInstance();
            paid_date = Calendar.getInstance();
            payments.add_payment_record(-1, -1, -1.0, generated_date, -1, paid_date, -1);
            return payments;
        }
    }


    /* add_new_payment_record: This method adds a new record to the table payments in the SQL database.
            Takes a database Connection conn and a PaymentRecord object pr. Verifies that pr.reference_num
            is not contained in the database; if it is, changes pr.reference_num to a new value. Inserts a
            new row into payments containing information from pr. Returns 0 if successful. Returns 1 if
            an SQLException occurs.
     */
    static public int add_new_payment_record(Connection conn, PaymentRecord pr) {
        Random rand = new Random();
        long reference_num = pr.reference_num;
        int generated_month = (pr.generated_date.get(Calendar.MONTH)) + 1;

        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT generated_year FROM payments WHERE reference_num = " + reference_num;
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() == true) {    //if an id_num matching the number in cr.record_num is found
                while (true) {  //this will continue generating prescription id numbers until a unique one is found
                    reference_num = rand.nextInt(1000000000);    //this will generate values between 0 and 999999999 inclusive; work on ways to return larger random numbers
                    query = "SELECT generated_year FROM payments WHERE reference_num = " + reference_num;
                    rs = stmt.executeQuery(query);
                    if (rs.next() != true) {
                        pr.reference_num = reference_num;
                        break;
                    }
                }
            }

            //Modify pr.amount to be in the correct string format
            String amount_str = Double.toString(pr.amount);
            int decimal_index = amount_str.indexOf('.');
            while (decimal_index < 8) {
                amount_str = "0" + amount_str;
                decimal_index++;
            }

            byte[] enc_amount = new byte[16];
            enc_amount = DatabaseSecurity.encrypt(amount_str, 16);
            String ea = DatabaseSecurity.byte_array_to_hex_string(enc_amount);

            if (pr.paid_check == 1) {
                int paid_month = (pr.paid_date.get(Calendar.MONTH)) + 1;
                query = "INSERT INTO payments VALUES (" + reference_num + ", " + pr.patient_id + ", '" + ea + "', " +
                        pr.generated_date.get(Calendar.DATE) + ", " + generated_month + ", " + pr.generated_date.get(Calendar.YEAR) +
                        ", " + pr.paid_check + ", " + pr.paid_date.get(Calendar.DATE) + ", " + paid_month + ", " +
                        pr.paid_date.get(Calendar.YEAR) + ", " + pr.payment_type + ")";
            } else {
                query = "INSERT INTO payments VALUES (" + reference_num + ", " + pr.patient_id + ", '" + ea + "', " +
                        pr.generated_date.get(Calendar.DATE) + ", " + generated_month + ", " + pr.generated_date.get(Calendar.YEAR) +
                        ", " + pr.paid_check + ", '', '', '', '')";
            }
            stmt.executeQuery(query);

            return 0;
        } catch (Exception SQLException) {
            return 1;
        }
    }


    /* update_payment_record: This method updates a payment record that already exists in the SQL database.
            Takes a database Connection conn and a PaymentRecord object pr. Overwrites the previous
            information in the database associated with pr.reference_num with the current information in pr.
            Returns 0 if overwrite is successful. If a record with reference_num equal to cr.reference_num
            is not found in the database, returns 3. If an SQLException occurs, returns 1.
     */
    static public int update_payment_record(Connection conn, PaymentRecord pr) {
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT generated_year FROM payments WHERE reference_num = " + pr.reference_num;
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() != true)
                return 3;

            int paid_day = pr.paid_date.get(Calendar.DATE);
            int paid_month = (pr.paid_date.get(Calendar.MONTH)) + 1;
            int paid_year = pr.paid_date.get(Calendar.YEAR);

            query = "UPDATE payments SET paid_check = " + pr.paid_check + ", paid_day = " + paid_day + ", paid_month = " + paid_month +
                ", paid_year = " + paid_year + ", payment_type = " + pr.payment_type + " WHERE reference_num = " + pr.reference_num;
            stmt.executeQuery(query);

            return 0;
        } catch (Exception SQLException) {
            return 1;
        }
    }


    /* find_drug_id: Takes a database Connection conn and a drug_name as a string. Returns the corresponding
            drug_id for the given drug_name as stored in the database. If the drug_name is not found, returns
            -3. If an SQLException occurs, returns -1.
     */
    static public int find_drug_id(Connection conn, String drug_name) {
        byte[] enc_d_name = new byte[64];
        String edn;

        enc_d_name = DatabaseSecurity.encrypt(drug_name, 64);
        edn = DatabaseSecurity.byte_array_to_hex_string(enc_d_name);

        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT id_num FROM drug_types WHERE drug_name = '" + edn + "'";
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() != true)
                return -3;

            return rs.getInt("id_num");
        } catch (Exception SQLException) {
            return -1;
        }
    }


    /* add_new_prescription: Takes a database Connection conn and information about a prescription (patient_id,
            drug_id, dose, amount, instructions, and doctor_id). Adds this information to the prescriptions
            table in the database. Returns 0 if successful. Returns 1 if an SQLException occurs.
     */
    static public int add_new_prescription(Connection conn, int patient_id, int drug_id, String dose, int amount, String instructions, int doctor_id) {
        Random rand = new Random();
        int prescription_id;
        Calendar today = Calendar.getInstance();
        int month = (today.get(Calendar.MONTH)) + 1;
        String query;
        byte[] enc_dose = new byte[64];
        byte[] enc_instructions = new byte[256];

        enc_dose = DatabaseSecurity.encrypt(dose, 64);
        String ed = DatabaseSecurity.byte_array_to_hex_string(enc_dose);
        enc_instructions = DatabaseSecurity.encrypt(instructions, 256);
        String ei = DatabaseSecurity.byte_array_to_hex_string(enc_instructions);

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs;
            while (true) {  //this will continue generating prescription id numbers until a unique one is found
                prescription_id = rand.nextInt(1000000);    //this will generate values between 0 and 999999 inclusive
                query = "SELECT drug_id FROM prescriptions WHERE id_num = " + prescription_id;
                rs = stmt.executeQuery(query);
                if (rs.next() != true)
                    break;
            }

            query = "INSERT INTO prescriptions VALUES (" + prescription_id + ", " + patient_id + ", " + drug_id + ", " +
                today.get(Calendar.DATE) + ", " + month + ", " + today.get(Calendar.YEAR) + ", '" + ed + "', " +
                dose.length() + ", " + amount + ", '" + ei + "', " + instructions.length() + ", " + doctor_id + ")";
            stmt.executeQuery(query);

            return 0;
        } catch (Exception SQLException) {
            return 1;
        }
    }


    /* pull_doctor_schedule_as_str_dates: Takes a database connection conn and a doctor_id. Returns days and
            months from the doctor_schedule table of the database as a comma-separated list in string format.
     */
    static public String pull_doctor_schedule_as_str_dates(Connection conn, int doctor_id) {
        int schedule_length;
        int day;
        int month;
        String out_str = "";

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

                if (i != 0) {
                    out_str = out_str + ", ";
                }
                out_str = out_str + month + "/" + day;

                rs.next();
            }

            return out_str;
        } catch (Exception SQLException) {
            System.out.println(SQLException);
            return "An error occurred.";
        }
    }


    /* pull_doctor_schedule_as_str_times: Takes a database connection conn and a doctor_id. Returns start
            times and end times from the doctor_schedule table of the database as a comma-separated list in
            string format.
     */
    static public String pull_doctor_schedule_as_str_times(Connection conn, int doctor_id) {
        int schedule_length;
        int start_hour;
        int start_minute;
        int end_hour;
        int end_minute;
        String out_str = "";

        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = "SELECT * FROM doctor_schedule WHERE doctor_id = " + doctor_id;
            ResultSet rs = stmt.executeQuery(query); //rs now contains all rows that correspond to the given doctor_id

            rs.last();
            schedule_length = rs.getRow(); //This determines how many rows are in rs
            rs.first();

            for (int i = 0; i < schedule_length; i++) {
                start_hour = rs.getInt("start_hour");
                start_minute = rs.getInt("start_minute");
                end_hour = rs.getInt("end_hour");
                end_minute = rs.getInt("end_minute");

                String start_minute_str = Integer.toString(start_minute);
                if (start_minute_str.length() < 2)
                    start_minute_str = "0" + start_minute_str;
                String end_minute_str = Integer.toString(end_minute);
                if (end_minute_str.length() < 2)
                    end_minute_str = "0" + end_minute_str;

                if (i != 0) {
                    out_str = out_str + ", ";
                }
                out_str = out_str + start_hour + ":" + start_minute_str + "-" + end_hour + ":" + end_minute_str;

                rs.next();
            }

            return out_str;
        } catch (Exception SQLException) {
            System.out.println(SQLException);
            return "An exception occurred.";
        }
    }


    /* find_patient_id_from_name_only: Takes a database Connection conn, a first_name, and a last_name. Returns the id_num of
            the corresponding patient. If no id is found, returns -3. If more than one patient was found with
            the same name, returns -2. If an SQLException occurs, returns -1.
     */
    static public int find_patient_id_from_name_only(Connection conn, String first_name, String last_name) {
        byte[] enc_f_name = new byte[64];
        byte[] enc_l_name = new byte[64];

        try {
            enc_f_name = DatabaseSecurity.encrypt(first_name, 64);
            String efn = DatabaseSecurity.byte_array_to_hex_string(enc_f_name);
            enc_l_name = DatabaseSecurity.encrypt(last_name, 64);
            String eln = DatabaseSecurity.byte_array_to_hex_string(enc_l_name);

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("SELECT * FROM patients WHERE first_name = '" + efn + "' AND last_name = '" + eln + "'");

            if (rs.next() != true)
                return -3;

            rs.last();
            int num_results = rs.getRow(); //This determines how many rows are in rs
            rs.first();

            if (num_results > 1)
                return -2;

            return rs.getInt("id_num");
        } catch (Exception SQLException) {
            return -1;
        }
    }


    /* does_patient_have_appt_today: Takes a database Connection conn and a patient_id. Checks the database
            to see if an appointment has been scheduled with patient_id for today's date. If at least one
            appointment has been scheduled for today with the given patient_id, returns a string containing
            the time of the first appointment for today. If no appointment was found with the given
            information, returns the string "-3". If an SQLException occurs, returns the string "-1".
     */
    static public String does_patient_have_appt_today(Connection conn, int patient_id) {
        Calendar today = Calendar.getInstance();
        int month = (today.get(Calendar.MONTH)) + 1;
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM appointments WHERE appt_day = " + today.get(Calendar.DATE) + " AND appt_month = " +
                month + " AND appt_year = " + today.get(Calendar.YEAR) + " AND patient_id = " + patient_id;
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next() != true) //if no appointment for today is found, returns the string "-3"
                return "-3";

            int appt_hour = rs.getInt("appt_hour");
            int appt_minute = rs.getInt("appt_minute");

            String appt_hour_str = Integer.toString(appt_hour);
            String appt_minute_str = Integer.toString(appt_minute);
            if (appt_minute_str.length() < 2)
                appt_minute_str = "0" + appt_minute_str;

            return appt_hour_str + ":" + appt_minute_str;
        } catch (Exception SQLException) {
            return "-1";
        }
    }
}