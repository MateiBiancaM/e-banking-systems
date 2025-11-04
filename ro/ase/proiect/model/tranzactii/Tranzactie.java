package ro.ase.proiect.model.tranzactii;

import java.time.LocalDate;
import java.util.Objects;

/**
 *Clasa constituie o operatiune efectuata in sistem.
 * Aceasta are drept scop principal <b>stocarea</b> unui set de valori ce nu se mai schimba odata ce tranzactia a fost creata.
 *
 * @author Matei Maria-Bianca
 * @version 1.2
 * @since 4.11.2025
 */
public final class Tranzactie implements Comparable<Tranzactie> {
    private final String idTranzactie;
    private final LocalDate data;
    private final String tipTransfer;
    private final double suma;
    private final String ibanSursa;
    private final String ibanDestinatie;

    public Tranzactie(String idTranzactie, LocalDate data, String tipTransfer, double suma, String ibanSursa, String ibanDestinatie) {
        this.idTranzactie = idTranzactie;
        this.data = data;
        this.tipTransfer = tipTransfer;
        this.suma = suma;
        this.ibanSursa = ibanSursa;
        this.ibanDestinatie = ibanDestinatie;
    }

    public String getIdTranzactie() {
        return idTranzactie;
    }

    public LocalDate getData() {
        return data;
    }

    public String getTipTransfer() {
        return tipTransfer;
    }

    public double getSuma() {
        return suma;
    }

    public String getIbanSursa() {
        return ibanSursa;
    }

    public String getIbanDestinatie() {
        return ibanDestinatie;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tranzactie that = (Tranzactie) o;
        return Double.compare(suma, that.suma) == 0 && Objects.equals(idTranzactie, that.idTranzactie) && Objects.equals(data, that.data) && Objects.equals(tipTransfer, that.tipTransfer) && Objects.equals(ibanSursa, that.ibanSursa) && Objects.equals(ibanDestinatie, that.ibanDestinatie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTranzactie, data, tipTransfer, suma, ibanSursa, ibanDestinatie);
    }

    @Override
    public String toString() {
        return "Tranzactie{" +
                "idTranzactue='" + idTranzactie + '\'' +
                ", data=" + data +
                ", tipTransfer='" + tipTransfer + '\'' +
                ", suma=" + suma +
                ", ibanSursa='" + ibanSursa + '\'' +
                ", ibanDestinatie='" + ibanDestinatie + '\'' +
                '}';
    }

    @Override
    public int compareTo(Tranzactie o) {
        int comparatieData = this.data.compareTo(o.data);
        if (comparatieData != 0) {
            return comparatieData;
        }
        return this.idTranzactie.compareTo(o.idTranzactie);
    }
}
