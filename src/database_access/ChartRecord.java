/* ChartRecord.java
   Created by Christopher Walker.
   Created 25 June 2022.
   Last modified 25 June 2022.
 */


package database_access;

import java.util.Calendar;


public class ChartRecord {
    int record_num;
    int patient_id;
    Calendar record_date;
    int temperature;
    int pulse_rate;
    int breathing_rate;
    int blood_pressure_systolic;
    int blood_pressure_diastolic;

    ChartRecord(int record_num, int patient_id, Calendar record_date, int temperature, int pulse_rate, int breathing_rate, int blood_pressure_systolic, int blood_pressure_diastolic) {
        this.record_num = record_num;
        this.patient_id = patient_id;
        this.record_date = record_date;
        this.temperature = temperature;
        this.pulse_rate = pulse_rate;
        this.breathing_rate = breathing_rate;
        this.blood_pressure_systolic = blood_pressure_systolic;
        this.blood_pressure_diastolic = blood_pressure_diastolic;
    }
}