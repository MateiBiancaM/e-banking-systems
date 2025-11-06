package ro.ase.proiect.persistenta;

import ro.ase.proiect.exceptii.ExceptieDateInvalide;
import ro.ase.proiect.model.cont.Cont;
import ro.ase.proiect.model.tranzactii.Tranzactie;
import ro.ase.proiect.model.utilizator.Client;

import java.util.Map;
import java.util.Set;

/**
 *Interfata ce asigura pesistenta datelor, prin incarcarea si salvarea acestora.
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 4.11.2025
 * @see StocareDateText
 */
public interface StocareDate {
    /**
     * Încarcă datele clienților din sursa de persistență.
     *
     * @return O hartă (Map) a clienților, unde cheia este ID-ul clientului.
     * @throws ExceptieDateInvalide dacă datele din sursă sunt corupte sau au un format invalid.
     */
    Map<String, Client> incarcaClienti() throws ExceptieDateInvalide;

    /**
     * Încarcă datele conturilor din sursa de persistență și le asociază clienților existenți.
     *
     * @param clientiExistenti Harta clienților existenți, necesară pentru a lega conturile de deținători.
     * @return O hartă (Map) a conturilor, unde cheia este IBAN-ul.
     * @throws ExceptieDateInvalide dacă datele sunt corupte, au un format invalid sau
     * dacă un cont face referire la un client inexistent.
     */
    Map<String, Cont> incarcaConturi(Map<String, Client> clientiExistenti) throws ExceptieDateInvalide;

    /**
     * Încarcă istoricul tranzacțiilor din sursa de persistență.
     *
     * @return Un set (Set) de obiecte {@link Tranzactie}.
     * @throws ExceptieDateInvalide dacă datele sunt corupte sau au un format invalid.
     */
    Set<Tranzactie> incarcaTranzactii() throws ExceptieDateInvalide;

    /**
     * Salvează starea curentă a clienților în sursa de persistență.
     *
     * @param clienti Harta clienților (ID -> Client) care trebuie salvată.
     */
    void salveazaClienti(Map<String, Client> clienti);

    /**
     * Salvează starea curentă a conturilor în sursa de persistență.
     *
     * @param conturi Harta conturilor (IBAN -> Cont) care trebuie salvată.
     */
    void salveazaConturi(Map<String, Cont> conturi);

    /**
     * Salvează starea curentă a tranzacțiilor în sursa de persistență.
     *
     * @param tranzactii Setul de tranzacții care trebuie salvat.
     */
    void salveazaTranzactii(Set<Tranzactie> tranzactii);

}
