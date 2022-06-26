/* TestMainForDatabase.java
   Created by Christopher Walker.
   Created 14 June 2022.
   Last modified 26 June 2022.
   THIS FILE SHOULD NOT BE INCLUDED IN THE FINAL PROJECT.
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


import database_access.*;

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
            System.out.println("Test of DatabaseAccess.verify_user_pass()");
            int num = DatabaseAccess.verify_user_pass(conn, "nurse_martinez", "password3");
            System.out.println("Verify password result: " + num);
            System.out.println();

            //Test DatabaseAccess.pull_user_info()
            System.out.println("Test of DatabaseAccess.pull_user_info()");
            UserInfo u_info = DatabaseAccess.pull_user_info(conn, "nurse_martinez");
            System.out.println("System ID number: " + u_info.id_num);
            System.out.println("First name: " + u_info.first_name);
            System.out.println("Last name: " + u_info.last_name);
            System.out.println("Authorization level: " + u_info.authorization_level);
            System.out.println();

            //Test DatabaseAccess.pull_doctor_id_list()
            System.out.println("Test of DatabaseAccess.pull_doctor_id_list()");
            DoctorIDList d_list = DatabaseAccess.pull_doctor_id_list(conn);
            ListIterator<DoctorID> litr3 = d_list.doctors_list.listIterator();
            while (litr3.hasNext()) {
                System.out.print("Doctor " + litr3.next().first_name);
                litr3.previous();
                System.out.print(" " + litr3.next().last_name);
                litr3.previous();
                System.out.println(", ID number: " + litr3.next().id_num);
            }
            System.out.println();

            //Test DatabaseAccess.find_doctor_id()
            System.out.println("Test of DatabaseAccess.find_doctor_id()");
            int doc_id = DatabaseAccess.find_doctor_id(conn, "Amy", "Stephens");
            System.out.println("Doctor ID: " + doc_id);
            System.out.println();

            //Test DatabaseAccess.find_doctor_name()
            System.out.println("Test of DatabaseAccess.find_doctor_name()");
            String doc_name = DatabaseAccess.find_doctor_name(conn, 9427);
            System.out.println("Doctor name: " + doc_name);
            System.out.println();

            //Test DatabaseAccess.pull_doctor_schedule()
            System.out.println("Test of DatabaseAccess.pull_doctor_schedule()");
            DoctorSchedule ds = DatabaseAccess.pull_doctor_schedule(conn, 9427); //9427 is the id_num of Doctor Stephens
            ListIterator<TimeSlot> litr1 = ds.time_slot_list.listIterator();
            while (litr1.hasNext()) {
                System.out.print("Availability start (DD/MM/YYYY): " + litr1.next().start_time.get(Calendar.DATE));
                litr1.previous();
                System.out.print("/" + litr1.next().start_time.get(Calendar.MONTH));
                litr1.previous();
                System.out.print("/" + litr1.next().start_time.get(Calendar.YEAR));
                litr1.previous();
                System.out.print(" " + litr1.next().start_time.get(Calendar.HOUR));
                litr1.previous();
                System.out.println(":" + litr1.next().start_time.get(Calendar.MINUTE));
                litr1.previous();
                System.out.print("Availability end (DD/MM/YYYY): " + litr1.next().end_time.get(Calendar.DATE));
                litr1.previous();
                System.out.print("/" + litr1.next().end_time.get(Calendar.MONTH));
                litr1.previous();
                System.out.print("/" + litr1.next().end_time.get(Calendar.YEAR));
                litr1.previous();
                System.out.print(" " + litr1.next().end_time.get(Calendar.HOUR));
                litr1.previous();
                System.out.println(":" + litr1.next().end_time.get(Calendar.MINUTE));
            }
            System.out.println();

            //Test DatabaseAccess.pull_patient_info() for patient Jia Chen
            System.out.println("Test of DatabaseAccess.pull_patient_info()");
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(1982, 7, 16);
            PatientInfo p_info = DatabaseAccess.pull_patient_info(conn, "Jia", "Chen", calendar1);
            System.out.println("Patient ID number: " + p_info.id_num);
            System.out.println("Street address: " + p_info.street_address);
            System.out.println("City: " + p_info.city);
            System.out.println("State: " + p_info.us_state);
            System.out.println("ZIP code: " + p_info.zip_code);
            System.out.println("Phone number: " + p_info.phone_number);
            System.out.println("Insurance provider id number: " + p_info.insurance_provider);
            System.out.println("Insurance policy number: " + p_info.policy_num);
            System.out.println();

            //Test DatabaseAccess.find_patient_id() for same patient Jia Chen
            System.out.println("Test of DatabaseAccess.find_patient_id()");
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(1982, 7, 16);
            int patient_id = DatabaseAccess.find_patient_id(conn, "Jia", "Chen", calendar2);
            System.out.println("Patient ID: " + patient_id);
            System.out.println();

            //Test DatabaseAccess.pull_patient_ssn() for the same patient Jia Chen
            System.out.println("Test of DatabaseAccess.pull_patient_ssn()");
            String ssn = DatabaseAccess.pull_patient_ssn(conn, p_info);
            System.out.println("Patient SSN: " + ssn);
            System.out.println();

            //Test DatabaseAccess.pull_appt_schedule()
            System.out.println("Test of DatabaseAccess.pull_appt_schedule()");
            ApptSchedule appt_s = DatabaseAccess.pull_appt_schedule(conn);
            ListIterator<Appointment> litr2 = appt_s.appt_list.listIterator();
            while (litr2.hasNext()) {
                System.out.println("Appointment ID: " + litr2.next().appt_id);
                litr2.previous();
                System.out.println("Patient ID: " + litr2.next().patient_id);
                litr2.previous();
                System.out.println("Doctor ID: " + litr2.next().doctor_id);
                litr2.previous();
                System.out.print("Date (DD/MM/YYYY): " + litr2.next().date_time.get(Calendar.DATE));
                litr2.previous();
                System.out.print("/" + litr2.next().date_time.get(Calendar.MONTH));
                litr2.previous();
                System.out.println("/" + litr2.next().date_time.get(Calendar.YEAR));
                litr2.previous();
                System.out.print("Time: " + litr2.next().date_time.get(Calendar.HOUR));
                litr2.previous();
                System.out.println(":" + litr2.next().date_time.get(Calendar.MINUTE));
                litr2.previous();
                System.out.println("Arrived or no show: " + litr2.next().no_show);
            }
            System.out.println();

            //Test DatabaseAccess.add_appt_to_schedule() using the AppointmentSchedule object appt_s and the PatientInfo object p_info created above.
            System.out.println("Test of DatabaseAccess.add_appt_to_schedule()");
            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(2022, 7, 8, 16, 0);
            int success_appt = DatabaseAccess.add_appt_to_schedule(conn, appt_s, p_info.id_num, 3852, calendar3);
            if (success_appt == 0)
                System.out.println("Appointment scheduled successfully.");
            else
                System.out.println("Appointment scheduling failed.");
            System.out.println();

            //Test DatabaseAccess.pull_chart_records() for patient Jia Chen, whose info is saved above in the object p_info
            System.out.println("Test of DatabaseAccess.pull_chart_records()");
            PatientChart p_chart = DatabaseAccess.pull_chart_records(conn, p_info.id_num);
            System.out.println("Patient ID: " + p_chart.patient_id);
            ListIterator<ChartRecord> litr4 = p_chart.record_list.listIterator();
            while (litr4.hasNext()) {
                System.out.println("Record number: " + litr4.next().record_num);
                litr4.previous();
                System.out.println("Patient ID: " + litr4.next().patient_id);
                litr4.previous();
                System.out.print("Record date (DD/MM/YY): " + litr4.next().record_date.get(Calendar.DATE));
                litr4.previous();
                System.out.print("/" + litr4.next().record_date.get(Calendar.MONTH));
                litr4.previous();
                System.out.println("/" + litr4.next().record_date.get(Calendar.YEAR));
                litr4.previous();
                System.out.println("Temperature: " + litr4.next().temperature + " degrees F");
                litr4.previous();
                System.out.println("Pulse rate: " + litr4.next().pulse_rate + " bpm");
                litr4.previous();
                System.out.println("Breathing rate: " + litr4.next().breathing_rate + " breaths/min");
                litr4.previous();
                System.out.print("Blood pressure: " + litr4.next().blood_pressure_systolic);
                litr4.previous();
                System.out.println("/" + litr4.next().blood_pressure_diastolic);
            }

            conn.close();
        } catch (Exception SQLException) {
            System.out.println(SQLException);
        }
    }
}