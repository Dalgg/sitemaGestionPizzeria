package mx.uv.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mx.uv.controller.OrderController;
import mx.uv.controller.UserService;
import mx.uv.model.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

public class OrderListPanel extends VBox {

    private final Stage owner;
    private final Consumer<Node> navigator;
    private final OrderController ctrl   = new OrderController();
    private final UserService usuCtrl = new UserService();

    private final TableView<Order> tabla = new TableView<>();
    private final ObservableList<Order> datos = FXCollections.observableArrayList();

    private final TextField       txtCliente = UiStyles.campo("Nombre del cliente...");
    private final DatePicker      dpDesde    = new DatePicker();
    private final DatePicker      dpHasta    = new DatePicker();
    private final ComboBox<String> cmbEstado = new ComboBox<>();

    public OrderListPanel(Stage owner, Consumer<Node> navigator) {
        this.owner = owner;
        this.navigator = navigator;
        setSpacing(16);
        construir();
        search();
    }

    @SuppressWarnings("unchecked")
    private void construir() {

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox tituloBox = new VBox(2,
            lbl("Gestión de Pedidos", "-fx-font-size:22;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK),
            lbl("Monitorea y procesa las órdenes de los clientes en tiempo real.", "-fx-font-size:12;-fx-text-fill:" + UiStyles.GRIS_TEXTO));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnExportar = UiStyles.botonSecundario("↓ Exportar CSV");
        btnExportar.setOnAction(e -> exportarCSV());
        Button btnNuevo = UiStyles.botonPrimario("+ Nuevo Order");
        btnNuevo.setOnAction(e -> navigator.accept(new NewOrderPanel(owner, this::volverAPedidos)));
        header.getChildren().addAll(tituloBox, sp, btnExportar, btnNuevo);

        VBox cardFiltros = new VBox(12);
        cardFiltros.setStyle(UiStyles.CARD + "-fx-padding:16;");

        Label lblFiltros = new Label("▽  FILTROS DE BÚSQUEDA");
        lblFiltros.setStyle("-fx-font-size:12;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.GRIS_TEXTO);

        String estiloFecha = UiStyles.CAMPO + "-fx-pref-width:180;";
        dpDesde.setStyle(estiloFecha); dpDesde.setPromptText("Fecha desde");
        dpHasta.setStyle(estiloFecha); dpHasta.setPromptText("Fecha hasta");

        cmbEstado.getItems().addAll("Todos los estados","En Proceso","Entregado","Cancelado");
        cmbEstado.setValue("Todos los estados");
        cmbEstado.setStyle(UiStyles.CAMPO); cmbEstado.setPrefWidth(180);

        txtCliente.setPrefWidth(200);

        Button btnLimpiar = UiStyles.botonSecundario("Limpiar");
        btnLimpiar.setOnAction(e -> {
            txtCliente.clear(); dpDesde.setValue(null); dpHasta.setValue(null);
            cmbEstado.setValue("Todos los estados"); search();
        });

        GridPane filtrosGrid = new GridPane();
        filtrosGrid.setHgap(16); filtrosGrid.setVgap(8);
        filtrosGrid.add(campoFiltro("CLIENTE", txtCliente), 0, 0);
        filtrosGrid.add(campoFiltro("FECHA DESDE", dpDesde), 1, 0);
        filtrosGrid.add(campoFiltro("FECHA HASTA", dpHasta), 2, 0);
        filtrosGrid.add(campoFiltro("ESTATUS", cmbEstado), 3, 0);
        filtrosGrid.add(new VBox(18, new Label(""), btnLimpiar), 4, 0);

        cardFiltros.getChildren().addAll(lblFiltros, filtrosGrid);

        txtCliente.textProperty().addListener((o,ov,nv) -> search());
        dpDesde.valueProperty().addListener((o,ov,nv) -> search());
        dpHasta.valueProperty().addListener((o,ov,nv) -> search());
        cmbEstado.valueProperty().addListener((o,ov,nv) -> search());

        tabla.setItems(datos);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setStyle("-fx-background-color:white;");
        tabla.setFixedCellSize(52);
        tabla.setPlaceholder(new Label("Sin pedidos registrados"));
        tabla.getStyleClass().add("edge-to-edge");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\nHH:mm");

        TableColumn<Order, String> colId = new TableColumn<>("IDpedido");
        colId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colId.setPrefWidth(90);
        colId.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                setText(v);
                setStyle("-fx-text-fill:" + UiStyles.ROJO + ";-fx-font-weight:bold;-fx-cursor:hand;");
                setOnMouseClicked(e -> {
                    Order p = tabla.getItems().get(getIndex());
                    navigator.accept(new OrderDetailPanel(owner, p, this::volverAPedidos));
                });
            }
            private void volverAPedidos() { navigator.accept(new OrderListPanel(owner, navigator)); }
        });

