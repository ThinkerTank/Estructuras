# Kruskal y Floyd-Warshall en Java

Implementación de dos algoritmos clásicos de grafos, con la teoría al lado del código. Todo está hecho en **Java puro, sin `import`**: las estructuras (lista de aristas, ordenamiento, Union-Find) son propias.

- **Kruskal**: árbol de expansión mínima (conectar todo al menor costo).
- **Floyd-Warshall**: camino más corto entre *todos* los pares de vértices.

## Estructura del repo

```
src/
  Arista.java          arista con origen, destino y peso
  ListaAristas.java    lista dinámica propia (en vez de ArrayList)
  Ordenamiento.java    merge sort propio (en vez de Collections.sort)
  UnionFind.java       conjuntos disjuntos (detecta ciclos para Kruskal)
  Kruskal.java         algoritmo de Kruskal
  FloydWarshall.java   algoritmo de Floyd-Warshall (con caminos y ciclos negativos)
  Main.java            demo con ejemplos y salida explicada
  Pruebas.java         pruebas sin librerías (31 casos)
```

## Cómo compilar y ejecutar

```bash
javac -d out src/*.java
java -cp out Main       # demo
java -cp out Pruebas    # pruebas
```

## Vocabulario mínimo

- **Vértice**: un nodo (una ciudad, una computadora). Aquí se numeran 0, 1, 2, …
- **Arista**: conexión entre dos vértices, con un **peso** (distancia, costo, tiempo).
- **No dirigido**: la arista A–B se puede recorrer en ambos sentidos. **Dirigido**: A→B no implica B→A.
- **Conexo**: desde cualquier vértice se puede llegar a cualquier otro.

---

## 1. Kruskal: árbol de expansión mínima

### ¿Para qué sirve?

Dado un grafo **no dirigido y con pesos**, encuentra el conjunto de aristas que:

1. conecta **todos** los vértices,
2. **no tiene ciclos** (por eso es un árbol, con exactamente V−1 aristas),
3. tiene el **menor peso total posible**.

Ejemplos reales: tender cable o tuberías entre casas gastando lo mínimo, diseñar una red de carreteras o de fibra óptica, agrupar datos (clustering), y como base para aproximar problemas más difíciles (como el viajante).

### ¿Cuándo usarlo?

| Úsalo cuando… | No lo uses cuando… |
|---|---|
| Necesitas **conectar todo** al menor costo total | Quieres el camino más corto **entre dos puntos** (eso es Dijkstra/Floyd) |
| El grafo es **no dirigido** | El grafo es **dirigido** (el problema equivalente es otro: arborescencias) |
| El grafo es **ralo** (pocas aristas respecto a V²) | El grafo es muy **denso**: Prim suele ir mejor |
| Tienes las aristas en lista | |

### ¿Cómo funciona?

Es un algoritmo **voraz (greedy)**:

1. Ordena todas las aristas de menor a mayor peso.
2. Recorre la lista en ese orden. Para cada arista, pregunta: *¿sus extremos ya están conectados entre sí?*
   - **No** → la acepta (une dos componentes distintas).
   - **Sí** → la descarta (cerraría un ciclo).
3. Se detiene al tener V−1 aristas.

La pregunta del paso 2 se responde con **Union-Find** (`UnionFind.java`): `encontrar(x)` dice a qué componente pertenece x y `unir(a, b)` junta dos componentes. Sin esta estructura, detectar ciclos costaría mucho más.

### Ejemplo trazado

Grafo de 7 vértices (A–G), el mismo de `Main.java`. Aristas ordenadas y decisión de Kruskal:

| Arista | Peso | ¿Se acepta? | Motivo |
|---|---|---|---|
| A–D | 5 | Sí | une {A} con {D} |
| C–E | 5 | Sí | une {C} con {E} |
| D–F | 6 | Sí | une {A,D} con {F} |
| A–B | 7 | Sí | une {A,D,F} con {B} |
| B–E | 7 | Sí | une {A,B,D,F} con {C,E} |
| B–C | 8 | No | B y C ya están conectados (ciclo) |
| E–F | 8 | No | E y F ya están conectados (ciclo) |
| B–D | 9 | No | ya están conectados (ciclo) |
| E–G | 9 | Sí | une todo con {G}: ya hay 6 aristas = V−1 |

Resultado: **A–D, C–E, D–F, A–B, B–E, E–G con peso total 39**. Las aristas F–G (11) y D–E (15) ni se revisan.

### Complejidad

- Tiempo: **O(E log E)**, dominado por el ordenamiento (Union-Find es casi O(1) por operación).
- Memoria: **O(V + E)**.

### Cómo usarlo en el código

```java
ListaAristas aristas = new ListaAristas();
aristas.agregar(0, 1, 7);   // origen, destino, peso
aristas.agregar(0, 3, 5);
// ... más aristas

Kruskal.Resultado r = Kruskal.ejecutar(7, aristas);   // 7 = número de vértices

System.out.println(r.pesoTotal);   // peso del MST
System.out.println(r.conexo);      // false si el grafo tiene varias componentes
for (int i = 0; i < r.cantidad; i++) {
    System.out.println(r.arbol[i]);   // aristas elegidas
}
```

Si el grafo **no es conexo**, no existe un árbol que cubra todo: el resultado es un **bosque de expansión mínima** (un árbol por cada componente) y `conexo` es `false`.

---

## 2. Floyd-Warshall: caminos más cortos entre todos los pares

### ¿Para qué sirve?

Calcula la distancia mínima de **cada vértice a cada otro vértice** de una sola vez, y deja una tabla (matriz) con todas las respuestas. Funciona con grafos dirigidos o no dirigidos y admite **pesos negativos**.

