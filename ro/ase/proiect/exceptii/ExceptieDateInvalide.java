package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care datele corespunzatoare unui fisier sunt corupte.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 26.10.2025
 */
public class ExceptieDateInvalide extends Exception{
    /**
     * Constructor pentru exceptia de date invalide esuata.
     *
     * @param message Mesajul de eroare care descrie motivul esecului.
     */
    public ExceptieDateInvalide(String message) {
        super(message);
    }
}
