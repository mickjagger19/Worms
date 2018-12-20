package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{

//        System.load("/Library/Java/javafx-sdk-11.0.1/lib/libglass.dylib");
//        System.load("/Library/Java/javafx-sdk-12/lib/libglass.dylib");
//        System.load("/Library/Java/javafx-sdk-12/lib/libglass.dylib");
//        System.load("/Library/Java/javafx-sdk-12/lib/libglass.dylib");
//        System.load("/Library/Java/javafx-sdk-12/lib/libglass.dylib");

        Parent root = FXMLLoader.load(getClass().getResource("/client/view/Mainform.fxml"));
        primaryStage.setTitle("Worms");
        primaryStage.setScene(new Scene(root, 400, 700));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }



}
