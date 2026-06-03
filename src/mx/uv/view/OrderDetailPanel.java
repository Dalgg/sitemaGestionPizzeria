package mx.uv.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mx.uv.controller.OrderController;
import mx.uv.controller.SessionController;
import mx.uv.model.*;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrderDetailPanel extends VBox {

    private final Stage owner;
    private final Order pedido;
    private final Runnable onVolver;
    private final OrderController ctrl = new OrderController();

    private final ObservableList<OrderItem> detalles = FXCollections.observableArrayList();
    private final TableView<OrderItem>       tabla    = new TableView<>();
    private final Label lblSubtotal = lbl("$0.00", "-fx-font-size:13;");
    private final Label lblIva      = lbl("$0.00", "-fx-font-size:13;");
    private final Label lblTotal    = lbl("$0.00",
            "-fx-font-size:18;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.ROJO);

    public OrderDetailPanel(Stage owner, Order pedido, Runnable onVolver) {
        this.owner    = owner;
        this.pedido   = pedido;
        this.onVolver = onVolver;
        setSpacing(16);
        construir();
        cargarDetalles();
    }

    @SuppressWarnings("unchecked")
    private void construir() {

        Button btnVolver = new Button("< Volver a pedidos");
        btnVolver.setStyle("-fx-background-color:transparent;-fx-text-fill:" + UiStyles.GRIS_TEXTO +
                ";-fx-cursor:hand;-fx-font-size:13;");
        btnVolver.setOnAction(e -> onVolver.run());

        VBox card = new VBox(20);
        card.setStyle(UiStyles.CARD + "-fx-padding:24;");

        HBox titRow = new HBox(12);
        titRow.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label("🛒");
        ico.setStyle("-fx-font-size:20;-fx-background-color:#FEE2E2;-fx-background-radius:8;-fx-padding:8;");
        VBox titInfo = new VBox(2,
            lbl("Detalle del Order #" + pedido.getId(),
                    "-fx-font-size:18;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK),
            lbl("Revisión y edición de pedido registrado en el sistema",
                    "-fx-font-size:11;-fx-text-fill:" + UiStyles.GRIS_TEXTO));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnCerrar = new Button("✕");
        btnCerrar.setStyle("-fx-background-color:transparent;-fx-cursor:hand;-fx-font-size:14;");
        btnCerrar.setOnAction(e -> onVolver.run());
        titRow.getChildren().addAll(ico, titInfo, sp, btnCerrar);

        HBox infoBar = new HBox(0);
        infoBar.setStyle(UiStyles.CARD + "-fx-background-color:#FAFAFA;");
        String sdf = new SimpleDateFormat("dd 'de' MMMM, yyyy",
                new java.util.Locale("es","MX")).format(pedido.getOrderDate());
        infoBar.getChildren().addAll(
            infoItem("👤 CLIENTE",   pedido.getCustomer().getFirstName() + " " + pedido.getCustomer().getLastName()),
            infoItem("📅 FECHA",     sdf),
            infoItem("👤 EMPLEADO",  pedido.getEmployee().getFirstName()),
            infoItem("⏱ ESTATUS",   pedido.getStatus().toString())
        );
        for (javafx.scene.Node n : infoBar.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);

        Label lblProd = lbl("Productos del Order",
                "-fx-font-size:16;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK);

        tabla.setItems(detalles);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setStyle("-fx-background-color:white;");
        tabla.setPrefHeight(220);

        TableColumn<OrderItem, String> colNom = new TableColumn<>("ProductoISDS");
        colNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProduct().getName()));
        colNom.setPrefWidth(280);

        TableColumn<OrderItem, Void> colCant = new TableColumn<>("Cantidad");
        colCant.setPrefWidth(120);
        colCant.setCellFactory(c -> new TableCell<>() {
            final TextField tf = new TextField();
            {
                tf.setStyle("-fx-border-color:" + UiStyles.GRIS_BORDE + ";-fx-background-radius:6;" +
                        "-fx-border-radius:6;-fx-padding:4 8;-fx-pref-width:70;");
                tf.textProperty().addListener((o,ov,nv) -> actualizarTotal());
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                tf.setText(String.valueOf(detalles.get(getIndex()).getQuantity()));
                setGraphic(tf);
            }
        });

        TableColumn<OrderItem, Void> colTotal = new TableColumn<>("TOTAL");
        colTotal.setPrefWidth(120);
        colTotal.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setText(null); return; }
                OrderItem dp = detalles.get(getIndex());
                setText(String.format("$%.2f", dp.getQuantity() * dp.getPrice()));
                setStyle("-fx-font-weight:bold;");
            }
        });

        TableColumn<OrderItem, Void> colDel = new TableColumn<>("");
        colDel.setPrefWidth(50);
        colDel.setCellFactory(c -> new TableCell<>() {
            final Button btn = UiStyles.botonIconoRojo("🗑");
            { btn.setOnAction(e -> { OrderItem dp = detalles.get(getIndex());
                ctrl.removeItem(pedido.getId(), dp.getProduct().getId());
                detalles.remove(dp); actualizarTotal(); }); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tabla.getColumns().addAll(colNom, colCant, colTotal, colDel);

        VBox resumen = new VBox(12);
        resumen.setStyle(UiStyles.CARD + "-fx-padding:20;-fx-min-width:260;-fx-max-width:320;");
        resumen.setAlignment(Pos.TOP_RIGHT);
        resumen.getChildren().addAll(
            filaR("Subtotal", lblSubtotal),
            filaR("IVA (16%)", lblIva),
            new Separator(),
            filaRGrande("Total", lblTotal)
        );

        HBox bottomSection = new HBox(0, resumen);
        bottomSection.setAlignment(Pos.CENTER_RIGHT);

        ComboBox<OrderStatus> cmbEstado = new ComboBox<>();
        cmbEstado.getItems().addAll(OrderStatus.values());
        cmbEstado.setValue(pedido.getStatus());
        cmbEstado.setStyle(UiStyles.CAMPO + "-fx-pref-width:160;");

        Button btnCambiarEstado = UiStyles.botonPrimario("🔄 Cambiar Estatus");
        btnCambiarEstado.setOnAction(e -> {
            OrderStatus nuevo = cmbEstado.getValue();
            if (nuevo == null || nuevo == pedido.getStatus()) return;
            int idEmp = SessionController.getInstance().getCurrentEmployee().getId();
            if (ctrl.changeStatus(pedido.getId(), nuevo, idEmp)) {
                pedido.setStatus(nuevo);
                new Alert(Alert.AlertType.INFORMATION,
                        "Estatus actualizado a: " + nuevo).showAndWait();
            }
        });

        HBox estatusRow = new HBox(10,
                lbl("Cambiar estatus:", "-fx-font-size:12;-fx-text-fill:" + UiStyles.GRIS_TEXTO),
                cmbEstado, btnCambiarEstado);
        estatusRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        estatusRow.setStyle(UiStyles.CARD + "-fx-padding:12 16;");

        card.getChildren().addAll(titRow, infoBar, lblProd, tabla, bottomSection, estatusRow);

        HBox botonesRow = new HBox(12);
        botonesRow.setAlignment(Pos.CENTER_LEFT);

        Button btnHistorial = new Button("⟳ Ver Historial de Estatus");
        btnHistorial.setStyle("-fx-background-color:transparent;-fx-text-fill:" + UiStyles.ROJO +
                ";-fx-border-color:" + UiStyles.ROJO + ";-fx-border-radius:6;" +
                "-fx-padding:8 16;-fx-cursor:hand;-fx-font-size:12;");
        btnHistorial.setOnAction(e -> verHistorial());

        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        Button btnCancelarAcc = UiStyles.botonSecundario("Cancelar");
        Button btnGuardar     = UiStyles.botonPrimario("🖫 Guardar Cambios");
        btnCancelarAcc.setOnAction(e -> onVolver.run());
        btnGuardar.setOnAction(e -> save());

        botonesRow.getChildren().addAll(btnHistorial, sp2, btnCancelarAcc, btnGuardar);

        getChildren().addAll(btnVolver, card, botonesRow);
    }

    private void cargarDetalles() {
        detalles.setAll(ctrl.getItems(pedido.getId()));
        actualizarTotal();
    }

    private void actualizarTotal() {
        double subtotal = detalles.stream()
                .mapToDouble(dp -> dp.getQuantity() * dp.getPrice()).sum();
        double iva   = subtotal * 0.16;
        double total = subtotal + iva;
        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblIva.setText(String.format("$%.2f", iva));
        lblTotal.setText(String.format("$%.2f", total));
    }

    private void save() {
        new Alert(Alert.AlertType.INFORMATION, "Cambios guardados en pedido #" + pedido.getId()).showAndWait();
        onVolver.run();
    }

    private void verHistorial() {
        List<OrderStatusLog> bit = ctrl.getStatusLog(pedido.getId());
        if (bit.isEmpty()) { new Alert(Alert.AlertType.INFORMATION, "Sin historial aún.").showAndWait(); return; }

        ObservableList<OrderStatusLog> obs = FXCollections.observableArrayList(bit);
        TableView<OrderStatusLog> tv = new TableView<>(obs);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPrefHeight(220);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        TableColumn<OrderStatusLog,String> c1 = new TableColumn<>("Fecha/Hora");
        c1.setCellValueFactory(d -> new SimpleStringProperty(sdf.format(d.getValue().getDateTime())));
        TableColumn<OrderStatusLog,String> c2 = new TableColumn<>("Estado");
        c2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().toString()));
        TableColumn<OrderStatusLog,String> c3 = new TableColumn<>("Employee");
        c3.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmployee().getFirstName()));
        tv.getColumns().addAll(c1,c2,c3);

        javafx.stage.Stage s = new javafx.stage.Stage();
        s.setTitle("Historial de Estatus — Order #" + pedido.getId());
        s.initOwner(owner);
        s.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        VBox vb = new VBox(12, lbl("Historial de Estatus",
                "-fx-font-size:15;-fx-font-weight:bold;"), tv);
        vb.setPadding(new Insets(16));
        vb.setStyle("-fx-background-color:white;");
        s.setScene(new javafx.scene.Scene(vb, 520, 300));
        s.showAndWait();
    }

    private static Label lbl(String t, String s) { Label l = new Label(t); l.setStyle(s); return l; }

    private VBox infoItem(String titulo, String valor) {
        VBox v = new VBox(4,
            lbl(titulo, "-fx-font-size:10;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.GRIS_TEXTO),
            lbl(valor,  "-fx-font-size:13;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK));
        v.setPadding(new Insets(14));
        v.setStyle("-fx-border-color:transparent " + UiStyles.GRIS_BORDE + " transparent transparent;");
        return v;
    }

    private HBox filaR(String t, Label val) {
        Label l = new Label(t); l.setStyle("-fx-font-size:13;-fx-text-fill:" + UiStyles.GRIS_TEXTO);
        Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
        return new HBox(l, r, val);
    }

    private HBox filaRGrande(String t, Label val) {
        Label l = new Label(t); l.setStyle("-fx-font-size:15;-fx-font-weight:bold;");
        Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
        return new HBox(l, r, val);
    }
}
