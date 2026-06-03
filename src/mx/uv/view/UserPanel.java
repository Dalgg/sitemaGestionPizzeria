package mx.uv.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mx.uv.controller.SessionController;
import mx.uv.controller.UserService;
import mx.uv.model.Employee;
import mx.uv.model.User;

import java.util.List;

public class UserPanel extends VBox {

    private final Stage owner;
    private final UserService ctrl = new UserService();
    private final TableView<User> tabla = new TableView<>();
    private final ObservableList<User> datos = FXCollections.observableArrayList();
    private final TextField txtBuscar = UiStyles.campo("Buscar usuario...");
    private final TabPane tabs = new TabPane();

    private final TableView<Employee> tablaEmp = new TableView<>();
    private final ObservableList<Employee> datosEmp = FXCollections.observableArrayList();

    public UserPanel(Stage owner) {
        this.owner = owner;
        setSpacing(16);
        setPadding(new Insets(0));
        construir();
        cargar();
    }

    private void construir() {

        getChildren().add(UiStyles.tituloPagina("Gestión de Usuarios"));

        HBox barra = new HBox(12);
        barra.setAlignment(Pos.CENTER_LEFT);
        txtBuscar.setPrefWidth(320);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnAgregar = UiStyles.botonPrimario("+ Agregar");
        btnAgregar.setOnAction(e -> nuevo());
        barra.getChildren().addAll(txtBuscar, spacer, btnAgregar);

        txtBuscar.setOnAction(e -> cargar());
        txtBuscar.textProperty().addListener((o, ov, nv) -> cargar());

        Tab tabClientes  = new Tab("Clientes",  buildTablaClientes());
        Tab tabEmpleados = new Tab("Empleados", buildTablaEmpleados());
        tabClientes.setClosable(false);
        tabEmpleados.setClosable(false);
        tabs.getTabs().addAll(tabClientes, tabEmpleados);
        tabs.setStyle("-fx-background-color:white;");

        VBox card = new VBox(16, barra, tabs);
        card.setPadding(new Insets(20));
        card.setStyle(UiStyles.CARD);
        VBox.setVgrow(card, Priority.ALWAYS);

        getChildren().add(card);
    }

    @SuppressWarnings("unchecked")
    private TableView<User> buildTablaClientes() {
        tabla.setItems(datos);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setStyle(UiStyles.TABLA);
        tabla.setPlaceholder(new Label("Sin usuarios registrados"));

        tabla.getColumns().addAll(
            col("ID",        u -> String.valueOf(u.getId()),         60),
            col("Nombre",    u -> u.getFirstName(),                   120),
            col("Apellidos", u -> u.getLastName(),                 120),
            col("Teléfono",  u -> u.getPhone(),                  110),
            col("Email",     u -> u.getEmail(),                     170),
            col("Ciudad",    u -> u.getCity(),                    110),
            colEstatus(),
            colAcciones(tabla, datos)
        );
        estilizarHeader(tabla);
        return tabla;
    }

