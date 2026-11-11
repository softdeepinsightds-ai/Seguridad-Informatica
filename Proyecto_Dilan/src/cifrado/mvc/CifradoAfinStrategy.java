/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cifrado.mvc;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación concreta del Cifrado Afín extendido (Módulo 42), soportando
 * letras, tildes comunes en español, la 'ñ' y números del 0 al 9.
 */
public class CifradoAfinStrategy implements CifradoStrategy {

    private static final char[] ALFABETO = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'Á', 'É', 'Í', 'Ó', 'Ú', 'Ñ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private static final int MODULO = ALFABETO.length;
    private static final char MARCADOR_MINUSCULA = '~';

    public static Integer[] getValoresAValidos() {
        List<Integer> validos = new ArrayList<>();
        for (int i = 1; i < MODULO; i++) {
            if (mcd(i, MODULO) == 1) {
                validos.add(i);
            }
        }
        return validos.toArray(new Integer[0]);
    }

    public static int mcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    public boolean esBValido(int b) {
        return b >= 0 && b < MODULO;
    }

    public int inversoModular(int a) {
        for (int x = 1; x < MODULO; x++) {
            if ((a * x) % MODULO == 1) {
                return x;
            }
        }
        throw new IllegalArgumentException("El valor de a=" + a + " no tiene inverso modular módulo " + MODULO + ".");
    }

    public int normalizarModulo(int valor, int modulo) {
        int resultado = valor % modulo;
        if (resultado < 0) {
            resultado += modulo;
        }
        return resultado;
    }

    private int obtenerIndice(char c) {
        char upper = Character.toUpperCase(c);
        for (int i = 0; i < ALFABETO.length; i++) {
            if (ALFABETO[i] == upper) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String cifrar(String texto, int a, int b) {
        if (!esBValido(b)) {
            throw new IllegalArgumentException("El valor de 'b' debe estar entre 0 y " + (MODULO - 1) + ".");
        }

        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            int idx = obtenerIndice(c);

            if (idx != -1) {
                if (Character.isLowerCase(c)) {
                    resultado.append(MARCADOR_MINUSCULA);
                }
                int cifradoPos = normalizarModulo((a * idx + b), MODULO);
                resultado.append(ALFABETO[cifradoPos]);
            } else if (c == MARCADOR_MINUSCULA) {
                resultado.append(MARCADOR_MINUSCULA).append(MARCADOR_MINUSCULA);
            } else {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }

    @Override
    public String descifrar(String textoCifrado, int a, int b) {
        if (!esBValido(b)) {
            throw new IllegalArgumentException("El valor de 'b' debe estar entre 0 y " + (MODULO - 1) + ".");
        }

        int aInverso = inversoModular(a);
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < textoCifrado.length(); i++) {
            char c = textoCifrado.charAt(i);
            if (c == MARCADOR_MINUSCULA) {
                if (i + 1 < textoCifrado.length()
                        && textoCifrado.charAt(i + 1) == MARCADOR_MINUSCULA) {
                    resultado.append(MARCADOR_MINUSCULA);
                    i++;
                    continue;
                }
                if (i + 1 < textoCifrado.length()) {
                    c = textoCifrado.charAt(++i);
                    agregarCaracterDescifrado(resultado, c, aInverso, b, true);
                } else {
                    resultado.append(MARCADOR_MINUSCULA);
                }
                continue;
            }
            agregarCaracterDescifrado(resultado, c, aInverso, b, Character.isLowerCase(c));
        }
        return resultado.toString();
    }

    private void agregarCaracterDescifrado(StringBuilder resultado, char c, int aInverso, int b,
            boolean minuscula) {
        int idx = obtenerIndice(c);

        if (idx != -1) {
            int descifradoPos = normalizarModulo(aInverso * (idx - b), MODULO);
            char descifradoChar = ALFABETO[descifradoPos];
            if (minuscula) {
                resultado.append(Character.toLowerCase(descifradoChar));
            } else {
                resultado.append(descifradoChar);
            }
        } else {
            resultado.append(c);
        }
    }
}
