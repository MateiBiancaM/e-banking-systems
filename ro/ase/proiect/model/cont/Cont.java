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

    public Cont(String iban, Client client, TipMoneda moneda) {
        this.iban = iban;
        this.client = client;
        this.moneda = moneda;
    }

    public String getIban() {
        return iban;
    }

    public Client getClient() {
        return client;
    }

    public TipMoneda getMoneda() {
        return moneda;
    }
}
