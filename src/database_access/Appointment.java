/* Appointment.java
   Created by Christopher Walker.
   Created 25 June 2022.
   Last modified 25 June 2022.
 */


package database_access;

import java.util.Calendar;


public class Appointment {
    int appt_id;
    int patient_id;
    int doctor_id;
    Calendar date_time;
    int no_show;    //contains 0 for not arrived or 1 for arrived

    Appointment(int appt_id, int patient_id, int doctor_id, Calendar date_time, int no_show) {
        this.appt_id = appt_id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.date_time = date_time;
        this.no_show = no_show;
    }
}