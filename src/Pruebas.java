/**
 * Pruebas simples sin librerías (sin JUnit y sin imports).
 * Compilar:  javac -d out src/*.java
 * Ejecutar:  java -cp out Pruebas
 * Termina con código de salida 1 si alguna prueba falla.
 */
public class Pruebas {
    private static int fallos = 0;
    private static int total = 0;

    public static void main(String[] args) {
        pruebaUnionFind();
        pruebaOrdenamiento();
        pruebaKruskalConexo();
        pruebaKruskalDesconectado();
        pruebaKruskalPesosIguales();
        pruebaFloydDistancias();
        pruebaFloydCaminos();
        pruebaFloydSinCamino();
        pruebaFloydPesoNegativoSinCiclo();
        pruebaFloydCicloNegativo();

        System.out.println();
        System.out.println((total - fallos) + "/" + total + " pruebas correctas");
        if (fallos > 0) {
            System.exit(1);
        }
    }

    private static void verificar(String nombre, boolean condicion) {
        total++;
        if (condicion) {
            System.out.println("OK    " + nombre);
        } else {
            fallos++;
            System.out.println("FALLO " + nombre);
        }
    }

    // ------------------------------------------------------------ UnionFind
    private static void pruebaUnionFind() {
        UnionFind uf = new UnionFind(5);
        verificar("UnionFind: 5 componentes al inicio", uf.componentes() == 5);
        verificar("UnionFind: unir(0,1) tiene éxito", uf.unir(0, 1));
        verificar("UnionFind: unir(1,2) tiene éxito", uf.unir(1, 2));
        verificar("UnionFind: 0 y 2 quedan conectados", uf.conectados(0, 2));
        verificar("UnionFind: unir(0,2) ya forma ciclo", !uf.unir(0, 2));
        verificar("UnionFind: 3 y 4 siguen separados", !uf.conectados(3, 4));
        verificar("UnionFind: quedan 3 componentes", uf.componentes() == 3);
    }

