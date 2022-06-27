/* DoctorSchedule.java
   Create by Christopher Walker.
   Created 26 June 2022.
   Last modified 26 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. The class Payments defines an entity meant to be returned by the method
   pull_payment_records. This entity could be used elsewhere in the program, but its main purpose is to be a
   collection of information from the database about a transactions associated with a given patient.
 */


package database_access;


import java.util.ArrayList;
import java.util.Calendar;

public class Payments {
    public int patient_id;
    public ArrayList<PaymentRecord> payments_list = new ArrayList<PaymentRecord>();
    public int payments_list_length = 0;


    Payments(int patient_id) {
        this.patient_id = patient_id;
    }


    /* add_payment_record: Takes information about a payment transaction. Creates a new PaymentRecord object
            and appends it to payments_list.
     */
    void add_payment_record(long reference_num, int patient_id, double amount, Calendar generated_date, int paid_check, Calendar paid_date, int payment_type) {
        PaymentRecord pr = new PaymentRecord(reference_num, patient_id, amount, generated_date, paid_check, paid_date, payment_type);
        payments_list.add(pr);
        payments_list_length++;
    }
}