package ro.ase.proiect.model.tranzactii;

import java.time.LocalDate;

/**
 *Clasa constituie o operatiune efectuata in sistem.
 *
 *
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 26.10.2025
 */
public class Tranzactie {
    String idTranzactue;
    LocalDate data;
    String tipTransfer;
    double suma;
    String ibanSursa;
    String ibanDestinatie;

}
