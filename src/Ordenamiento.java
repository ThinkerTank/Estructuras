/**
 * Merge sort propio para ordenar aristas por peso (de menor a mayor).
 * Es estable (aristas con el mismo peso conservan su orden) y cuesta O(E log E).
 */
public class Ordenamiento {

    public static void ordenarPorPeso(Arista[] aristas) {
        if (aristas.length < 2) {
            return;
        }
        Arista[] auxiliar = new Arista[aristas.length];
        mergeSort(aristas, auxiliar, 0, aristas.length - 1);
    }

    private static void mergeSort(Arista[] a, Arista[] aux, int izq, int der) {
        if (izq >= der) {
            return;
        }
        int medio = izq + (der - izq) / 2;
        mergeSort(a, aux, izq, medio);
        mergeSort(a, aux, medio + 1, der);
        mezclar(a, aux, izq, medio, der);
    }

    private static void mezclar(Arista[] a, Arista[] aux, int izq, int medio, int der) {
        for (int k = izq; k <= der; k++) {
            aux[k] = a[k];
        }
        int i = izq;       // recorre la mitad izquierda
        int j = medio + 1; // recorre la mitad derecha
        for (int k = izq; k <= der; k++) {
            if (i > medio) {
                a[k] = aux[j];
                j++;
            } else if (j > der) {
                a[k] = aux[i];
                i++;
            } else if (aux[j].peso < aux[i].peso) {
                a[k] = aux[j];
                j++;
            } else {
                a[k] = aux[i];
                i++;
            }
        }
    }
}
