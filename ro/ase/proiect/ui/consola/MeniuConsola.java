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
 * Clasa gestioneaza interfata cu utilizatorul in consola. Va fi preluat inputul de la utilizator, iar sarcinile vor
 * fi delegate catre SistemBancarService.
 * Această clasă, deși rulează în consolă pentru input-ul de date,
 * lansează ferestre GUI pentru a afișa opțiunile și rapoartele.
 *
 * @author Matei Maria-Bianca
 * @version 1.4
 * @since 4.11.2025
 * @see SistemBancarService
 * @see FereastraAutentificare
 * @see FereastraGrafic
 * @see FereastraMeniuPrincipal
 * @see FereastraRaport
 */
public class MeniuConsola {
    /**
     * Serviciul care conține logica de business a aplicației.
     */
    private SistemBancarService sistemBancarService;
    /**
     * Obiectul Scanner folosit pentru a citi input-ul de la utilizator din consolă.
     */
    private Scanner scanner;
    /**
     * Stochează starea clientului curent după o autentificare reușită.
     */
    private Client clientAutentificat;
    /**
     * Constructor pentru MeniuConsola. Inițializează scannerul și injectează serviciul bancar.
     *
     * @param sistemBancarService Serviciul de business care va fi utilizat de meniu.
     */
    public MeniuConsola(SistemBancarService sistemBancarService) {
        this.sistemBancarService = sistemBancarService;
        this.scanner = new Scanner(System.in);
        this.clientAutentificat=null;
    }
    /**
     * Pornește interfața cu utilizatorul.
     * Afișează bucla de autentificare/înregistrare (folosind {@link FereastraAutentificare}).
     * După autentificarea cu succes, apelează {@link #ruleazaMeniuPrincipal()}.
     * Bucla continuă până când utilizatorul alege să iasă.
     */
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
    /**
     * Rulează bucla meniului principal după ce un client a fost autentificat.
     * Afișează opțiunile principale folosind {@link FereastraMeniuPrincipal} și
     * deleagă acțiunile către metodele de gestionare corespunzătoare.
     * Gestionează și deconectarea (setând {@code clientAutentificat} la null și
     * reapelând {@link #start()}).
     */
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
    /**
     * Gestionează fluxul de autentificare.
     * Preia numele și CNP-ul de la consolă și apelează serviciul de autentificare.
     * Setează {@link #clientAutentificat} dacă autentificarea reușește.
     *
     * @throws ExceptieAutentificareEsuata dacă datele de autentificare sunt incorecte.
     * @throws ExceptieClientInexistent    dacă clientul nu este găsit (conform semnăturii metodei).
     */
    private void gestioneazaAutentificare() throws ExceptieAutentificareEsuata, ExceptieClientInexistent {
        System.out.print("Introduceți numele de familie: ");
        String nume = scanner.nextLine();
        System.out.print("Introduceți CNP-ul: ");
        String cnp = scanner.nextLine();

        this.clientAutentificat = sistemBancarService.autentificareClient(nume, cnp);
    }
    /**
     * Gestionează fluxul de înregistrare a unui client nou.
     * Preia numele, prenumele și CNP-ul de la consolă.
     * Apelează serviciul de înregistrare și setează clientul nou ca {@link #clientAutentificat}.
     *
     * @throws ExceptieClientExistent dacă un client cu același CNP există deja.
     * @throws ExceptieDateInvalide   dacă formatul CNP-ului este invalid.
     */
    private void gestioneazaInregistrare() throws ExceptieClientExistent, ExceptieDateInvalide {
        System.out.print("Introduceți numele de familie: ");
        String nume = scanner.nextLine();
        System.out.print("Introduceți prenumele: ");
        String prenume = scanner.nextLine();
        System.out.print("Introduceți CNP-ul: ");
        String cnp = scanner.nextLine();

        this.clientAutentificat = sistemBancarService.inregistrareClient(nume, prenume, cnp);
    }
    /**
     * Gestionează operațiunea de depunere. Citește IBAN-ul destinație și suma de la consolă.
     *
     * @throws ExceptieContInexistent           dacă IBAN-ul nu este găsit.
     * @throws ExceptieLimitaDepunereDepasita   dacă suma depășește limita.
     * @throws ExceptieOperatiuneInvalida       dacă operațiunea este invalidă.
     */
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
    /**
     * Gestionează operațiunea de retragere.Citește IBAN-ul sursă și suma, apoi validează operațiunea pentru clientul autentificat.
     *
     * @throws ExceptieContInexistent           dacă IBAN-ul nu este găsit.
     * @throws ExceptieFonduriInsuficiente      dacă fondurile sunt insuficiente.
     * @throws ExceptieRetragereZilnicaDepasita dacă limita zilnică este depășită.
     * @throws ExceptiePermisiuneRespinsa       dacă contul nu aparține clientului autentificat.
     */
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
    /**
     * Gestionează operațiunea de transfer bancar. Citește IBAN-ul sursă, destinație și suma.
     *
     * @throws ExceptieContInexistent           dacă unul dintre IBAN-uri nu este găsit.
     * @throws ExceptieFonduriInsuficiente      dacă fondurile sursă sunt insuficiente.
     * @throws ExceptieRetragereZilnicaDepasita dacă limita zilnică sursă este depășită.
     * @throws ExceptiePermisiuneRespinsa       dacă contul sursă nu aparține clientului.
     * @throws ExceptieLimitaDepunereDepasita   dacă suma depășește limita la destinație.
     * @throws ExceptieTransferInvalid          dacă monedele nu corespund.
     * @throws ExceptieOperatiuneInvalida       dacă depunerea la destinație eșuează.
     */
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
    /**
     * Afișează la consolă detaliile tuturor conturilor deținute de clientul autentificat.
     *
     * @throws ExceptieClientInexistent dacă ID-ul clientului autentificat nu este găsit.
     */
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
    /**
     * Gestionează fluxul de creare a unui cont nou (DEBIT sau CREDIT) pentru clientul autentificat.
     * Preia tipul contului și moneda de la utilizator.
     */
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
    /**
     * Gestionează generarea raportului de activitate. Permite clientului să aleagă un cont,
     * apoi lansează {@link FereastraRaport} cu statisticile corespunzătoare.
     *
     * @throws ExceptieClientInexistent     dacă clientul nu este găsit.
     * @throws ExceptieContInexistent       dacă contul selectat nu este găsit.
     * @throws ExceptiePermisiuneRespinsa   dacă contul nu aparține clientului.
     */
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
    /**
     * Metodă utilitară pentru a citi un număr întreg (opțiune de meniu) de la consolă.
     *
     * @return Opțiunea numerică citită, sau -1 în caz de input invalid.
     */
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
    /**
     * Metodă utilitară pentru a citi o sumă (double) validă de la consolă.
     * Rulează într-o buclă până când utilizatorul introduce o valoare numerică pozitivă
     * sau -1 pentru a anula.
     *
     * @return Suma validă citită, sau -1.0 dacă operațiunea este anulată.
     */
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
    /**
     * Gestionează fluxul de generare a unui extras de cont în fișier text.
     * Permite utilizatorului să selecteze contul și perioada (3 luni, 6 luni, complet).
     * Afișează calea fișierului generat.
     *
     * @throws ExceptieClientInexistent     dacă clientul nu este găsit.
     * @throws ExceptieContInexistent       dacă contul selectat nu este găsit.
     * @throws ExceptiePermisiuneRespinsa   dacă contul nu aparține clientului.
     * @throws IOException                dacă apare o eroare la scrierea fișierului extras.
     */
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
     * Gestionează lansarea ferestrei cu graficul fluxului financiar zilnic.
     * Permite utilizatorului să selecteze un cont și lansează {@link FereastraGrafic}
     * pentru ultimele 30 de zile.
     *
     * @throws ExceptieClientInexistent     dacă clientul nu este găsit.
     * @throws ExceptieContInexistent       dacă contul selectat nu este găsit.
     * @throws ExceptiePermisiuneRespinsa   dacă serviciul detectează o problemă de permisiune
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
