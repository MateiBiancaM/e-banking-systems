package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care conectarea unui client nu s-a putut realiza.
 * In acest caz autentificarea a esuat.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 02.11.2025
 */
public class ExceptieAutentificareEsuata extends Exception{
    public ExceptieAutentificareEsuata() {
    }

    public ExceptieAutentificareEsuata(String message) {
        super(message);
    }
}
