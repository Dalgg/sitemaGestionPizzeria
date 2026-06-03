package mx.uv.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UiStyles {

    public static final String ROJO        = "#BE1E2D";
    public static final String ROJO_OSCURO = "#9B1826";
    public static final String ROJO_HOVER  = "#A01525";
    public static final String FONDO       = "#F3F4F6";
    public static final String BLANCO      = "#FFFFFF";
    public static final String GRIS_BORDE  = "#E5E7EB";
    public static final String GRIS_TEXTO  = "#6B7280";
    public static final String TEXTO_DARK  = "#111827";
    public static final String VERDE       = "#16A34A";
    public static final String GRIS_BADGE  = "#9CA3AF";

    public static final String BTN_PRIMARIO =
            "-fx-background-color:" + ROJO + ";" +
            "-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:13;" +
            "-fx-background-radius:6;-fx-cursor:hand;-fx-padding:8 18;";

    public static final String BTN_PRIMARIO_HOVER =
            "-fx-background-color:" + ROJO_HOVER + ";" +
            "-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:13;" +
            "-fx-background-radius:6;-fx-cursor:hand;-fx-padding:8 18;";

    public static final String BTN_SECUNDARIO =
            "-fx-background-color:white;-fx-text-fill:" + TEXTO_DARK + ";" +
            "-fx-font-size:13;-fx-background-radius:6;-fx-cursor:hand;" +
            "-fx-border-color:" + GRIS_BORDE + ";-fx-border-radius:6;-fx-padding:8 18;";

    public static final String BTN_ICONO =
            "-fx-background-color:transparent;-fx-cursor:hand;-fx-padding:4 8;-fx-font-size:14;";

    public static final String BTN_ICONO_ROJO =
            "-fx-background-color:transparent;-fx-text-fill:" + ROJO + ";-fx-cursor:hand;-fx-padding:4 8;-fx-font-size:14;";

    public static final String CAMPO =
            "-fx-background-color:white;-fx-border-color:" + GRIS_BORDE + ";" +
            "-fx-border-radius:6;-fx-background-radius:6;" +
            "-fx-padding:8 12;-fx-font-size:13;";

    public static final String CAMPO_FOCUS =
            "-fx-background-color:white;-fx-border-color:" + ROJO + ";" +
            "-fx-border-radius:6;-fx-background-radius:6;" +
            "-fx-padding:8 12;-fx-font-size:13;";

    public static final String TABLA =
            "-fx-background-color:white;-fx-border-color:" + GRIS_BORDE + ";" +
            "-fx-border-radius:8;-fx-background-radius:8;";

    public static final String CARD =
            "-fx-background-color:white;" +
            "-fx-border-color:" + GRIS_BORDE + ";" +
            "-fx-border-radius:10;-fx-background-radius:10;";

    public static Button botonPrimario(String texto) {
        Button btn = new Button(texto);
        btn.setStyle(BTN_PRIMARIO);
        btn.setOnMouseEntered(e -> btn.setStyle(BTN_PRIMARIO_HOVER));
        btn.setOnMouseExited(e  -> btn.setStyle(BTN_PRIMARIO));
        return btn;
    }

    public static Button botonSecundario(String texto) {
        Button btn = new Button(texto);
        btn.setStyle(BTN_SECUNDARIO);
        return btn;
    }

    public static Button botonIcono(String icono) {
        Button btn = new Button(icono);
        btn.setStyle(BTN_ICONO);
        return btn;
    }

    public static Button botonIconoRojo(String icono) {
        Button btn = new Button(icono);
        btn.setStyle(BTN_ICONO_ROJO);
        return btn;
    }

    public static TextField campo(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setStyle(CAMPO);
        tf.focusedProperty().addListener((o, ov, nv) ->
                tf.setStyle(nv ? CAMPO_FOCUS : CAMPO));
        return tf;
    }

    public static PasswordField campoClave(String placeholder) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(placeholder);
        pf.setStyle(CAMPO);
        pf.focusedProperty().addListener((o, ov, nv) ->
                pf.setStyle(nv ? CAMPO_FOCUS : CAMPO));
        return pf;
    }

    public static Label etiqueta(String texto) {
        Label lbl = new Label(texto);
        lbl.setStyle("-fx-font-size:12;-fx-font-weight:bold;-fx-text-fill:" + TEXTO_DARK + ";");
        return lbl;
    }

    public static Label etiquetaSeccion(String texto) {
        Label lbl = new Label(texto);
        lbl.setStyle("-fx-font-size:11;-fx-font-weight:bold;-fx-text-fill:" + GRIS_TEXTO + ";");
        return lbl;
    }

    public static Label badgeActivo(boolean activo) {
        Label badge = new Label(activo ? "Activo" : "Inactivo");
        badge.setStyle("-fx-background-color:" + (activo ? VERDE : GRIS_BADGE) + ";" +
                "-fx-text-fill:white;-fx-font-size:11;-fx-font-weight:bold;" +
                "-fx-background-radius:20;-fx-padding:3 10;");
        return badge;
    }

    public static Label badgeEstado(String estado) {
        String color = switch (estado.toLowerCase()) {
            case "entregado"  -> VERDE;
            case "cancelado"  -> "#EF4444";
            default           -> "#F59E0B";
        };
        Label badge = new Label(estado);
        badge.setStyle("-fx-background-color:" + color + ";" +
                "-fx-text-fill:white;-fx-font-size:11;-fx-font-weight:bold;" +
                "-fx-background-radius:20;-fx-padding:3 10;");
        return badge;
    }

    public static Separator separador() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:" + GRIS_BORDE + ";");
        return sep;
    }

    public static HBox header(String nombreEmpleado, String rol, Runnable onLogout,
                              javafx.stage.Stage ownerStage) {
        HBox bar = new HBox();
        bar.setStyle("-fx-background-color:" + ROJO + ";-fx-padding:0 24;");
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPrefHeight(56);

        Label logo = new Label("IP");
        logo.setStyle("-fx-background-color:white;-fx-text-fill:" + ROJO + ";" +
                "-fx-font-weight:bold;-fx-font-size:14;-fx-background-radius:6;" +
                "-fx-padding:6 10;");

        Label titulo = new Label("  Italia Pizza — Sistema de Gestión");
        titulo.setStyle("-fx-text-fill:white;-fx-font-size:16;-fx-font-weight:bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox userInfo = new VBox();
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        Label lblNombre = new Label(nombreEmpleado);
        lblNombre.setStyle("-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:13;");
        Label lblRol = new Label(rol);
        lblRol.setStyle("-fx-text-fill:#FECACA;-fx-font-size:11;");
        userInfo.getChildren().addAll(lblNombre, lblRol);

        Button btnLogout = new Button("⇥");
        btnLogout.setStyle("-fx-background-color:transparent;-fx-text-fill:white;" +
                "-fx-font-size:18;-fx-cursor:hand;-fx-padding:4 12;");
        btnLogout.setOnAction(e -> onLogout.run());
        btnLogout.setTooltip(new Tooltip("Cerrar sesión"));

        bar.getChildren().addAll(logo, titulo, spacer, userInfo, btnLogout);
        return bar;
    }

    public static HBox header(String nombreEmpleado, String rol, Runnable onLogout) {
        return header(nombreEmpleado, rol, onLogout, null);
    }

    public static HBox footer(String version) {
        HBox foot = new HBox();
        foot.setStyle("-fx-background-color:" + FONDO + ";-fx-border-color:" + GRIS_BORDE +
                ";-fx-border-width:1 0 0 0;-fx-padding:8 24;");
        foot.setAlignment(Pos.CENTER_LEFT);

        Label izq = new Label("SISTEMA " + version);
        izq.setStyle("-fx-font-size:11;-fx-text-fill:" + GRIS_TEXTO + ";-fx-font-weight:bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label der = new Label("© 2026 Todos los derechos reservados.");
        der.setStyle("-fx-font-size:11;-fx-text-fill:" + GRIS_TEXTO + ";");

        foot.getChildren().addAll(izq, spacer, der);
        return foot;
    }

    public static HBox tituloPagina(String texto) {
        Region borde = new Region();
        borde.setStyle("-fx-background-color:" + ROJO + ";");
        borde.setPrefWidth(4);
        borde.setPrefHeight(30);

        Label lbl = new Label("  " + texto);
        lbl.setStyle("-fx-font-size:20;-fx-font-weight:bold;-fx-text-fill:" + TEXTO_DARK + ";");

        HBox box = new HBox(borde, lbl);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}
