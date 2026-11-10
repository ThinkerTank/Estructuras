/**
 * Demostración de Kruskal y Floyd-Warshall.
 * Compilar:  javac -d out src/*.java
 * Ejecutar:  java -cp out Main
 */
public class Main {

    public static void main(String[] args) {
        demoKruskal();
        System.out.println();
        demoFloydWarshall();
        System.out.println();
        demoCicloNegativo();
    }

    // ---------------------------------------------------------------- Kruskal
    private static void demoKruskal() {
        System.out.println("=== KRUSKAL: árbol de expansión mínima ===");
        String[] nombres = {"A", "B", "C", "D", "E", "F", "G"};

        ListaAristas aristas = new ListaAristas();
        aristas.agregar(0, 1, 7);   // A-B
        aristas.agregar(0, 3, 5);   // A-D
        aristas.agregar(1, 2, 8);   // B-C
        aristas.agregar(1, 3, 9);   // B-D
        aristas.agregar(1, 4, 7);   // B-E
        aristas.agregar(2, 4, 5);   // C-E
        aristas.agregar(3, 4, 15);  // D-E
        aristas.agregar(3, 5, 6);   // D-F
        aristas.agregar(4, 5, 8);   // E-F
        aristas.agregar(4, 6, 9);   // E-G
        aristas.agregar(5, 6, 11);  // F-G

        // Mostrar el orden en que Kruskal va a procesar las aristas
        Arista[] ordenadas = aristas.aArreglo();
        Ordenamiento.ordenarPorPeso(ordenadas);
        System.out.println("Aristas ordenadas por peso:");
        for (int i = 0; i < ordenadas.length; i++) {
            Arista a = ordenadas[i];
            System.out.println("  " + nombres[a.origen] + "-" + nombres[a.destino] + " (" + a.peso + ")");
        }

        Kruskal.Resultado r = Kruskal.ejecutar(nombres.length, aristas);
        System.out.println("Aristas del árbol de expansión mínima:");
        for (int i = 0; i < r.cantidad; i++) {
            Arista a = r.arbol[i];
            System.out.println("  " + nombres[a.origen] + "-" + nombres[a.destino] + " (" + a.peso + ")");
        }
        System.out.println("Peso total: " + r.pesoTotal);
        System.out.println("¿Cubre todo el grafo (conexo)? " + r.conexo);
    }

    // --------------------------------------------------------- Floyd-Warshall
    private static void demoFloydWarshall() {
        System.out.println("=== FLOYD-WARSHALL: caminos más cortos entre todos los pares ===");
        String[] nombres = {"0", "1", "2", "3"};
        int INF = FloydWarshall.INF;

        // Grafo dirigido. INF = no hay arista directa.
        int[][] pesos = {
            {0,   3,   INF, 7  },
            {8,   0,   2,   INF},
            {5,   INF, 0,   1  },
            {2,   INF, INF, 0  }
        };
        System.out.println("Matriz de adyacencia de entrada:");
        imprimirMatriz(pesos);

        FloydWarshall fw = new FloydWarshall(pesos);
        System.out.println("Matriz de distancias mínimas:");
        int n = fw.numVertices();
        int[][] resultado = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                resultado[i][j] = fw.distancia(i, j);
            }
        }
        imprimirMatriz(resultado);

        System.out.println("Algunos caminos reconstruidos:");
        imprimirCamino(fw, 0, 3, nombres);
        imprimirCamino(fw, 1, 0, nombres);
        imprimirCamino(fw, 3, 2, nombres);
        System.out.println("¿Ciclo negativo? " + fw.hayCicloNegativo());
    }

    private static void demoCicloNegativo() {
        System.out.println("=== FLOYD-WARSHALL: detección de ciclo negativo ===");
        int INF = FloydWarshall.INF;
        // 0 -> 1 (1), 1 -> 2 (-3), 2 -> 0 (1): el ciclo suma -1
        int[][] pesos = {
            {0,   1,   INF},
            {INF, 0,   -3 },
            {1,   INF, 0  }
        };
        FloydWarshall fw = new FloydWarshall(pesos);
        System.out.println("¿Ciclo negativo? " + fw.hayCicloNegativo()
                + "  (si es true, las distancias ya no son confiables)");
    }

    // ---------------------------------------------------------------- Helpers
    private static void imprimirCamino(FloydWarshall fw, int origen, int destino, String[] nombres) {
        int[] ruta = fw.camino(origen, destino);
        if (ruta == null) {
            System.out.println("  " + nombres[origen] + " -> " + nombres[destino] + ": sin camino");
            return;
        }
        String texto = "";
        for (int i = 0; i < ruta.length; i++) {
            texto = texto + nombres[ruta[i]];
            if (i < ruta.length - 1) {
                texto = texto + " -> ";
            }
        }
        System.out.println("  " + nombres[origen] + " -> " + nombres[destino]
                + ": " + texto + "  (costo " + fw.distancia(origen, destino) + ")");
    }

    private static void imprimirMatriz(int[][] m) {
        for (int i = 0; i < m.length; i++) {
            String fila = "  ";
            for (int j = 0; j < m[i].length; j++) {
                String celda = m[i][j] >= FloydWarshall.INF ? "INF" : "" + m[i][j];
                while (celda.length() < 4) {
                    celda = " " + celda;
                }
                fila = fila + celda;
            }
            System.out.println(fila);
        }
    }
}
