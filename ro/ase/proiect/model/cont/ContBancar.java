package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieLimitaDepunereDepasita;
import ro.ase.proiect.exceptii.ExceptieOperatiuneInvalida;
import ro.ase.proiect.model.utilizator.Client;

import java.util.EnumMap;
import java.util.Map;

/**
 * Clasa abstracta intermediara ce extinde {@link Cont} si adauga conceptul de <i>sold</i>,
 * precum si introducerea unor limite maxime de depunere in functie de tipul de moneda.
 * In plus, aceasta clasa abstracta implemenenteaza {@link OperatiuniBancare}.
 *
 * @author Matei Maria-Bianca
 * @version 1.2
 * @since 4.11.2025
 * @see Cont
 * @see ContDebitor
 * @see ContCreditor
 * @see OperatiuniBancare
 */
public abstract class ContBancar extends Cont implements OperatiuniBancare {
    /**
     * Soldul curent disponibil în contul bancar.
     */
    protected double sold;
    /**
     * Harta statică ce definește limitele maxime permise pentru o singură depunere, în funcție de {@link TipMoneda}.
     */
    protected static final Map<TipMoneda,Double>LIMITE_MAXIME_DEPUNERE= new EnumMap<>(TipMoneda.class);
    static{
        LIMITE_MAXIME_DEPUNERE.put(TipMoneda.RON,20000.0);
        LIMITE_MAXIME_DEPUNERE.put(TipMoneda.EUR,5000.0);
        LIMITE_MAXIME_DEPUNERE.put(TipMoneda.USD,5000.0);
    }
    /**
     * Constructor pentru ContBancar.
     * Apelează constructorul superclasei {@link Cont} și inițializează soldul cu 0.0.
     *
     * @param iban   Codul IBAN unic asociat contului.
     * @param client Deținătorul contului (obiect de tip {@link Client}).
     * @param moneda Tipul monedei ({@link TipMoneda}) al contului.
     */
    public ContBancar(String iban, Client client, TipMoneda moneda) {
        super(iban, client, moneda);
        this.sold=0.0;
    }

    /**
     * Implementarea concreta pentru depunerea intr-un cont.
     * Verifică dacă suma este pozitivă și dacă nu depășește limita maximă de depunere.
     *
     * @param suma Reprezinta suma ce trebuie depusa.
     * @throws ExceptieLimitaDepunereDepasita dacă suma depășește limita unică de depunere.
     * @throws ExceptieOperatiuneInvalida     dacă operațiunea nu este validă.
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

    /**
     * Returnează soldul curent al contului.
     *
     * @return Soldul contului, valoare de tip double.
     */
    public double getSold() {
        return this.sold;
    }

    /**
     * Setează soldul contului. Metoda este folosită în general la inițializarea sau încărcarea datelor din fisiere.
     *
     * @param sold Noul sold al contului.
     */
    public void setSold(double sold) {
        this.sold = sold;
    }

    /**
     * Metodă utilitară protejată pentru a rotunji o valoare la 3 zecimale.
     * Folosită pentru a menține precizia operațiunilor financiare.
     *
     * @param valoare Valoarea double care trebuie rotunjită.
     * @return Valoarea rotunjită la 3 zecimale.
     */
    protected double rotunjeste(double valoare) {
        return Math.round(valoare * 1000.0) / 1000.0;
    }
}
