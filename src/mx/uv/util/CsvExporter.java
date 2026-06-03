package mx.uv.util;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvExporter {

    private CsvExporter() {}

    public static void exportar(Window owner, String nombreSugerido, List<String> encabezados, List<List<String>> filas) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Exportar a CSV");
        fc.setInitialFileName(nombreSugerido + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));

        File archivo = fc.showSaveDialog(owner);
        if (archivo == null) return;
        if (!archivo.getName().toLowerCase().endsWith(".csv"))
            archivo = new File(archivo.getAbsolutePath() + ".csv");

        try (Writer w = new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8)) {

            w.write('﻿');

            w.write(unirFila(encabezados));
            w.write("\r\n");
            for (List<String> fila : filas) {
                w.write(unirFila(fila));
                w.write("\r\n");
            }

            Alert a = new Alert(Alert.AlertType.INFORMATION,
                    "Archivo exportado:\n" + archivo.getAbsolutePath());
            a.setHeaderText(null);
            a.initOwner(owner);
            a.showAndWait();
        } catch (IOException e) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Error al exportar: " + e.getMessage());
            a.setHeaderText(null);
            a.initOwner(owner);
            a.showAndWait();
        }
    }

    private static String unirFila(List<String> valores) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valores.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(escaparCSV(valores.get(i)));
        }
        return sb.toString();
    }

    private static String escaparCSV(String valor) {
        if (valor == null) return "";
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n") || valor.contains("\r")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