Ejemplos reales: tabla de distancias entre todas las ciudades de una red, rutas de enrutamiento, saber si un vértice puede llegar a otro (cerradura transitiva), calcular qué nodo es el más "central" de una red, y detectar ciclos de pérdida (arbitraje) con pesos negativos.

### ¿Cuándo usarlo?

| Úsalo cuando… | Mejor usa otra cosa cuando… |
|---|---|
| Necesitas la distancia entre **muchos o todos** los pares | Solo te interesa **un origen**: Dijkstra (sin pesos negativos) o Bellman-Ford (con negativos) |
| El grafo es **pequeño o mediano** (unos cientos de vértices) | El grafo es **enorme y ralo**: Dijkstra desde cada vértice o Johnson |
| Hay **pesos negativos** (sin ciclos negativos) | |
| Quieres un código muy corto y fácil de depurar | |

### ¿Cómo funciona?

Programación dinámica. Se mantiene una matriz `dist[i][j]` con la mejor distancia conocida de `i` a `j`. Al principio es el peso de la arista directa (o `INF` si no existe). Luego, para cada vértice `k` como posible intermedio, se prueba si pasar por `k` mejora algo:

```
dist[i][j] = min( dist[i][j],  dist[i][k] + dist[k][j] )
```

Son tres bucles anidados, y **`k` debe ser el bucle exterior**. Así, al terminar de procesar `k`, `dist[i][j]` ya es el camino más corto que usa como intermedios solo los vértices `0..k`. Al final de `k = V−1` se han considerado todos los intermedios posibles.

### Ejemplo trazado

Grafo dirigido de 4 vértices (`INF` = sin arista directa):

```
Entrada:               Distancias mínimas:
   0   3 INF   7          0   3   5   6
   8   0   2 INF          5   0   2   3
   5 INF   0   1          3   6   0   1
   2 INF INF   0          2   5   7   0
```

Dos mejoras concretas durante la ejecución:

- **0 → 2** empieza en `INF` (no hay arista). Con `k = 1`: `dist[0][1] + dist[1][2] = 3 + 2 = 5`, así que mejora a 5.
- **0 → 3** empieza en 7 (arista directa). Con `k = 2`: `dist[0][2] + dist[2][3] = 5 + 1 = 6 < 7`, así que mejora a 6. El camino real es `0 → 1 → 2 → 3`.

### Reconstruir el camino

Además de la distancia, se guarda `siguiente[i][j]` (el primer vértice después de `i` en el mejor camino hacia `j`). Con eso `camino(i, j)` devuelve el recorrido completo, por ejemplo `0 → 1 → 2 → 3`.

### Ciclos negativos

Si hay un ciclo cuya suma es negativa, se puede dar vueltas infinitamente y la "distancia mínima" no existe. Floyd-Warshall lo detecta: si al terminar algún `dist[i][i] < 0`, hay un ciclo negativo. `hayCicloNegativo()` lo informa y, en ese caso, `camino()` devuelve `null`.

### Complejidad

- Tiempo: **O(V³)**. Con V = 500 son unos 125 millones de operaciones, que Java resuelve en un instante.
- Memoria: **O(V²)** por la matriz.

### Cómo usarlo en el código

```java
int INF = FloydWarshall.INF;
int[][] pesos = {
    {0,   3,   INF, 7  },
    {8,   0,   2,   INF},
    {5,   INF, 0,   1  },
    {2,   INF, INF, 0  }
};

FloydWarshall fw = new FloydWarshall(pesos);   // ya calcula todo en el constructor

fw.distancia(0, 3);            // 6
fw.hayCaminoEntre(0, 3);       // true
int[] ruta = fw.camino(0, 3);  // {0, 1, 2, 3}
fw.hayCicloNegativo();         // false
```

La matriz de entrada lleva `0` en la diagonal y `FloydWarshall.INF` donde no hay arista. `INF` vale mil millones: es lo bastante grande, pero sumar dos `INF` no desborda un `int`.

---

## 3. ¿Cuál uso?

| Pregunta que quieres responder | Algoritmo |
|---|---|
| ¿Cómo conecto todo gastando lo mínimo? | **Kruskal** (o Prim) |
| ¿Cuál es la distancia entre todos los pares? | **Floyd-Warshall** |
| ¿Cuál es el camino más corto desde un solo origen? | Dijkstra / Bellman-Ford |
| ¿Hay un ciclo negativo? | Floyd-Warshall o Bellman-Ford |

Kruskal trabaja con una **lista de aristas** y produce **un subconjunto de aristas**. Floyd-Warshall trabaja con una **matriz** y produce **una matriz de distancias**.

## 4. Errores comunes

- **Floyd-Warshall:** poner `k` en un bucle interno en vez del exterior. Da resultados incorrectos.
- **Floyd-Warshall:** usar `Integer.MAX_VALUE` como infinito. Al sumar dos infinitos se desborda a un número negativo.
- **Floyd-Warshall:** olvidar la diagonal en 0.
- **Kruskal:** aplicarlo a un grafo dirigido.
- **Kruskal:** no revisar `conexo`. Si es `false`, el resultado no cubre todo el grafo.
- **Ambos:** vértices numerados desde 1 en lugar de 0. Aquí van de `0` a `V-1`.

## 5. Estructuras propias usadas

| Aquí | Reemplaza a |
|---|---|
| `ListaAristas` | `ArrayList<Arista>` |
| `Ordenamiento` (merge sort) | `Collections.sort` / `Arrays.sort` |
| `UnionFind` | no tiene equivalente directo en la librería estándar |