    @SuppressWarnings("unchecked")
    private TableView<Employee> buildTablaEmpleados() {
        tablaEmp.setItems(datosEmp);
        tablaEmp.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaEmp.setStyle(UiStyles.TABLA);
        tablaEmp.setPlaceholder(new Label("Sin empleados registrados"));

        TableColumn<Employee, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colId.setPrefWidth(60);

        TableColumn<Employee, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFirstName()));

        TableColumn<Employee, String> colAp = new TableColumn<>("Apellidos");
        colAp.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLastName()));

        TableColumn<Employee, String> colUser = new TableColumn<>("User");
        colUser.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));

        TableColumn<Employee, String> colRol = new TableColumn<>("Rol");
        colRol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole().toString()));

        TableColumn<Employee, Void> colAcc = new TableColumn<>("Acciones");
        colAcc.setPrefWidth(100);
        colAcc.setCellFactory(c -> new TableCell<>() {
            final Button btnEdit = UiStyles.botonIcono("✏");
            final Button btnDel  = UiStyles.botonIconoRojo("🗑");
            { btnEdit.setOnAction(e -> editarEmpleado(getTableView().getItems().get(getIndex())));
              btnDel.setOnAction(e  -> deleteUser(getTableView().getItems().get(getIndex()).getId())); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(4, btnEdit, btnDel));
            }
        });

        tablaEmp.getColumns().addAll(colId, colNom, colAp, colUser, colRol, colAcc);
        estilizarHeader(tablaEmp);
        return tablaEmp;
    }

    private TableColumn<User, String> col(String titulo,
            java.util.function.Function<User, String> fn, double w) {
        TableColumn<User, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(d -> new SimpleStringProperty(fn.apply(d.getValue())));
        if (w > 0) c.setPrefWidth(w);
        return c;
    }

    private TableColumn<User, Void> colEstatus() {
        TableColumn<User, Void> c = new TableColumn<>("Estatus");
        c.setPrefWidth(90);
        c.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                boolean activo = getTableView().getItems().get(getIndex()).isActive();
                setGraphic(UiStyles.badgeActivo(activo));
            }
        });
        return c;
    }

    private TableColumn<User, Void> colAcciones(TableView<User> tv,
            ObservableList<User> lista) {
        TableColumn<User, Void> c = new TableColumn<>("Acciones");
        c.setPrefWidth(100);
        c.setCellFactory(col -> new TableCell<>() {
            final Button btnEdit = UiStyles.botonIcono("✏");
            final Button btnDel  = UiStyles.botonIconoRojo("🗑");
            { btnEdit.setOnAction(e -> editarCliente(tv.getItems().get(getIndex())));
              btnDel.setOnAction(e  -> deleteUser(tv.getItems().get(getIndex()).getId())); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(4, btnEdit, btnDel));
            }
        });
        return c;
    }

    private <T> void estilizarHeader(TableView<T> tv) {
        tv.widthProperty().addListener((o, ov, nv) -> {
            javafx.scene.control.skin.TableViewSkin<?> skin =
                    (javafx.scene.control.skin.TableViewSkin<?>) tv.getSkin();
            if (skin == null) return;
            javafx.scene.Node header = tv.lookup("TableHeaderRow");
            if (header != null)
                header.setStyle("-fx-background-color:" + UiStyles.ROJO + ";");
        });
        tv.skinProperty().addListener((o, ov, nv) -> {
            javafx.scene.Node header = tv.lookup("TableHeaderRow");
            if (header != null)
                header.setStyle("-fx-background-color:" + UiStyles.ROJO + ";");
        });
    }

    private void cargar() {
        String filtro = txtBuscar.getText().trim();
        List<User> clientes = ctrl.searchCustomers(filtro.isEmpty() ? null : filtro);
        datos.setAll(clientes);

        List<Employee> empleados = ctrl.searchEmployees(filtro.isEmpty() ? null : filtro);
        datosEmp.setAll(empleados);
    }

    private void nuevo() {
        boolean esCliente = tabs.getSelectionModel().getSelectedIndex() == 0;
        UserDialog dlg = new UserDialog(owner, null, esCliente, ctrl);
        dlg.showAndWait();
        cargar();
    }

    private void editarCliente(User u) {
        UserDialog dlg = new UserDialog(owner, u, true, ctrl);
        dlg.showAndWait();
        cargar();
    }

    private void editarEmpleado(Employee e) {
        UserDialog dlg = new UserDialog(owner, e, false, ctrl);
        dlg.showAndWait();
        cargar();
    }

    private void deleteUser(int id) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el usuario seleccionado?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null); confirm.initOwner(owner);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                int sesionId = SessionController.getInstance().getCurrentEmployee().getId();
                ctrl.deleteUser(id, sesionId);
                cargar();
            }
        });
    }
}
