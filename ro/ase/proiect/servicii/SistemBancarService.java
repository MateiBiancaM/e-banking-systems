package ro.ase.proiect.servicii;

import ro.ase.proiect.exceptii.ExceptieDateInvalide;
import ro.ase.proiect.model.cont.Cont;
import ro.ase.proiect.model.tranzactii.Tranzactie;
import ro.ase.proiect.model.utilizator.Client;
import ro.ase.proiect.persistenta.StocareDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *Clasa care gestioneaza operatiuniile si starea aplicatiei in memorie
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 29.10.2025
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

    /**
     * Constructorul primeste o implementare pentru interfata StocareDate.
     * @param managerStocare este responsabil cu citirea/scrierea datelor, indiferent de format.
     */
    public SistemBancarService(StocareDate managerStocare) {
        this.managerStocare = managerStocare;
        this.clienti = new HashMap<>();
        this.conturi = new HashMap<>();
        this.istoricTranzactii = new ArrayList<>();
    }

    /**
     * Metoda apeleaza menegerul de stocare a datelor pentru a incarca datele din fisiere in colectiile din memorie
     * Este apelata la pornirea aplicatiei
     */
    public void incarcareDate() throws ExceptieDateInvalide {
        try{
            this.clienti=managerStocare.incarcaClienti();
            this.conturi=managerStocare.incarcaConturi(this.clienti);
            this.istoricTranzactii=managerStocare.incarcaTranzactii();
            System.out.println("-->Datele au fost incarcate cu succes!");
        }catch (ExceptieDateInvalide e){
            throw new ExceptieDateInvalide("Eroare la incarcarea datelor:"+e.getMessage());
        }
    }
    public void salvareDate(){
        try {
            managerStocare.salveazaClienti(this.clienti);
            managerStocare.salveazaConturi(this.conturi);
            managerStocare.salveazaTranzactii(this.istoricTranzactii);
            System.out.println("-->Datele au fost salvate cu succes!");
        } catch (Exception e) {
            throw new RuntimeException("Eroare la incarcarea datelor:"+e.getMessage());
        }
    }



}
