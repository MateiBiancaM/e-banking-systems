package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;

/**
 *Clasa finala/concreta. Aceasta extinde {@link ContBancar} si reprezinta un  <b>cont debitor</b>
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 26.10.2025
 * @see ContBancar
 * @see ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente
 */
public final class ContDebitor extends ContBancar {
    private double limitaRetragereZilnica;
    @Override
    public void retragere(double suma) throws ExceptieFonduriInsuficiente {

    }

    @Override
    public double getSold() {
        return 0;
    }
}
