package mx.uv.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mx.uv.controller.SessionController;
import mx.uv.model.Employee;
import mx.uv.model.Role;

public class MainWindow {

    private final Stage stage;
    private final Employee empleado;
    private final BorderPane root = new BorderPane();
    private final StackPane contenido = new StackPane();

    private Button btnActivo;

    public MainWindow(Stage stage, Employee empleado) {
        this.stage = stage;
        this.empleado = empleado;

        stage.setTitle("Italia Pizza — Sistema de Gestión");
        stage.setMinWidth(1000);
        stage.setMinHeight(650);

        root.setStyle("-fx-background-color:" + UiStyles.FONDO + ";");
        root.setTop(construirCabecera());
        root.setCenter(construirCuerpo());

        mostrar(panelBienvenida());

        Scene scene = new Scene(root, 1500, 1220);
        stage.setScene(scene);
        stage.show();
    }

    private VBox construirCabecera() {
        HBox header = UiStyles.header(
                empleado.getFirstName() + " " + empleado.getLastName(),
                empleado.getRole().toString(),
                this::logout,
                stage);

        HBox nav = new HBox(0);
        nav.setStyle("-fx-background-color:white;-fx-border-color:" + UiStyles.GRIS_BORDE +
                ";-fx-border-width:0 0 1 0;-fx-padding:0 16;");
        nav.setAlignment(Pos.CENTER_LEFT);

        if (empleado.getRole() == Role.ADMINISTRADOR) {
            nav.getChildren().add(tabBtn("⊞  Administración", () -> mostrar(new UserPanel(stage))));
            nav.getChildren().add(tabBtn("◉  Productos", () -> mostrar(new ProductPanel(stage))));
            nav.getChildren().add(tabBtn("✅  Inventario", () -> mostrar(new InventoryPanel(stage))));
        }
        nav.getChildren().add(tabBtn("☰  Pedidos", () -> mostrar(new OrderListPanel(stage, this::mostrar))));
        nav.getChildren().add(tabBtn("?  Ayuda", () -> mostrar(new AboutPanel(stage))));

        VBox top = new VBox(header, nav);
        return top;
    }

    private Button tabBtn(String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.setStyle(estiloTab(false));
        btn.setOnAction(e -> {
            if (btnActivo != null) btnActivo.setStyle(estiloTab(false));
            btn.setStyle(estiloTab(true));
            btnActivo = btn;
            accion.run();
        });
        return btn;
    }

    private String estiloTab(boolean activo) {
        return "-fx-background-color:transparent;-fx-font-size:13;" +
                "-fx-cursor:hand;-fx-padding:12 16;" +
                (activo
                        ? "-fx-text-fill:" + UiStyles.ROJO + ";-fx-font-weight:bold;" +
                          "-fx-border-color:transparent transparent " + UiStyles.ROJO + " transparent;" +
                          "-fx-border-width:0 0 2 0;"
                        : "-fx-text-fill:" + UiStyles.GRIS_TEXTO + ";-fx-border-color:transparent;");
    }

    private ScrollPane construirCuerpo() {
        contenido.setPadding(new Insets(24));
        contenido.setStyle("-fx-background-color:" + UiStyles.FONDO + ";");
        ScrollPane sp = new ScrollPane(contenido);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + UiStyles.FONDO + ";-fx-background:transparent;");
        return sp;
    }

    public void mostrar(javafx.scene.Node panel) {
        contenido.getChildren().setAll(panel);
        StackPane.setAlignment(panel, Pos.TOP_LEFT);
    }

    private VBox panelBienvenida() {
        VBox box = new VBox(32);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(32));
        box.setStyle(UiStyles.CARD + "-fx-max-width:900;");

        Label titulo = new Label("¡Bienvenido al sistema!");
        titulo.setStyle("-fx-font-size:24;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK + ";");

        HBox cards = new HBox(16);
        cards.setAlignment(Pos.CENTER);

        if (empleado.getRole() == Role.ADMINISTRADOR) {
            cards.getChildren().add(cardAcceso("👥", "Usuarios",   "Gestionar clientes y\nempleados",
                    () -> mostrar(new UserPanel(stage))));
            cards.getChildren().add(cardAcceso("🍕", "Productos",  "Administrar productos e\ninsumos",
                    () -> mostrar(new ProductPanel(stage))));
        }
        cards.getChildren().add(cardAcceso("📋", "Pedidos",        "Crear y administrar\npedidos",
                () -> mostrar(new OrderListPanel(stage, this::mostrar))));
        if (empleado.getRole() == Role.ADMINISTRADOR) {
            cards.getChildren().add(cardAcceso("✅", "Validación de Inventory", "Validar cantidades de\ninventario",
                    () -> mostrar(new InventoryPanel(stage))));
        }

        box.getChildren().addAll(titulo, cards);
        return box;
    }

    private VBox cardAcceso(String icono, String titulo, String desc, Runnable accion) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setPrefHeight(180);
        card.setStyle(UiStyles.CARD + "-fx-padding:24;-fx-cursor:hand;");
        card.setOnMouseEntered(e -> card.setStyle(
                UiStyles.CARD + "-fx-padding:24;-fx-cursor:hand;" +
                "-fx-border-color:" + UiStyles.ROJO + ";-fx-effect:dropshadow(gaussian,rgba(190,30,45,0.15),10,0,0,2);"));
        card.setOnMouseExited(e -> card.setStyle(UiStyles.CARD + "-fx-padding:24;-fx-cursor:hand;"));
        card.setOnMouseClicked(e -> accion.run());

        Label ico = new Label(icono);
        ico.setStyle("-fx-font-size:32;-fx-text-fill:" + UiStyles.ROJO + ";");

        Label t = new Label(titulo);
        t.setStyle("-fx-font-size:15;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK + ";");

        Label d = new Label(desc);
        d.setStyle("-fx-font-size:12;-fx-text-fill:" + UiStyles.GRIS_TEXTO + ";");
        d.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        d.setAlignment(Pos.CENTER);
        d.setWrapText(true);

        card.getChildren().addAll(ico, t, d);
        return card;
    }

    private void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea cerrar sesión?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText(null);
        alert.initOwner(stage);
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                SessionController.getInstance().logout();
                new LoginWindow(stage);
            }
        });
    }

    private void mostrarAcercaDe() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Acerca de");
        a.setHeaderText("Italia Pizza — Sistema de Gestión");
        a.setContentText(
                "Proyecto Final – Bases de Datos para el Desarrollo de Software\n\n" +
                "Ingeniería en Sistemas Computacionales\n" +
                "Universidad Veracruzana\n\n" +
                "Versión 1.0.2 – 2026");
        a.initOwner(stage);
        a.showAndWait();
    }
}
