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
    public ExceptieClientInexistent() {
    }

    public ExceptieClientInexistent(String message) {
        super(message);
    }
}
