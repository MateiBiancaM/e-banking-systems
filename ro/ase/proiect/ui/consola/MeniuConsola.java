package ro.ase.proiect.ui.consola;

import ro.ase.proiect.exceptii.*;
import ro.ase.proiect.model.cont.Cont;
import ro.ase.proiect.model.cont.ContCreditor;
import ro.ase.proiect.model.cont.ContDebitor;
import ro.ase.proiect.model.utilizator.Client;
import ro.ase.proiect.servicii.SistemBancarService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 *Clasa gestioneaza interfata cu utilizatorul in consola. Va fi preluat inputul de la utilizator, iar sarcinile vor
 * fi delegate catre SistemBancarService
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 2.11.2025
 * @see SistemBancarService
 */
public class MeniuConsola {
    private SistemBancarService sistemBancarService;
    private Scanner scanner;
    private Client clientAutentificat;

    public MeniuConsola(SistemBancarService sistemBancarService) {
        this.sistemBancarService = sistemBancarService;
        this.scanner = new Scanner(System.in);
        this.clientAutentificat=null;
    }

    public void start() {
        while(this.clientAutentificat==null){
            afiseazaMeniuAutentificare();
            int optiune=citesteOptiune();
            try{
                switch (optiune){
                    case 1:
                        gestioneazaAutentificare();
                        break;
                    case 2:
                        gestioneazaInregistrare();
                        break;
                    case 0:
                        System.out.println("Aplicatie inchisa!");
                        return;
                    default:
                        System.out.println("Optiune invalida!");
                }
            } catch (ExceptieClientInexistent | ExceptieAutentificareEsuata | ExceptieClientExistent e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("\nBun venit, "+this.clientAutentificat.getNume()+" "+this.clientAutentificat.getPrenume()+ " !\n");
        ruleazaMeniuPrincipal();
    }

    private void ruleazaMeniuPrincipal() {
        boolean ruleaza = true;
        while (ruleaza) {
            afiseazaMeniuPrincipal();
            int optiune = citesteOptiune();

            try {
                switch (optiune) {
                    case 1:
                        gestioneazaDepunere();
                        break;
                    case 2:
                        gestioneazaRetragere();
                        break;
                    case 3:
                        gestioneazaTransfer();
                        break;
                    case 4:
                        gestioneazaAfisareConturiClient();
                        break;
                    case 0:
                        ruleaza = false;
                        System.out.println("La revedere!");
                        this.clientAutentificat=null;
                        start();
                        break;
                    default:
                        System.err.println("Opțiune invalidă. Vă rugăm reîncercați.");
                }
            } catch (ExceptiePermisiuneRespinsa | ExceptieContInexistent | ExceptieFonduriInsuficiente | ExceptieRetragereZilnicaDepasita e) {
                System.err.println("EROARE: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("EROARE DE VALIDARE: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("A APĂRUT O EROARE NEAȘTEPTATĂ: " + e.getMessage());
                e.printStackTrace();
            }

            if (ruleaza) {
                System.out.println("Apăsați Enter pentru a continua...");
                scanner.nextLine(); // Așteaptă Enter
            }
        }
    }

    private void afiseazaMeniuAutentificare(){
        System.out.println("--- AUTENTIFICARE E-BANKING ---\n");
        System.out.println("1. Autentificare");
        System.out.println("2. Inregistrare");
        System.out.println("0. Ieșire");
        System.out.print("Alegeți opțiunea: ");
    }

    private void afiseazaMeniuPrincipal() {
        System.out.println("--- MENIU E-BANKING ---\n");
        System.out.println("1. Efectuează o depunere");
        System.out.println("2. Efectuează o retragere");
        System.out.println("3. Efectuează un transfer");
        System.out.println("4. Vizualizează conturile");
        System.out.println("0. Ieșire");
        System.out.print("Alegeți opțiunea: ");
    }



    private void gestioneazaAutentificare() throws ExceptieAutentificareEsuata, ExceptieClientInexistent {
        System.out.print("Introduceți Numele de familie: ");
        String nume = scanner.nextLine();
        System.out.print("Introduceți CNP-ul: ");
        String cnp = scanner.nextLine();

        this.clientAutentificat = sistemBancarService.autentificareClient(nume, cnp);
    }

    private void gestioneazaInregistrare() throws ExceptieClientExistent {
        System.out.print("Introduceți Numele de familie: ");
        String nume = scanner.nextLine();
        System.out.print("Introduceți Prenumele: ");
        String prenume = scanner.nextLine();
        System.out.print("Introduceți CNP-ul: ");
        String cnp = scanner.nextLine();

        this.clientAutentificat = sistemBancarService.inregistrareClient(nume, prenume, cnp);
    }

    private void gestioneazaDepunere() throws ExceptieContInexistent {
        System.out.print("Introduceți IBAN-ul destinație: ");
        String iban = scanner.nextLine();
        System.out.print("Introduceți suma de depus: ");
        double suma = citesteSuma();
        sistemBancarService.efectueazaDepunere(iban, suma);
        System.out.println("-->Suma:"+suma+" a fost depusa in contul:"+ iban+".");
    }

    private void gestioneazaRetragere() throws ExceptieContInexistent, ExceptieFonduriInsuficiente, ExceptieRetragereZilnicaDepasita, ExceptiePermisiuneRespinsa {
        System.out.print("Introduceți IBAN-ul sursă: ");
        String iban = scanner.nextLine();
        System.out.print("Introduceți suma de retras: ");
        double suma = citesteSuma();
        sistemBancarService.efectueazaRetragere(this.clientAutentificat,iban, suma);
        System.out.println("-->Suma:"+suma+" a fost retrasa din contul:"+ iban+".");
    }

    private void gestioneazaTransfer() throws ExceptieContInexistent, ExceptieFonduriInsuficiente, ExceptieRetragereZilnicaDepasita, ExceptiePermisiuneRespinsa {
        System.out.print("Introduceți IBAN-ul sursă: ");
        String ibanSursa = scanner.nextLine();
        System.out.print("Introduceți IBAN-ul destinație: ");
        String ibanDestinatie = scanner.nextLine();
        System.out.print("Introduceți suma de transferat: ");
        double suma = citesteSuma();
        sistemBancarService.efecteazaTransfer(this.clientAutentificat,ibanSursa, ibanDestinatie, suma);
        System.out.println("-->Suma:"+suma+" a fost transferata din contul:"+ ibanSursa+", catre contul:"+ ibanDestinatie+".");
    }

    private void gestioneazaAfisareConturiClient() throws ExceptieClientInexistent {
        String clientId = this.clientAutentificat.getClientId();
        List<Cont> conturiClient = sistemBancarService.getConturiByClientId(clientId);
        if (conturiClient.isEmpty()) {
            System.out.println("Nu detineti nici-un cont in acest moment!");
            return;
        }
        System.out.println("Conturile mele:");
        for(Cont cont : conturiClient) {
            System.out.println("->IBAN:"+cont.getIban()+" Moneda:"+cont.getMoneda());
            if(cont instanceof ContDebitor){
                ContDebitor contDebitor = (ContDebitor) cont;
                System.out.println("Cont Debit");
                System.out.println("Sold:"+contDebitor.getSold());
                System.out.println("Total retras azi:"+contDebitor.getTotalRetrasAstazi());
            }else if(cont instanceof ContCreditor){
                ContCreditor contCreditor = (ContCreditor) cont;
                System.out.println("Credit disponibil:"+contCreditor.getSoldDisponibil());
                System.out.println("Datorie curenta:"+contCreditor.getDatorieCurenta());

            }
        }
    }

    private int citesteOptiune() {
        try {
            int optiune = scanner.nextInt();
            scanner.nextLine();
            return optiune;
        } catch (InputMismatchException e) {
            System.err.println("Vă rugăm introduceți un număr.");
            scanner.nextLine();
            return -1;
        }
    }

    private double citesteSuma() {
        while (true) {
            try {
                double suma = scanner.nextDouble();
                scanner.nextLine();
                if (suma <= 0) {
                    System.err.println("Suma trebuie să fie pozitivă. Reîncercați:");
                } else {
                    return suma;
                }
            } catch (InputMismatchException e) {
                System.err.println("Vă rugăm introduceți o valoare numerică validă. Reîncercați:");
                scanner.nextLine();
            }
        }
    }
}
