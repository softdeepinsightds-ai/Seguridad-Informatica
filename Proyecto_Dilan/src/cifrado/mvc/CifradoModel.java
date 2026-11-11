package cifrado.mvc;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo que gestiona el historial de operaciones y delega el cálculo al
 * algoritmo seleccionado mediante el Patrón Strategy.
 */
public class CifradoModel {

    private CifradoStrategy estrategia;
    private final List<String> historial = new ArrayList<>();

    public CifradoModel(CifradoStrategy estrategia) {
        this.estrategia = estrategia;
    }

    public void setEstrategia(CifradoStrategy estrategia) {
        this.estrategia = estrategia;
    }

    public String cifrar(String texto, int a, int b) {
        String resultado = estrategia.cifrar(texto, a, b);
        agregarAlHistorial("CIFRAR", texto, resultado, a, b);
        return resultado;
    }

    public String descifrar(String textoCifrado, int a, int b) {
        String resultado = estrategia.descifrar(textoCifrado, a, b);
        agregarAlHistorial("DESCIFRAR", textoCifrado, resultado, a, b);
        return resultado;
    }

    private void agregarAlHistorial(String operacion, String entrada, String salida, int a, int b) {
        String registro = String.format("[%s] a=%d, b=%d\nEnt: %s\nSal: %s\n--------------------\n",
                operacion, a, b, entrada.replace("\n", " "), salida.replace("\n", " "));
        historial.add(0, registro);
    }

    public void limpiarHistorial() {
        historial.clear();
    }

    public String getHistorialTexto() {
        StringBuilder sb = new StringBuilder();
        for (String h : historial) {
            sb.append(h);
        }
        return sb.toString();
    }
}
