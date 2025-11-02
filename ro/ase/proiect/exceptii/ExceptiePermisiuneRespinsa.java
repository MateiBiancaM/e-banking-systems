package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care un client incearca sa foloseasca un cont ce nu ii apartine.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 02.11.2025
 */
public class ExceptiePermisiuneRespinsa extends Exception {
    public ExceptiePermisiuneRespinsa() {
    }

    public ExceptiePermisiuneRespinsa(String message) {
        super(message);
    }
}
