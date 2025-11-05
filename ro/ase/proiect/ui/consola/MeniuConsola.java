package ro.ase.proiect.ui.consola;

import ro.ase.proiect.exceptii.*;
import ro.ase.proiect.model.cont.*;
import ro.ase.proiect.model.utilizator.Client;
import ro.ase.proiect.servicii.SistemBancarService;
import ro.ase.proiect.ui.gui.FereastraAutentificare;
import ro.ase.proiect.ui.gui.FereastraGrafic;
import ro.ase.proiect.ui.gui.FereastraMeniuPrincipal;
import ro.ase.proiect.ui.gui.FereastraRaport;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *Clasa gestioneaza interfata cu utilizatorul in consola. Va fi preluat inputul de la utilizator, iar sarcinile vor
 * fi delegate catre SistemBancarService
 *
 * @author Matei Maria-Bianca
 * @version 1.4
 * @since 4.11.2025
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
        while (this.clientAutentificat == null) {
            FereastraAutentificare loginGui = new FereastraAutentificare(null);
            String actiune = loginGui.afiseazaSiAsteaptaAlegere();
            try {
                switch (actiune) {
                    case "LOGIN":
                        gestioneazaAutentificare();
                        break;
                    case "REGISTER":
                        gestioneazaInregistrare();
                        break;
                    case "EXIT":
                        System.out.println("La revedere!");
                        return;
                    default:
                        System.err.println("Opțiune necunoscută.");
                }
            } catch (ExceptieAutentificareEsuata | ExceptieClientExistent |ExceptieDateInvalide e) {
                System.err.println("EROARE: " + e.getMessage());
                System.out.println("Apăsați Enter pentru a reîncerca...");
                scanner.nextLine();
            } catch (ExceptieClientInexistent e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("\nAutentificare reușită ca: " + clientAutentificat.getNume());
        ruleazaMeniuPrincipal();
    }

    private void ruleazaMeniuPrincipal() {
        boolean ruleaza = true;
        while (ruleaza) {
            FereastraMeniuPrincipal meniuGui = new FereastraMeniuPrincipal(null, clientAutentificat.getNume());

            System.out.println("\nAlegeti o optiune din meniul GUI principal.");
            int optiune = meniuGui.afiseazaSiAsteaptaAlegere();
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
                    case 5:
                        gestioneazaDeschidereCont();
                        break;
                    case 6:
                        gestioneazaRaportActivitate();
                        break;
                    case 7:
                        gestioneazaExtrasDeCont();
                        break;
                    case 8:
                        gestioneazaGraficFluxFinanciar();
                        break;
                    case 0:
                        ruleaza = false;
                        System.out.println("Deconectare.");
                        this.clientAutentificat = null;
                        start();
                        break;
                    case -1:
                        System.out.println("Acțiune anulată. Revenire la meniu.");
                        break;
                    default:
                        System.err.println("Opțiune invalidă. Vă rugăm reîncercați.");
                }
            } catch (Exception e) {
                System.err.println("EROARE: " + e.getMessage());
            }

            if (ruleaza) {
                System.out.println("\nOperațiune finalizată. Apăsați Enter pentru a reveni la meniu...");
                scanner.nextLine();
            }
        }
    }

    private void gestioneazaAutentificare() throws ExceptieAutentificareEsuata, ExceptieClientInexistent {
        System.out.print("Introduceți numele de familie: ");
        String nume = scanner.nextLine();
        System.out.print("Introduceți CNP-ul: ");
        String cnp = scanner.nextLine();

        this.clientAutentificat = sistemBancarService.autentificareClient(nume, cnp);
    }

    private void gestioneazaInregistrare() throws ExceptieClientExistent, ExceptieDateInvalide {
        System.out.print("Introduceți numele de familie: ");
        String nume = scanner.nextLine();
        System.out.print("Introduceți prenumele: ");
        String prenume = scanner.nextLine();
        System.out.print("Introduceți CNP-ul: ");
        String cnp = scanner.nextLine();

        this.clientAutentificat = sistemBancarService.inregistrareClient(nume, prenume, cnp);
    }

    private void gestioneazaDepunere() throws ExceptieContInexistent, ExceptieLimitaDepunereDepasita, ExceptieOperatiuneInvalida {
        System.out.println("\tPentru anulare introduceti:-1");
        System.out.print("Introduceți IBAN-ul destinație: ");
        String iban = scanner.nextLine();
        if(iban.equals("-1")){
            System.out.println("Operatiune anulata!");
            return;
        }
        double suma = citesteSuma();
        if(suma<0){
            return;
        }
        sistemBancarService.efectueazaDepunere(iban, suma);
        System.out.println("SUCCES:Suma:"+suma+" a fost depusa in contul:"+ iban+".");
    }

    private void gestioneazaRetragere() throws ExceptieContInexistent, ExceptieFonduriInsuficiente, ExceptieRetragereZilnicaDepasita, ExceptiePermisiuneRespinsa {
        System.out.println("\tPentru anulare introduceti:-1");
        System.out.print("Introduceți IBAN-ul destinație: ");
        String iban = scanner.nextLine();
        if(iban.equals("-1")){
            System.out.println("Operatiune anulata!");
            return;
        }
        double suma = citesteSuma();
        if(suma<0){
            return;
        }
        sistemBancarService.efectueazaRetragere(this.clientAutentificat,iban, suma);
        System.out.println("SUCCES:Suma:"+suma+" a fost retrasa din contul:"+ iban+".");
    }

    private void gestioneazaTransfer() throws ExceptieContInexistent, ExceptieFonduriInsuficiente, ExceptieRetragereZilnicaDepasita, ExceptiePermisiuneRespinsa, ExceptieLimitaDepunereDepasita, ExceptieTransferInvalid, ExceptieOperatiuneInvalida {
        System.out.println("\tPentru anulare introduceti:-1");
        System.out.print("Introduceți IBAN-ul sursă: ");
        String ibanSursa = scanner.nextLine();
        if(ibanSursa.equals("-1")){
            System.out.println("Operatiune anulata!");
            return;
        }
        System.out.print("Introduceți IBAN-ul destinație: ");
        String ibanDestinatie = scanner.nextLine();
        if(ibanDestinatie.equals("-1")){
            return;
        }
        double suma = citesteSuma();
        if(suma<0){
            return;
        }
        sistemBancarService.efecteazaTransfer(this.clientAutentificat,ibanSursa, ibanDestinatie, suma);
        System.out.println("SUCCES:Suma:"+suma+" a fost transferata din contul:"+ ibanSursa+", catre contul:"+ ibanDestinatie+".");
    }

    private void gestioneazaAfisareConturiClient() throws ExceptieClientInexistent {
        String clientId = this.clientAutentificat.getClientId();
        List<Cont> conturiClient = sistemBancarService.getConturiByClientId(clientId);
        if (conturiClient.isEmpty()) {
            System.out.println("Nu detineti nici-un cont in acest moment!");
            return;
        }
        System.out.println("\n\t\tConturile mele:\n");
        for(Cont cont : conturiClient) {
            System.out.println("\t-->IBAN:"+cont.getIban()+" Moneda:"+cont.getMoneda());
            if(cont instanceof ContDebitor){
                ContDebitor contDebitor = (ContDebitor) cont;
                System.out.println("CONT DEBIT");
                System.out.println("Sold:"+contDebitor.getSold());
                System.out.println("Total retras azi:"+contDebitor.getTotalRetrasAstazi());
            }else if(cont instanceof ContCreditor){
                ContCreditor contCreditor = (ContCreditor) cont;
                System.out.println("CONT CREDIT");
                System.out.println("Credit disponibil:"+contCreditor.getSoldDisponibil());
                System.out.println("Datorie curenta:"+contCreditor.getDatorieCurenta());

            }
        }
    }

    private void gestioneazaDeschidereCont(){
        System.out.println("Deschidere cont nou");
        System.out.println("Ce tip de cont doriti sa deschideti?");
        System.out.println("1. Cont debit");
        System.out.println("2. Cont credit");
        System.out.println("0. Anulare");
        TipCont tipCont;
        int optiuneTip=citesteOptiune();
        if(optiuneTip==1){
            tipCont=TipCont.DEBIT;
        }else if(optiuneTip==2){
            tipCont=TipCont.CREDIT;
        }else if(optiuneTip==0){
            System.out.println("Optiune anulata!");
            return;
        }else{
            System.err.println("Optiune invalida");
            return;
        }
        System.out.println("Alegeti moneda:");
        System.out.println("1. RON");
        System.out.println("2. EUR");
        System.out.println("3. USD");
        System.out.println("0. Anulare");
        TipMoneda tipMoneda;
        int optiuneMoneda=citesteOptiune();
        switch (optiuneMoneda){
            case 1:
                tipMoneda=TipMoneda.RON;
                break;
            case 2:
                tipMoneda=TipMoneda.EUR;
                break;
            case 3:
                tipMoneda=TipMoneda.USD;
                break;
            case 0:
                System.out.println("Optiune anulata!");
                return;
            default:
                System.err.println("Optiune invalida");
                return;
        }
        sistemBancarService.creazaContNou(this.clientAutentificat,tipCont,tipMoneda);
    }

    private void gestioneazaRaportActivitate() throws ExceptieClientInexistent, ExceptieContInexistent, ExceptiePermisiuneRespinsa {
        List<Cont> conturileMele=sistemBancarService.getConturiByClientId(this.clientAutentificat.getClientId());
        if(conturileMele.isEmpty()){
            System.out.println("Nu detineti niciun cont pentru generarea unui raport!");
            return;
        }
        int i=1;
        for(Cont c:conturileMele){
            System.out.println(i+". "+c.getIban()+" ["+c.getMoneda()+"]");
            i++;
        }
        System.out.println("0. Anulare");
        System.out.println("Alegere cont pentru generare raport:");
        int opt= citesteOptiune();
        if(opt<=0|| opt>conturileMele.size()){
            System.out.println("Optiune anulata!");
            return;
        }
        Cont contSelectat=conturileMele.get(opt-1);
        String ibanSelectat=contSelectat.getIban();
        TipMoneda monedaSelectata=contSelectat.getMoneda();
        double[][] statistici= sistemBancarService.genereazaStatisticiContClient(this.clientAutentificat,ibanSelectat);
        new FereastraRaport(statistici,clientAutentificat.getNume(),clientAutentificat.getPrenume(),contSelectat);
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
            System.out.println("Introduceti suma dorita:");
            String input=scanner.nextLine();
            try {
                if(input.equals("-1")){
                    System.out.println("Operatiune anulata!");
                    return -1.0;
                }
                double suma = Double.parseDouble(input);
                if (suma <= 0) {
                    System.err.println("Suma trebuie să fie pozitivă. Reîncercați.");
                } else {
                    return suma;
                }
            } catch (NumberFormatException e) {
                System.err.println("Vă rugăm introduceți o valoare numerică validă. Reîncercați:");
            }
        }
    }

    private void gestioneazaExtrasDeCont()
            throws ExceptieClientInexistent, ExceptieContInexistent, ExceptiePermisiuneRespinsa, IOException {

        System.out.println("--- Generare Extras de Cont  ---");
        List<Cont> conturileMele = sistemBancarService.getConturiByClientId(this.clientAutentificat.getClientId());
        if (conturileMele.isEmpty()) {
            System.out.println("Nu dețineți niciun cont pentru a genera un extras.");
            return;
        }
        int index = 1;
        for (Cont cont : conturileMele) {
            System.out.println(index + ". " + cont.getIban() + " [" + cont.getMoneda() + "]");
            index++;
        }
        System.out.println("0. Anulare");
        System.out.println("Alegeți contul pentru care doriți extrasul:");

        int optiune = citesteOptiune();
        if (optiune <= 0 || optiune > conturileMele.size()) {
            System.out.println("Operațiune anulată.");
            return;
        }
        Cont contSelectat = conturileMele.get(optiune - 1);


        System.out.println("1. Ultimele 3 luni");
        System.out.println("2. Ultimele 6 luni");
        System.out.println("3. Istoricul complet");
        System.out.println("0. Anulare");
        System.out.println("Alegeți perioada pentru extras:");
        int optiunePerioada = citesteOptiune();
        int luniInUrma;
        switch (optiunePerioada) {
            case 1:
                luniInUrma = 3;
                break;
            case 2:
                luniInUrma = 6;
                break;
            case 3:
                luniInUrma = 0;
                break;
            default:
                System.out.println("Operațiune anulată.");
                return;
        }

        System.out.println("Se generează extrasul...");
        String caleFisier = sistemBancarService.genereazaExtrasDeCont(this.clientAutentificat, contSelectat.getIban(), luniInUrma);

        System.out.println("SUCCES: Extrasul de cont a fost generat și salvat aici:");
        System.out.println(caleFisier);
    }

    /**
     * NOU/MODIFICAT: Gestionează lansarea ferestrei cu graficul fluxului financiar zilnic,
     * după ce utilizatorul selectează un cont.
     */
    private void gestioneazaGraficFluxFinanciar()
            throws ExceptieClientInexistent, ExceptieContInexistent, ExceptiePermisiuneRespinsa {

        List<Cont> conturileMele = sistemBancarService.getConturiByClientId(this.clientAutentificat.getClientId());
        if (conturileMele.isEmpty()) {
            System.out.println("Nu aveți conturi înregistrate pentru a genera un grafic.");
            System.out.println("Operațiune anulată.");
            return;
        }
        int index = 1;
        for (Cont cont : conturileMele) {
            System.out.println(index + ". " + cont.getIban() + " [" + cont.getMoneda() + "]");
            index++;
        }
        System.out.println("0. Anulare");
        System.out.println("Vă rugăm alegeți contul pentru care doriți graficul fluxului financiar:");

        int optiune = citesteOptiune();
        if (optiune <= 0 || optiune > conturileMele.size()) {
            System.out.println("Operațiune anulată.");
            return;
        }

        Cont contSelectat = conturileMele.get(optiune - 1);
        String ibanContSelectat = contSelectat.getIban();
        String numeClient = clientAutentificat.getNume();
        int zileInUrma = 30;

        Map<String, double[]> statistici = sistemBancarService.genereazaStatisticiZilnice(
                ibanContSelectat,
                zileInUrma
        );

        new FereastraGrafic(statistici, numeClient, zileInUrma);
        System.out.println("Fereastra cu graficul fluxului financiar a fost lansată.");
    }

}