        tabla.getColumns().addAll(
            colId,
            strCol("Cliente",  p -> p.getCustomer().getFirstName() + " " + p.getCustomer().getLastName(), 140),
            strCol("Employee", p -> p.getEmployee().getFirstName(), 110),
            strCol("Fecha/Hora", p -> sdf.format(p.getOrderDate()), 100),
            colTotal(),
            colEstatus(),
            colAcciones()
        );

        VBox cardTabla = new VBox(tabla);
        cardTabla.setStyle(UiStyles.CARD);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        getChildren().addAll(header, cardFiltros, cardTabla);
    }

    private TableColumn<Order, String> strCol(String t, java.util.function.Function<Order,String> fn, double w) {
        TableColumn<Order, String> c = new TableColumn<>(t);
        c.setCellValueFactory(d -> new SimpleStringProperty(fn.apply(d.getValue())));
        if (w > 0) c.setPrefWidth(w);
        return c;
    }

    private TableColumn<Order, Void> colTotal() {
        TableColumn<Order, Void> c = new TableColumn<>("Total ↕");
        c.setPrefWidth(90);
        c.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setText(null); return; }
                double t = tabla.getItems().get(getIndex()).getTotal();
                setText(String.format("$%.2f", t));
                setStyle("-fx-font-weight:bold;");
            }
        });
        return c;
    }

    private TableColumn<Order, Void> colEstatus() {
        TableColumn<Order, Void> c = new TableColumn<>("Estatus");
        c.setPrefWidth(120);
        c.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                OrderStatus ep = tabla.getItems().get(getIndex()).getStatus();
                setGraphic(UiStyles.badgeEstado(ep.toString()));
            }
        });
        return c;
    }

    private TableColumn<Order, Void> colAcciones() {
        TableColumn<Order, Void> c = new TableColumn<>("Acciones");
        c.setPrefWidth(100);
        c.setCellFactory(col -> new TableCell<>() {
            final Button btnVer  = UiStyles.botonIcono("👁");
            final Button btnEdit = UiStyles.botonIcono("✏");
            {
                btnVer.setOnAction(e -> {
                    Order p = tabla.getItems().get(getIndex());
                    navigator.accept(new OrderDetailPanel(owner, p, () -> navigator.accept(new OrderListPanel(owner, navigator))));
                });
                btnEdit.setOnAction(e -> {
                    Order p = tabla.getItems().get(getIndex());
                    navigator.accept(new OrderDetailPanel(owner, p, () -> navigator.accept(new OrderListPanel(owner, navigator))));
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(4, btnVer, btnEdit));
            }
        });
        return c;
    }

    private VBox campoFiltro(String titulo, Node campo) {
        Label l = new Label(titulo);
        l.setStyle("-fx-font-size:10;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.GRIS_TEXTO);
        return new VBox(4, l, campo);
    }

    private Label lbl(String t, String style) {
        Label l = new Label(t); l.setStyle(style); return l;
    }

    private void search() {
        String filtroCliente = txtCliente.getText().trim().toLowerCase();
        Date desde = dpDesde.getValue() != null ? Date.valueOf(dpDesde.getValue()) : null;
        Date hasta  = dpHasta.getValue() != null  ? Date.valueOf(dpHasta.getValue())  : null;

        OrderStatus estado = null;
        String sel = cmbEstado.getValue();
        if (sel != null && !sel.startsWith("Todos"))
            estado = OrderStatus.fromValue(sel.toLowerCase().replace(" ", "_"));

        var lista = ctrl.search(null, desde, estado);
        if (!filtroCliente.isEmpty()) {
            lista = lista.stream()
                .filter(p -> (p.getCustomer().getFirstName() + " " + p.getCustomer().getLastName())
                        .toLowerCase().contains(filtroCliente))
                .toList();
        }
        if (hasta != null) {
            final Date h = hasta;
            lista = lista.stream()
                .filter(p -> !new Date(p.getOrderDate().getTime()).after(h))
                .toList();
        }
        datos.setAll(lista);
    }

    private void volverAPedidos() {
        navigator.accept(new OrderListPanel(owner, navigator));
    }

    private void exportarCSV() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.List<String> encabezados = java.util.List.of(
                "ID", "Cliente", "Employee", "Fecha/Hora", "Total", "Estatus");
        java.util.List<java.util.List<String>> filas = new java.util.ArrayList<>();
        for (Order p : datos) {
            filas.add(java.util.List.of(
                    String.valueOf(p.getId()),
                    p.getCustomer().getFirstName() + " " + p.getCustomer().getLastName(),
                    p.getEmployee().getFirstName(),
                    sdf.format(p.getOrderDate()),
                    String.format("%.2f", p.getTotal()),
                    p.getStatus().toString()));
        }
        mx.uv.util.CsvExporter.exportar(
                getScene() != null ? getScene().getWindow() : owner,
                "pedidos", encabezados, filas);
    }
}
