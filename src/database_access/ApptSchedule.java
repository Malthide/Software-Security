/* ApptSchedule.java
   Created by Christopher Walker.
   Created 25 June 2022.
   Last modified 25 June 2022.
 */


package database_access;


import java.util.ArrayList;
import java.util.Calendar;

public class ApptSchedule {
    public ArrayList<Appointment> appt_list = new ArrayList<Appointment>();
    public int appt_list_length = 0;


    /* add_appointment: Takes an id_num, patient_id, doctor_id, and no_show (0 for not arrived or 1 for arrived).
            Also takes the date and time of the appointment as a Calendar object date_time. Creates a new
            Appointment object using the given variables, then appends that object to appt_list.
     */
    void add_appointment(int appt_id, int patient_id, int doctor_id, Calendar date_time, int no_show) {
        Appointment a = new Appointment(appt_id, patient_id, doctor_id, date_time, no_show);
        appt_list.add(a);
        appt_list_length++;
    }
}