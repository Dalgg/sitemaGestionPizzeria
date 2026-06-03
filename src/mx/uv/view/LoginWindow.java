package mx.uv.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mx.uv.controller.SessionController;
import mx.uv.model.Employee;

public class LoginWindow {

    public LoginWindow(Stage stage) {
        stage.setTitle("Italia Pizza — Sistema de Gestión");
        stage.setResizable(false);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color:" + UiStyles.FONDO + ";");
        root.setPrefSize(900, 600);

        HBox card = new HBox();
        card.setStyle("-fx-background-color:white;-fx-background-radius:14;" +
                "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.12),20,0,0,4);");
        card.setMaxSize(760, 460);
        card.setMinSize(760, 460);
        VBox izquierdo = new VBox(16);
        izquierdo.setAlignment(Pos.CENTER);
        izquierdo.setPrefWidth(300);
        izquierdo.setStyle("-fx-background-color:#FAFAFA;-fx-background-radius:14 0 0 14;");
        izquierdo.setPadding(new Insets(40));

        Label icono = new Label("🍕");
        icono.setStyle("-fx-background-color:" + UiStyles.ROJO + ";" +
                "-fx-background-radius:50;-fx-padding:20;-fx-font-size:36;");

        Label marca = new Label("ITALIA PIZZA");
        marca.setStyle("-fx-font-size:22;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.ROJO + ";");

        Label subtitulo = new Label("Sistema de Gestión de\nPizzería");
        subtitulo.setStyle("-fx-font-size:13;-fx-text-fill:" + UiStyles.GRIS_TEXTO + ";");
        subtitulo.setAlignment(Pos.CENTER);
        subtitulo.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        izquierdo.getChildren().addAll(icono, marca, subtitulo);

        VBox derecho = new VBox(14);
        derecho.setPadding(new Insets(50, 48, 40, 48));
        derecho.setPrefWidth(460);
        derecho.setAlignment(Pos.CENTER_LEFT);

        Label titulo = new Label("Iniciar sesión");
        titulo.setStyle("-fx-font-size:24;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK + ";");

        Label hint = new Label("Ingrese sus credenciales para acceder");
        hint.setStyle("-fx-font-size:13;-fx-text-fill:" + UiStyles.GRIS_TEXTO + ";");

        Label lblUser = UiStyles.etiquetaSeccion("USUARIO");
        TextField txtUsuario = UiStyles.campo("Ingrese su usuario");
        txtUsuario.setMaxWidth(Double.MAX_VALUE);

        Label lblPass = UiStyles.etiquetaSeccion("CONTRASEÑA");
        PasswordField txtPass = UiStyles.campoClave("Ingrese su contraseña");
        txtPass.setMaxWidth(Double.MAX_VALUE);

        Button btnEntrar = UiStyles.botonPrimario("Iniciar sesión");
        btnEntrar.setMaxWidth(Double.MAX_VALUE);
        btnEntrar.setPrefHeight(44);

        Label lblError = new Label("⚠  User o contraseña incorrectos");
        lblError.setStyle("-fx-background-color:#FEE2E2;-fx-text-fill:#DC2626;" +
                "-fx-background-radius:6;-fx-padding:10 14;-fx-font-size:12;");
        lblError.setMaxWidth(Double.MAX_VALUE);
        lblError.setVisible(false);
        lblError.setManaged(false);

        derecho.getChildren().addAll(titulo, hint,
                new VBox(4, lblUser, txtUsuario),
                new VBox(4, lblPass, txtPass),
                btnEntrar, lblError);

        card.getChildren().addAll(izquierdo, derecho);
        HBox.setHgrow(derecho, Priority.ALWAYS);

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color:" + UiStyles.FONDO + ";");
        layout.setCenter(card);
        BorderPane.setAlignment(card, Pos.CENTER);

        Runnable login = () -> {
            String user = txtUsuario.getText().trim();
            String pass = txtPass.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                lblError.setText("⚠  Complete usuario y contraseña.");
                lblError.setVisible(true); lblError.setManaged(true); return;
            }
            Employee emp = SessionController.getInstance().authenticate(user, pass);
            if (emp != null) {
                lblError.setVisible(false); lblError.setManaged(false);
                new MainWindow(stage, emp);
            } else {
                lblError.setText("⚠  User o contraseña incorrectos");
                lblError.setVisible(true); lblError.setManaged(true);
                txtPass.clear();
            }
        };

        btnEntrar.setOnAction(e -> login.run());
        txtPass.setOnAction(e -> login.run());
        txtUsuario.setOnAction(e -> txtPass.requestFocus());

        Scene scene = new Scene(layout, 900, 600);
        stage.setScene(scene);
        stage.show();
    }
}
