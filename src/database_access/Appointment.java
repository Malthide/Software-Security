/* Appointment.java
   Created by Christopher Walker.
   Created 25 June 2022.
   Last modified 25 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. It is mainly used by the class ApptSchedule, defined in the file ApptSchedule.java.
 */


package database_access;

import java.util.Calendar;


/* Appointment: An entity class containing the appt_id, patient_id, doctor_id, and arrival status (no_show). It
        also contains a Calendar object date_time. Especially used in the class ApptSchedule.
 */
public class Appointment {
    public int appt_id;     //unique identifier for each appointment
    public int patient_id;
    public int doctor_id;
    public Calendar date_time;
    public int no_show;    //contains 0 for not arrived or 1 for arrived

    Appointment(int appt_id, int patient_id, int doctor_id, Calendar date_time, int no_show) {
        this.appt_id = appt_id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.date_time = date_time;
        this.no_show = no_show;
    }
}