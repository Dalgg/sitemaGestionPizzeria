package mx.uv.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AboutPanel extends VBox {

    public AboutPanel(Stage owner) {
        setSpacing(24);
        setPadding(new Insets(0));
        construir();
    }

    private void construir() {
        VBox titulo = new VBox(4);
        Label t = new Label("Acerca de");
        t.setStyle("-fx-font-size:26;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK);
        Label sub = new Label("Integrantes del proyecto");
        sub.setStyle("-fx-font-size:13;-fx-text-fill:" + UiStyles.GRIS_TEXTO);
        titulo.getChildren().addAll(t, sub);

        String[][] integrantes = {
            {"Diego León", "4° Semestre", "Ingeniería de Software", "Universidad Veracruzana"},
            {"Jorge Araujo", "4° Semestre", "Ingeniería de Software", "Universidad Veracruzana"},
            {"Valentin Benavidez", "4° Semestre", "Ingeniería de Software", "Universidad Veracruzana"},
            {"Luis Jesus Guzmán", "4° Semestre", "Ingeniería de Software", "Universidad Veracruzana"},
        };

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.getColumnConstraints().addAll(col50(), col50());

        for (int i = 0; i < integrantes.length; i++) {
            grid.add(cardIntegrante(integrantes[i]), i % 2, i / 2);
        }

        HBox contenido = new HBox(32);
        contenido.getChildren().addAll(grid);
        HBox.setHgrow(grid, Priority.ALWAYS);

        VBox card = new VBox(20, titulo, contenido);
        card.setPadding(new Insets(32));
        card.setStyle(UiStyles.CARD);
        VBox.setVgrow(card, Priority.ALWAYS);

        getChildren().add(card);
    }

    private VBox cardIntegrante(String[] datos) {
        VBox card = new VBox(12);
        card.setStyle(UiStyles.CARD + "-fx-padding:20;");

        Label ico = new Label("👤");
        ico.setStyle("-fx-font-size:20;-fx-text-fill:" + UiStyles.GRIS_TEXTO);
        Label nombre = new Label(datos[0]);
        nombre.setStyle("-fx-font-size:16;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK);
        HBox encabezado = new HBox(10, ico, nombre);
        encabezado.setAlignment(Pos.CENTER_LEFT);

        Separator sep = UiStyles.separador();

        VBox info = new VBox(8,
            fila("📅", "Semestre:",    datos[1]),
            fila("🎓", "Carrera:",     datos[2]),
            fila("🏛", "Institución:", datos[3])
        );

        card.getChildren().addAll(encabezado, sep, info);
        return card;
    }

    private HBox fila(String ico, String lbl, String valor) {
        Label i = new Label(ico);
        i.setStyle("-fx-font-size:13;");
        Label l = new Label(lbl);
        l.setStyle("-fx-font-size:12;-fx-text-fill:" + UiStyles.GRIS_TEXTO);
        Label v = new Label("  " + valor);
        v.setStyle("-fx-font-size:12;-fx-font-weight:bold;-fx-text-fill:" + UiStyles.TEXTO_DARK);
        HBox h = new HBox(6, i, l, v);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private ColumnConstraints col50() {
        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(50);
        return c;
    }
}
