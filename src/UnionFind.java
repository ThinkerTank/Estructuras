/**
 * Union-Find (conjuntos disjuntos) con compresión de caminos y unión por rango.
 *
 * Responde dos preguntas casi en O(1):
 *   - encontrar(x): ¿a qué componente pertenece x?
 *   - unir(a, b):   junta las componentes de a y b (false si ya estaban juntas).
 *
 * Es la pieza que permite a Kruskal detectar ciclos rápidamente.
 */
public class UnionFind {
    private final int[] padre;
    private final int[] rango;
    private int componentes;

    public UnionFind(int n) {
        padre = new int[n];
        rango = new int[n];
        componentes = n;
        for (int i = 0; i < n; i++) {
            padre[i] = i; // al inicio cada vértice es su propia componente
        }
    }

    /** Devuelve el representante (raíz) de la componente de x. */
    public int encontrar(int x) {
        int raiz = x;
        while (padre[raiz] != raiz) {
            raiz = padre[raiz];
        }
        // compresión de caminos: todos los nodos visitados apuntan directo a la raíz
        while (padre[x] != raiz) {
            int siguiente = padre[x];
            padre[x] = raiz;
            x = siguiente;
        }
        return raiz;
    }

    /** Une las componentes de a y b. Devuelve false si ya estaban en la misma (formaría ciclo). */
    public boolean unir(int a, int b) {
        int raizA = encontrar(a);
        int raizB = encontrar(b);
        if (raizA == raizB) {
            return false;
        }
        // unión por rango: el árbol más bajo cuelga del más alto
        if (rango[raizA] < rango[raizB]) {
            padre[raizA] = raizB;
        } else if (rango[raizA] > rango[raizB]) {
            padre[raizB] = raizA;
        } else {
            padre[raizB] = raizA;
            rango[raizA]++;
        }
        componentes--;
        return true;
    }

    public boolean conectados(int a, int b) {
        return encontrar(a) == encontrar(b);
    }

    public int componentes() {
        return componentes;
    }
}
