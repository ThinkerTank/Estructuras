/**
 * Lista dinámica de aristas (reemplazo propio de ArrayList).
 * Guarda las aristas en un arreglo que se duplica cuando se llena.
 */
public class ListaAristas {
    private Arista[] datos;
    private int cantidad;

    public ListaAristas() {
        datos = new Arista[8];
        cantidad = 0;
    }

    public void agregar(Arista a) {
        if (cantidad == datos.length) {
            crecer();
        }
        datos[cantidad] = a;
        cantidad++;
    }

    public void agregar(int origen, int destino, int peso) {
        agregar(new Arista(origen, destino, peso));
    }

    private void crecer() {
        Arista[] nuevo = new Arista[datos.length * 2];
        for (int i = 0; i < cantidad; i++) {
            nuevo[i] = datos[i];
        }
        datos = nuevo;
    }

    public Arista obtener(int i) {
        return datos[i];
    }

    public int tamano() {
        return cantidad;
    }

    /** Copia exacta (sin espacios vacíos) para poder ordenarla sin tocar la lista original. */
    public Arista[] aArreglo() {
        Arista[] copia = new Arista[cantidad];
        for (int i = 0; i < cantidad; i++) {
            copia[i] = datos[i];
        }
        return copia;
    }
}
