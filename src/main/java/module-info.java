module com.example.interfazftp_v2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.net;
    requires java.logging;


    opens com.example.interfazftp_v2 to javafx.fxml;
    exports com.example.interfazftp_v2;
}