/* ChartRecord.java
   Created by Christopher Walker.
   Created 25 June 2022.
   Last modified 26 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. The class ChartRecord is used by the class PatientChart, defined in PatientChart.java.
 */


package database_access;

import java.util.Calendar;


/* ChartRecord: An entity class containing a record_num, patient_id, record_date, and health information about
        the patient. Especially used in the class PatientChart.
 */
public class ChartRecord {
    public long record_num;
    public int patient_id;
    public Calendar record_date;
    public double temperature;
    public int pulse_rate;
    public int breathing_rate;
    public int blood_pressure_systolic;
    public int blood_pressure_diastolic;
    public int doctor_visited;  //doctor's id number

    ChartRecord(long record_num, int patient_id, Calendar record_date, double temperature, int pulse_rate, int breathing_rate, int blood_pressure_systolic, int blood_pressure_diastolic, int doctor_visited) {
        this.record_num = record_num;
        this.patient_id = patient_id;
        this.record_date = record_date;
        this.temperature = temperature;
        this.pulse_rate = pulse_rate;
        this.breathing_rate = breathing_rate;
        this.blood_pressure_systolic = blood_pressure_systolic;
        this.blood_pressure_diastolic = blood_pressure_diastolic;
        this.doctor_visited = doctor_visited;
    }
}