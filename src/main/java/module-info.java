module com.example.joj2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.web;
    requires java.sql;


    opens com.example.joj2 to javafx.fxml;
    exports com.example.joj2;
}