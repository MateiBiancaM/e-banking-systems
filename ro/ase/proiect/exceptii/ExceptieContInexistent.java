package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care se incearca accesarea unui iban care nu exista in sistem.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 26.10.2025
 */
public class ExceptieContInexistent extends Exception{
    public ExceptieContInexistent() {
    }

    public ExceptieContInexistent(String message) {
        super(message);
    }
}
