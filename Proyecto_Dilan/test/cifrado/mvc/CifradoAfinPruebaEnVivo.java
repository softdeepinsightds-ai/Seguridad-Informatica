package cifrado.mvc;

public final class CifradoAfinPruebaEnVivo {

    private static int pruebasEjecutadas;

    private CifradoAfinPruebaEnVivo() {
    }

    public static void main(String[] args) {
        CifradoAfinStrategy strategy = new CifradoAfinStrategy();

        comprobar("MCD de coprimos", CifradoAfinStrategy.mcd(5, 42) == 1);
        comprobar("MCD de 14 y 21", CifradoAfinStrategy.mcd(14, 21) == 7);
        comprobar("Inverso modular de 5",
                (5 * strategy.inversoModular(5)) % 42 == 1);

        String original = "Laboratorio de Cifrado 2026: ÁÉÍÓÚ Ñ!";
        String cifrado = strategy.cifrar(original, 5, 8);
        comprobar("Cifrar y descifrar texto con tildes y enie",
                original.equals(strategy.descifrar(cifrado, 5, 8)));
        String textoConMarcador = "Caso~con tilde";
        comprobar("Conservar mayusculas, minusculas y el caracter ~",
                textoConMarcador.equals(strategy.descifrar(
                        strategy.cifrar(textoConMarcador, 5, 8), 5, 8)));
        comprobar("Conservar espacios y signos",
                strategy.cifrar("Hola, mundo!", 5, 8).contains(", "));
        comprobar("Rechazar clave b fuera de rango",
                lanzaIllegalArgumentException(() -> strategy.cifrar("TEXTO", 5, 42)));
        comprobar("Rechazar clave a sin inverso",
                lanzaIllegalArgumentException(() -> strategy.descifrar("TEXTO", 6, 8)));

        System.out.println();
        System.out.println("Resultado: " + pruebasEjecutadas + " pruebas correctas.");
    }

    private static void comprobar(String nombre, boolean resultado) {
        if (!resultado) {
            throw new AssertionError("FALLO: " + nombre);
        }
        pruebasEjecutadas++;
        System.out.println("OK: " + nombre);
    }

    private static boolean lanzaIllegalArgumentException(Runnable operacion) {
        try {
            operacion.run();
            return false;
        } catch (IllegalArgumentException exception) {
            return true;
        }
    }
}
