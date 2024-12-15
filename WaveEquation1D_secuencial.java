import java.util.Arrays;

/**
 * Clase para resolver la ecuación de ondas 1D de forma secuencial.
 * Utiliza diferencias finitas en el tiempo y el espacio para aproximar la solución.
 */
public class WaveEquation1D_secuencial {
    /**
     * Método principal que ejecuta la simulación.
     * @param args Argumentos de la línea de comandos (no se usan).
     */
    public static void main(String[] args) {
        // Configuración inicial
        double c = 1.0; // Velocidad de propagación
        double L = 10.0; // Longitud del dominio espacial
        double T = 5.0; // Tiempo total de simulación
        int nx = 101; // Número de puntos espaciales
        int nt = 200; // Número de pasos temporales
        double dx = L / (nx - 1); // Paso espacial
        double dt = T / nt; // Paso temporal
        double cfl = c * dt / dx; // Factor CFL
        
        if (cfl > 1) {
            System.out.println("Error: Condición CFL no cumplida. Reducir dt o aumentar dx.");
            return;
        }
        
        // Inicialización de variables
        double[][] u = new double[nt][nx]; // Solución u(x,t)
        double[] x = new double[nx]; // Puntos espaciales
        for (int i = 0; i < nx; i++) {
            x[i] = i * dx; // Posiciones espaciales
        }
        
        // Condiciones iniciales
        for (int i = 0; i < nx; i++) {
            u[0][i] = initialCondition(x[i]); // u(x,0)
        }
        for (int i = 1; i < nx - 1; i++) {
            u[1][i] = u[0][i] + 0.5 * cfl * cfl * (u[0][i+1] - 2 * u[0][i] + u[0][i-1]);
        }

        // Bucle principal
        for (int n = 1; n < nt - 1; n++) {
            for (int i = 1; i < nx - 1; i++) {
                u[n+1][i] = 2 * u[n][i] - u[n-1][i] + cfl * cfl * (u[n][i+1] - 2 * u[n][i] + u[n][i-1]);
            }
            u[n+1][0] = 0; // Condición de frontera izquierda
            u[n+1][nx-1] = 0; // Condición de frontera derecha
        }

        // Resultados finales
        System.out.println("Resultados finales:");
        for (int i = 0; i < nx; i++) {
            System.out.printf("x=%.2f, u=%.2f\n", x[i], u[nt-1][i]);
        }
    }

    /**
     * Función para definir la condición inicial f(x).
     * @param x Posición en el dominio espacial.
     * @return Valor de la condición inicial en la posición x.
     */
    private static double initialCondition(double x) {
        double L = 10.0;
        if (x < L / 2.0) {
            return 2.0 * x / L;
        } else {
            return 2.0 * (1 - x / L);
        }
    }
}
