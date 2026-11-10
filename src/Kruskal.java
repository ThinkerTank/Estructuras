/**
 * Algoritmo de Kruskal: árbol de expansión mínima (MST) de un grafo
 * NO dirigido y con pesos.
 *
 * Idea (voraz / greedy):
 *   1. Ordenar todas las aristas de menor a mayor peso.
 *   2. Recorrerlas en ese orden; una arista se acepta solo si une dos
 *      componentes distintas (si no, formaría un ciclo).
 *   3. Parar cuando se tengan V-1 aristas.
 *
 * Complejidad: O(E log E) por el ordenamiento (las operaciones de
 * Union-Find son casi O(1)). Memoria: O(V + E).
 *
 * Si el grafo NO es conexo, el resultado es un bosque de expansión mínima
 * (un árbol por componente) y "conexo" queda en false.
 */
public class Kruskal {

    /** Resultado de ejecutar Kruskal. */
    public static class Resultado {
        public final Arista[] arbol;   // aristas elegidas (solo las primeras "cantidad" son válidas)
        public final int cantidad;     // cuántas aristas tiene el árbol/bosque
        public final int pesoTotal;    // suma de los pesos de las aristas elegidas
        public final boolean conexo;   // true si el MST cubre todo el grafo

        Resultado(Arista[] arbol, int cantidad, int pesoTotal, boolean conexo) {
            this.arbol = arbol;
            this.cantidad = cantidad;
            this.pesoTotal = pesoTotal;
            this.conexo = conexo;
        }
    }

    /**
     * @param numVertices número de vértices (numerados de 0 a numVertices-1)
     * @param aristas     lista de aristas del grafo (no se modifica)
     */
    public static Resultado ejecutar(int numVertices, ListaAristas aristas) {
        Arista[] ordenadas = aristas.aArreglo();      // copia para no alterar la lista original
        Ordenamiento.ordenarPorPeso(ordenadas);       // paso 1

        UnionFind uf = new UnionFind(numVertices);
        int maximo = numVertices > 0 ? numVertices - 1 : 0;
        Arista[] arbol = new Arista[maximo];
        int cantidad = 0;
        int pesoTotal = 0;

        for (int i = 0; i < ordenadas.length && cantidad < maximo; i++) {   // paso 2 y 3
            Arista a = ordenadas[i];
            if (uf.unir(a.origen, a.destino)) {       // ¿une dos componentes distintas?
                arbol[cantidad] = a;
                cantidad++;
                pesoTotal += a.peso;
            }
        }

        boolean conexo = numVertices <= 1 || cantidad == numVertices - 1;
        return new Resultado(arbol, cantidad, pesoTotal, conexo);
    }
}
