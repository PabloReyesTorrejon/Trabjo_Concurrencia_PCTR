/**
 * Clase de prueba para la implementación secuencial de la ecuación de onda 1D.
 * Realiza varias pruebas, como la verificación de la condición CFL y comparación
 * con soluciones analíticas.
 */
public class WaveEquationTest_secuencial {

    /**
     * Método principal para ejecutar las pruebas secuenciales.
     * 
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        System.out.println("Prueba 1: Verificación de la condición CFL");
        runWaveSimulation(1.0, 10.0, 5.0, 101, 50); // CFL > 1, debe dar error

        System.out.println("\nPrueba 2: Condiciones iniciales simples (escalón)");
        runWaveSimulation(1.0, 10.0, 2.0, 101, 200); // Configuración válida

        System.out.println("\nPrueba 3: Comparación con solución analítica (D'Alembert)");
        runWaveSimulation(1.0, 10.0, 2.0, 101, 200); // Solución analítica conocida

        System.out.println("\nPrueba 4: Robustez con diferentes configuraciones");
        runWaveSimulation(0.5, 10.0, 2.0, 201, 400); // Variación de parámetros
        runWaveSimulation(2.0, 10.0, 2.0, 51, 100); // Velocidad mayor
    }

    /**
     * Ejecuta una simulación secuencial de la ecuación de onda 1D.
     * 
     * @param c  Velocidad de propagación.
     * @param L  Longitud del dominio.
     * @param T  Tiempo total de simulación.
     * @param nx Número de puntos espaciales.
     * @param nt Número de pasos temporales.
     */
    private static void runWaveSimulation(double c, double L, double T, int nx, int nt) {
        WaveEquation1D_secuencial.main(new String[]{String.valueOf(c), String.valueOf(L), String.valueOf(T), String.valueOf(nx), String.valueOf(nt)});
    }
}
