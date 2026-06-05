package mx.uv.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mx.uv.controller.OrderController;
import mx.uv.controller.ProductController;
import mx.uv.controller.SessionController;
import mx.uv.controller.UserService;
import mx.uv.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NewOrderPanel extends VBox {

    private final Stage owner;
    private final Runnable onVolver;
    private final OrderController ctrl      = new OrderController();
    private final ProductController prodCtrl = new ProductController();
    private final UserService  usuCtrl  = new UserService();

    private final ComboBox<Object> cmbCliente = new ComboBox<>();
    private int idPedidoActual = -1;

    private final List<String[]>  filas      = new ArrayList<>();
    private final VBox            filasBox   = new VBox(8);
    private final Label           lblSubtotal = lbl("$0.00", "-fx-font-size:14;");
    private final Label           lblIva      = lbl("$0.00", "-fx-font-size:14;");
    private final Label           lblTotal    = lbl("$0.00",
            "-fx-font-size:18;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.ROJO);

    public NewOrderPanel(Stage owner, Runnable onVolver) {
        this.owner    = owner;
        this.onVolver = onVolver;
        setSpacing(16);
        construir();
        cargarCombos();
    }

    private void construir() {

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button btnVolver = new Button("< Volver a pedidos");
        btnVolver.setStyle("-fx-background-color:transparent;-fx-text-fill:" + UiStyles.GRIS_TEXTO +
                ";-fx-cursor:hand;-fx-font-size:13;");
        btnVolver.setOnAction(e -> onVolver.run());
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label chip = new Label("Modo Edición: Nuevo pedido");
        chip.setStyle("-fx-background-color:#FEF3C7;-fx-text-fill:#92400E;" +
                "-fx-background-radius:20;-fx-padding:4 12;-fx-font-size:11;-fx-font-weight:bold;");
        topBar.getChildren().addAll(btnVolver, sp, chip);

        VBox card = new VBox(20);
        card.setStyle(UiStyles.CARD + "-fx-padding:24;");

        HBox tituloRow = new HBox();
        tituloRow.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label("🍕");
        ico.setStyle("-fx-font-size:22;-fx-background-color:#FEE2E2;-fx-background-radius:8;-fx-padding:8;");
        Label titLbl = new Label("  Nuevo Order");
        titLbl.setStyle("-fx-font-size:20;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK);
        Region spT = new Region(); HBox.setHgrow(spT, Priority.ALWAYS);
        VBox folioBox = new VBox(2, lbl("FOLIO TEMPORAL", "-fx-font-size:10;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.GRIS_TEXTO),
                lbl("#ORD-TEMP-001", "-fx-font-size:14;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.ROJO));
        folioBox.setAlignment(Pos.CENTER_RIGHT);
        tituloRow.getChildren().addAll(ico, titLbl, spT, folioBox);

        Employee empActual = SessionController.getInstance().getCurrentEmployee();
        if (empActual == null) {
            new Alert(Alert.AlertType.ERROR, "No hay sesión activa. Reinicie sesión.").showAndWait();
            onVolver.run();
            return;
        }
        String nombreEmpleado = empActual.getFirstName() + " " + empActual.getLastName() + " (" + empActual.getRole() + ")";
        Label cmpEmpleado = new Label(nombreEmpleado);
        cmpEmpleado.setStyle("-fx-font-size:13; -fx-text-fill:" + UiStyles.TEXTO_DARK + ";" +
                "-fx-border-color:" + UiStyles.GRIS_BORDE + ";-fx-border-radius:6;" +
                "-fx-padding:8 12;-fx-background-color:white;-fx-background-radius:6;");
        cmbCliente.setMaxWidth(Double.MAX_VALUE);
        cmbCliente.setStyle(UiStyles.CAMPO);
        cmbCliente.setButtonCell(clienteCell()); cmbCliente.setCellFactory(l -> clienteCell());
        Label cmpFecha = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        cmpFecha.setStyle("-fx-font-size:13; -fx-text-fill:" + UiStyles.TEXTO_DARK + ";" +
                "-fx-border-color:" + UiStyles.GRIS_BORDE + ";-fx-border-radius:6;" +
                "-fx-padding:8 12;-fx-background-color:white;-fx-background-radius:6;");

        GridPane info = new GridPane();
        info.setHgap(16); info.setVgap(8);
        info.getColumnConstraints().addAll(col33(), col33(), col33());
        info.add(campoLabel("CLIENTE *", cmbCliente), 0, 0);
        info.add(campoLabel("EMPLEADO *", cmpEmpleado), 1, 0);
        info.add(campoLabel("FECHA DEL PEDIDO *", cmpFecha), 2, 0);

        HBox headerProd = new HBox();
        headerProd.setStyle("-fx-background-color:" + UiStyles.ROJO + ";-fx-padding:8 12;" +
                "-fx-background-radius:6 6 0 0;");
        for (String t : new String[]{"PRODUCTO", "CANTIDAD", "SUBTOTAL", "ACCIONES"}) {
            Label h = new Label(t);
            h.setStyle("-fx-text-fill:white;-fx-font-size:11;-fx-font-weight:bold;");
            Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
            headerProd.getChildren().addAll(h, r);
        }

        VBox productosBox = new VBox(0, headerProd, filasBox);
        productosBox.setStyle("-fx-border-color:" + UiStyles.GRIS_BORDE + ";-fx-border-radius:6;");

        Button btnAgregar = new Button("+ Agregar producto");
        btnAgregar.setStyle("-fx-background-color:transparent;-fx-text-fill:" + UiStyles.ROJO +
                ";-fx-border-color:" + UiStyles.ROJO + ";-fx-border-radius:6;" +
                "-fx-padding:8 16;-fx-cursor:hand;-fx-font-weight:bold;");
        btnAgregar.setOnAction(e -> agregarFila());

        TextArea txtObs = new TextArea();
        txtObs.setPromptText("Ej. Sin cebolla, extra salsa, entregar en puerta principal...");
        txtObs.setPrefRowCount(4);
        txtObs.setStyle(UiStyles.CAMPO + "-fx-font-size:13;");
        txtObs.setWrapText(true);
        VBox obsBox = new VBox(6, lbl("OBSERVACIONES DEL PEDIDO",
                "-fx-font-size:10;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.GRIS_TEXTO), txtObs);

        VBox resumen = new VBox(12);
        resumen.setStyle(UiStyles.CARD + "-fx-padding:20;-fx-min-width:280;");
        resumen.getChildren().addAll(
            filaResumen("Subtotal", lblSubtotal),
            filaResumen("IVA (16%)", lblIva),
            new Separator(),
            filaResumenGrande("Total a Pagar", lblTotal)
        );

        HBox bottomRow = new HBox(20, obsBox, resumen);
        HBox.setHgrow(obsBox, Priority.ALWAYS);
        bottomRow.setAlignment(Pos.TOP_LEFT);

        card.getChildren().addAll(tituloRow, new Separator(), info,
                new Separator(), productosBox, btnAgregar,
                new Separator(), bottomRow);

        HBox botonesRow = new HBox(12);
        botonesRow.setAlignment(Pos.CENTER_RIGHT);
        Button btnCancelar = UiStyles.botonSecundario("✕  Cancelar");
        Button btnGuardar  = UiStyles.botonPrimario("🖫  Guardar Order");
        btnCancelar.setOnAction(e -> onVolver.run());
        btnGuardar.setOnAction(e -> save());
        botonesRow.getChildren().addAll(btnCancelar, btnGuardar);

        HBox statusBar = new HBox(0);
        statusBar.setStyle("-fx-border-color:" + UiStyles.GRIS_BORDE + ";-fx-border-radius:6;");
        for (String t : new String[]{"Sincronizado con inventario central", "Pendiente  |  Estado inicial del pedido", "Última actualización: hace un momento"}) {
            Label l = new Label(t);
            l.setStyle("-fx-font-size:11;-fx-text-fill:" + UiStyles.GRIS_TEXTO + ";-fx-padding:8 16;");
            l.setMaxWidth(Double.MAX_VALUE);
            l.setStyle(l.getStyle() + "-fx-border-color:transparent " + UiStyles.GRIS_BORDE + " transparent transparent;");
            HBox.setHgrow(l, Priority.ALWAYS);
            statusBar.getChildren().add(l);
        }

        VBox contenedor = new VBox(16, topBar, card, botonesRow, statusBar);
        getChildren().add(contenedor);

        agregarFila();
    }

    private void agregarFila() {
        List<SaleProduct> productos = prodCtrl.listAvailable();
        if (productos.isEmpty()) return;

        ComboBox<SaleProduct> cbProd = new ComboBox<>();
        cbProd.getItems().addAll(productos);
        cbProd.getSelectionModel().selectFirst();
        cbProd.setMaxWidth(Double.MAX_VALUE);
        cbProd.setStyle(UiStyles.CAMPO);
        cbProd.setButtonCell(prodCell()); cbProd.setCellFactory(l -> prodCell());

        TextField tfCant = UiStyles.campo("1");
        tfCant.setPrefWidth(80);
        tfCant.setText("1");

        Label lblSub = lbl("$0.00", "-fx-font-size:13;-fx-font-weight:bold;-fx-min-width:90;");

        Button btnDel = UiStyles.botonIconoRojo("🗑");

        HBox fila = new HBox(12, cbProd, tfCant, lblSub, btnDel);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(8, 12, 8, 12));
        fila.setStyle("-fx-border-color:transparent transparent " + UiStyles.GRIS_BORDE + " transparent;");
        HBox.setHgrow(cbProd, Priority.ALWAYS);

        Runnable recalcular = () -> {
            try {
                SaleProduct p = cbProd.getValue();
                if (p == null) return;
                int cant = Integer.parseInt(tfCant.getText().trim());
                double sub = cant * p.getPrice();
                lblSub.setText(String.format("$%.2f", sub));
            } catch (NumberFormatException ignored) {}
            actualizarTotal();
        };

        cbProd.valueProperty().addListener((o,ov,nv) -> recalcular.run());
        tfCant.textProperty().addListener((o,ov,nv) -> recalcular.run());
        btnDel.setOnAction(e -> { filasBox.getChildren().remove(fila); actualizarTotal(); });

        filasBox.getChildren().add(fila);
        recalcular.run();
    }

    private void actualizarTotal() {
        double subtotal = 0;
        for (javafx.scene.Node n : filasBox.getChildren()) {
            if (n instanceof HBox fila) {
                Label lblS = (Label) fila.getChildren().get(2);
                try { subtotal += Double.parseDouble(lblS.getText().replace("$","")); } catch (NumberFormatException ignored) {}
            }
        }
        double iva   = subtotal * 0.16;
        double total = subtotal + iva;
        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblIva.setText(String.format("$%.2f", iva));
        lblTotal.setText(String.format("$%.2f", total));
    }

    private void save() {
        if (!(cmbCliente.getValue() instanceof User u)) {
            new Alert(Alert.AlertType.WARNING, "Seleccione un cliente.").showAndWait(); return;
        }
        if (filasBox.getChildren().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Agregue al menos un producto.").showAndWait(); return;
        }

        Employee emp = SessionController.getInstance().getCurrentEmployee();
        int idPedido = ctrl.createOrder(emp.getId(), u.getId());
        if (idPedido < 0) return;

        for (javafx.scene.Node n : filasBox.getChildren()) {
            if (n instanceof HBox fila) {
                @SuppressWarnings("unchecked")
                ComboBox<SaleProduct> cb = (ComboBox<SaleProduct>) fila.getChildren().get(0);
                TextField tf = (TextField) fila.getChildren().get(1);
                SaleProduct p = cb.getValue();
                if (p == null) continue;
                try {
                    int cant = Integer.parseInt(tf.getText().trim());
                    ctrl.addItem(idPedido, p.getId(), cant, p.getPrice());
                } catch (NumberFormatException ignored) {}
            }
        }
        new Alert(Alert.AlertType.INFORMATION, "Order #" + idPedido + " creado correctamente.").showAndWait();
        onVolver.run();
    }

    private void cargarCombos() {
        for (User u : usuCtrl.listCustomers()) cmbCliente.getItems().add(u);
        if (!cmbCliente.getItems().isEmpty()) cmbCliente.getSelectionModel().selectFirst();
    }

    private static Label lbl(String t, String s) { Label l = new Label(t); l.setStyle(s); return l; }

    private VBox campoLabel(String titulo, javafx.scene.Node control) {
        Label l = new Label(titulo);
        l.setStyle("-fx-font-size:10;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.GRIS_TEXTO);
        if (control instanceof Control c) c.setMaxWidth(Double.MAX_VALUE);
        return new VBox(4, l, control);
    }

    private HBox filaResumen(String lbl, Label valor) {
        Label l = new Label(lbl); l.setStyle("-fx-font-size:13;-fx-text-fill:" + UiStyles.GRIS_TEXTO);
        Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
        HBox h = new HBox(r, valor); h.getChildren().add(0, l);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private HBox filaResumenGrande(String lbl, Label valor) {
        Label l = new Label(lbl); l.setStyle("-fx-font-size:15;-fx-font-weight:bold;");
        Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
        HBox h = new HBox(r, valor); h.getChildren().add(0, l);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private ListCell<Object> clienteCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Object v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? "" : v instanceof User u
                        ? u.getFirstName() + " (" + u.getPhone() + ")" : v.toString());
            }
        };
    }

    private ListCell<SaleProduct> prodCell() {
        return new ListCell<>() {
            @Override protected void updateItem(SaleProduct p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? "" : p.getName());
            }
        };
    }

    private ColumnConstraints col33() {
        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(33.3);
        return c;
    }

}
