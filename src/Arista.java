/**
 * Arista con peso de un grafo.
 * Para grafos no dirigidos (Kruskal) el orden origen/destino no importa.
 */
public class Arista {
    public final int origen;
    public final int destino;
    public final int peso;

    public Arista(int origen, int destino, int peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    @Override
    public String toString() {
        return origen + " - " + destino + " (peso " + peso + ")";
    }
}
