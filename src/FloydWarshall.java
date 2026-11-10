/**
 * Algoritmo de Floyd-Warshall: caminos más cortos entre TODOS los pares de
 * vértices, usando programación dinámica.
 *
 * Idea: dist[i][j] = mejor distancia conocida de i a j. Se prueba, para cada
 * vértice k, si pasar por k mejora el camino:
 *
 *     dist[i][j] = min( dist[i][j], dist[i][k] + dist[k][j] )
 *
 * Después de procesar el vértice k, dist[i][j] es el camino más corto de i a j
 * que solo usa como intermedios los vértices 0..k.
 *
 * Complejidad: O(V^3) en tiempo, O(V^2) en memoria.
 * Funciona con grafos dirigidos o no dirigidos y con pesos NEGATIVOS,
 * siempre que no haya ciclos negativos (se detectan: dist[i][i] < 0).
 *
 * Representación de entrada: matriz de adyacencia donde pesos[i][j] es el peso
 * de la arista i -> j, 0 en la diagonal e INF si no hay arista.
 */
public class FloydWarshall {

    /** "Infinito": suficientemente grande, pero que sumado a otro INF no desborda un int. */
    public static final int INF = 1_000_000_000;

    private final int n;
    private final int[][] dist;       // distancias mínimas
    private final int[][] siguiente;  // siguiente[i][j] = primer vértice después de i en el mejor camino a j
    private boolean cicloNegativo;

    public FloydWarshall(int[][] pesos) {
        n = pesos.length;
        dist = new int[n][n];
        siguiente = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    // la distancia a uno mismo es 0, salvo que haya un bucle negativo
                    dist[i][j] = pesos[i][j] < 0 ? pesos[i][j] : 0;
                    siguiente[i][j] = -1;
                } else {
                    dist[i][j] = pesos[i][j];
                    siguiente[i][j] = pesos[i][j] < INF ? j : -1;
                }
            }
        }
        ejecutar();
    }

    private void ejecutar() {
        for (int k = 0; k < n; k++) {               // vértice intermedio
            for (int i = 0; i < n; i++) {           // origen
                if (dist[i][k] == INF) {
                    continue;                       // no se puede llegar a k desde i
                }
                for (int j = 0; j < n; j++) {       // destino
                    if (dist[k][j] == INF) {
                        continue;                   // no se puede salir de k hacia j
                    }
                    int nueva = dist[i][k] + dist[k][j];
                    if (nueva < -INF) {
                        nueva = -INF;               // evita desbordes cuando hay ciclos negativos
                    }
                    if (nueva < dist[i][j]) {
                        dist[i][j] = nueva;
                        siguiente[i][j] = siguiente[i][k];
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            if (dist[i][i] < 0) {
                cicloNegativo = true;
            }
        }
    }

    /** Distancia mínima de i a j (INF si no hay camino). Solo es confiable si no hay ciclo negativo. */
    public int distancia(int i, int j) {
        return dist[i][j];
    }

    public boolean hayCaminoEntre(int i, int j) {
        return dist[i][j] < INF;
    }

    public boolean hayCicloNegativo() {
        return cicloNegativo;
    }

    /**
     * Reconstruye el camino más corto de origen a destino como arreglo de vértices.
     * Devuelve null si no hay camino o si el grafo tiene un ciclo negativo.
     */
    public int[] camino(int origen, int destino) {
        if (cicloNegativo || dist[origen][destino] >= INF) {
            return null;
        }
        int largo = 1;
        int actual = origen;
        while (actual != destino) {
            actual = siguiente[actual][destino];
            largo++;
        }
        int[] ruta = new int[largo];
        ruta[0] = origen;
        actual = origen;
        int pos = 1;
        while (actual != destino) {
            actual = siguiente[actual][destino];
            ruta[pos] = actual;
            pos++;
        }
        return ruta;
    }

    public int numVertices() {
        return n;
    }
}
