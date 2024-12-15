set terminal png size 800,600
set output 'execution_time.png'
set title "Tiempo de Ejecución vs. Número de Hebras"
set xlabel "Número de Hebras"
set ylabel "Tiempo de Ejecución (s)"
set grid
plot "performance_data_fixed.dat" using 1:2 with linespoints title "Tiempo de Ejecución"
