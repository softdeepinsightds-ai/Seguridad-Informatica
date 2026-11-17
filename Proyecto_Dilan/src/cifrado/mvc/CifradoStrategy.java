/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cifrado.mvc;

/**
 * Interfaz que define la estrategia común para algoritmos de cifrado,
 * permitiendo escalar el proyecto fácilmente hacia nuevos cifrados (César,
 * Vigenère, etc.).
 */
public interface CifradoStrategy {

    String cifrar(String texto, int a, int b);

    String descifrar(String textoCifrado, int a, int b);
}
