/* UserInfo.java
   Create by Christopher Walker.
   Created 20 June 2022.
   Last modified 20 June 2022.
   This file is an addition to the database_access package, most of which is defined in the file
   DatabaseAccess.java. The class UserInfo defines an entity meant to be returned by the method
   pull_user_info. This entity could be used elsewhere in the program, but its main purpose is to be a
   collection of information from the database about a user's account.
 */


package database_access;


/* UserInfo: An entity class containing an id_num, username, first_name, last_name, and authorization_level.
        Authorization_level is an integer equal to 0 for staff, 1 for nurse, or 2 for doctor. Used by the
        method DatabaseAccess.pull_user_info to return account information about a given user.
 */
public class UserInfo {
    public int id_num;
    public String username;
    public String first_name;
    public String last_name;
    public int authorization_level;

    UserInfo(int id_num, String username, String first_name, String last_name, int authorization_level) {
        this.id_num = id_num;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.authorization_level = authorization_level;
    }
}