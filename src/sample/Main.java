package sample;

import database_access.ApptSchedule;
import database_access.DatabaseAccess;
import database_access.DoctorSchedule;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;


class tempVars{
    String times;
    String dates;

}
public class Main extends Application {
    String times;
    String dates;
    int doctorID;

    public void run() {
        launch();
    }

    Stage primaryStage;

    public void start(Stage stage) throws FileNotFoundException {
        primaryStage = stage;
        changeStage(stage, 0);
    }

    public void changeStage(Stage stage, int newStage) throws FileNotFoundException {
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////LOG IN PAGE//////////////////////////////////////////////////////////////////
        if (newStage == 0) {
            stage.setTitle("Health-Care System");
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.setVgap(18);
            grid.setHgap(10);


////////////////////////////////////////////////////////////////////////CREATE TEXTFIELD FOR NAME
            Label nameLabel = new Label("Name:");
            GridPane.setConstraints(nameLabel, 14, 4);
            TextField nameText = new TextField();
            nameText.setMinSize(100, 45);
            GridPane.setConstraints(nameText, 15, 4);

////////////////////////////////////////////////////////////////////////CREATE TEXTFIELD FOR ID
            Label IDLabel = new Label("ID:");
            GridPane.setConstraints(IDLabel, 14, 6);
            PasswordField IDText = new PasswordField();
            IDText.setMinSize(100, 45);
            GridPane.setConstraints(IDText, 15, 6);

/////////////////////////////////////////////////////////////////////////LOG IN BUTTON
            Button logInButton = new Button("LOG IN");
            logInButton.setStyle("-fx-background-color: MediumSeaGreen");
            logInButton.setMinSize(60, 45);
            GridPane.setConstraints(logInButton, 15, 8);
            logInButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    String nameTemp = nameText.getText();
                    String IDTemp = IDText.getText();
                    try {
                        logInButton(nameTemp, IDTemp);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }
            });

/////////////////////////////////////////////////////////////////////////////////
            Label logIn = new Label("Enter Log-In");
            GridPane.setConstraints(logIn, 15, 2);

            grid.getChildren().addAll(logInButton, logIn, nameText, IDText, nameLabel, IDLabel);
            Scene scene = new Scene(grid, 500, 500);
            stage.setScene(scene);
            stage.show();

        }//END OF NEW STAGE = 0
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////ENTER THE DOCTORS FIRST AND LAST NAME////////////////////////////////////////////////////////////////////////////////////////////////

        else if(newStage == 1){//Display textfield to enter doctor name
            stage.setTitle("Health-Care System");
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.setVgap(18);
            grid.setHgap(10);

///////////////////////////////////////////////////////////////////////Enter Doctor's Name
            Label logInLabel = new Label("LOG IN SUCCESSFUL");
            GridPane.setConstraints(logInLabel, 10, 3);

            Label nameLabel = new Label("Enter Doctor's First Name:");
            GridPane.setConstraints(nameLabel, 10, 4);
            TextField doctorNameText = new TextField();
            doctorNameText.setMinSize(100, 45);
            GridPane.setConstraints(doctorNameText, 10, 5);

            Label nameLabel2 = new Label("Enter Doctor's Last Name:");
            GridPane.setConstraints(nameLabel2, 12, 4);
            TextField doctorNameText2 = new TextField();
            doctorNameText2.setMinSize(100, 45);
            GridPane.setConstraints(doctorNameText2, 12, 5);

//////////////////////////////////////////////////////////////////////////////Button to enter doctors name
            Button enterDoctorNameButton = new Button("ENTER");
            enterDoctorNameButton.setStyle("-fx-background-color: MediumSeaGreen");
            enterDoctorNameButton.setMinSize(60, 45);
            GridPane.setConstraints(enterDoctorNameButton, 15, 8);
            enterDoctorNameButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    String firstNameTemp = doctorNameText.getText();
                    String lastNameTemp = doctorNameText2.getText();
                    try {
                        doctorNameButton(firstNameTemp,lastNameTemp);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }
            });
