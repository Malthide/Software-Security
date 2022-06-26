/* PatientChart.java
   Created by Christopher Walker.
   Created 25 June 2022.
   Last modified 25 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. The class PatientChart defines an entity meant to be returned by the method
   pull_chart_records. This entity could be used elsewhere in the program, but its main purpose is to be a
   collection of health records from the database.
 */


package database_access;


import java.util.ArrayList;
import java.util.Calendar;

public class PatientChart {
    public ArrayList<ChartRecord> record_list = new ArrayList<ChartRecord>();
    public int record_list_length = 0;
    public int patient_id;


    PatientChart(int patient_id) {
        this.patient_id = patient_id;
    }


    /* add_record: Takes a record_num, patient_id, Calendar object record_date, temperature, pulse_rate,
            breathing_rate, systolic blood pressure, and diastolic blood pressure. Creates a new ChartRecord
            object using the given variables, then appends that object to record_list.
     */
    void add_record(long record_num, int patient_id, Calendar record_date, double temperature, int pulse_rate, int breathing_rate, int blood_pressure_systolic, int blood_pressure_diastolic) {
        ChartRecord cr = new ChartRecord(record_num, patient_id, record_date, temperature, pulse_rate, breathing_rate, blood_pressure_systolic, blood_pressure_diastolic);
        record_list.add(cr);
        record_list_length++;
    }
}