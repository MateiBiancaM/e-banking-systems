package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care operatiunea de depunere esueaza din cauza depasirii sumei maxime ce este admisa de banca pentru a fi depusa intr-o zi..
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 4.11.2025
 */
public class ExceptieLimitaDepunereDepasita extends Exception {
    /**
     * Constructor pentru exceptia de limita depunere depasita esuata.
     *
     * @param message Mesajul de eroare care descrie motivul esecului.
     */
    public ExceptieLimitaDepunereDepasita(String message) {
        super(message);
    }
}
