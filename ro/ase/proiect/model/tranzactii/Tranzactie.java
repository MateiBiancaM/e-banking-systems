package ro.ase.proiect.model.tranzactii;

import java.time.LocalDate;
import java.util.Objects;

/**
 *Clasa constituie o operatiune efectuata in sistem.
 *Aceasta are drept scop principal <b>stocarea</b> unui set de valori ce nu se mai schimba odata ce tranzactia a fost creata.
 *Clasa este finală și imutabilă.
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
    /**
     * Constructor pentru crearea unui obiect Tranzactie.
     *
     * @param idTranzactie   ID-ul unic al tranzacției.
     * @param data           Data la care a fost efectuată tranzacția.
     * @param tipTransfer    Tipul operațiunii (ex. DEPUNERE, RETRAGERE, TRANSFER).
     * @param suma           Suma tranzacționată.
     * @param ibanSursa      IBAN-ul contului sursă.
     * @param ibanDestinatie IBAN-ul contului destinație.
     */
    public Tranzactie(String idTranzactie, LocalDate data, String tipTransfer, double suma, String ibanSursa, String ibanDestinatie) {
        this.idTranzactie = idTranzactie;
        this.data = data;
        this.tipTransfer = tipTransfer;
        this.suma = suma;
        this.ibanSursa = ibanSursa;
        this.ibanDestinatie = ibanDestinatie;
    }

    /**
     * Returnează ID-ul unic al tranzacției.
     *
     * @return ID-ul tranzacției (String).
     */
    public String getIdTranzactie() {
        return idTranzactie;
    }

    /**
     * Returnează data la care a fost înregistrată tranzacția.
     *
     * @return Data tranzacției ({@link LocalDate}).
     */
    public LocalDate getData() {
        return data;
    }

    /**
     * Returnează tipul tranzacției.
     *
     * @return Tipul transferului (String).
     */
    public String getTipTransfer() {
        return tipTransfer;
    }

    /**
     * Returnează suma monetară a tranzacției.
     *
     * @return Suma tranzacționată (double).
     */
    public double getSuma() {
        return suma;
    }

    /**
     * Returnează IBAN-ul contului sursă (de unde au plecat fondurile).
     *
     * @return IBAN-ul sursă (String).
     */
    public String getIbanSursa() {
        return ibanSursa;
    }

    /**
     * Returnează IBAN-ul contului destinație (unde au ajuns fondurile).
     *
     * @return IBAN-ul destinație (String).
     */
    public String getIbanDestinatie() {
        return ibanDestinatie;
    }
    /**
     * Verifică dacă două tranzacții sunt egale.
     * Comparația se bazează pe toate câmpurile: id, dată, tip, sumă și IBAN-uri.
     *
     * @param o Obiectul cu care se compară.
     * @return true dacă obiectele sunt egale, false în caz contrar.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tranzactie that = (Tranzactie) o;
        return Double.compare(suma, that.suma) == 0 && Objects.equals(idTranzactie, that.idTranzactie) && Objects.equals(data, that.data) && Objects.equals(tipTransfer, that.tipTransfer) && Objects.equals(ibanSursa, that.ibanSursa) && Objects.equals(ibanDestinatie, that.ibanDestinatie);
    }
    /**
     * Generează codul hash pentru tranzacție.Se bazează pe toate câmpurile.
     *
     * @return Valoarea hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(idTranzactie, data, tipTransfer, suma, ibanSursa, ibanDestinatie);
    }
    /**
     * Returnează o reprezentare sub formă de String a obiectului Tranzactie.
     * Utilă pentru debugging și logging.
     *
     * @return Un String care conține toate atributele tranzacției.
     */
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
    /**
     * Compară tranzacțiile pentru a stabili ordinea. Acestea sunt sortate în primul rând după dată
     * în ordine crescătoare (cronologică).Dacă datele sunt identice, sortarea secundară se face după
     * ID-ul tranzacției, tot în ordine crescătoare.
     */
    @Override
    public int compareTo(Tranzactie o) {
        int comparatieData = this.data.compareTo(o.data);
        if (comparatieData != 0) {
            return comparatieData;
        }
        return this.idTranzactie.compareTo(o.idTranzactie);
    }
}
