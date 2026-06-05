package mx.uv.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.controller.UserService;
import mx.uv.model.Employee;
import mx.uv.model.Role;
import mx.uv.model.User;

public class UserDialog extends Dialog<Boolean> {

    private final UserService ctrl;
    private final User usuario;
    private final boolean esCliente;

    private final TextField txtNombres   = UiStyles.campo("Ingrese nombre(s)");
    private final TextField txtApellidos = UiStyles.campo("Ingrese apellidos");
    private final TextField txtTelefono  = UiStyles.campo("Ingrese teléfono");
    private final TextField txtEmail     = UiStyles.campo("ingrese email");
    private final TextField txtCalle     = UiStyles.campo("Ingrese calle");
    private final TextField txtNumero = UiStyles.campo("Ingrese código");
    private final ComboBox<String> cmbCiudad = new ComboBox<>();
    private final ComboBox<String> cmbTipo   = new ComboBox<>();
    private final ComboBox<String> cmbEstatus = new ComboBox<>();
    private final TextField txtUsername  = UiStyles.campo("Ingrese usuario");
    private final PasswordField txtPass  = UiStyles.campoClave("Ingrese contraseña");
    private final ComboBox<String> cmbRol = new ComboBox<>();
    private VBox sectionSistema;

    public UserDialog(Stage owner, User usuario, boolean esCliente, UserService ctrl) {
        this.ctrl = ctrl;
        this.usuario = usuario;
        this.esCliente = esCliente;
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle(usuario == null ? "Nuevo usuario" : "Editar usuario");
        setResizable(true);
        construir();
        if (usuario != null) prellenar();
    }

