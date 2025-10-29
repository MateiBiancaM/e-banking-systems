package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;
import ro.ase.proiect.exceptii.ExceptieRetragereZilnicaDepasita;
import ro.ase.proiect.model.utilizator.Client;

import java.time.LocalDate;

/**
 *Clasa finala/concreta. Aceasta extinde {@link ContBancar} si reprezinta un  <b>cont debitor</b>.
 * Soldul constituie banii clientului, reprezentand o valoare pozitiva, ce nu are voie sa scada sub 0.
 *
 * @author Matei Maria-Bianca
 * @version 1.3
 * @since 29.10.2025
 * @see ContBancar
 * @see ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente
 */
public final class ContDebitor extends ContBancar {
    private static final double LIMITA_RETRAGERE_ZILNICA_ATM=3500.0;
    private double totalRetrasAstazi=0.0;
    private LocalDate dataUltimeiRetrageri=null;

    public ContDebitor(String iban, Client client, TipMoneda moneda) {
        super(iban, client, moneda);
    }

    @Override
    public void retragere(double suma) throws ExceptieFonduriInsuficiente, ExceptieRetragereZilnicaDepasita {
        if(suma<=0){
            throw  new IllegalArgumentException("Suma retrasa trebuie sa fie pozitiva!");
        }
        if(suma>this.sold){
            throw  new ExceptieFonduriInsuficiente("Fonduri insuficiente. Suma dorita:"+suma+" Sold disponibil:"+this.sold+".");
        }
        LocalDate dataCurenta=LocalDate.now();
        if(this.dataUltimeiRetrageri==null || !this.dataUltimeiRetrageri.equals(dataCurenta)){
            this.totalRetrasAstazi=0.0;
        }
        if(this.totalRetrasAstazi+suma> LIMITA_RETRAGERE_ZILNICA_ATM){
            double disponibilAzi=LIMITA_RETRAGERE_ZILNICA_ATM-this.totalRetrasAstazi;
            throw new ExceptieRetragereZilnicaDepasita("Limita zilnica de retragere("+LIMITA_RETRAGERE_ZILNICA_ATM+") a fost depasita. Astazi se mai pot retrage:"+disponibilAzi+".");

        }
        this.sold-=suma;
        this.totalRetrasAstazi+=suma;
        this.dataUltimeiRetrageri=dataCurenta;
        System.out.println("-->Suma retrasa din contul de debit:"+suma+". Sold:"+this.sold+". Total retras astazi:"+this.totalRetrasAstazi+".");
    }

    @Override
    public double getSoldDisponibil() {
        return this.sold;
    }

    @Override
    public double getSold() {
        return this.sold;
    }

    public double getTotalRetrasAstazi() {
        return totalRetrasAstazi;
    }

    public LocalDate getDataUltimeiRetrageri() {
        return dataUltimeiRetrageri;
    }

    public void setStareZilnica(double totalRetrasAstazi,LocalDate dataUltimeiRetrageri) {
        this.totalRetrasAstazi = totalRetrasAstazi;
        this.dataUltimeiRetrageri = dataUltimeiRetrageri;
    }

}
