package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care operatiunea de retragere esueaza din cauza depasirii sumei maxime ce este admisa de banca pentru a fi retrasa intr-o zi.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 27.10.2025
 */
public class ExceptieRetragereZilnicaDepasita extends Exception{
    /**
     * Constructor pentru exceptia de retragere zilnica depasita esuata.
     *
     * @param message Mesajul de eroare care descrie motivul esecului.
     */
    public ExceptieRetragereZilnicaDepasita(String message) {
        super(message);
    }
}
