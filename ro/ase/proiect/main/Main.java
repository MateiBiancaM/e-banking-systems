package ro.ase.proiect.main;

import ro.ase.proiect.exceptii.ExceptieDateInvalide;
import ro.ase.proiect.persistenta.StocareDate;
import ro.ase.proiect.persistenta.StocareDateText;
import ro.ase.proiect.servicii.SistemBancarService;
import ro.ase.proiect.ui.consola.MeniuConsola;


/**
 * Punctul de intrare al aplicatei. Clasa este responsabila cu gestionarea fluxului principal al aplicatiei.
 *
 * @author Matei Maria-Bianca
 * @version 1.2
 * @since 29.10.2025
 */
public class Main {
    /**
     * Punctul de intrare pentru aplicația E-Banking.
     *<p>
     * Această metodă cuprinde întregul ciclu de viață al aplicației:
     * <ol>
     * <li>Inițializează persistența datelor ({@link StocareDateText}), serviciile ({@link SistemBancarService}) și interfața cu utilizatorul ({@link MeniuConsola}).</li>
     * <li>Invocă încărcarea datelor din fișiere. O {@link ExceptieDateInvalide} va opri aplicația cu un numar de 0 pentru clienti,conturi și tranzacții.</li>
     * <li>Pornește meniul interactiv pentru utilizator.</li>
     * <li>Salvează starea curentă a datelor în fișiere la închiderea <B>normală</B> a aplicației.</li>
     *</ol>
     * @param args Argumentele primite din linia de comandă (nu sunt utilizate în această aplicație).
     */
    public static void main(String[] args)  {
        System.out.println("--- BUN VENIT LA E-BANKING ---");
        String clientiFile = "ro/ase/proiect/clienti.txt";
        String conturiFile = "ro/ase/proiect/conturi.txt";
        String tranzactiiFile = "ro/ase/proiect/tranzactii.txt";

        StocareDate stocare = new StocareDateText(clientiFile, conturiFile, tranzactiiFile);
        SistemBancarService serviciuBancar = new SistemBancarService(stocare);
        MeniuConsola meniu = new MeniuConsola(serviciuBancar);
        try {
            serviciuBancar.incarcareDate();
        } catch (ExceptieDateInvalide e) {
            throw new RuntimeException(e);
        }
        meniu.start();
        serviciuBancar.salvareDate();
        System.out.println("--- APLICAȚIE ÎNCHISĂ ---");
    }
}
