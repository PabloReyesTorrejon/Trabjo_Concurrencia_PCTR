set terminal png size 800,600
set output 'speedup.png'
set title "Speedup vs. Número de Hebras"
set xlabel "Número de Hebras"
set ylabel "Speedup"
set grid
plot "performance_data_fixed.dat" using 1:3 with linespoints title "Speedup"