    private void construir() {
        getDialogPane().setStyle("-fx-background-color:white;-fx-font-size:13;");
        getDialogPane().setPrefWidth(760);

        Label titulo = new Label(getTitle());
        titulo.setStyle("-fx-font-size:18;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK + ";");

        VBox secPersonal = seccion("INFORMACIÓN PERSONAL",
                row2(campo("Nombre(s) *", txtNombres), campo("Apellidos *", txtApellidos)),
                row2(campo("Teléfono", txtTelefono),   campo("Email", txtEmail))
        );

        cmbCiudad.getItems().addAll("Xalapa","Veracruz","Boca del Río","Coatepec","Orizaba","Córdoba","Poza Rica");
        cmbCiudad.setValue("Xalapa");
        cmbCiudad.setMaxWidth(Double.MAX_VALUE);
        cmbCiudad.setStyle(UiStyles.CAMPO);

        cmbTipo.getItems().addAll("Cliente","Empleado");
        cmbTipo.setValue(esCliente ? "Cliente" : "Empleado");
        cmbTipo.setMaxWidth(Double.MAX_VALUE);
        cmbTipo.setStyle(UiStyles.CAMPO);

        cmbEstatus.getItems().addAll("Activo","Inactivo");
        cmbEstatus.setValue("Activo");
        cmbEstatus.setMaxWidth(Double.MAX_VALUE);
        cmbEstatus.setStyle(UiStyles.CAMPO);

        VBox secDireccion = seccion("DIRECCIÓN",
                row1ancho("Calle", txtCalle, "Número", txtNumero),
                row3(campo("Ciudad", cmbCiudad), campo("Tipo de usuario", cmbTipo), campo("Estatus", cmbEstatus))
        );

        cmbRol.getItems().addAll("Administrador","Cajero");
        cmbRol.setValue("Administrador");
        cmbRol.setMaxWidth(Double.MAX_VALUE);
        cmbRol.setStyle(UiStyles.CAMPO);

        sectionSistema = seccion("INFORMACIÓN DE SISTEMA",
                row3(campo("Usuario", txtUsername), campo("Contraseña", txtPass), campo("Rol", cmbRol))
        );

        cmbTipo.valueProperty().addListener((o, ov, nv) -> {
            boolean emp = "Empleado".equals(nv);
            sectionSistema.setVisible(emp);
            sectionSistema.setManaged(emp);
        });
        boolean empInicial = !esCliente || (usuario instanceof Employee);
        sectionSistema.setVisible(empInicial);
        sectionSistema.setManaged(empInicial);

        Separator sep1 = new Separator(); sep1.setStyle("-fx-background-color:" + UiStyles.GRIS_BORDE + ";");
        Separator sep2 = new Separator(); sep2.setStyle("-fx-background-color:" + UiStyles.GRIS_BORDE + ";");

        VBox body = new VBox(16, titulo, sep1, secPersonal, sep2, secDireccion, sectionSistema);
        body.setPadding(new Insets(24, 32, 8, 32));
        body.setStyle("-fx-background-color:white;");

        getDialogPane().setContent(body);

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(btnCancelar, btnGuardar);

        Button btnG = (Button) getDialogPane().lookupButton(btnGuardar);
        btnG.setStyle(UiStyles.BTN_PRIMARIO);
        btnG.setDefaultButton(true);

        setResultConverter(bt -> {
            if (bt == btnGuardar) return save();
            return null;
        });
    }

    private void prellenar() {
        txtNombres.setText(usuario.getFirstName());
        txtApellidos.setText(usuario.getLastName());
        txtTelefono.setText(usuario.getPhone());
        txtEmail.setText(usuario.getEmail());
        txtCalle.setText(usuario.getStreet());
        txtNumero.setText(String.valueOf(usuario.getHouseNumber()));
        if (usuario.getCity() != null) cmbCiudad.setValue(usuario.getCity());
        cmbTipo.setValue(usuario instanceof Employee ? "Empleado" : "Cliente");
        cmbEstatus.setValue(usuario.isActive() ? "Activo" : "Inactivo");
        if (usuario instanceof Employee emp) {
            txtUsername.setText(emp.getUsername());
            if (emp.getRole() != null) cmbRol.setValue(emp.getRole().toString());
        }
    }

    private boolean save() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        if (nombres.isEmpty() || apellidos.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Nombre y Apellidos son requeridos.").showAndWait();
            return false;
        }
        boolean esEmp = "Empleado".equals(cmbTipo.getValue());
        if (esEmp) {
            if (txtUsername.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "El nombre de usuario es requerido.").showAndWait();
                return false;
            }
            if (usuario == null && txtPass.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "La contraseña es requerida.").showAndWait();
                return false;
            }
            String username = txtUsername.getText().trim();
            boolean esNuevo = (usuario == null);
            Integer excludeId = (usuario instanceof Employee emp && !esNuevo) ? emp.getId() : null;
            if (ctrl.usernameExists(username, excludeId)) {
                new Alert(Alert.AlertType.WARNING, "El nombre de usuario ya existe. Elija otro.").showAndWait();
                return false;
            }
            Employee e = (usuario instanceof Employee emp) ? emp : new Employee(null, null, null);
            e.setFirstName(nombres); e.setLastName(apellidos);
            e.setPhone(txtTelefono.getText().trim());
            e.setEmail(txtEmail.getText().trim());
            e.setStreet(txtCalle.getText().trim());
            try { e.setHouseNumber(Integer.parseInt(txtNumero.getText().trim())); } catch (NumberFormatException ignored) {}
            e.setCity(cmbCiudad.getValue());
            e.setActive("Activo".equals(cmbEstatus.getValue()));
            e.setUsername(txtUsername.getText().trim());
            if (!txtPass.getText().isEmpty()) e.setPassword(txtPass.getText());
            e.setRole("Administrador".equals(cmbRol.getValue()) ? Role.ADMINISTRADOR : Role.CAJERO);
            return ctrl.saveEmployee(e, usuario == null);
        } else {
            User u = (usuario != null && !(usuario instanceof Employee)) ? usuario : new User();
            u.setFirstName(nombres); u.setLastName(apellidos);
            u.setPhone(txtTelefono.getText().trim());
            u.setEmail(txtEmail.getText().trim());
            u.setStreet(txtCalle.getText().trim());
            try { u.setHouseNumber(Integer.parseInt(txtNumero.getText().trim())); } catch (NumberFormatException ignored) {}
            u.setCity(cmbCiudad.getValue());
            u.setActive("Activo".equals(cmbEstatus.getValue()));
            return ctrl.saveCustomer(u, usuario == null);
        }
    }

    private VBox seccion(String titulo, javafx.scene.Node... filas) {
        Label lbl = UiStyles.etiquetaSeccion(titulo);
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:" + UiStyles.GRIS_BORDE + ";");
        VBox box = new VBox(10);
        box.getChildren().addAll(lbl, sep);
        box.getChildren().addAll(filas);
        return box;
    }

    private HBox row2(javafx.scene.Node a, javafx.scene.Node b) {
        HBox h = new HBox(16, a, b);
        HBox.setHgrow(a, Priority.ALWAYS); HBox.setHgrow(b, Priority.ALWAYS);
        return h;
    }

    private HBox row3(javafx.scene.Node a, javafx.scene.Node b, javafx.scene.Node c) {
        HBox h = new HBox(16, a, b, c);
        HBox.setHgrow(a, Priority.ALWAYS); HBox.setHgrow(b, Priority.ALWAYS); HBox.setHgrow(c, Priority.ALWAYS);
        return h;
    }

    private HBox row1ancho(String lbl1, javafx.scene.Node n1, String lbl2, javafx.scene.Node n2) {
        VBox v1 = campo(lbl1, n1); HBox.setHgrow(v1, Priority.ALWAYS);
        VBox v2 = campo(lbl2, n2); v2.setPrefWidth(160);
        HBox h = new HBox(16, v1, v2);
        return h;
    }

    private VBox campo(String label, javafx.scene.Node control) {
        VBox v = new VBox(4, UiStyles.etiqueta(label), control);
        VBox.setVgrow(control, Priority.NEVER);
        if (control instanceof Control c) c.setMaxWidth(Double.MAX_VALUE);
        return v;
    }
}
