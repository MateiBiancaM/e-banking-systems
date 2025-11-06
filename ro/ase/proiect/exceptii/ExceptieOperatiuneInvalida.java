package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care operatiunea din punct de vedere logic, este invalida, chiar daca datele de intrare sunt valide.
 * (ex:plata unei datorii inexistente)
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 4.11.2025
 */
public class ExceptieOperatiuneInvalida extends Exception {
    /**
     * Constructor pentru exceptia de operatiune invalida esuata.
     *
     * @param message Mesajul de eroare care descrie motivul esecului.
     */
    public ExceptieOperatiuneInvalida(String message) {
        super(message);
    }
}
