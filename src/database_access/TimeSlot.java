/* TimeSlot.java
   Create by Christopher Walker.
   Created 23 June 2022.
   Last modified 23 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. Originally part of the file DoctorSchedule.java, I separated the code into a new file
   to make it easier to access.
 */


package database_access;

import java.util.Calendar;


/* TimeSlot: An entity class containing two Calendar objects, start_time and end_time. Especially used in the
        class DoctorSchedule.
 */
public class TimeSlot {
    public Calendar start_time;
    public Calendar end_time;


    TimeSlot(Calendar start_time, Calendar end_time) {
        this.start_time = start_time;
        this.end_time = end_time;
    }
}