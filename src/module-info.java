module com.example.bebetaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens home to javafx.fxml;
    exports home;
    exports home.customer;
    exports home.appointments;
    exports home.reports;
    opens home.customer to javafx.fxml;
    opens home.appointments to javafx.fxml;
    opens home.reports to javafx.fxml;
}