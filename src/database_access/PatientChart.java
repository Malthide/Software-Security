/* PatientChart.java
   Created by Christopher Walker.
   Created 25 June 2022.
   Last modified 25 June 2022.
 */


package database_access;


import java.util.ArrayList;
import java.util.Calendar;

public class PatientChart {
    public ArrayList<ChartRecord> record_list = new ArrayList<ChartRecord>();
    public int record_list_length = 0;


    /* add_record: Takes a record_num, patient_id, Calendar object record_date, temperature, pulse_rate,
            breathing_rate, systolic blood pressure, and diastolic blood pressure. Creates a new ChartRecord
            object using the given variables, then appends that object to record_list.
     */
    void add_record(int record_num, int patient_id, Calendar record_date, int temperature, int pulse_rate, int breathing_rate, int blood_pressure_systolic, int blood_pressure_diastolic) {
        ChartRecord cr = new ChartRecord(record_num, patient_id, record_date, temperature, pulse_rate, breathing_rate, blood_pressure_systolic, blood_pressure_diastolic);
        record_list.add(cr);
        record_list_length++;
    }
}
