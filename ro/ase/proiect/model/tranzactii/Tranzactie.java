package ro.ase.proiect.model.tranzactii;

import java.time.LocalDate;
import java.util.Objects;

/**
 *Clasa constituie o operatiune efectuata in sistem.
 * Aceasta are drept scop principal <b>stocarea</b> unui set de valori ce nu se mai schimba odata ce tranzactia a fost creata.
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 27.10.2025
 */
public final class Tranzactie {
    private final String idTranzactue;
    private final LocalDate data;
    private final String tipTransfer;
    private final double suma;
    private final String ibanSursa;
    private final String ibanDestinatie;

    public Tranzactie(String idTranzactue, LocalDate data, String tipTransfer, double suma, String ibanSursa, String ibanDestinatie) {
        this.idTranzactue = idTranzactue;
        this.data = data;
        this.tipTransfer = tipTransfer;
        this.suma = suma;
        this.ibanSursa = ibanSursa;
        this.ibanDestinatie = ibanDestinatie;
    }

    public String getIdTranzactue() {
        return idTranzactue;
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
        return Double.compare(suma, that.suma) == 0 && Objects.equals(idTranzactue, that.idTranzactue) && Objects.equals(data, that.data) && Objects.equals(tipTransfer, that.tipTransfer) && Objects.equals(ibanSursa, that.ibanSursa) && Objects.equals(ibanDestinatie, that.ibanDestinatie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTranzactue, data, tipTransfer, suma, ibanSursa, ibanDestinatie);
    }

    @Override
    public String toString() {
        return "Tranzactie{" +
                "idTranzactue='" + idTranzactue + '\'' +
                ", data=" + data +
                ", tipTransfer='" + tipTransfer + '\'' +
                ", suma=" + suma +
                ", ibanSursa='" + ibanSursa + '\'' +
                ", ibanDestinatie='" + ibanDestinatie + '\'' +
                '}';
    }
}
