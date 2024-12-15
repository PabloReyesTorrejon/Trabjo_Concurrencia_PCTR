import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

/**
 * Clase para resolver la ecuación de ondas 1D utilizando paralelización con ForkJoinPool.
 */
public class WaveEquation1D_paralela {
    /**
     * Método principal que ejecuta la simulación en paralelo.
     * @param args Argumentos de la línea de comandos (no se usan).
     */
    public static void main(String[] args) {
        double c = 1.0, L = 10.0, T = 5.0;
        int nx = 101, nt = 200;
        double dx = L / (nx - 1), dt = T / nt;
        double cfl = c * dt / dx;

        if (cfl > 1) {
            System.out.println("Error: Condición CFL no cumplida.");
            return;
        }

        double[][] u = new double[nt][nx];
        double[] x = new double[nx];
        for (int i = 0; i < nx; i++) x[i] = i * dx;

        // Condiciones iniciales
        for (int i = 0; i < nx; i++) u[0][i] = initialCondition(x[i]);
        for (int i = 1; i < nx - 1; i++) {
            u[1][i] = u[0][i] + 0.5 * cfl * cfl * (u[0][i+1] - 2 * u[0][i] + u[0][i-1]);
        }

        // Paralelización
        ForkJoinPool pool = new ForkJoinPool();
        for (int n = 1; n < nt - 1; n++) {
            pool.invoke(new ComputeWaveTask(u, n, nx, cfl));
        }

        // Resultados
        System.out.println("Resultados finales:");
        for (int i = 0; i < nx; i++) {
            System.out.printf("x=%.2f, u=%.2f\n", x[i], u[nt-1][i]);
        }
    }

    /**
     * Clase interna que representa una tarea paralelizable para el cálculo de la ecuación.
     */
    static class ComputeWaveTask extends RecursiveAction {
        /** Límite para dividir tareas en subrangos más pequeños. */
        private static final int THRESHOLD = 10;

        /** Matriz que almacena las soluciones de la ecuación en cada paso temporal. */
        private final double[][] u;

        /** Paso temporal actual en el que se realiza el cálculo. */
        private final int n;

        /** Número total de puntos espaciales en la simulación. */
        private final int nx;

        /** Índice inicial del rango de puntos espaciales a calcular. */
        private final int start;

        /** Índice final del rango de puntos espaciales a calcular. */
        private final int end;

        /** Número CFL (Condición de estabilidad de Courant–Friedrichs–Lewy). */
        private final double cfl;

        /**
         * Constructor principal que inicializa una tarea para el cálculo paralelo.
         * @param u Matriz de la solución.
         * @param n Paso temporal actual.
         * @param nx Número total de puntos espaciales.
         * @param cfl Número CFL.
         */
        public ComputeWaveTask(double[][] u, int n, int nx, double cfl) {
            this(u, n, nx, cfl, 1, nx - 1);
        }

        /**
         * Constructor que permite definir un rango específico de puntos para el cálculo.
         * @param u Matriz de la solución.
         * @param n Paso temporal actual.
         * @param nx Número total de puntos espaciales.
         * @param cfl Número CFL.
         * @param start Índice inicial del rango.
         * @param end Índice final del rango.
         */
        private ComputeWaveTask(double[][] u, int n, int nx, double cfl, int start, int end) {
            this.u = u;
            this.n = n;
            this.nx = nx;
            this.cfl = cfl;
            this.start = start;
            this.end = end;
        }

        /**
         * Método que realiza el cálculo de la solución en el rango definido.
         * Si el rango es mayor que el umbral {@link #THRESHOLD}, se divide en subtareas.
         */
        @Override
        protected void compute() {
            if (end - start <= THRESHOLD) {
                for (int i = start; i < end; i++) {
                    u[n+1][i] = 2 * u[n][i] - u[n-1][i] + cfl * cfl * (u[n][i+1] - 2 * u[n][i] + u[n][i-1]);
                }
            } else {
                int mid = (start + end) / 2;
                invokeAll(new ComputeWaveTask(u, n, nx, cfl, start, mid),
                          new ComputeWaveTask(u, n, nx, cfl, mid, end));
            }
        }
    }

    /**
     * Función que define la condición inicial f(x) como una onda triangular.
     * @param x Posición en el dominio espacial.
     * @return Valor de la condición inicial en la posición x.
     */
    private static double initialCondition(double x) {
        double L = 10.0;
        return (x < L / 2.0) ? 2.0 * x / L : 2.0 * (1 - x / L);
    }
}
