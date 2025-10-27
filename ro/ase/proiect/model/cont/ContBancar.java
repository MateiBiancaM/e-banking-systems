package ro.ase.proiect.model.cont;

import ro.ase.proiect.model.utilizator.Client;

/**
 *Clasa abstracta intermediara ce extinde {@link Cont} si adauga conceptul de <i>sold</i>.
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 27.10.2025
 * @see Cont
 * @see ContDebitor
 * @see ContCreditor
 */
public abstract class ContBancar extends Cont implements OperatiuniBancare {
    protected double sold;

    public ContBancar(String iban, Client client, TipMoneda moneda) {
        super(iban, client, moneda);
        this.sold=0.0;
    }

    /**
     * Implementarea concreta pentru depunerea intr-un cont.
     * @param suma Reprezinta suma ce trebuie depusa.
     */
    @Override
    public void depunere(double suma) {
        if (suma > 0) {
            this.sold += suma;
            System.out.println("-->Suma depusa:" + suma + ". Sold nou:" + this.sold);
        } else {
            throw new IllegalArgumentException("Suma depusa trebuie sa fie pozitiva!");
        }
    }

    public double getSold() {
        return this.sold;
    }
}
