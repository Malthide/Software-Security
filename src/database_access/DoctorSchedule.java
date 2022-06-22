/* DoctorSchedule.java
   Create by Christopher Walker.
   Created 20 June 2022.
   Last modified 20 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. The class DoctorSchedule defines an entity meant to be returned by the method
   pull_doctor_schedule. This entity could be used elsewhere in the program, but its main purpose is to be a
   collection of information from the database about a doctor's schedule.
 */


package database_access;

import java.util.Calendar;
import java.util.ArrayList;


/* TimeSlot: An entity class containing two Calendar objects, start_time and end_time.
 */
class TimeSlot {
    Calendar start_time;
    Calendar end_time;


    TimeSlot(Calendar start_time, Calendar end_time) {
        this.start_time = start_time;
        this.end_time = end_time;
    }
}


/* DoctorSchedule: An entity class containing a doctor_id and an array list of TimeSlot objects called
        time_slot_list. Used by the method DatabaseAccess.pull_doctor_schedule to return data about a given
        doctor's availability.
 */
public class DoctorSchedule {
    int doctor_id;
    ArrayList<TimeSlot> time_slot_list = new ArrayList<TimeSlot>();


    DoctorSchedule(int doctor_id) {
        this.doctor_id = doctor_id;
    }


    /* add_time_slot: Takes two Calendar objects, start_time and end_time. Creates a new TimeSlot object using
            start_time and end_time, then appends that object to time_slot_list.
     */
    void add_time_slot(Calendar start_time, Calendar end_time) {
        TimeSlot ts = new TimeSlot(start_time, end_time);
        time_slot_list.add(ts);
    }
}