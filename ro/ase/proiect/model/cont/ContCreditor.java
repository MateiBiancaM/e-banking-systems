package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;
import ro.ase.proiect.model.utilizator.Client;

/**
 *Clasa finala/concreta. Aceasta extinde {@link ContBancar} si si reprezinta un  <b>cont creditor</b>
 * Soldul reprezinta banii bancii, reprezentand o valoare negativa ce reprezinta datoria clientului fata de banca.
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 27.10.2025
 * @see ContBancar
 */
public final class ContCreditor extends ContBancar {
    private static final double LIMITA_CREDIT=10000;

    public ContCreditor(String iban, Client client, TipMoneda moneda) {
        super(iban, client, moneda);
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
        System.out.println("-->Suma cheltuita din contul de credit:"+suma+". Datoria curenta:"+this.getDatorieCurenta()+". Creditul disponibil este:"+this.getSoldDisponibil()+".");
    }

    @Override
    public double getSoldDisponibil() {
        return LIMITA_CREDIT+this.sold;
    }

    @Override
    public double getSold() {
        return this.sold;
    }

    public double getDatorieCurenta(){
        return -1*this.sold;
    }


}
