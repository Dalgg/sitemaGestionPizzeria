module sistemaGestionPizzeria {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    exports mx.uv;

    opens mx.uv to javafx.graphics, javafx.fxml;
}
