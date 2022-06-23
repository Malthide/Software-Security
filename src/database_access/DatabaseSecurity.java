/* DatabaseSecurity.java
   Created by Christopher Walker.
   Created 16 June 2022.
   Last modified 22 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. This class, called DatabaseSecurity, provides methods for use when writing to and
   reading from the database, including encryption and decryption methods and a message digest (hashing)
   method. It also includes a method for modifying strings for SQL queries in an attempt to prevent SQL
   injection attacks. The class DatabaseSecurity should rarely be called outside the file
   DatabaseAccess.java.
 */


package database_access;

import java.security.MessageDigest;
import java.util.ArrayList;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;


public class DatabaseSecurity {
    //This is the 128-bit secret key that will be used in encrypt and decrypt.
    static private final byte[] key = {68, 56, 30, 85, 92, -72, 61, 58, 60, 114, 122, -30, -23, -113, 79, 92};


    /* encrypt: Takes a plaintext string and its destination block_size (which must be a multiple of 16).
            Passes the string through the AES encryption algorithm defined in javax.crypto using the 128-bit
            secret key "key" defined above. Returns the encrypted string as a byte array of size block_size.
            Based on a program provided by Michael Shin, Ph.D.
     */
    static public byte[] encrypt(String in_str, int block_size) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKey sk = new SecretKeySpec(key, 0, key.length, "AES");

            byte[] plaintext_gap = in_str.getBytes();
            byte[] plaintext = new byte[block_size];
            for (int i = 0; i < plaintext_gap.length; i++)
                plaintext[i] = plaintext_gap[i];

            c.init(Cipher.ENCRYPT_MODE, sk);
            return c.doFinal(plaintext);
        } catch (Exception e) {
            System.out.println(e);
            byte[] error_code = {0, 1};
            return error_code;
        }
    }


    /* decrypt: Takes an encrypted byte array and passes it through the AES decryption algorithm defined in
            javax.crypto using the 128-bit secret key "key" defined above. Returns the decrypted array as a
            plaintext string. Based on a program provided by Michael Shin, Ph.D.
     */
    static public String decrypt(byte[] ciphertext) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKey sk = new SecretKeySpec(key, 0, key.length, "AES");

            c.init(Cipher.DECRYPT_MODE, sk);
            byte[] plaintext = c.doFinal(ciphertext);
            String plain_str = new String(plaintext);
            return plain_str;
        } catch (Exception e) {
            System.out.println(e);
            return "Decryption error";
        }
    }


    /* hashing: Takes a string and passes it through the SHA-256 algorithm defined in
            java.security.MessageDigest. Returns the hash output as a byte array. Based on a function
            provided by Michael Shin, Ph.D.
     */
    static public byte[] hashing(String p) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] pass = p.getBytes();
        md.update(pass);
        byte[] mdbytes = md.digest();
        return mdbytes;
    }


    /* check_for_illegal_SQL: Takes a string and determines if it contains any illegal SQL characters. Returns
            0 if the string is safe or returns 1 if it is not safe.
     */
    /*
    static public int check_for_illegal_SQL(byte[] s) {
        for (int i = 0; i < s.length; i++) {
            if (s[i] == '\'') {
                return 1;
            }
        }
        return 0;
    }*/

    /* indicate_illegal_SQL: Takes a string and finds every instance of illegal SQL characters. Returns an
            integer array containing the indexes of those illegal characters in the string.
     */
    /*
    static public int[] indicate_illegal_SQL(byte[] s) {
        ArrayList<Integer> illegal_positions = new ArrayList<Integer>();

        for (int i = 0; i < s.length; i++) {
            if (s[i] == '\'') {
                Integer i_ob = Integer.valueOf(i);
                illegal_positions.add(i_ob);
            }
        }

        //This line of code comes from geeksforgeeks.org
        int[] ret_array = illegal_positions.stream().mapToInt(i -> i).toArray();
        return ret_array;
    }*/


    /* replace_illegal_SQL: Replaces suspicious characters with underscores to help prevent SQL injections.
            Takes a string and returns a new safe string.
     */
    static public String replace_illegal_SQL(String s) {
        String new_s = s;

        //new_s = new_s.replace(';', '_');
        //new_s = new_s.replace(' ', '_');
        new_s = new_s.replace('\'', '_');
        // new_s = new_s.replace('*', '_');
        //new_s = new_s.replace('=', '_');

        return new_s;
    }


    /* replace_illegal_SQL2: Replaces suspicious characters with underscores to help prevent SQL injections.
            Takes a string and returns a new safe string.
     */
    /*
    static public byte[] replace_illegal_SQL2(byte[] s) {
        byte[] new_s = s;

        for (int i = 0; i < new_s.length; i++) {
            if (new_s[i] == '\'') {
                new_s[i] = '_';
            }
        }

        return new_s;
    }*/


    /* SQLarray_to_int: Takes an integer array transforms it into a long integer in which each bit equals 1 if
            its index is found in the array. For example, and array containing {0, 1, 7, 10} would be transformed
            into the integer 2^10+2^7+2^1+2^0. In base 10 this is 1155, or in binary, 0000 0100 1000 0011.
     */
    /*
    static public long SQLarray_to_int(int[] i_array) {
        long ret_int = 0;

        for (int i = 0; i < i_array.length; i++) {
            ret_int += Math.pow(2, i_array[i]);
        }

        return ret_int;
    }*/

/*
    static public byte[] put_apostrophes_back_in(byte[] s, long illegal_index) {
        int counter = 0;
        byte[] new_s = s;

        for (int i = new_s.length - 1; i >= 0; i--) {
            if (illegal_index / (Math.pow(2, i)) == 1) {
                new_s[counter] = '\'';
            }
            illegal_index -= Math.pow(2, i);
            counter++;
        }

        return new_s;
    }*/


    /* byte_array_to_hex_string: Takes a byte array and turns each character into a two-character hexadecimal
            representation. Returns a string containing the hexadecimal values. The new string's length is
            twice as long as the original byte array's length.
     */
    static public String byte_array_to_hex_string(byte[] s) {
        int temp_int;
        String temp_str;
        String new_s;

        temp_int = (int) s[0];
        new_s = Integer.toHexString(temp_int);
        if (new_s.length() == 1)
            new_s = "0" + new_s;
        if (new_s.length() == 8)
            new_s = new_s.substring(6);

        if (s.length > 1) {
            for (int i = 1; i < s.length; i++) {
                temp_int = (int) s[i];
                temp_str = Integer.toHexString(temp_int);
                if (temp_str.length() == 1)
                    temp_str = "0" + temp_str;
                if (temp_str.length() == 8)
                    temp_str = temp_str.substring(6);
                new_s = new_s + temp_str;
            }
        }

        return new_s;
    }


    /* hex_string_to_byte_array: Takes a string containing two-digit hexadecimal numbers. Converts each pair
            of hexadecimal digits into a byte. Returns an array of those bytes. The new byte array is half
            as long as the original string. This is an inverse function to the above method
            byte_array_to_hex_string.
     */
    static public byte[] hex_string_to_byte_array(String s) {
        byte[] new_s = new byte[s.length() / 2];
        int index;
        int temp_int;

        //This loop based on code from geeksforgeeks.org
        for (int i = 0; i < new_s.length; i++) {
            index = i * 2;
            temp_int = Integer.parseInt(s.substring(index, index + 2), 16);
            new_s[i] = (byte) temp_int;
        }

        return new_s;
    }
}