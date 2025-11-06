package ro.ase.proiect.model.cont;
/**
 * Enumeratie ce corespunde tipurilor de conturi acceptate de sistemul bancar
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 3.11.2025
 */
public enum TipCont {
    /**
     * Reprezintă un cont de tip Debitor ({@link ContDebitor}). Fondurile sunt deținute de client.
     */
    DEBIT,
    /**
     * Reprezintă un cont de tip Creditor ({@link ContCreditor}). Fondurile reprezintă o linie de credit (datorie) a clientului față de bancă.
     */
    CREDIT
}
