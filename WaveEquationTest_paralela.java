import java.util.concurrent.ForkJoinPool;

/**
 * Clase para realizar pruebas de rendimiento de la implementación paralela
 * y secuencial de la ecuación de onda 1D. Mide tiempos de ejecución para
 * calcular el speedup con diferentes números de hilos.
 */
public class WaveEquationTest_paralela {

    /**
     * Método principal para ejecutar pruebas de rendimiento.
     * 
     * @param args Argumentos de línea de comandos (no utilizados).
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

        int[] threadCounts = {1, 2, 4, 8, 16};
        double[] sequentialTimes = new double[1];
        double[] parallelTimes = new double[threadCounts.length];

        System.out.println("Iniciando prueba secuencial...");
        sequentialTimes[0] = runSequentialTest(c, L, nx, nt, dx, dt, cfl);
        System.out.printf("Tiempo secuencial: %.4f segundos\n", sequentialTimes[0]);

        System.out.println("\nIniciando pruebas paralelas...");
        for (int i = 0; i < threadCounts.length; i++) {
            parallelTimes[i] = runParallelTest(c, L, nx, nt, dx, dt, cfl, threadCounts[i]);
            System.out.printf("Tiempo con %d hebras: %.4f segundos\n", threadCounts[i], parallelTimes[i]);
        }

        exportData(threadCounts, sequentialTimes[0], parallelTimes, "performance_data.dat");
    }

    /**
     * Ejecuta la simulación secuencial de la ecuación de onda 1D y mide su tiempo de ejecución.
     * 
     * @param c   Velocidad de propagación.
     * @param L   Longitud del dominio.
     * @param nx  Número de puntos espaciales.
     * @param nt  Número de pasos temporales.
     * @param dx  Tamaño del paso espacial.
     * @param dt  Tamaño del paso temporal.
     * @param cfl Factor CFL calculado.
     * @return Tiempo de ejecución en segundos.
     */
    private static double runSequentialTest(double c, double L, int nx, int nt, double dx, double dt, double cfl) {
        double[][] u = new double[nt][nx];
        double[] x = new double[nx];
        for (int i = 0; i < nx; i++) x[i] = i * dx;

        long startTime = System.nanoTime();
        for (int n = 1; n < nt - 1; n++) {
            for (int i = 1; i < nx - 1; i++) {
                u[n + 1][i] = 2 * u[n][i] - u[n - 1][i] + cfl * cfl * (u[n][i + 1] - 2 * u[n][i] + u[n][i - 1]);
            }
        }
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1e9;
    }

    /**
     * Ejecuta la simulación paralela de la ecuación de onda 1D y mide su tiempo de ejecución.
     * 
     * @param c       Velocidad de propagación.
     * @param L       Longitud del dominio.
     * @param nx      Número de puntos espaciales.
     * @param nt      Número de pasos temporales.
     * @param dx      Tamaño del paso espacial.
     * @param dt      Tamaño del paso temporal.
     * @param cfl     Factor CFL calculado.
     * @param threads Número de hilos para el ForkJoinPool.
     * @return Tiempo de ejecución en segundos.
     */
    private static double runParallelTest(double c, double L, int nx, int nt, double dx, double dt, double cfl, int threads) {
        double[][] u = new double[nt][nx];
        double[] x = new double[nx];
        for (int i = 0; i < nx; i++) x[i] = i * dx;

        ForkJoinPool pool = new ForkJoinPool(threads);

        long startTime = System.nanoTime();
        for (int n = 1; n < nt - 1; n++) {
            pool.invoke(new WaveEquation1D_paralela.ComputeWaveTask(u, n, nx, cfl));
        }
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1e9;
    }

    /**
     * Exporta los resultados de rendimiento a un archivo.
     * 
     * @param threadCounts   Arreglo de conteos de hebras.
     * @param sequentialTime Tiempo secuencial.
     * @param parallelTimes  Tiempos paralelos.
     * @param filename       Nombre del archivo de salida.
     */
    private static void exportData(int[] threadCounts, double sequentialTime, double[] parallelTimes, String filename) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(filename)) {
            for (int i = 0; i < threadCounts.length; i++) {
                double speedup = sequentialTime / parallelTimes[i];
                writer.printf("%d %.4f %.4f\n", threadCounts[i], parallelTimes[i], speedup);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
