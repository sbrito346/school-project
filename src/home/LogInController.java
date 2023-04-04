package home;

import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import home.appointments.User;
import home.customer.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class manages the login form view
 * JavaFx controls are managed and modified on this class
 */
public class LogInController
{
    @FXML
    private Label userNameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Text zoneLabel;
    @FXML
    private Button signInButton;
    @FXML
    private TextField userNameInput;
    @FXML
    private PasswordField passwordInput;

    private ResourceBundle loginBundle = ApplicationMain.getLogInBundle();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static User userLoggedOn = null;

    /**
     * This method initializes the login form labels
     */
    @FXML
    public void initialize() {
        zoneLabel.setText(loginBundle.getString("timeZoneLabel") + ZoneId.systemDefault());
        userNameLabel.setText(loginBundle.getString("userNameLabel"));
        passwordLabel.setText(loginBundle.getString("passwordLabel"));
        signInButton.setText(loginBundle.getString("logInButtonText"));
    }

    /**
     * This method handles the button click when attempting to sign in
     * It will generate error dialogs based on user input credentials and system localization
     * It will also track login attempts and write to file
     * @throws IOException
     */
    @FXML
    protected void onSignIn() throws IOException
    {
        String username = userNameInput.getText().trim();
        String formPassword = passwordInput.getText().trim();

        if (username.isEmpty()) {
            ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.systemDefault());
            FileWriter fileWriter = new FileWriter("src/login_activity.txt", true);
            fileWriter.write("Login Unsuccessful - Username field was empty - Timestamp : " + now.format(formatter) + "\n");
            fileWriter.close();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle(loginBundle.getString("error"));
            errorAlert.setHeaderText(loginBundle.getString("missingUserName"));
            errorAlert.setContentText(loginBundle.getString("missingUserNameInfo"));
            errorAlert.showAndWait();
        } else if (formPassword.isEmpty()) {
            ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.systemDefault());
            FileWriter fileWriter = new FileWriter("src/login_activity.txt", true);
            fileWriter.write("Login Unsuccessful - Password field was empty - " + "username provided: " + username +   " - Timestamp : " + now.format(formatter) + "\n");
            fileWriter.close();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle(loginBundle.getString("error"));
            errorAlert.setHeaderText(loginBundle.getString("missingPassword"));
            errorAlert.setContentText(loginBundle.getString("missingPasswordInfo"));
            errorAlert.showAndWait();
        } else if (formPassword != null && !userFound(username, formPassword)) {
            ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.systemDefault());
            FileWriter fileWriter = new FileWriter("src/login_activity.txt", true);
            fileWriter.write("Login Unsuccessful - Incorrect credentials - " + "username provided: " + username + " password provided: " + formPassword +  " - Timestamp : " + now.format(formatter) + "\n");
            fileWriter.close();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle(loginBundle.getString("error"));
            errorAlert.setHeaderText(loginBundle.getString("invalidCredentials"));
            errorAlert.setContentText(loginBundle.getString("invalidCredentialsInfo"));
            errorAlert.showAndWait();
        } else {
            ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.systemDefault());
            FileWriter fileWriter = new FileWriter("src/login_activity.txt", true);
            fileWriter.write("Login Successful - " + "username provided: " + username + " password provided: " + formPassword +  " - Timestamp : " + now.format(formatter) + "\n");
            fileWriter.close();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/views/customers-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) signInButton.getScene().getWindow();
            stage.setTitle("Customers");
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Helper method to find correct user from credentials provided on login from and set the user currently logged on
     * Lambda expression used here to filter list of users by comparing the username and password
     * @param username username from form
     * @param password password from form
     * @return true if user found otherwise false
     */
    private boolean userFound(String username, String password) {
        Optional<User> user = DataManager.getInstance().users.stream().filter(u -> u.getUsername().equals(username) && u.getPassword()
            .equals(password)).findFirst();

        if (user.isPresent()) {
            userLoggedOn = user.get();
        }

        return user.isPresent();
    }
}