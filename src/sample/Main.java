package sample;

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

public class Main extends Application {

    public void run(){
        launch();
    }

    Stage primaryStage;
    public void start(Stage stage) throws FileNotFoundException {
        primaryStage = stage;
        changeStage(stage, 0);
    }

    public void changeStage(Stage stage, int newStage) throws FileNotFoundException{
        if(newStage == 0){
            stage.setTitle("Health-Care System");
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(25, 25, 25, 25));
            grid.setVgap(18);
            grid.setHgap(10);


////////////////////////////////////////////////////////////////////////CREATE TEXTFIELD FOR NAME
            Label nameLabel = new Label("Name:");
            GridPane.setConstraints(nameLabel, 14, 4);
            TextField nameText = new TextField();
            nameText.setMinSize(100,45);
            GridPane.setConstraints(nameText,15,4);

////////////////////////////////////////////////////////////////////////CREATE TEXTFIELD FOR ID
            Label IDLabel = new Label("ID:");
            GridPane.setConstraints(IDLabel, 14, 6);
            TextField IDText = new TextField();
            IDText.setMinSize(100,45);
            GridPane.setConstraints(IDText,15,6);

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
                    logInButton(nameTemp, IDTemp);

                }
            });

/////////////////////////////////////////////////////////////////////////////////
            Label logIn = new Label("Enter Log-In");
            GridPane.setConstraints(logIn, 15, 2);

            grid.getChildren().addAll(logInButton,logIn, nameText, IDText,nameLabel,IDLabel);
            Scene scene = new Scene(grid, 500, 500);
            stage.setScene(scene);
            stage.show();

        }//END OF NEW STAGE = 0

    }//END OF CHANGE STAGE FUNCTION

    ///////////////////////////////////////////////////////////////FUNCTION FOR LOGGING IN
    public void logInButton(String nameTemp, String IDTemp){
        System.out.println(nameTemp+ " "+IDTemp);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
