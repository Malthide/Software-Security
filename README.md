# Software-Security

This is the final project for TTU CS 4331, Software Security Class, Group 6. 
I get that it was a little <i>sus</i> that we were asking to just make a video,
but in all honesty our project just has a lot of dependencies that take time to set up. 
I've made this as detailed as possible. Good luck. 🍀

Language: Java
Recommended IDE: Intelij IDEA 
 
Preliminary Steps:
  1. Install JavaFX
      https://gluonhq.com/products/javafx/
      
      To add JavaFX SDK in IDEA:
      1. File --> Project Strucutre --> Project Settings --> Libraries --> + Java --> select the <b>lib</b> file inside of the JavaFX file downloaded 
        --> apply, okay 
      
      2. Run --> Edit Configurations --> Application --> sample.Main --> ad this to VM Options:
      
              Linux/Mac: --module-path /path/to/javafx-sdk-15.0.1/lib --add-modules javafx.controls,javafx.fxml
          
              Windows: --module-path "\path\to\javafx-sdk-15.0.1\lib" --add-modules javafx.controls,javafx.fxml
       
       3. Change the path to the path of the <b> lib </b> file in the JavaFX file download.
       4. Apply/Okay
          
      <i>A supplementary guide for setting up JavaFX:</i>
      
      https://www.youtube.com/watch?v=Ope4icw6bVk&ab_channel=BroCode
      
  2. Install a database manager, we have been using Oracle Database 21C 
      https://www.oracle.com/in/database/technologies/xe-downloads.html
      
      Make sure to run the oracle database manager setup.exe as an administrator on Windows.
      - When prompted to enter a password, this can be any password, you will use
      this in step 1 below.
      
      After opening the project file in an IDE (we used Intelij IDEA):
      
      It is likely that you will have to determine which database driver is needed for your JDK
      1. Determine what JDK you are running by entering this into cmd: 
      
               javac -version 
           
      2. Based on your JDK version running, download the appropriate JAR file, and add it to the class path. 
      
          What are the Oracle JDBC releases Vs JDK versions?
          https://www.oracle.com/database/technologies/faq-jdbc.html
      
          Download the JAR file:
          https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html
      
        Add to class path (in IDEA):
        File --> Project Strucutre --> Project Settings --> Modules --> +ojdbc#.jar
      
      3. Delete the non-necessary ojdbc#.jar file. 

--------------------------------------------------------------------------------
Steps to Run
  1. In the SRC --> "PopulateDatabase.java" file, enter your database password as the variable 
      for database_password. 
      
      EXAMPLE:
        String database_password = "your_password";
        
        
  2. In SRC --> sample --> Main.java, change all instances of database_password to your password 
     there are instances on lines: 
     361, 401
      
  3. Run PopulateDatabase.java 
  
  4. Run the Main.java class
       A GUI should pop up asking for a login
     1. Names for Staff, Doctors, and Nurses currently in the database can be seen in the PopulateDatabase.java file
     
     To test the login, choose a name from the database, or enter an incorrect user name or password for error testing. 
     
     EXAMPLE: 
     
      Name: nurse_martinez 
      
      ID: password3 
      
 3. Make an Appointment 
    After successfully logging in enter the first and last name of a doctor and click "Enter" to make an appointment.
    
    EXAMPLE: 
    
    Doctors First Name: Amy
    
    Doctors Last Name: Stephens
    
 4. Enter the apppointment details 
      Available dates and times are shown in the bottom left of the window. Enter one of those times to make an appointment. 
      
      EXAMPLE:
      
       Patient First: Jia
      
       Patient Last: Chen
      
       Day: 7
      
       Month: 5
      
       Year: 2022
      
       Hour:8 
      
       Minute: 30
     



