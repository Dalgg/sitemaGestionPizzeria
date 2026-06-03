package mx.uv.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mx.uv.controller.InventoryController;
import mx.uv.controller.ProductController;
import mx.uv.controller.SessionController;
import mx.uv.model.SaleProduct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryPanel extends VBox {

    private final Stage owner;
    private final ProductController prodCtrl = new ProductController();
    private final InventoryController invCtrl = new InventoryController();

    private final ObservableList<String[]> datos = FXCollections.observableArrayList();
    private final TableView<String[]>      tabla = new TableView<>();

    public InventoryPanel(Stage owner) {
        this.owner = owner;
        setSpacing(16);
        construir();
        cargar();
    }

    @SuppressWarnings("unchecked")
    private void construir() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label("🍕");
        ico.setStyle("-fx-font-size:22;-fx-background-color:#F3F4F6;-fx-background-radius:50;-fx-padding:10;");
        Label titLbl = new Label("Inventario");
        titLbl.setStyle("-fx-font-size:22;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnGuardar = UiStyles.botonPrimario("💾 Guardar Validación");
        btnGuardar.setOnAction(e -> save());
        header.getChildren().addAll(ico, titLbl, sp, btnGuardar);

        tabla.setItems(datos);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setStyle("-fx-background-color:white;");
        tabla.setFixedCellSize(52);
        tabla.setPlaceholder(new Label("Sin productos en inventario"));

        TableColumn<String[], String> colId  = strCol("ID",     0, 70);
        TableColumn<String[], String> colNom = strCol("Nombre (producto)", 2, 220);

        TableColumn<String[], Void> colCant = new TableColumn<>("Cantidad disponible");
        colCant.setPrefWidth(150);
        colCant.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                String[] row = tabla.getItems().get(getIndex());
                int cant = Integer.parseInt(row[3]);
                Label badge = new Label(cant + " uds");
                boolean bajo = cant < 10;
                badge.setStyle("-fx-background-color:" + (bajo ? UiStyles.ROJO : "#F3F4F6") + ";" +
                        "-fx-text-fill:" + (bajo ? "white" : UiStyles.TEXTO_DARK) + ";" +
                        "-fx-background-radius:20;-fx-padding:3 12;-fx-font-size:11;");
                setGraphic(badge);
            }
        });

        TableColumn<String[], Void> colAcc = new TableColumn<>("Acciones");
        colAcc.setPrefWidth(220);
        colAcc.setCellFactory(c -> new TableCell<>() {
            final Button btnAum  = new Button("⊕  Aumentar cantidad");
            final Button btnEdit = UiStyles.botonIcono("✏");
            final Button btnDel  = UiStyles.botonIconoRojo("🗑");
            {
                btnAum.setStyle("-fx-background-color:transparent;-fx-text-fill:" + UiStyles.TEXTO_DARK +
                        ";-fx-border-color:" + UiStyles.GRIS_BORDE + ";-fx-border-radius:20;" +
                        "-fx-background-radius:20;-fx-padding:4 12;-fx-cursor:hand;-fx-font-size:12;");
                btnAum.setOnAction(e -> aumentarCantidad(getIndex()));
                btnEdit.setOnAction(e -> editarContada(getIndex()));
                btnDel.setOnAction(e -> {

                    String[] row = tabla.getItems().get(getIndex());
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                            "¿Dar de baja el producto " + row[2] + "?", ButtonType.YES, ButtonType.NO);
                    a.initOwner(owner); a.showAndWait();
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(8, btnAum, btnEdit, btnDel));
            }
        });

        tabla.getColumns().addAll(colId, colNom, colCant, colAcc);

        VBox card = new VBox(0, header, new Separator(), tabla);
        card.setStyle(UiStyles.CARD + "-fx-padding:20;");
        VBox.setVgrow(tabla, Priority.ALWAYS);

        Label pieFoot = new Label("🍕");
        pieFoot.setStyle("-fx-font-size:48;-fx-opacity:0.15;-fx-padding:20;");
        pieFoot.setMaxWidth(Double.MAX_VALUE);
        pieFoot.setAlignment(Pos.CENTER);

        getChildren().addAll(card, pieFoot);
    }

    private TableColumn<String[], String> strCol(String t, int idx, double w) {
        TableColumn<String[], String> c = new TableColumn<>(t);
        c.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[idx]));
        if (w > 0) c.setPrefWidth(w);
        return c;
    }

    private void cargar() {
        List<SaleProduct> productos = prodCtrl.listAvailable();
        datos.clear();
        for (SaleProduct p : productos) {
            datos.add(new String[]{
                String.valueOf(p.getId()), p.getCode(), p.getName(),
                String.valueOf(p.getQuantity()), "0",
                String.valueOf(-p.getQuantity())
            });
        }
    }

    private void aumentarCantidad(int idx) {
        TextInputDialog dlg = new TextInputDialog("1");
        dlg.setTitle("Aumentar Cantidad");
        dlg.setHeaderText(null);
        dlg.setContentText("¿Cuántas unidades deseas agregar?");
        dlg.initOwner(owner);
        dlg.showAndWait().ifPresent(val -> {
            try {
                int inc = Integer.parseInt(val.trim());
                if (inc <= 0) return;
                String[] row = datos.get(idx);
                int nueva = Integer.parseInt(row[3]) + inc;
                row[3] = String.valueOf(nueva);
                row[5] = String.valueOf(Integer.parseInt(row[4]) - nueva);
                tabla.refresh();
            } catch (NumberFormatException ignored) {}
        });
    }

    private void editarContada(int idx) {
        String[] row = datos.get(idx);
        TextInputDialog dlg = new TextInputDialog(row[4]);
        dlg.setTitle("Cantidad Contada");
        dlg.setHeaderText(null);
        dlg.setContentText("Ingrese la cantidad física contada:");
        dlg.initOwner(owner);
        dlg.showAndWait().ifPresent(val -> {
            try {
                int contada = Integer.parseInt(val.trim());
                row[4] = String.valueOf(contada);
                row[5] = String.valueOf(contada - Integer.parseInt(row[3]));
                tabla.refresh();
            } catch (NumberFormatException ignored) {}
        });
    }

    private void save() {
        Map<Integer, Integer> cantidades = new HashMap<>();
        for (String[] row : datos) {
            try { cantidades.put(Integer.parseInt(row[0]), Integer.parseInt(row[4])); }
            catch (NumberFormatException ignored) {}
        }
        int idEmp = SessionController.getInstance().getCurrentEmployee().getId();
        if (invCtrl.saveValidation(idEmp, cantidades))
            new Alert(Alert.AlertType.INFORMATION, "Validación guardada correctamente.").showAndWait();
    }
}
