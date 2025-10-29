package ro.ase.proiect.main;

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

import java.time.LocalDate;
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
    public static void main(String[] args) {

        String clientiFile = "ro/ase/proiect/clienti.txt";
        String conturiFile = "ro/ase/proiect/conturi.txt";
        String tranzactiiFile = "ro/ase/proiect/tranzactii.txt";

        StocareDate stocare = new StocareDateText(clientiFile, conturiFile, tranzactiiFile);
        System.out.println("--- SALVAREA DATELOR ---");
        salveazaDateDeTest(stocare);

        System.out.println("\n--- ÎNCĂRCAREA ȘI AFIȘAREA DATELOR ---");
        incarcaSiAfiseazaDate(stocare);
    }

    private static void salveazaDateDeTest(StocareDate stocare) {

        Client clientIon = new Client("C1", "Popescu", "Ion", "1901010123456");
        Client clientAna = new Client("C2", "Vasilescu", "Ana", "2950505123456");

        ContDebitor contDebitAna = new ContDebitor("RO01DEBIT_ANA", clientAna, TipMoneda.RON);
        ContCreditor contCreditAna = new ContCreditor("RO02CREDIT_ANA", clientAna, TipMoneda.EUR, 10000.0);

        try {
            contDebitAna.depunere(500);
            contDebitAna.retragere(50);
        } catch (Exception e) { e.printStackTrace(); }

        Map<String, Client> clientiDeSalvat = new HashMap<>();
        clientiDeSalvat.put(clientIon.getClientId(), clientIon);
        clientiDeSalvat.put(clientAna.getClientId(), clientAna);
        Map<String, Cont> conturiDeSalvat = new HashMap<>();
        conturiDeSalvat.put(contDebitAna.getIban(), contDebitAna);
        conturiDeSalvat.put(contCreditAna.getIban(), contCreditAna);
        List<Tranzactie> tranzactiiDeSalvat = new ArrayList<>();
        try {
            stocare.salveazaClienti(clientiDeSalvat);
            stocare.salveazaConturi(conturiDeSalvat);
            stocare.salveazaTranzactii(tranzactiiDeSalvat);
            System.out.println("Salvare finalizată. Verifică fișierele .txt!");
        } catch (Exception e) {
            System.err.println("A eșuat salvarea: " + e.getMessage());
        }
    }

    private static void incarcaSiAfiseazaDate(StocareDate stocare) {
        try {
            Map<String, Client> clientiIncarcati = stocare.incarcaClienti();
            Map<String, Cont> conturiIncarcate = stocare.incarcaConturi(clientiIncarcati);

            System.out.println("CLIENȚI ÎNCĂRCAȚI (" + clientiIncarcati.size() + "):");
            for (Client client : clientiIncarcati.values()) {
                System.out.println("  -> Client: " + client.getNume() + " " + client.getPrenume() + " (ID: " + client.getClientId() + ")");
            }
            System.out.println("\nCONTURI ÎNCĂRCATE (" + conturiIncarcate.size() + "):");
            for (Cont cont : conturiIncarcate.values()) {
                System.out.print("  -> Cont IBAN: " + cont.getIban());
                System.out.print(", Proprietar: " + cont.getClient().getNume());

                if (cont instanceof ContDebitor) {
                    ContDebitor cd = (ContDebitor) cont;
                    System.out.println(" [DEBIT], Sold: " + cd.getSold() + ", Total retras azi: " + cd.getTotalRetrasAstazi());
                } else if (cont instanceof ContCreditor) {
                    ContCreditor cc = (ContCreditor) cont;
                    System.out.println(" [CREDIT], Limită: " + cc.getLimitaCredit() + ", Datorie: " + cc.getDatorieCurenta());
                }
            }

        } catch (ExceptieDateInvalide e) {
            System.err.println("A eșuat încărcarea: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
