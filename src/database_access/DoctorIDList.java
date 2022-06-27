/* DoctorIDList.java
   Created by Christopher Walker.
   Created 25 June 2022.
   Last modified 25 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. The class DoctorIDList defines an entity meant to be returned by the method
   pull_doctor_id_list(). This entity could be used elsewhere in the program, but its main purpose is to be a
   collection of doctors' id numbers and names, as found in the database.
 */


package database_access;

import java.util.ArrayList;


public class DoctorIDList {
    public ArrayList<DoctorID> doctors_list = new ArrayList<DoctorID>();
    public int doctors_list_length = 0;


    /* add_item: Takes an id_num, a first_name, and a last_name. Creates a new DoctorID object using the given
            variables, then appends that object to doctors_list.
     */
    void add_item(int id_num, String first_name, String last_name) {
        DoctorID d = new DoctorID(id_num, first_name, last_name);
        doctors_list.add(d);
        doctors_list_length++;
    }
}