/////////////////////////////////////////////////////////////////////////////Check in Patient Button
            Label checkInLabel = new Label("Check In Patient:");
            GridPane.setConstraints(checkInLabel, 15, 9);
            Button checkInButton = new Button("Check In");
            checkInButton.setStyle("-fx-background-color: MediumSeaGreen");
            checkInButton.setMinSize(60, 45);
            GridPane.setConstraints(checkInButton, 15, 10);
            checkInButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {

                    try {
                        changeStage(primaryStage, 4);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });
 /////////////////////////////////////////////////////////////////////////////Log Out Button
            Button logOutButton = new Button("LOG OUT");
            logOutButton.setStyle("-fx-background-color: MediumSeaGreen");
            logOutButton.setMinSize(60, 45);
            GridPane.setConstraints(logOutButton, 15, 12);
            logOutButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {

                    try {
                        changeStage(primaryStage, 0);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });
            //////////////////////////////////////////////////////////////////////////////////

            grid.getChildren().addAll(logInLabel,nameLabel,doctorNameText,enterDoctorNameButton,checkInLabel,checkInButton,logOutButton,nameLabel2,doctorNameText2);
            Scene scene = new Scene(grid, 700, 700);
            stage.setScene(scene);
            stage.show();

        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////STAGE 2 --> Make Appointment Use Case/////////////////////////////////////////////////////
        else if(newStage == 2){ //Enter Patient name, date and time
            stage.setTitle("Health-Care System");
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.setVgap(18);
            grid.setHgap(12);

            Label nameDateTimeLabel = new Label("Enter Name, Date, and Time");
            GridPane.setConstraints(nameDateTimeLabel, 10, 2);
            ////////////////////////////////////////////////////////////////////////Display doctor available date and time
            Label availableDateLabel = new Label("Available Dates are: "+ dates);
            GridPane.setConstraints(availableDateLabel, 1, 15);
            Label availableTimeLabel = new Label("Available times are: "+ times);
            GridPane.setConstraints(availableTimeLabel, 1, 16);
//////////////////////////////////////////////////////////////////////////////////////Enter the patients name
            Label patientFirstNameLabel = new Label("Enter Patient First Name:");
            GridPane.setConstraints(patientFirstNameLabel, 8, 3);
            TextField patientFirstNameText = new TextField();
            patientFirstNameText.setMinSize(100, 45);
            GridPane.setConstraints(patientFirstNameText, 8, 4);

            Label patientLastNameLabel = new Label("Enter Patient Last Name:");
            GridPane.setConstraints(patientLastNameLabel, 10, 3);
            TextField patientLastNameText = new TextField();
            patientLastNameText.setMinSize(100, 45);
            GridPane.setConstraints(patientLastNameText, 10, 4);

//////////////////////////////////////////////////////////////////////////////////////
            Label selectDayLabel = new Label("Enter Day:");
            GridPane.setConstraints(selectDayLabel, 8, 5);
            TextField enterDayText = new TextField();
            enterDayText.setMinSize(100, 45);
            GridPane.setConstraints(enterDayText, 8, 6);
            ///////////////////////////////////////////////////////////////
            Label selectMonthLabel = new Label("Enter Month:");
            GridPane.setConstraints(selectMonthLabel, 10, 5);

            TextField enterMonthText = new TextField();
            enterMonthText.setMinSize(100, 45);
            GridPane.setConstraints(enterMonthText, 10, 6);
/////////////////////////////////////////////////////////////////////////////
            Label selectYearLabel = new Label("Enter Year:");
            GridPane.setConstraints(selectYearLabel, 12, 5);

            TextField enterYearText = new TextField();
            enterYearText.setMinSize(100, 45);
            GridPane.setConstraints(enterYearText, 12, 6);

///////////////////////////////////////////////////////////////////////////////////////Enter the time Hours then min.

            Label selectHourLabel = new Label("Select Hour:");
            GridPane.setConstraints(selectHourLabel, 8, 7);
            TextField enterHourText = new TextField();
            enterHourText.setMinSize(100, 45);
            GridPane.setConstraints(enterHourText, 8, 8);

            Label selectMinLabel = new Label("Select Min:");
            GridPane.setConstraints(selectMinLabel, 10, 7);
            TextField enterMinText = new TextField();
            enterMinText.setMinSize(100, 45);
            GridPane.setConstraints(enterMinText, 10, 8);

/////////////////////////////////////////////////////////////////////////////////////////////Button
            Button makeAppointmentButton = new Button("Make Appointment");
            makeAppointmentButton.setStyle("-fx-background-color: MediumSeaGreen");
            makeAppointmentButton.setMinSize(70, 45);
            GridPane.setConstraints(makeAppointmentButton, 10, 15);
            makeAppointmentButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    String firstNameTemp = patientFirstNameText.getText();
                    String lastNameTemp = patientLastNameText.getText();
                    String dayTemp = enterDayText.getText();
                    String monthTemp = enterMonthText.getText();
                    String yearTemp = enterYearText.getText();
                    String hourTemp = enterHourText.getText();
                    String minTemp = enterMinText.getText();

                    try {
                        storeAppointment(firstNameTemp,lastNameTemp,dayTemp,monthTemp,yearTemp,hourTemp, minTemp);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }
            });
            
             Button backbutton1 = new Button("Back");
            backbutton1.setStyle("-fx-background-color: MediumSeaGreen");
            backbutton1.setMinSize(70, 45);
            GridPane.setConstraints(backbutton1, 10, 16);
            backbutton1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        changeStage(primaryStage, 1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });
