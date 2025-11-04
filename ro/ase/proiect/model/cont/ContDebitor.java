package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;
import ro.ase.proiect.exceptii.ExceptieRetragereZilnicaDepasita;
import ro.ase.proiect.model.utilizator.Client;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

/**
 *Clasa finala/concreta. Aceasta extinde {@link ContBancar} si reprezinta un  <b>cont debitor</b>.
 * Soldul constituie banii clientului, reprezentand o valoare pozitiva, ce nu are voie sa scada sub 0.
 *
 * @author Matei Maria-Bianca
 * @version 1.4
 * @since 4.11.2025
 * @see ContBancar
 * @see ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente
 */
public final class ContDebitor extends ContBancar {
    private static final Map<TipMoneda,Double> LIMITE_RETRAGERE_ZILNICA= new EnumMap<>(TipMoneda.class);
    static{
        LIMITE_RETRAGERE_ZILNICA.put(TipMoneda.RON,3500.0);
        LIMITE_RETRAGERE_ZILNICA.put(TipMoneda.EUR,1000.0);
        LIMITE_RETRAGERE_ZILNICA.put(TipMoneda.USD,1000.0);
    }
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
        Double limitaZilica=LIMITE_RETRAGERE_ZILNICA.get(this.moneda);
        if(limitaZilica==null){
            limitaZilica=0.0;
        }
        LocalDate dataCurenta=LocalDate.now();
        if(this.dataUltimeiRetrageri==null || !this.dataUltimeiRetrageri.equals(dataCurenta)){
            this.totalRetrasAstazi=0.0;
        }
        if(this.totalRetrasAstazi+suma>limitaZilica){
            double disponibilAzi=limitaZilica-this.totalRetrasAstazi;
            throw new ExceptieRetragereZilnicaDepasita("Limita zilnica de retragere("+limitaZilica+" "+this.moneda+") a fost depasita!"+
                    "Astazi se mai pot retrage:"+disponibilAzi+" "+this.moneda+".");
        }
        this.sold-=suma;
        this.sold = rotunjeste(this.sold);
        this.totalRetrasAstazi+=suma;
        this.dataUltimeiRetrageri=dataCurenta;
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
