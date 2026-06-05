package mx.uv.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mx.uv.controller.ProductController;
import mx.uv.controller.SessionController;
import mx.uv.model.Employee;
import mx.uv.model.Role;
import mx.uv.model.SaleProduct;
import mx.uv.util.CsvExporter;

import java.util.List;

public class ProductPanel extends VBox {

    private final Stage owner;
    private final ProductController ctrl = new ProductController();
    private final TableView<SaleProduct> tabla = new TableView<>();
    private final ObservableList<SaleProduct> datos = FXCollections.observableArrayList();
    private final TextField txtBuscar = UiStyles.campo("Buscar por SKU o nombre...");

    public ProductPanel(Stage owner) {
        this.owner = owner;
        setSpacing(16);
        Employee current = SessionController.getInstance().getCurrentEmployee();
        if (current == null || current.getRole() != Role.ADMINISTRADOR) {
            mostrarAccesoDenegado();
            return;
        }
        construir();
        cargar(null);
    }

    private void construir() {

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(2);
        Label ico = new Label("🍕");
        ico.setStyle("-fx-background-color:#FEE2E2;-fx-background-radius:8;-fx-padding:8;-fx-font-size:20;");
        HBox tituloH = new HBox(12, ico, new VBox(2,
                labelEstilo("Gestión de Productos", "-fx-font-size:22;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK),
                labelEstilo("Administra el catálogo de pizzas, entradas y bebidas del sistema.", "-fx-font-size:12;-fx-text-fill:" + UiStyles.GRIS_TEXTO)));
        tituloH.setAlignment(Pos.CENTER_LEFT);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnNuevo = UiStyles.botonPrimario("+ Nuevo Producto");
        btnNuevo.setOnAction(e -> nuevo());
        topRow.getChildren().addAll(tituloH, sp, btnNuevo);

        VBox card = new VBox(12);
        card.setStyle(UiStyles.CARD);
        card.setPadding(new Insets(20));

        HBox cardHeader = new HBox(16);
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        VBox cardTitulo = new VBox(2,
                labelEstilo("Catálogo de Productos", "-fx-font-size:15;-fx-font-weight:bold;"),
                labelEstilo("Visualiza y filtra los productos disponibles.", "-fx-font-size:11;-fx-text-fill:" + UiStyles.GRIS_TEXTO));

        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        txtBuscar.setPrefWidth(240);
        txtBuscar.setOnAction(e -> cargar(txtBuscar.getText().trim()));
        txtBuscar.textProperty().addListener((o, ov, nv) -> cargar(nv.trim()));
        Button btnExportar = UiStyles.botonSecundario("↓ Exportar CSV");
        btnExportar.setOnAction(e -> exportarCSV());
        cardHeader.getChildren().addAll(cardTitulo, sp2, txtBuscar, btnExportar);

        tabla.setItems(datos);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setStyle(UiStyles.TABLA);
        tabla.setPlaceholder(new Label("Sin productos registrados"));

        TableColumn<SaleProduct, String> colId = col("ID", p -> String.valueOf(p.getId()), 60);
        TableColumn<SaleProduct, String> colNom = col("Nombre del Product", p -> p.getName(), 200);

        TableColumn<SaleProduct, Void> colCant = new TableColumn<>("Cantidad");
        colCant.setPrefWidth(80);
        colCant.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                int cant = getTableView().getItems().get(getIndex()).getQuantity();
                Label badge = new Label(String.valueOf(cant));
                badge.setStyle("-fx-background-color:#F3F4F6;-fx-background-radius:20;" +
                        "-fx-padding:2 10;-fx-font-size:11;");
                setGraphic(badge);
            }
        });

        TableColumn<SaleProduct, String> colDesc  = col("Descripción", p -> p.getDescription(), 160);
        TableColumn<SaleProduct, String> colPrecio = col("Precio Base", p -> String.format("$%.2f", p.getPrice()), 100);

        TableColumn<SaleProduct, Void> colEst = new TableColumn<>("Estatus");
        colEst.setPrefWidth(90);
        colEst.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                boolean activo = getTableView().getItems().get(getIndex()).isAvailable();
                setGraphic(UiStyles.badgeActivo(activo));
            }
        });

        TableColumn<SaleProduct, String> colCod = col("Código", p -> p.getCode(), 90);

        TableColumn<SaleProduct, Void> colAcc = new TableColumn<>("Acciones");
        colAcc.setPrefWidth(100);
        colAcc.setCellFactory(c -> new TableCell<>() {
            final Button btnE = UiStyles.botonIcono("✏");
            final Button btnD = UiStyles.botonIconoRojo("🗑");
            { btnE.setOnAction(e -> editar(tabla.getItems().get(getIndex())));
              btnD.setOnAction(e -> delete(tabla.getItems().get(getIndex()).getId())); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(4, btnE, btnD));
            }
        });

        tabla.getColumns().addAll(colId, colNom, colCant, colDesc, colPrecio, colEst, colCod, colAcc);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        card.getChildren().addAll(cardHeader, tabla);

        getChildren().addAll(topRow, card);
    }

    private TableColumn<SaleProduct, String> col(String t, java.util.function.Function<SaleProduct, String> fn, double w) {
        TableColumn<SaleProduct, String> c = new TableColumn<>(t);
        c.setCellValueFactory(d -> new SimpleStringProperty(fn.apply(d.getValue())));
        if (w > 0) c.setPrefWidth(w);
        return c;
    }

    private Label labelEstilo(String texto, String estilo) {
        Label l = new Label(texto); l.setStyle(estilo); return l;
    }

    private void cargar(String filtro) {
        List<SaleProduct> lista = (filtro == null || filtro.isEmpty())
                ? ctrl.listAvailable() : ctrl.search(filtro);
        datos.setAll(lista);
    }

    private void nuevo() {
        new ProductDialog(owner, null, ctrl).showAndWait();
        cargar(null);
    }

    private void editar(SaleProduct p) {
        new ProductDialog(owner, p, ctrl).showAndWait();
        cargar(null);
    }

    private void delete(int id) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el producto seleccionado?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null); a.initOwner(owner);
        a.showAndWait().ifPresent(r -> { if (r == ButtonType.YES && ctrl.delete(id)) cargar(null); });
    }

    private void exportarCSV() {
        List<SaleProduct> productos = ctrl.listAvailable();
        List<String> encabezados = java.util.List.of(
                "ID", "Código", "Nombre", "Descripción", "Precio", "Restricciones", "Cantidad");
        List<List<String>> filas = new java.util.ArrayList<>();
        for (SaleProduct p : productos) {
            filas.add(java.util.List.of(
                    String.valueOf(p.getId()),
                    p.getCode() == null ? "" : p.getCode(),
                    p.getName() == null ? "" : p.getName(),
                    p.getDescription() == null ? "" : p.getDescription(),
                    String.format("%.2f", p.getPrice()),
                    p.getRestrictions() == null ? "" : p.getRestrictions(),
                    String.valueOf(p.getQuantity())));
        }
        CsvExporter.exportar(owner, "inventario_productos", encabezados, filas);
    }
    private void mostrarAccesoDenegado() {
        VBox denied = new VBox(20);
        denied.setAlignment(Pos.CENTER);
        denied.setPadding(new Insets(40));
        Label lbl = new Label("⛔ Acceso restringido\nSolo administradores pueden gestionar usuarios.");
        lbl.setStyle("-fx-font-size:16;-fx-text-fill:" + UiStyles.ROJO + ";-fx-alignment:center;");
        lbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        denied.getChildren().add(lbl);
        getChildren().add(denied);
    }
}
