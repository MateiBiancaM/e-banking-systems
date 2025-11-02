package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;
import ro.ase.proiect.model.utilizator.Client;

/**
 *Clasa finala/concreta. Aceasta extinde {@link ContBancar} si si reprezinta un  <b>cont creditor</b>
 * Soldul reprezinta banii bancii, reprezentand o valoare negativa ce reprezinta datoria clientului fata de banca.
 *
 * @author Matei Maria-Bianca
 * @version 1.2
 * @since 29.10.2025
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
        return -1*this.sold;
    }

    public double getLimitaCredit() {
        return this.limitaCredit;
    }


}
