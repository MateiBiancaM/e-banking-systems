package ro.ase.proiect.servicii;

import ro.ase.proiect.model.cont.Cont;
import ro.ase.proiect.model.tranzactii.Tranzactie;
import ro.ase.proiect.model.utilizator.Client;
import ro.ase.proiect.persistenta.StocareDate;

import java.util.List;
import java.util.Map;
/**
 *Clasa care gestioneaza operatiuniile si starea aplicatiei in memorie
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 26.10.2025
 * @see StocareDate
 * @see Cont
 * @see Client
 * @see Tranzactie
 */
public class SistemBancarService {
    private Map<String, Client> clienti;
    private Map<String, Cont> conturi;
    private List<Tranzactie> istoricTranzactii;
    private StocareDate managerStocare;

}