////////////////////////////////////////////////////////////////////////////////////////////
            grid.getChildren().addAll(nameDateTimeLabel, patientFirstNameLabel, patientFirstNameText, patientLastNameLabel,patientLastNameText, selectDayLabel, enterDayText,selectMonthLabel,enterMonthText,selectYearLabel,enterYearText,selectHourLabel,enterHourText,selectMinLabel,enterMinText,makeAppointmentButton,availableDateLabel, availableTimeLabel, backbutton1);
            Scene scene = new Scene(grid, 800, 800);
            stage.setScene(scene);
            stage.show();

        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 /////////////////////////////////////////////////////////////STAGE FOR IF THE LOG IN FAILS/////////////////////////////////////////////////////////////////////////////
        else if(newStage == 3){
            stage.setTitle("Health-Care System");
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.setVgap(18);
            grid.setHgap(10);


////////////////////////////////////////////////////////////////////////CREATE TEXTFIELD FOR NAME
            Label nameLabel = new Label("Name:");
            GridPane.setConstraints(nameLabel, 14, 4);
            TextField nameText = new TextField();
            nameText.setMinSize(100, 45);
            GridPane.setConstraints(nameText, 15, 4);

////////////////////////////////////////////////////////////////////////CREATE TEXTFIELD FOR ID
            Label IDLabel = new Label("ID:");
            GridPane.setConstraints(IDLabel, 14, 6);
            TextField IDText = new TextField();
            IDText.setMinSize(100, 45);
            GridPane.setConstraints(IDText, 15, 6);

/////////////////////////////////////////////////////////////////////////LOG IN BUTTON
            Button logInButton = new Button("LOG IN");
            logInButton.setStyle("-fx-background-color: MediumSeaGreen");
            logInButton.setMinSize(60, 45);
            GridPane.setConstraints(logInButton, 15, 8);
            logInButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    String nameTemp = nameText.getText();
                    String IDTemp = IDText.getText();
                    try {
                        logInButton(nameTemp, IDTemp);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }
            });
            Label logIn = new Label("LOG IN FAILED - PLEASE RE-ENTER LOGIN");
            GridPane.setConstraints(logIn, 15, 2);

            grid.getChildren().addAll(logInButton, logIn, nameText, IDText, nameLabel, IDLabel);
            Scene scene = new Scene(grid, 500, 500);
            stage.setScene(scene);
            stage.show();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////Check in patient use case///////////////////
        else if(newStage == 4){
            stage.setTitle("Health-Care System");
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.setVgap(18);
            grid.setHgap(10);

            Label patientNameLabel = new Label("Enter Patient Name:");
            GridPane.setConstraints(patientNameLabel, 10, 3);
            TextField patientNameText = new TextField();
            patientNameText.setMinSize(100, 45);
            GridPane.setConstraints(patientNameText, 10, 4);

            Label patientDOBLabel = new Label("Enter Patient DOB:");
            GridPane.setConstraints(patientDOBLabel, 10, 5);
            TextField patientDOBText = new TextField();
            patientDOBText.setMinSize(100, 45);
            GridPane.setConstraints(patientDOBText, 10, 6);

            Button getPatientInfoButton = new Button("Get Patient Info");
            getPatientInfoButton.setStyle("-fx-background-color: MediumSeaGreen");
            getPatientInfoButton.setMinSize(60, 45);
            GridPane.setConstraints(getPatientInfoButton, 10, 7);
            getPatientInfoButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        changeStage(primaryStage, 5);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });
            
            Button backbutton1 = new Button("Back");
            backbutton1.setStyle("-fx-background-color: MediumSeaGreen");
            backbutton1.setMinSize(60, 45);
            GridPane.setConstraints(backbutton1, 10, 8);
            backbutton1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        changeStage(primaryStage, 1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });
            grid.getChildren().addAll(patientNameLabel,patientNameText,patientDOBLabel,patientDOBText,getPatientInfoButton,backbutton1);
            Scene scene = new Scene(grid, 700, 700);
            stage.setScene(scene);
            stage.show();
        }
        ////////////////////////////////////////////////////////Once patient name and DOB are entered the system pulls up their appointment info.
        else if(newStage == 5){
            stage.setTitle("Health-Care System");
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.setVgap(18);
            grid.setHgap(10);

            Label patientNameLabel = new Label("Patient Name Goes Here");
            GridPane.setConstraints(patientNameLabel, 5, 3);


        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





    }//END OF CHANGE STAGE FUNCTION

    ///////////////////////////////////////////////////////////////FUNCTION FOR LOGGING IN
    public void logInButton(String nameTemp, String IDTemp) throws SQLException {
        System.out.println(nameTemp + " " + IDTemp);

        String database_address = "jdbc:oracle:thin:@localhost:1521:xe";
        String database_username = "system";
        String database_password = "YellowGreen27";
        Connection conn = DriverManager.getConnection(database_address, database_username, database_password);









        if((DatabaseAccess.verify_user_pass(conn,nameTemp,IDTemp)) == 1){
            try {
                changeStage(primaryStage, 1);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {

            try {
                changeStage(primaryStage, 3);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }







    }

    public void doctorNameButton(String doctorFirstNameTemp,String doctorLastNameTemp) throws SQLException {//get Doctors available date and time
        //Test DatabaseAccess.find_doctor_name()

        String database_address = "jdbc:oracle:thin:@localhost:1521:xe";
        String database_username = "system";
        String database_password = "YellowGreen27";
        Connection conn = DriverManager.getConnection(database_address, database_username, database_password);



        int tempDoctorID = DatabaseAccess.find_doctor_id(conn,doctorFirstNameTemp,doctorLastNameTemp);
        doctorID = tempDoctorID;



        dates = DatabaseAccess.pull_doctor_schedule_as_str_dates(conn, tempDoctorID);
        times = DatabaseAccess.pull_doctor_schedule_as_str_times(conn, tempDoctorID);





        try {
            changeStage(primaryStage, 2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }
    /////////////////////////////////////////////////////////////////////////////////////////////////STORE APPOINTMENT INTO THE DATABASE
    public void storeAppointment(String firstNameTemp,String lastNameTemp,String dayTemp,String monthTemp,String yearTemp, String hourTemp, String minTemp) throws SQLException {

        String database_address = "jdbc:oracle:thin:@localhost:1521:xe";
        String database_username = "system";
        String database_password = "YellowGreen27";
        Connection conn = DriverManager.getConnection(database_address, database_username, database_password);
        ApptSchedule appt_schedule = DatabaseAccess.pull_appt_schedule(conn);



        ///////////////////////////////////////////////////
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.set(Integer.parseInt(yearTemp),Integer.parseInt(monthTemp),Integer.parseInt(dayTemp),Integer.parseInt(hourTemp), Integer.parseInt(minTemp));
        /////////////////////////////////////////////////////

        int my_patient_id = DatabaseAccess.find_patient_id_from_name_only(conn,firstNameTemp,lastNameTemp);
        DatabaseAccess.add_appt_to_schedule(conn,appt_schedule,my_patient_id,doctorID,newCalendar);

         /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        try {
            changeStage(primaryStage, 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    public void checkInPatient(){
        try {
            changeStage(primaryStage, 4);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
