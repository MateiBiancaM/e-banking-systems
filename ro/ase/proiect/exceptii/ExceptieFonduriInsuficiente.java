package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care operatiunea de retragere esueaza din cauza fondurilor insuficiente.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 26.10.2025
 */
public class ExceptieFonduriInsuficiente extends Exception{
    /**
     * Constructor pentru exceptia de fonduri insuficiente esuata.
     *
     * @param message Mesajul de eroare care descrie motivul esecului.
     */
    public ExceptieFonduriInsuficiente(String message) {
        super(message);
    }
}
