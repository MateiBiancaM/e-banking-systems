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
    public ExceptieClientExistent() {
    }

    public ExceptieClientExistent(String message) {
        super(message);
    }
}
