package ro.ase.proiect.persistenta;

import ro.ase.proiect.exceptii.ExceptieDateInvalide;
import ro.ase.proiect.model.cont.Cont;
import ro.ase.proiect.model.tranzactii.Tranzactie;
import ro.ase.proiect.model.utilizator.Client;

import java.util.List;
import java.util.Map;
/**
 *Implementarea concreta a interfetei {@link StocareDate} ce se ocupa de gestionarea datelor in fisiere text.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 26.10.2025
 */
public class StocareDateText implements StocareDate{
    private String caleFisierClienti;
    private String caleFisierConturi;
    private String caleFisierTranzactii;
    @Override
    public Map<String, Client> incarcaClienti() throws ExceptieDateInvalide {
        return Map.of();
    }

    @Override
    public Map<String, Cont> incarcaConturi(Map<String, Client> clientiExistenti) throws ExceptieDateInvalide {
        return Map.of();
    }

    @Override
    public List<Tranzactie> incarcaTranzactii() throws ExceptieDateInvalide {
        return List.of();
    }

    @Override
    public void salveazaClienti(Map<String, Client> clienti) {

    }

    @Override
    public void salveazaCConturi(Map<String, Cont> conturi) {

    }

    @Override
    public void salveazaTranzactii(List<Tranzactie> tranzactii) {

    }
}
