package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;
import ro.ase.proiect.exceptii.ExceptieLimitaDepunereDepasita;
import ro.ase.proiect.exceptii.ExceptieOperatiuneInvalida;
import ro.ase.proiect.model.utilizator.Client;

/**
 * Clasa finala/concreta. Aceasta extinde {@link ContBancar} si reprezinta un  <b>cont creditor</b>
 * Soldul este o valoare negativa ce reprezinta datoria clientului fata de banca.
 *
 * @author Matei Maria-Bianca
 * @version 1.3
 * @since 4.11.2025
 * @see ContBancar
 * @see TipMoneda
 */
public final class ContCreditor extends ContBancar {
    /**
     * Limita maximă de credit acordată contului.
     */
    private final double limitaCredit;

    /**
     * Constructor pentru un cont de credit. Inițializează contul cu o limită de credit specificată. Soldul inițial este 0.
     *
     * @param iban         Codul IBAN unic al contului.
     * @param client       Deținătorul contului ({@link Client}).
     * @param moneda       Moneda contului ({@link TipMoneda}).
     * @param limitaCredit Suma maximă (pozitivă) care poate fi cheltuită (limita de credit).
     */
    public ContCreditor(String iban, Client client, TipMoneda moneda, double limitaCredit) {
        super(iban, client, moneda);
        this.limitaCredit = limitaCredit;
    }

    /**
     * Metoda implementeaza modul de retragere al unei sume in cadrul unui cont creditor.
     * Metoda validează dacă suma solicitată este pozitivă și dacă există
     * suficient credit disponibil pentru a acoperi tranzacția.
     * Dacă validările sunt îndeplinite, suma este scăzută din soldul curent, iar noul sold este rotunjit.
     *
     * @param suma Reprezinta suma ce trebuie retrasa .
     * @throws ExceptieFonduriInsuficiente dacă suma depășește creditul disponibil.
     * @throws IllegalArgumentException    dacă suma este negativă sau zero.
     */
    @Override
    public void retragere(double suma) throws ExceptieFonduriInsuficiente {
        if(suma<=0){
            throw new IllegalArgumentException("Suma cheltuita trebuie sa fie pozitiva!");
        }
        if(suma>this.getSoldDisponibil()){
            throw new ExceptieFonduriInsuficiente("Credit insuficient! Creditul disponibil este:"+this.getSoldDisponibil()+ " Suma dorita:"+suma);
        }
        this.sold-=suma;
        this.sold = rotunjeste(this.sold);
    }

    /**
     * Calculează și returnează suma disponibilă pentru cheltuieli (creditul rămas).
     *
     * @return Creditul disponibil (limitaCredit + soldul curent).
     */
    @Override
    public double getSoldDisponibil() {
        return this.limitaCredit + this.sold;
    }

    /**
     * Returnează soldul curent al contului. Pentru un cont creditor, această valoare este de obicei
     * negativă (reprezentând datoria) sau zero.
     *
     * @return Soldul curent.
     */
    @Override
    public double getSold() {
        return this.sold;
    }

    /**
     * Returnează datoria curentă a clientului (valoarea absolută a soldului).
     *
     * @return Datoria curentă ca o valoare pozitivă.
     */
    public double getDatorieCurenta() {
        return Math.abs(this.sold);
    }

    /**
     * Returnează limita totală de credit aprobată pentru acest cont.
     *
     * @return Limita de credit (o valoare pozitivă).
     */
    public double getLimitaCredit() {
        return this.limitaCredit;
    }

    /**
     * Metoda procesează o depunere (plată) în cont, destinată acoperirii unei datorii.
     * Metoda efectuează următoarele validări:
     * <ol>
     * <li>Suma depusă trebuie să fie strict pozitivă.</li>
     * <li>Suma depusă nu trebuie să depășească limita maximă per tranzacție
     * stabilită pentru moneda contului.</li>
     * <li>Contul trebuie să aibă o datorie curentă</li>
     * <li>Suma depusă nu poate fi mai mare decât datoria curentă .</li>
     * </ol>
     * Dacă toate validările trec, soldul contului este actualizat și rotunjit.
     * @param suma Reprezinta suma ce trebuie depusa.
     * @throws ExceptieLimitaDepunereDepasita dacă suma depășește limita unică de tranzacție.
     * @throws ExceptieOperatiuneInvalida     dacă nu există datorii de plătit sau dacă suma depusă este mai mare decât datoria existentă.
     * @throws IllegalArgumentException       dacă suma este negativă sau zero.
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
        if(this.sold>=0.0){
            throw new ExceptieOperatiuneInvalida("Operatiune esuata! Nu exista datorii curente de platit!");
        }
        double soldNou=this.sold +suma;
        if(soldNou>0.0){
            throw new ExceptieOperatiuneInvalida("Operatiune esuata! Suma:"+suma+" este mai mare decat daoria curenta:"+getDatorieCurenta()+".");
        }
        this.sold=soldNou;
        this.sold = rotunjeste(this.sold);
    }
}
