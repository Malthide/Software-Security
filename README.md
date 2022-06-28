# Software-Security

This is the final project for TTU CS 4331, Software Security Class. 
This project has a few dependencies to run.

 
Preliminary Steps:
  1. Install JavaFX
      https://gluonhq.com/products/javafx/
      <i>A supplementary guide for setting up JavaFX:</i>
      https://www.youtube.com/watch?v=Ope4icw6bVk&ab_channel=BroCode
      
  2. Install a database manager, we have been using Oracle Database 21C 
      https://www.oracle.com/in/database/technologies/xe-downloads.html
      
      Make sure to run the oracle database manager setup as an administrator on Windows.
      
      <i>A supplementary guide for setting up Oracle Database Manager</i>
       https://www.oracle.com/in/database/technologies/appdev/xe/quickstart.html
       
      - When prompted to enter a password, this can be any password, you will use
      this in step 1 below.
   
        

--------------------------------------------------------------------------------
Steps to Run
  1. In the SRC-> "PopulateDatabase.java" file, enter your database password as the variable 
      for database_password. 
      EXAMPLE:
        String database_password = "your_password";
        
  2. Run the main.java class
       A GUI should pop up asking for a login
     1. Names for Staff, Doctors, and Nurses can be seen in the database can be seen in the PopulateDatabase.java file 



