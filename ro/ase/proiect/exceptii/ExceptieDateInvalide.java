package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care datele corespunzatoare unui fisier sunt corupte.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 26.10.2025
 */
public class ExceptieDateInvalide extends Exception{
    public ExceptieDateInvalide() {
    }

    public ExceptieDateInvalide(String message) {
        super(message);
    }
}
