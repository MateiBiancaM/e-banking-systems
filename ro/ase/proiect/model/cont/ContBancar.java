package ro.ase.proiect.model.cont;
/**
 *Clasa abstracta intermediara ce extinde {@link Cont} si adauga conceptul de <i>sold</i>.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 26.10.2025
 * @see Cont
 * @see ContDebitor
 * @see ContCreditor
 */
public abstract class ContBancar extends Cont implements OperatiuniBancare {
    protected double sold;
    /**
     * Implementarea concreta pentru depunerea intr-un cont.
     * @param suma Reprezinta suma ce trebuie depusa.
     */
    @Override
    public void depunere(double suma) {

    }
}
