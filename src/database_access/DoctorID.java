/* DoctorID.java
   Created by Christopher Walker.
   Created 25 June 2022.
   Last modified 26 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. It is primarily used in the class DoctorIDList, defined in the file DoctorIDList.java.
 */


package database_access;


/* DoctorID: An entity class containing an id_num, first_name, and last_name. Especially used in the class
        DoctorIDList.
 */
public class DoctorID {
    public int id_num;
    public String first_name;
    public String last_name;

    public DoctorID(int id_num, String first_name, String last_name) {
        this.id_num = id_num;
        this.first_name = first_name;
        this.last_name = last_name;
    }
}