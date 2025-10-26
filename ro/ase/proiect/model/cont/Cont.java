package ro.ase.proiect.model.cont;

import ro.ase.proiect.model.utilizator.Client;

/**
 *Clasa abstracta radacina ce reprezinta cel mai general concept asupra contului.
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 26.10.2025
 * @see ro.ase.proiect.model.utilizator.Client
 * @see ContBancar
 * @see ContCreditor
 * @see TipMoneda
 */
public abstract class Cont {
    protected String iban;
    protected Client client;
    protected TipMoneda moneda;
}
