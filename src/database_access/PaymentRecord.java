/* PaymentRecord.java
   Create by Christopher Walker.
   Created 26 June 2022.
   Last modified 27 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. PaymentRecord is an entity class that can be used to record information about a payment
   transaction.
 */


package database_access;

import java.util.Calendar;


/* PaymentRecord: An entity class for storing information about a payment bill and transaction. Primarily used
        in the class Payments, defined in the file Payments.java. Note that paid_date could be an empty
        Calendar object if payment has not yet been received, so paid_check should be verified before pulling
        paid_date and payment_type for use.
 */
public class PaymentRecord {
    public long reference_num;
    int patient_id;
    public double amount;
    public Calendar generated_date;
    public int paid_check;     //contains 1 if paid or 0 if not paid
    public Calendar paid_date;
    public int payment_type;   //contains 0 for cash, 1 for credit card, or 2 for debit card

    public PaymentRecord(long reference_num, int patient_id, double amount, Calendar generated_date, int paid_check, Calendar paid_date, int payment_type) {
        this.reference_num = reference_num;
        this.patient_id = patient_id;
        this.amount = amount;
        this.generated_date = generated_date;
        this.paid_check = paid_check;
        this.paid_date = paid_date;
        this.payment_type = payment_type;
    }
}