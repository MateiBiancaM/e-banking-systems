package ro.ase.proiect.main;

import ro.ase.proiect.exceptii.ExceptieContInexistent;
import ro.ase.proiect.exceptii.ExceptieDateInvalide;
import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;
import ro.ase.proiect.exceptii.ExceptieRetragereZilnicaDepasita;
import ro.ase.proiect.model.cont.Cont;
import ro.ase.proiect.model.cont.ContCreditor;
import ro.ase.proiect.model.cont.ContDebitor;
import ro.ase.proiect.model.cont.TipMoneda;
import ro.ase.proiect.model.tranzactii.Tranzactie;
import ro.ase.proiect.model.utilizator.Client;
import ro.ase.proiect.persistenta.StocareDate;
import ro.ase.proiect.persistenta.StocareDateText;
import ro.ase.proiect.servicii.SistemBancarService;
import ro.ase.proiect.ui.consola.MeniuConsola;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Punctul de intrare al aplicatei. Clasa este responsabila cu gestionarea fluxului principal al aplicatiei.
 *
 * @author Matei Maria-Bianca
 * @version 1.2
 * @since 29.10.2025
 */
public class Main {
    public static void main(String[] args) throws ExceptieDateInvalide {
        System.out.println("--- BUN VENIT LA E-BANKING ---");

        String clientiFile = "ro/ase/proiect/clienti.txt";
        String conturiFile = "ro/ase/proiect/conturi.txt";
        String tranzactiiFile = "ro/ase/proiect/tranzactii.txt";

        StocareDate stocare = new StocareDateText(clientiFile, conturiFile, tranzactiiFile);

        SistemBancarService serviciuBancar = new SistemBancarService(stocare);

        MeniuConsola meniu = new MeniuConsola(serviciuBancar);

        serviciuBancar.incarcareDate();

        meniu.start();

        serviciuBancar.salvareDate();

        System.out.println("--- APLICAȚIE ÎNCHISĂ ---");
    }
}
