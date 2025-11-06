package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care nu exista acest client in sistem.
 * In acest caz inregistrarea a esuat.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 02.11.2025
 */
public class ExceptieClientInexistent extends Exception{
    /**
     * Constructor pentru exceptia de client inexistent esuata.
     *
     * @param message Mesajul de eroare care descrie motivul esecului.
     */
    public ExceptieClientInexistent(String message) {
        super(message);
    }
}
