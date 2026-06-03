package mx.uv.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.controller.ProductController;
import mx.uv.model.SaleProduct;

public class ProductDialog extends Dialog<Boolean> {

    private final SaleProduct producto;
    private final ProductController ctrl;

    private final TextField txtCodigo      = UiStyles.campo("Ej. PIZ001");
    private final TextField txtNombre      = UiStyles.campo("Nombre del producto");
    private final TextArea  txtDescripcion = new TextArea();
    private final TextField txtPrecio      = UiStyles.campo("0.00");
    private final TextField txtCantidad    = UiStyles.campo("0");
    private final TextArea  txtRestricc    = new TextArea();

    public ProductDialog(Stage owner, SaleProduct producto, ProductController ctrl) {
        this.producto = producto;
        this.ctrl = ctrl;
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle(producto == null ? "Nuevo Product" : "Editar Product");
        setResizable(true);
        construir();
        if (producto != null) prellenar();
    }

    private void construir() {
        getDialogPane().setStyle("-fx-background-color:white;");
        getDialogPane().setPrefWidth(600);

        Label titulo = new Label(getTitle());
        titulo.setStyle("-fx-font-size:18;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK + ";");

        txtDescripcion.setPromptText("Descripción del producto");
        txtDescripcion.setPrefRowCount(3);
        txtDescripcion.setStyle(UiStyles.CAMPO + "-fx-font-size:13;");
        txtDescripcion.setWrapText(true);

        txtRestricc.setPromptText("Restricciones (alérgenos, etc.)");
        txtRestricc.setPrefRowCount(2);
        txtRestricc.setStyle(UiStyles.CAMPO + "-fx-font-size:13;");
        txtRestricc.setWrapText(true);

        if (producto != null) txtCodigo.setEditable(false);

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(12);
        grid.getColumnConstraints().addAll(col50(), col50());

        int r = 0;
        addRow(grid, r++, "Código *",     txtCodigo,      "Nombre *",       txtNombre);
        addRowFull(grid, r++, "Descripción", txtDescripcion);
        addRow(grid, r++, "Precio *",     txtPrecio,      "Cantidad",       txtCantidad);
        addRowFull(grid, r++, "Restricciones", txtRestricc);

        VBox body = new VBox(16, titulo, new Separator(), grid);
        body.setPadding(new Insets(24, 32, 8, 32));
        body.setStyle("-fx-background-color:white;");
        getDialogPane().setContent(body);

        ButtonType btnG = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnC = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(btnC, btnG);
        ((Button) getDialogPane().lookupButton(btnG)).setStyle(UiStyles.BTN_PRIMARIO);

        setResultConverter(bt -> bt == btnG ? save() : null);
        ((Button) getDialogPane().lookupButton(btnG))
                .addEventFilter(javafx.event.ActionEvent.ACTION, e -> { if (!save()) e.consume(); });
    }

    private void prellenar() {
        txtCodigo.setText(producto.getCode());
        txtNombre.setText(producto.getName());
        txtDescripcion.setText(producto.getDescription());
        txtPrecio.setText(String.valueOf(producto.getPrice()));
        txtCantidad.setText(String.valueOf(producto.getQuantity()));
        txtRestricc.setText(producto.getRestrictions() != null ? producto.getRestrictions() : "");
    }

    private boolean save() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        if (codigo.isEmpty() || nombre.isEmpty() || precioStr.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Código, Nombre y Precio son requeridos.").showAndWait();
            return false;
        }
        if (!nombre.matches("[\\p{L}0-9 ]+")) {
            new Alert(Alert.AlertType.WARNING, "El nombre solo puede contener letras, números y espacios.").showAndWait();
            return false;
        }
        float precio;
        try { precio = Float.parseFloat(precioStr); }
        catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "El precio debe ser un número válido.").showAndWait();
            return false;
        }
        if (precio <= 0) {
            new Alert(Alert.AlertType.WARNING, "El precio debe ser mayor a cero.").showAndWait();
            return false;
        }
        int cantidad = 0;
        try { cantidad = Integer.parseInt(txtCantidad.getText().trim()); } catch (NumberFormatException ignored) {}

        SaleProduct p = producto != null ? producto : new SaleProduct();
        p.setCode(codigo); p.setName(nombre);
        p.setDescription(txtDescripcion.getText().trim());
        p.setPrice(precio); p.setQuantity(cantidad);
        p.setRestrictions(txtRestricc.getText().trim());
        p.setAvailable(true);
        return ctrl.save(p, producto == null);
    }

    private void addRow(GridPane g, int r, String l1, javafx.scene.Node n1, String l2, javafx.scene.Node n2) {
        VBox v1 = new VBox(4, UiStyles.etiqueta(l1), n1);
        VBox v2 = new VBox(4, UiStyles.etiqueta(l2), n2);
        n1.setStyle(((TextField)n1).getStyle()); ((Control)n1).setMaxWidth(Double.MAX_VALUE);
        ((Control)n2).setMaxWidth(Double.MAX_VALUE);
        g.add(v1, 0, r); g.add(v2, 1, r);
    }

    private void addRowFull(GridPane g, int r, String l, javafx.scene.Node n) {
        VBox v = new VBox(4, UiStyles.etiqueta(l), n);
        g.add(v, 0, r, 2, 1);
    }

    private ColumnConstraints col50() {
        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(50);
        return c;
    }
}
