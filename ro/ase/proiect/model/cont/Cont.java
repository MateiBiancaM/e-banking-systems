package ro.ase.proiect.model.cont;

import ro.ase.proiect.model.utilizator.Client;

/**
 *Clasa abstracta radacina ce reprezinta cel mai general concept asupra contului.
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 27.10.2025
 * @see ro.ase.proiect.model.utilizator.Client
 * @see ContBancar
 * @see TipMoneda
 */
public abstract class Cont {
    protected String iban;
    protected Client client;
    protected TipMoneda moneda;
    /**
     * Constructor pentru clasa abstractă Cont. Inițializează un cont cu atributele sale de bază.
     *
     * @param iban   Codul IBAN unic asociat contului.
     * @param client Clientul (deținătorul) contului.
     * @param moneda Tipul monedei corespunzatoare contului.
     */
    public Cont(String iban, Client client, TipMoneda moneda) {
        this.iban = iban;
        this.client = client;
        this.moneda = moneda;
    }

    /**
     * Returnează codul IBAN al contului.
     *
     * @return IBAN-ul sub formă de String.
     */
    public String getIban() {
        return iban;
    }

    /**
     * Returnează clientul care deține contul.
     *
     * @return Obiectul {@link Client} asociat contului.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Returnează moneda în care este ținut contul.
     *
     * @return Enum-ul {@link TipMoneda} (ex. RON, EUR).
     */
    public TipMoneda getMoneda() {
        return moneda;
    }
}
