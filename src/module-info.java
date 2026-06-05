module sistemaGestionPizzeria {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.management;

    exports mx.uv;

    opens mx.uv to javafx.graphics, javafx.fxml;
}