    // ---------------------------------------------------------- Ordenamiento
    private static void pruebaOrdenamiento() {
        Arista[] a = {
            new Arista(0, 1, 9), new Arista(1, 2, 3), new Arista(2, 3, 7),
            new Arista(3, 4, 1), new Arista(4, 5, 3)
        };
        Ordenamiento.ordenarPorPeso(a);
        boolean ordenado = true;
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1].peso > a[i].peso) {
                ordenado = false;
            }
        }
        verificar("Ordenamiento: pesos de menor a mayor", ordenado);
        verificar("Ordenamiento: estable con pesos iguales", a[1].origen == 1 && a[2].origen == 4);
    }

    // --------------------------------------------------------------- Kruskal
    private static ListaAristas grafoEjemplo() {
        ListaAristas g = new ListaAristas();
        g.agregar(0, 1, 7);
        g.agregar(0, 3, 5);
        g.agregar(1, 2, 8);
        g.agregar(1, 3, 9);
        g.agregar(1, 4, 7);
        g.agregar(2, 4, 5);
        g.agregar(3, 4, 15);
        g.agregar(3, 5, 6);
        g.agregar(4, 5, 8);
        g.agregar(4, 6, 9);
        g.agregar(5, 6, 11);
        return g;
    }

    private static void pruebaKruskalConexo() {
        Kruskal.Resultado r = Kruskal.ejecutar(7, grafoEjemplo());
        verificar("Kruskal: peso total del MST = 39", r.pesoTotal == 39);
        verificar("Kruskal: el MST tiene V-1 = 6 aristas", r.cantidad == 6);
        verificar("Kruskal: grafo conexo", r.conexo);
    }

    private static void pruebaKruskalDesconectado() {
        // dos componentes: {0,1,2} y {3,4}
        ListaAristas g = new ListaAristas();
        g.agregar(0, 1, 4);
        g.agregar(1, 2, 2);
        g.agregar(0, 2, 5);
        g.agregar(3, 4, 1);
        Kruskal.Resultado r = Kruskal.ejecutar(5, g);
        verificar("Kruskal desconectado: marca conexo = false", !r.conexo);
        verificar("Kruskal desconectado: bosque con 3 aristas", r.cantidad == 3);
        verificar("Kruskal desconectado: peso del bosque = 7", r.pesoTotal == 7);
    }

    private static void pruebaKruskalPesosIguales() {
        // triángulo con pesos iguales: cualquier MST pesa 2
        ListaAristas g = new ListaAristas();
        g.agregar(0, 1, 1);
        g.agregar(1, 2, 1);
        g.agregar(0, 2, 1);
        Kruskal.Resultado r = Kruskal.ejecutar(3, g);
        verificar("Kruskal pesos iguales: peso total = 2", r.pesoTotal == 2 && r.cantidad == 2);
    }

    // ---------------------------------------------------------- Floyd-Warshall
    private static int[][] matrizEjemplo() {
        int INF = FloydWarshall.INF;
        return new int[][] {
            {0,   3,   INF, 7  },
            {8,   0,   2,   INF},
            {5,   INF, 0,   1  },
            {2,   INF, INF, 0  }
        };
    }

    private static void pruebaFloydDistancias() {
        FloydWarshall fw = new FloydWarshall(matrizEjemplo());
        verificar("Floyd: dist(0,1) = 3", fw.distancia(0, 1) == 3);
        verificar("Floyd: dist(0,2) = 5", fw.distancia(0, 2) == 5);
        verificar("Floyd: dist(0,3) = 6", fw.distancia(0, 3) == 6);
        verificar("Floyd: dist(1,0) = 5 (1->2->3->0)", fw.distancia(1, 0) == 5);
        verificar("Floyd: dist(3,2) = 7", fw.distancia(3, 2) == 7);
        verificar("Floyd: dist(i,i) = 0", fw.distancia(2, 2) == 0);
        verificar("Floyd: sin ciclo negativo", !fw.hayCicloNegativo());
    }

    private static void pruebaFloydCaminos() {
        FloydWarshall fw = new FloydWarshall(matrizEjemplo());
        int[] ruta = fw.camino(0, 3);
        verificar("Floyd camino 0->3 = 0,1,2,3",
                ruta != null && ruta.length == 4 && ruta[0] == 0 && ruta[1] == 1 && ruta[2] == 2 && ruta[3] == 3);
        int[] mismo = fw.camino(2, 2);
        verificar("Floyd camino i->i = [i]", mismo != null && mismo.length == 1 && mismo[0] == 2);
    }

    private static void pruebaFloydSinCamino() {
        int INF = FloydWarshall.INF;
        int[][] m = {
            {0,   1,   INF},
            {INF, 0,   INF},
            {INF, INF, 0  }
        };
        FloydWarshall fw = new FloydWarshall(m);
        verificar("Floyd: 0->2 sin camino", !fw.hayCaminoEntre(0, 2) && fw.camino(0, 2) == null);
        verificar("Floyd: 0->1 con camino", fw.hayCaminoEntre(0, 1));
    }

    private static void pruebaFloydPesoNegativoSinCiclo() {
        int INF = FloydWarshall.INF;
        // 0->1 (4), 0->2 (5), 1->2 (-3): el mejor camino 0->2 pasa por 1 y cuesta 1
        int[][] m = {
            {0,   4,   5  },
            {INF, 0,   -3 },
            {INF, INF, 0  }
        };
        FloydWarshall fw = new FloydWarshall(m);
        verificar("Floyd con peso negativo: dist(0,2) = 1", fw.distancia(0, 2) == 1);
        verificar("Floyd con peso negativo: sin ciclo negativo", !fw.hayCicloNegativo());
    }

    private static void pruebaFloydCicloNegativo() {
        int INF = FloydWarshall.INF;
        int[][] m = {
            {0,   1,   INF},
            {INF, 0,   -3 },
            {1,   INF, 0  }
        };
        FloydWarshall fw = new FloydWarshall(m);
        verificar("Floyd: detecta ciclo negativo", fw.hayCicloNegativo());
        verificar("Floyd: camino es null con ciclo negativo", fw.camino(0, 2) == null);
    }
}
