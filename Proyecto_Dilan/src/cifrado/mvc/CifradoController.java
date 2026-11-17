package cifrado.mvc;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Controlador optimizado con validaciones claras en español y manejo seguro de
 * excepciones.
 */
public class CifradoController {

    private final CifradoModel model;
    private final CifradoView view;

    public CifradoController(CifradoModel model, CifradoView view) {
        this.model = model;
        this.view = view;

        this.view.addCifrarListener(e -> procesarCifrado(true));
        this.view.addDescifrarListener(e -> procesarCifrado(false));

        this.view.addThemeToggleListener(e -> view.alternarTema());

        this.view.addCargarArchivoListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(view.getFrame()) == JFileChooser.APPROVE_OPTION) {
                try {
                    Path ruta = fileChooser.getSelectedFile().toPath();
                    String contenido = Files.readString(ruta);
                    view.setTextoEntrada(contenido);
                    view.setEstado("Archivo de texto cargado correctamente.");
                } catch (IOException ex) {
                    view.mostrarMensajeError("No se pudo leer el archivo: " + ex.getMessage());
                }
            }
        });

        this.view.addExportarArchivoListener(e -> {
            String resultado = view.getTextoResultado();
            if (resultado.isEmpty()) {
                view.mostrarMensajeError("No hay ningún resultado procesado disponible para exportar.");
                return;
            }
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(view.getFrame()) == JFileChooser.APPROVE_OPTION) {
                try {
                    Path ruta = fileChooser.getSelectedFile().toPath();
                    Files.writeString(ruta, resultado);
                    view.setEstado("Resultado exportado con éxito.");
                } catch (IOException ex) {
                    view.mostrarMensajeError("No se pudo guardar el archivo: " + ex.getMessage());
                }
            }
        });

        this.view.addUsarResultadoListener(e -> {
            String res = view.getTextoResultado();
            if (res.isEmpty()) {
                view.mostrarMensajeError("No hay ningún resultado disponible para reutilizar como entrada.");
                return;
            }
            view.setTextoEntrada(res);
            view.setTextoResultado("");
            view.setEstado("Resultado transferido al campo de entrada.");
        });

        this.view.addLimpiarListener(e -> {
            view.setTextoEntrada("");
            view.setTextoResultado("");
            view.setEstado("Áreas de texto restablecidas.");
        });

        this.view.addLimpiarHistorialListener(e -> {
            model.limpiarHistorial();
            view.setHistorial("");
            view.setEstado("Historial de operaciones borrado.");
        });
    }

    private void procesarCifrado(boolean esCifrar) {
        try {
            String texto = view.getTextoEntrada();
            if (texto.isEmpty()) {
                view.mostrarMensajeError("El campo de texto de entrada no puede estar vacío. Por favor, escriba o cargue un mensaje.");
                return;
            }

            String textoB = view.getTextoB();
            if (textoB.isEmpty()) {
                view.mostrarMensajeError("La clave 'b' es obligatoria. Por favor, ingrese un número entero entre 0 y 41.");
                return;
            }

            int a = view.getValorA();
            int b = Integer.parseInt(textoB);

            String resultado = esCifrar ? model.cifrar(texto, a, b) : model.descifrar(texto, a, b);

            view.setTextoResultado(resultado);
            view.setHistorial(model.getHistorialTexto());
            view.setEstado(esCifrar ? "Proceso de cifrado completado con éxito." : "Proceso de descifrado completado con éxito.");

        } catch (NumberFormatException ex) {
            view.mostrarMensajeError("La clave 'b' ingresada no tiene un formato numérico válido.");
        } catch (IllegalArgumentException ex) {
            view.mostrarMensajeError(ex.getMessage());
        }
    }
}
