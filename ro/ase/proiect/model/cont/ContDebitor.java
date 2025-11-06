package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;
import ro.ase.proiect.exceptii.ExceptieRetragereZilnicaDepasita;
import ro.ase.proiect.model.utilizator.Client;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

/**
 * Clasa finala/concreta. Aceasta extinde {@link ContBancar} si reprezinta un  <b>cont debitor</b>.
 * Soldul constituie banii clientului, reprezentand o valoare pozitiva, ce nu are voie sa scada sub 0.
 *
 * @author Matei Maria-Bianca
 * @version 1.4
 * @since 4.11.2025
 * @see ContBancar
 */
public final class ContDebitor extends ContBancar {
    /**
     * Contorizează suma totală retrasă în ziua curentă.
     */
    private double totalRetrasAstazi = 0.0;

    /**
     * Memorează data calendaristică a ultimei operațiuni de retragere,
     * pentru a reseta contorul zilnic (totalRetrasAstazi).
     */
    private LocalDate dataUltimeiRetrageri = null;

    /**
     * Hartă statică ce definește limitele maxime de retragere zilnică permise, în funcție de {@link TipMoneda}.
     */
    private static final Map<TipMoneda,Double> LIMITE_RETRAGERE_ZILNICA= new EnumMap<>(TipMoneda.class);
    static{
        LIMITE_RETRAGERE_ZILNICA.put(TipMoneda.RON,3500.0);
        LIMITE_RETRAGERE_ZILNICA.put(TipMoneda.EUR,1000.0);
        LIMITE_RETRAGERE_ZILNICA.put(TipMoneda.USD,1000.0);
    }
    /**
     * Constructor pentru un cont debitor.Apelează constructorul superclasei {@link ContBancar}.
     *
     * @param iban   Codul IBAN unic al contului.
     * @param client Deținătorul contului ({@link Client}).
     * @param moneda Moneda contului ({@link TipMoneda}).
     */
    public ContDebitor(String iban, Client client, TipMoneda moneda) {
        super(iban, client, moneda);
    }

    /**
     * Efectuează o retragere din cont, validând suma, fondurile disponibile și
     * limita zilnică de retragere.Metoda aplică următoarele reguli:
     * <ol>
     * <li>Suma retrasă trebuie să fie strict pozitivă.</li>
     * <li>Suma retrasă nu poate depăși soldul curent.</li>
     * <li>Suma retrasă, adăugată la totalul deja retras în ziua curentă,
     * nu poate depăși limita zilnică de retragere.</li>
     * </ol>
     * @param suma Reprezinta suma ce trebuie retrasa.
     * @throws ExceptieFonduriInsuficiente      dacă suma cerută depășește soldul disponibil.
     * @throws ExceptieRetragereZilnicaDepasita dacă suma cerută, cumulată cu retragerile din ziua curentă, depășește limita zilnică.
     * @throws IllegalArgumentException         dacă suma este negativă sau zero.
     */
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
        this.totalRetrasAstazi = rotunjeste(this.totalRetrasAstazi);
        this.dataUltimeiRetrageri=dataCurenta;
    }

    /**
     * Returnează soldul disponibil pentru operațiuni.
     * Pentru un cont debitor, acesta este identic cu soldul curent.
     *
     * @return Soldul disponibil (egal cu soldul curent).
     */
    @Override
    public double getSoldDisponibil() {
        return this.sold;
    }

    /**
     * Returnează soldul curent al contului.
     *
     * @return Soldul curent.
     */
    @Override
    public double getSold() {
        return this.sold;
    }

    /**
     * Returnează suma totală retrasă în ziua curentă.
     *
     * @return Totalul retras astăzi.
     */
    public double getTotalRetrasAstazi() {
        return totalRetrasAstazi;
    }

    /**
     * Returnează data ultimei retrageri.
     *
     * @return Data ultimei retrageri ({@link LocalDate}) sau null dacă nu
     * s-au făcut retrageri.
     */
    public LocalDate getDataUltimeiRetrageri() {
        return dataUltimeiRetrageri;
    }

    /**
     * Setează/Actualizează starea zilnică a retragerilor.
     *
     * @param totalRetrasAstazi Suma totală retrasă în ziua specificată.
     * @param dataUltimeiRetrageri Data calendaristică a ultimei operațiuni de retragere.
     */
    public void setStareZilnica(double totalRetrasAstazi,LocalDate dataUltimeiRetrageri) {
        this.totalRetrasAstazi = totalRetrasAstazi;
        this.dataUltimeiRetrageri = dataUltimeiRetrageri;
    }

}
