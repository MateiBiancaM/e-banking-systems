package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;
import ro.ase.proiect.exceptii.ExceptieLimitaDepunereDepasita;
import ro.ase.proiect.exceptii.ExceptieOperatiuneInvalida;
import ro.ase.proiect.model.utilizator.Client;

/**
 *Clasa finala/concreta. Aceasta extinde {@link ContBancar} si si reprezinta un  <b>cont creditor</b>
 * Soldul reprezinta banii bancii, reprezentand o valoare negativa ce reprezinta datoria clientului fata de banca.
 *
 * @author Matei Maria-Bianca
 * @version 1.3
 * @since 4.11.2025
 * @see ContBancar
 */
public final class ContCreditor extends ContBancar {
    private double limitaCredit;

    public ContCreditor(String iban, Client client, TipMoneda moneda, double limitaCredit) {
        super(iban, client, moneda);
        this.limitaCredit = limitaCredit;
    }

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

    @Override
    public double getSoldDisponibil() {
        return this.limitaCredit+this.sold;
    }

    @Override
    public double getSold() {
        return this.sold;
    }

    public double getDatorieCurenta(){
        return Math.abs(this.sold);
    }

    public double getLimitaCredit() {
        return this.limitaCredit;
    }

    @Override
    public void depunere(double suma) throws ExceptieLimitaDepunereDepasita, ExceptieOperatiuneInvalida {
        if (suma <= 0) {
            throw new IllegalArgumentException("Suma depusa trebuie sa fie pozitiva!");
        }
        Double limita=LIMITE_MAXIME_DEPUNERE.get(this.moneda);
        if(limita!=null && suma>limita){
            throw new ExceptieLimitaDepunereDepasita("Tranzactiile de peste "+ limita + " "+ this.moneda + " nu sunt permise!");
        }
        if(this.sold>=limita){
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
