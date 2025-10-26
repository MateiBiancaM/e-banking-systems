package ro.ase.proiect.exceptii;
/**
 *Exceptie specifica aruncata in momentul in care operatiunea de tretragere esueaza din cauza fondurilor insuficiente.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 26.10.2025
 */
public class ExceptieFonduriInsuficiente extends Exception{
    public ExceptieFonduriInsuficiente() {
    }

    public ExceptieFonduriInsuficiente(String message) {
        super(message);
    }
}
