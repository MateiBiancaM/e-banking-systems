package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care acelasi client incearca sa-si creeze alt cont.
 * In acest caz inregistrarea a esuat.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 02.11.2025
 */
public class ExceptieClientExistent extends Exception{
    /**
     * Constructor pentru exceptia de client existent esuata.
     *
     * @param message Mesajul de eroare care descrie motivul esecului.
     */
    public ExceptieClientExistent(String message) {
        super(message);
    }
}
