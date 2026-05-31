module org.example {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example to javafx.fxml;
    opens org.example.controlador to javafx.fxml;
    opens org.example.modelo to javafx.fxml;
    opens org.example.persistencia to javafx.fxml;

    exports org.example;
    exports org.example.controlador;
    exports org.example.modelo;
    exports org.example.persistencia;
}