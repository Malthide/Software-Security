package sample;

import database_access.DatabaseAccess;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {

    public void run() {
        launch();
    }

    Stage primaryStage;

    public void start(Stage stage) throws FileNotFoundException {
        primaryStage = stage;
        changeStage(stage, 0);
    }

    public void changeStage(Stage stage, int newStage) throws FileNotFoundException {
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

/////////////////////////////////////////////////////////////////////////////////
            Label logIn = new Label("Enter Log-In");
            GridPane.setConstraints(logIn, 15, 2);

            grid.getChildren().addAll(logInButton, logIn, nameText, IDText, nameLabel, IDLabel);
            Scene scene = new Scene(grid, 500, 500);
            stage.setScene(scene);
            stage.show();

        }//END OF NEW STAGE = 0



        else if(newStage == 1){//Display textfield to enter doctor name
            stage.setTitle("Health-Care System");
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.setVgap(18);
            grid.setHgap(10);

///////////////////////////////////////////////////////////////////////Enter Doctor's Name
            Label logInLabel = new Label("LOG IN SUCCESSFUL");
            GridPane.setConstraints(logInLabel, 10, 3);

            Label nameLabel = new Label("Enter Doctor's Name:");
            GridPane.setConstraints(nameLabel, 10, 4);
            TextField doctorNameText = new TextField();
            doctorNameText.setMinSize(100, 45);
            GridPane.setConstraints(doctorNameText, 15, 5);

//////////////////////////////////////////////////////////////////////////////Button to enter doctors name
            Button enterDoctorNameButton = new Button("ENTER");
            enterDoctorNameButton.setStyle("-fx-background-color: MediumSeaGreen");
            enterDoctorNameButton.setMinSize(60, 45);
            GridPane.setConstraints(enterDoctorNameButton, 15, 8);
            enterDoctorNameButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    String nameTemp = doctorNameText.getText();

                    doctorNameButton(nameTemp);

                }
            });
            grid.getChildren().addAll(logInLabel,nameLabel,doctorNameText,enterDoctorNameButton);
            Scene scene = new Scene(grid, 700, 700);
            stage.setScene(scene);
            stage.show();

        }
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
            Label availableDateLabel = new Label("Display available dates");
            GridPane.setConstraints(availableDateLabel, 5, 3);
            Label availableTimeLabel = new Label("Display available times");
            GridPane.setConstraints(availableTimeLabel, 5, 4);
//////////////////////////////////////////////////////////////////////////////////////Enter the patients name
            Label patientNameLabel = new Label("Enter Patient Name:");
            GridPane.setConstraints(patientNameLabel, 10, 3);
            TextField patientNameText = new TextField();
            patientNameText.setMinSize(100, 45);
            GridPane.setConstraints(patientNameText, 10, 4);

//////////////////////////////////////////////////////////////////////////////////////Enter the day --> Monday,Tuesday,Wednesday,Thursday,Friday
            Label selectDateLabel = new Label("Select Date(M,T,W,TH,F):");
            GridPane.setConstraints(selectDateLabel, 10, 5);
            TextField enterDateText = new TextField();
            enterDateText.setMinSize(100, 45);
            GridPane.setConstraints(enterDateText, 10, 6);

///////////////////////////////////////////////////////////////////////////////////////Enter the time

            Label selectTimeLabel = new Label("Select Time:");
            GridPane.setConstraints(selectTimeLabel, 10, 7);
            TextField enterTimeText = new TextField();
            enterTimeText.setMinSize(100, 45);
            GridPane.setConstraints(enterTimeText, 10, 8);

/////////////////////////////////////////////////////////////////////////////////////////////Button
            Button makeAppointmentButton = new Button("Make Appointment");
            makeAppointmentButton.setStyle("-fx-background-color: MediumSeaGreen");
            makeAppointmentButton.setMinSize(70, 45);
            GridPane.setConstraints(makeAppointmentButton, 10, 9);
            makeAppointmentButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    String nameTemp = patientNameText.getText();
                    String dateTemp = enterDateText.getText();
                    String timeTemp = enterTimeText.getText();

                    storeAppointment(nameTemp,dateTemp,timeTemp);

                }
            });
////////////////////////////////////////////////////////////////////////////////////////////
            grid.getChildren().addAll(nameDateTimeLabel, patientNameLabel, patientNameText, selectDateLabel, enterDateText,selectTimeLabel,enterTimeText,makeAppointmentButton,availableDateLabel, availableTimeLabel);
            Scene scene = new Scene(grid, 800, 800);
            stage.setScene(scene);
            stage.show();

        }






    }//END OF CHANGE STAGE FUNCTION

    ///////////////////////////////////////////////////////////////FUNCTION FOR LOGGING IN
    public void logInButton(String nameTemp, String IDTemp) throws SQLException {
        System.out.println(nameTemp + " " + IDTemp);
        String database_address = "jdbc:oracle:thin:@localhost:1521:xe";
        String database_username = "system";
        String database_password = "YellowGreen27";
       // Connection conn = DriverManager.getConnection(database_address, database_username, database_password);

        //DatabaseAccess.verify_user_pass(conn,nameTemp,IDTemp);

        try {
            changeStage(primaryStage,1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void doctorNameButton(String doctorNameTemp){//get Doctors available date and time

        try {
            changeStage(primaryStage,2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////STORE APPOINTMENT INTO THE DATABASE
    public void storeAppointment(String nameTemp,String dateTemp,String timeTemp){

    }

    public static void main(String[] args) {
        launch(args);
    }
}
