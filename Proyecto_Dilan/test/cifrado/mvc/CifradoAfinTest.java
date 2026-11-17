package cifrado.mvc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias en vivo configuradas con JUnit 5 para validar el correcto
 * funcionamiento matemático del algoritmo afín extendido.
 */
public class CifradoAfinTest {

    @Test
    public void testMCD() {
        assertEquals(1, CifradoAfinStrategy.mcd(5, 42));
        assertEquals(7, CifradoAfinStrategy.mcd(14, 21));
    }

    @Test
    public void testInversoModular() {
        CifradoAfinStrategy strategy = new CifradoAfinStrategy();
        int inverso = strategy.inversoModular(5);
        assertEquals(1, (5 * inverso) % 42);
    }

    @Test
    public void testIdaYVueltaCompleto() {
        CifradoAfinStrategy strategy = new CifradoAfinStrategy();
        String original = "Laboratorio de Cifrado 2026: ÁÉÍÓÚ Ñ!";
        int a = 5;
        int b = 8;

        String cifrado = strategy.cifrar(original, a, b);
        String descifrado = strategy.descifrar(cifrado, a, b);

        assertEquals(original, descifrado, "El texto descifrado debe coincidir exactamente con el original.");
    }

    @Test
    public void testConservaMayusculasMinusculasYMarcador() {
        CifradoAfinStrategy strategy = new CifradoAfinStrategy();
        String original = "MeZcla ~ de texto";

        String cifrado = strategy.cifrar(original, 5, 8);
        String descifrado = strategy.descifrar(cifrado, 5, 8);

        assertEquals(original, descifrado);
    }
}
