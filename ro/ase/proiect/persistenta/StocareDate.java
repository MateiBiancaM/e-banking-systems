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
 * @see ro.ase.proiect.servicii.SistemBancarService
 */
public interface StocareDate {
    Map<String, Client> incarcaClienti() throws ExceptieDateInvalide;
    Map<String, Cont> incarcaConturi(Map<String,Client> clientiExistenti) throws ExceptieDateInvalide;
    Set<Tranzactie> incarcaTranzactii() throws ExceptieDateInvalide;

    void salveazaClienti(Map<String,Client> clienti);
    void salveazaConturi(Map<String,Cont> conturi);
    void salveazaTranzactii(Set<Tranzactie> tranzactii);

}
