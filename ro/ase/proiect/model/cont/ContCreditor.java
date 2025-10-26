package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;

/**
 *Clasa finala/concreta. Aceasta extinde {@link ContBancar} si si reprezinta un  <b>cont creditor</b>
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 26.10.2025
 * @see ContBancar
 */
public final class ContCreditor extends ContBancar {
    private final double LIMITA_CREDIT=10000;

    @Override
    public void retragere(double suma) throws ExceptieFonduriInsuficiente {

    }

    @Override
    public double getSold() {
        return 0;
    }
}
