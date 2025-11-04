package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieLimitaDepunereDepasita;
import ro.ase.proiect.exceptii.ExceptieOperatiuneInvalida;
import ro.ase.proiect.model.utilizator.Client;

import java.util.EnumMap;
import java.util.Map;

/**
 *Clasa abstracta intermediara ce extinde {@link Cont} si adauga conceptul de <i>sold</i>.
 *
 * @author Matei Maria-Bianca
 * @version 1.2
 * @since 4.11.2025
 * @see Cont
 * @see ContDebitor
 * @see ContCreditor
 */
public abstract class ContBancar extends Cont implements OperatiuniBancare {
    protected double sold;
    protected static final Map<TipMoneda,Double>LIMITE_MAXIME_DEPUNERE= new EnumMap<>(TipMoneda.class);
    static{
        LIMITE_MAXIME_DEPUNERE.put(TipMoneda.RON,20000.0);
        LIMITE_MAXIME_DEPUNERE.put(TipMoneda.EUR,5000.0);
        LIMITE_MAXIME_DEPUNERE.put(TipMoneda.USD,5000.0);
    }

    public ContBancar(String iban, Client client, TipMoneda moneda) {
        super(iban, client, moneda);
        this.sold=0.0;
    }

    /**
     * Implementarea concreta pentru depunerea intr-un cont.
     * @param suma Reprezinta suma ce trebuie depusa.
     */
    @Override
    public void depunere(double suma) throws ExceptieLimitaDepunereDepasita, ExceptieOperatiuneInvalida {
        if (suma <= 0) {
            throw new IllegalArgumentException("Suma depusa trebuie sa fie pozitiva!");
        }
        Double limita=LIMITE_MAXIME_DEPUNERE.get(this.moneda);
        if(limita!=null && suma>limita){
            throw new ExceptieLimitaDepunereDepasita("Tranzactiile de peste "+ limita + " "+ this.moneda + " nu sunt permise!");
        }
        this.sold += suma;
        this.sold = rotunjeste(this.sold);
    }

    public double getSold() {
        return this.sold;
    }

    public void setSold(double sold) {
        this.sold = sold;
    }

    protected double rotunjeste(double valoare) {
        return Math.round(valoare * 1000.0) / 1000.0;
    }
}
