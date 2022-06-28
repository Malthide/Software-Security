/* TestMainForDatabase.java
   Created by Christopher Walker.
   Created 14 June 2022.
   Last modified 28 June 2022.
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
                System.out.print("/" + ((litr1.next().start_time.get(Calendar.MONTH)) + 1));
                litr1.previous();
                System.out.print("/" + litr1.next().start_time.get(Calendar.YEAR));
                litr1.previous();
                System.out.print(" " + litr1.next().start_time.get(Calendar.HOUR));
                litr1.previous();
                System.out.println(":" + litr1.next().start_time.get(Calendar.MINUTE));
                litr1.previous();
                System.out.print("Availability end (DD/MM/YYYY): " + litr1.next().end_time.get(Calendar.DATE));
                litr1.previous();
                System.out.print("/" + ((litr1.next().end_time.get(Calendar.MONTH)) + 1));
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
            calendar1.set(1982, 6, 16);
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
            calendar2.set(1982, 6, 16);
            int patient_id = DatabaseAccess.find_patient_id(conn, "Jia", "Chen", calendar2);
            System.out.println("Patient ID: " + patient_id);
            System.out.println();

            //Test DatabaseAccess.update_patient_info() for patient Jia Chen, whose info is saved above in the object p_info
            System.out.println("Test of DatabaseAccess.update_patient_info()");
            p_info.phone_number = "5555555551"; //changing the phone number to test database update
            int success_patient_info = DatabaseAccess.update_patient_info(conn, p_info);
            if (success_patient_info == 0)
                System.out.println("Database successfully updated.");
            else
                System.out.println("Database update failed.");
            System.out.println();

            //Test DatabaseAccess.pull_patient_ssn() for the same patient Jia Chen
            System.out.println("Test of DatabaseAccess.pull_patient_ssn()");
            String ssn = DatabaseAccess.pull_patient_ssn(conn, p_info);
            System.out.println("Patient SSN: " + ssn);
            System.out.println();

            //Test DatabaseAccess.update_patient_ssn() for the same patient Jia Chen
            System.out.println("Test of DatabaseAccess.update_patient_ssn()");
            String new_ssn = "223456789";
            int success_new_ssn = DatabaseAccess.update_patient_ssn(conn, p_info, new_ssn);
            if (success_new_ssn == 0)
                System.out.println("SSN update successful.");
            else
                System.out.println("SSN update failed.");
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
                System.out.print("/" + ((litr2.next().date_time.get(Calendar.MONTH)) + 1));
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

            //Test DatabaseAccess.add_appt_to_schedule()
            System.out.println("Test of DatabaseAccess.add_appt_to_schedule()");
            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(2022, 6, 8, 16, 0);
            ApptSchedule appt_schedule = DatabaseAccess.pull_appt_schedule(conn);
            int my_patient_id = DatabaseAccess.find_patient_id_from_name_only(conn, "Jia", "Chen");
            int my_doc_id = DatabaseAccess.find_doctor_id(conn, "Amy", "Stephens");
            int success_appt = DatabaseAccess.add_appt_to_schedule(conn, appt_schedule, my_patient_id, my_doc_id, calendar3);
            if (success_appt == 0)
                System.out.println("Appointment scheduled successfully.");
            else
                System.out.println("Appointment scheduling failed.");
            System.out.println();

            //Test DatabaseAccess.change_no_show_status() for appointment 2
            System.out.println("Test of DatabaseAccess.change_no_show_status()");
            int success_no_show = DatabaseAccess.change_no_show_status(conn, 2, 1);
            if (success_no_show == 0)
                System.out.println("No show status successful updated.");
            else
                System.out.println("No show status update failed.");
            System.out.println();

            //Test DatabaseAccess.pull_chart_records() for patient Jia Chen, whose info is saved above in the object p_info
            System.out.println("Test of DatabaseAccess.pull_chart_records()");
            PatientChart p_chart = DatabaseAccess.pull_chart_records(conn, p_info.id_num);
            System.out.println("p_chart length: " + p_chart.record_list_length);
            System.out.println("Patient ID: " + p_chart.patient_id);
            ListIterator<ChartRecord> litr4 = p_chart.record_list.listIterator();
            while (litr4.hasNext()) {
                System.out.println("Record number: " + litr4.next().record_num);
                litr4.previous();
                System.out.println("Patient ID: " + litr4.next().patient_id);
                litr4.previous();
                System.out.print("Record date (DD/MM/YY): " + litr4.next().record_date.get(Calendar.DATE));
                litr4.previous();
                System.out.print("/" + ((litr4.next().record_date.get(Calendar.MONTH)) + 1));
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
                litr4.previous();
                System.out.println("ID of doctor visited: " + litr4.next().doctor_visited);
            }
            System.out.println();

            //Test DatabaseAccess.add_new_chart_record() for Jia Chen, whose info is stored in the PatientInfo object p_info above
            System.out.println("Test of DatabaseAccess.add_new_chart_record()");
            Calendar today1 = Calendar.getInstance();
            ChartRecord c_record1 = new ChartRecord(817455210084L, p_info.id_num, today1, 99.1, 68, 53, 119, 79, 3852);
            int success_add_c_record = DatabaseAccess.add_new_chart_record(conn, c_record1);
            if (success_add_c_record == 0)
                System.out.println("New chart record added successfully.");
            else
                System.out.println("Chart recorded addition failed.");
            System.out.println();

            //Test DatabaseAccess.update_chart_record() for a chart record of patient Jia Chen, found in p_chart above
            System.out.println("Test of DatabaseAccess.update_chart_record()");
            ChartRecord c_record2 = litr4.previous();
            c_record2.pulse_rate = 82;
            int success_update_c_record = DatabaseAccess.update_chart_record(conn, c_record2);
            if (success_update_c_record == 0)
                System.out.println("Chart record successfully updated.");
            else
                System.out.println("Chart record update failed.");
            System.out.println();

            //Test DatabaseAccess.pull_payment_records() for patient Jia Chen, whose info is saved above in the object p_info
            System.out.println("Test of DatabaseAccess.pull_payment_records()");
            Payments payments = DatabaseAccess.pull_payment_records(conn, p_info.id_num);
            System.out.println("Patient ID: " + payments.patient_id);
            ListIterator<PaymentRecord> litr5 = payments.payments_list.listIterator();
            int paid_check;
            while (litr5.hasNext()) {
                System.out.println("Reference number: " + litr5.next().reference_num);
                litr5.previous();
                System.out.println("Amount: $" + litr5.next().amount);
                litr5.previous();
                System.out.print("Generated date (DD/MM/YYYY): " + litr5.next().generated_date.get(Calendar.DATE));
                litr5.previous();
                System.out.print("/" + ((litr5.next().generated_date.get(Calendar.MONTH)) + 1));
                litr5.previous();
                System.out.println("/" + litr5.next().generated_date.get(Calendar.YEAR));
                litr5.previous();
                paid_check = litr5.next().paid_check;
                System.out.println("Paid or unpaid: " + paid_check);
                if (paid_check == 1) {
                    litr5.previous();
                    System.out.print("Paid date (DD/MM/YYYY): " + litr5.next().paid_date.get(Calendar.DATE));
                    litr5.previous();
                    System.out.print("/" + ((litr5.next().paid_date.get(Calendar.MONTH)) + 1));
                    litr5.previous();
                    System.out.println("/" + litr5.next().paid_date.get(Calendar.YEAR));
                    litr5.previous();
                    System.out.println("Payment type ID: " + litr5.next().payment_type);
                }
            }
            System.out.println();

            //Test DatabaseAccess.add_new_payment_record() using a PaymentRecord object
            System.out.println("Test of DatabaseAccess.add_new_payment_record()");
            Calendar today2 = Calendar.getInstance();
            PaymentRecord pay_record1 = new PaymentRecord(6250916483948L, 1094273, 55.0, today2, 0, today2, -2); //note that if paid_check is 0, it doesn't matter what values are put in paid_date and payment_type
            int success_add_p_record = DatabaseAccess.add_new_payment_record(conn, pay_record1);
            if (success_add_p_record == 0)
                System.out.println("New payment record added successfully.");
            else
                System.out.println("New payment record addition failed.");
            System.out.println();

            //Test DatabaseAccess.update_payment_record() using a PaymentRecord object from the Payments object payments created above
            System.out.println("Test of DatabaseAccess.update_payment_record()");
            PaymentRecord pay_record2 = litr5.previous();
            pay_record2.paid_check = 1;
            pay_record2.paid_date.set(2022, 6, 1);
            pay_record2.payment_type = 0;
            int success_update_p_record = DatabaseAccess.update_payment_record(conn, pay_record2);
            if (success_update_p_record == 0)
                System.out.println("Payment record update successful.");
            else
                System.out.println("Payment record update failed.");
            System.out.println();

            //Test DatabaseAccess.find_drug_id()
            System.out.println("Test of DatabaseAccess.find_drug_id()");
            int drug_id = DatabaseAccess.find_drug_id(conn, "Prednisone");
            System.out.println("Drug ID for Prednisone: " + drug_id);
            System.out.println();

            //Test DatabaseAccess.add_new_prescription() for Jia Chen, using the PatientInfo object p_info from above
            System.out.println("Test of DatabaseAccess.add_new_prescription()");
            int success_prescription = DatabaseAccess.add_new_prescription(conn, p_info.id_num, 4, "50 mg", 10, "Take once daily for 10 days.", 9427);
            if (success_prescription == 0)
                System.out.println("Prescription successfully added.");
            else
                System.out.println("Prescription addition to database failed.");
            System.out.println();

            //Test DatabaseAccess.pull_doctor_schedule_as_str_dates
            System.out.println("Test of DatabaseAccess.pull_doctor_schedule_as_str_dates()");
            String doc_dates = DatabaseAccess.pull_doctor_schedule_as_str_dates(conn, 9427);
            System.out.println(doc_dates);
            System.out.println();

            //Test DatabaseAccess.pull_doctor_schedule_as_str_times
            System.out.println("Test of DatabaseAccess.pull_doctor_schedule_as_str_times()");
            String doc_times = DatabaseAccess.pull_doctor_schedule_as_str_times(conn, 9427);
            System.out.println(doc_times);
            System.out.println();

            //Test DatabaseAccess.does_patient_have_appt_today
            System.out.println("Test of DatabaseAccess.does_patient_have_appt_today()");
            int my_pat_id = DatabaseAccess.find_patient_id_from_name_only(conn, "Jia", "Chen");
            String appt_time = DatabaseAccess.does_patient_have_appt_today(conn, my_pat_id);
            System.out.println("Appointment not yet scheduled for today with error code: " + appt_time);

            Calendar today3 = Calendar.getInstance();
            int my_doctor_id = DatabaseAccess.find_doctor_id(conn, "Jose", "Gonzalez");
            ApptSchedule appt_s0 = DatabaseAccess.pull_appt_schedule(conn);
            DatabaseAccess.add_appt_to_schedule(conn, appt_s0, my_pat_id, my_doctor_id, today3); //adding an appointment to schedule for today (using today3, which contains the time and date at which it was created)
            appt_time = DatabaseAccess.does_patient_have_appt_today(conn, my_pat_id);
            System.out.println("Appointment now scheduled for today at time: " + appt_time);

            conn.close();
        } catch (Exception SQLException) {
            System.out.println(SQLException);
        }
    }
}