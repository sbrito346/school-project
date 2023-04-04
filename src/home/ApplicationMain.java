package home;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This is the entry point of the scheduling system application.
 * It uses JAVAFX to provide UI functionality
 */
public class ApplicationMain extends Application
{
    /**
     * It sets the starting login scene for the application and shows it on stage. It displays the title according to
     * language settings
     * @param stage - stage object is passed from JavaFx framework
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationMain.class.getResource("/resources/views/logIn-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 340, 240);
        stage.setTitle(getLogInBundle().getString("logInTitle"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Returns the resource bundle for login form for lacalization
     * @return the resource bundle
     */
    public static ResourceBundle getLogInBundle() {
        return ResourceBundle.getBundle("resources/home.LogInForm", Locale.getDefault());
    }

    /**
     * This method is the entry point of execution for the program
     * @param args : program entry string arguments
     */
    public static void main(String[] args)
    {
        launch();
    }
}