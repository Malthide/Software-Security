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
            Label logInLabel = new Label("LOG IN SUCCESSFUL:");
            GridPane.setConstraints(logInLabel, 10, 3);

            Label nameLabel = new Label("Enter Doctor's Name:");
            GridPane.setConstraints(nameLabel, 14, 4);
            TextField doctorNameText = new TextField();
            doctorNameText.setMinSize(100, 45);
            GridPane.setConstraints(doctorNameText, 16, 4);

//////////////////////////////////////////////////////////////////////////////Button
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

        }
        else if(newStage == 2){ //Enter Patient name, date and time

        }





    }//END OF CHANGE STAGE FUNCTION

    ///////////////////////////////////////////////////////////////FUNCTION FOR LOGGING IN
    public void logInButton(String nameTemp, String IDTemp) throws SQLException {
        System.out.println(nameTemp + " " + IDTemp);
        String database_address = "jdbc:oracle:thin:@localhost:1521:xe";
        String database_username = "system";
        String database_password = "YellowGreen27";
        Connection conn = DriverManager.getConnection(database_address, database_username, database_password);

        DatabaseAccess.verify_user_pass(conn,nameTemp,IDTemp);

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

    public static void main(String[] args) {
        launch(args);
    }
}
