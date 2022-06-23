/* PatientInfo.java
   Create by Christopher Walker.
   Created 22 June 2022.
   Last modified 22 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. The class PatientInfo defines an entity meant to be returned by the method
   pull_patient_info. This entity could be used elsewhere in the program, but its main purpose is to be a
   collection of information from the database about a specific patient.
 */


package database_access;

import java.util.Calendar;


/* PatientInfo: An entity class containing an id_num, first_name, last_name, birthdate, address, phone_number,
        insurance_provider, and policy_num. Insurance_provider is an integer that corresponds to a given
        insurance provider. Used by the method DatabaseAccess.pull_patient_info to return personal information
        about a given patient.
 */
public class PatientInfo {
    public int id_num;
    public String first_name;
    public String last_name;
    public Calendar birthdate;
    public String street_address;
    public String city;
    public String us_state;
    public String zip_code;
    public String phone_number;
    public int insurance_provider;
    public String policy_num;

    PatientInfo(int id_num, String first_name, String last_name, Calendar birthdate, String street_address, String city, String us_state, String zip_code, String phone_number, int insurance_provider, String policy_num) {
        this.id_num = id_num;
        this.first_name = first_name;
        this.last_name = last_name;
        this.birthdate = birthdate;
        this.street_address = street_address;
        this.city = city;
        this.us_state = us_state;
        this.zip_code = zip_code;
        this.phone_number = phone_number;
        this.insurance_provider = insurance_provider;
        this.policy_num = policy_num;
    }
}