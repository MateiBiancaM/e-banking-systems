package ro.ase.proiect.servicii;

import ro.ase.proiect.exceptii.*;
import ro.ase.proiect.model.cont.*;
import ro.ase.proiect.model.tranzactii.Tranzactie;
import ro.ase.proiect.model.utilizator.Client;
import ro.ase.proiect.persistenta.StocareDate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

/**
 *Clasa care gestioneaza operatiuniile si starea aplicatiei in memorie.
 *
 * @author Matei Maria-Bianca
 * @version 1.5
 * @since 04.11.2025
 * @see StocareDate
 * @see Cont
 * @see Client
 * @see Tranzactie
 */
public class SistemBancarService {
    /**
     * Directorul unde vor fi salvate fisierele text cu extrasele de cont.
     */
    private static final String FOLDER_EXTRASE = "ExtraseCont";
    /**
     * Limita de credit standard acordată la crearea unui nou cont de credit.
     */
    private static final double LIMITA_STANDARD_CREDIT=10000;
    /**
     * Colectia de clienti incarcati in memorie, mapati dupa ID-ul de client.
     */
    private Map<String, Client> clienti;
    /**
     * Colectia de conturi incarcate in memorie, mapate dupa IBAN.
     */
    private Map<String, Cont> conturi;
    /**
     * Colectia sortata (TreeSet) a tuturor tranzactiilor efectuate.
     */
    private SortedSet<Tranzactie> istoricTranzactii;
    /**
     * Obiectul responsabil cu persistenta datelor (citirea/scrierea din fisiere).
     */
    private StocareDate managerStocare;

    /**
     * Constructorul primeste o implementare pentru interfata StocareDate.
     *
     * @param managerStocare este responsabil cu citirea/scrierea datelor, indiferent de format.
     */
    public SistemBancarService(StocareDate managerStocare) {
        this.managerStocare = managerStocare;
        this.clienti = new HashMap<>();
        this.conturi = new HashMap<>();
        this.istoricTranzactii = new TreeSet<>();
    }

    /**
     * Metoda apeleaza manegerul de stocare a datelor pentru a incarca datele din fisiere in colectiile din memorie.
     * Este apelata la pornirea aplicatiei.
     *
     * @throws ExceptieDateInvalide dacă datele din fișiere sunt corupte sau au format invalid.
     */
    public void incarcareDate() throws ExceptieDateInvalide {
        this.clienti=managerStocare.incarcaClienti();
        this.conturi=managerStocare.incarcaConturi(this.clienti);
        this.istoricTranzactii= (SortedSet<Tranzactie>) managerStocare.incarcaTranzactii();
    }
    /**
     * Metoda apeleaza manegerul de stocare a datelor pentru a salva datele din colectiile din memorie in fisiere.
     * Este apelata la inchiderea aplicatiei.
     *
     * @throws RuntimeException dacă apare o eroare I/O în timpul salvării.
     */
    public void salvareDate(){
        try {
            managerStocare.salveazaClienti(this.clienti);
            managerStocare.salveazaConturi(this.conturi);
            managerStocare.salveazaTranzactii(this.istoricTranzactii);
        } catch (Exception e) {
            throw new RuntimeException("Eroare la incarcarea datelor:"+e.getMessage());
        }
    }

    /**
     * Efectuează o depunere în contul destinație și înregistrează tranzacția.
     *
     * @param ibanDestinatie Contul în care se depun banii.
     * @param suma           Suma de depus.
     * @throws ExceptieContInexistent           dacă IBAN-ul destinație nu este găsit.
     * @throws ExceptieLimitaDepunereDepasita   dacă suma depășește limita unică de depunere.
     * @throws ExceptieOperatiuneInvalida       dacă operațiunea este invalidă.
     */
    public void efectueazaDepunere(String ibanDestinatie, double suma) throws ExceptieContInexistent, ExceptieLimitaDepunereDepasita, ExceptieOperatiuneInvalida {
        ContBancar cont= (ContBancar) conturi.get(ibanDestinatie);
        if(cont==null){
            throw new ExceptieContInexistent("Contul:"+ibanDestinatie+" nu a fost gasit!");
        }
        cont.depunere(suma);
        Tranzactie t=new Tranzactie(UUID.randomUUID().toString(),
                LocalDate.now(),
                "Depunere",
                suma,
                null,
                ibanDestinatie);
        this.istoricTranzactii.add(t);
    }

    /**
     * Efectuează o retragere din contul sursă, validând proprietatea clientului. Înregistrează tranzacția în istoric.
     *
     * @param clientAutentificat Clientul care efectuează operațiunea.
     * @param ibanSursa          Contul din care se retrag banii.
     * @param suma               Suma de retras.
     * @throws ExceptieContInexistent           dacă IBAN-ul sursă nu este găsit.
     * @throws ExceptieRetragereZilnicaDepasita dacă limita zilnică de retragere este depășită.
     * @throws ExceptieFonduriInsuficiente      dacă soldul/creditul disponibil este insuficient.
     * @throws ExceptiePermisiuneRespinsa       dacă contul sursă nu aparține clientului autentificat.
     */
    public void efectueazaRetragere(Client clientAutentificat, String ibanSursa,double suma) throws ExceptieContInexistent, ExceptieRetragereZilnicaDepasita, ExceptieFonduriInsuficiente, ExceptiePermisiuneRespinsa {
        ContBancar cont= (ContBancar) conturi.get(ibanSursa) ;
        if(cont==null){
            throw new ExceptieContInexistent("Contul:"+ibanSursa+" nu a fost gasit!");
        }
        if(!cont.getClient().getClientId().equals(clientAutentificat.getClientId())){
            throw new ExceptiePermisiuneRespinsa("Acces refuzat! Contul nu va apartine!");
        }
        cont.retragere(suma);
        Tranzactie t=new Tranzactie(UUID.randomUUID().toString(),
                LocalDate.now(),
                "Retragere",
                suma,
                ibanSursa,
                null);
        this.istoricTranzactii.add(t);
    }

    /**
     * Transferă o sumă din contul sursă în contul destinație. Validează proprietatea contului sursă și identitatea monedelor.
     * Dacă depunerea în destinație eșuează, retragerea din sursă este anulată.
     *
     * @param clientAutentificat Clientul care inițiază transferul.
     * @param ibanSursa          Contul sursă.
     * @param ibanDestinatie     Contul destinație.
     * @param suma               Suma de transferat.
     * @throws ExceptieContInexistent           dacă oricare dintre IBAN-uri nu este găsit.
     * @throws ExceptieRetragereZilnicaDepasita dacă se depășește limita la retragerea din sursă.
     * @throws ExceptieFonduriInsuficiente      dacă fondurile din sursă sunt insuficiente.
     * @throws ExceptiePermisiuneRespinsa       dacă contul sursă nu aparține clientului.
     * @throws ExceptieLimitaDepunereDepasita   dacă se depășește limita la depunerea în destinație.
     * @throws ExceptieTransferInvalid          dacă monedele conturilor nu corespund.
     * @throws ExceptieOperatiuneInvalida       dacă depunerea în destinație este invalidă.
     */
    public void efecteazaTransfer(Client clientAutentificat, String ibanSursa, String ibanDestinatie, double suma) throws ExceptieContInexistent, ExceptieRetragereZilnicaDepasita, ExceptieFonduriInsuficiente, ExceptiePermisiuneRespinsa, ExceptieLimitaDepunereDepasita, ExceptieTransferInvalid, ExceptieOperatiuneInvalida {
        ContBancar contSursa= (ContBancar) conturi.get(ibanSursa);
        ContBancar contDestinatie= (ContBancar) conturi.get(ibanDestinatie);
        if(contSursa==null){
            throw new ExceptieContInexistent("Contul:"+ibanSursa+" nu a fost gasit!");
        }
        if(contDestinatie==null){
            throw new ExceptieContInexistent("Contul:"+ibanDestinatie+" nu a fost gasit!");
        }
        if(!contSursa.getClient().getClientId().equals(clientAutentificat.getClientId())){
            throw new ExceptiePermisiuneRespinsa("Acces refuzat! Contul sursa nu va apartine!");
        }
        if (contSursa.getMoneda() != contDestinatie.getMoneda()) {
            throw new ExceptieTransferInvalid(
                    "Transferurile între monede diferite (" + contSursa.getMoneda() +
                            " -> " + contDestinatie.getMoneda() + ") nu sunt permise de bancă."
            );
        }
        contSursa.retragere(suma);
        try{
            contDestinatie.depunere(suma);
        }catch (Exception e){
            contSursa.depunere(suma);
            throw new RuntimeException("Eroare la depunere in contul destinatie! Transfer ANULAT!");
        }
        Tranzactie t=new Tranzactie(UUID.randomUUID().toString(),
                LocalDate.now(),
                "Transfer",
                suma,
                ibanSursa,
                ibanDestinatie);
        this.istoricTranzactii.add(t);
    }
    /**
     * Metodă privată utilitară pentru a valida formatul unui CNP.
     *
     * @param cnp CNP-ul de validat.
     * @return true dacă CNP-ul are 13 caractere și conține doar cifre, false altfel.
     */
    private boolean esteFormatCnpValid(String cnp) {
        if (cnp == null) {
            return false;
        }
        if (cnp.length() != 13) {
            return false;
        }
        return cnp.matches("\\d+");
    }

    /**
     * Înregistrează un client nou în sistem, validând formatul și unicitatea CNP-ului.
     *
     * @param nume    Numele de familie al clientului.
     * @param prenume Prenumele clientului.
     * @param cnp     CNP-ul trebuie să aibă 13 cifre și să fie unic.
     * @return Obiectul Client nou creat și adăugat în sistem.
     * @throws ExceptieClientExistent dacă un client cu același CNP există deja.
     * @throws ExceptieDateInvalide   dacă formatul CNP-ului este invalid.
     */
    public Client inregistrareClient(String nume, String prenume, String cnp) throws ExceptieClientExistent, ExceptieDateInvalide {
        if (!esteFormatCnpValid(cnp)) {
            throw new ExceptieDateInvalide("Format CNP invalid. CNP-ul trebuie să conțină exact 13 cifre.");
        }
        for(Client clientExistent : clienti.values()){
            if(clientExistent.getCnp().equals(cnp)){
                throw new ExceptieClientExistent("Un client cu CNP-ul "+cnp+" este deja inregistrat!");
            }
        }
        String idClient=UUID.randomUUID().toString();
        Client client = new Client(idClient,nume, prenume, cnp);
        clienti.put(idClient,client);
        System.out.println("-->Client inregistrata cu succes!");
        return  client;
    }

    /**
     * Autentifică un client existent, verificând potrivirea CNP-ului și a numelui.
     *
     * @param nume Numele de familie al clientului.
     * @param cnp  CNP-ul clientului.
     * @return Obiectul Client autentificat.
     * @throws ExceptieAutentificareEsuata dacă CNP-ul nu este găsit sau dacă numele nu corespunde CNP-ului.
     */
    public Client autentificareClient(String nume, String cnp) throws ExceptieAutentificareEsuata {
        Client clientGasit=null;
        for(Client clientExistent : clienti.values()){
            if(clientExistent.getCnp().equals(cnp)){
                clientGasit=clientExistent;
                break;
            }
        }
        if(clientGasit==null){
            throw new ExceptieAutentificareEsuata("CNP-ul NU corespunde unui cont existent!");
        }

        if(clientGasit.getNume().equalsIgnoreCase(nume)){
            return clientGasit;
        }else{
            throw new ExceptieAutentificareEsuata("Numele NU corespunde unui cont existent!");
        }
    }

    /**
     * Returnează lista tuturor conturilor deținute de un client specificat.
     *
     * @param clientId ID-ul clientului pentru care se caută conturile.
     * @return O listă (posibil goală) cu conturile deținute de client.
     * @throws ExceptieClientInexistent dacă ID-ul clientului nu este găsit în sistem.
     */
    public List<Cont> getConturiByClientId(String clientId) throws ExceptieClientInexistent {
        if(!clienti.containsKey(clientId)){
            throw new ExceptieClientInexistent("Clientul cu ID-ul cautat nu exista!");
        }
        List<Cont> conturiGasite=new ArrayList<>();
        for(Cont cont:conturi.values()){
            if(cont.getClient().getClientId().equals(clientId)){
                conturiGasite.add(cont);
            }
        }
        return conturiGasite;
    }
    /**
     * Creează un cont nou (DEBIT sau CREDIT) pentru un client. Generează un IBAN unic și îl înregistrează în sistem.
     *
     * @param client Deținătorul contului.
     * @param tip    Tipul contului.
     * @param moneda Moneda contului.
     */
    public void creazaContNou(Client client, TipCont tip, TipMoneda moneda){
        String ibanNou= genereazaIbanUnic();
        Cont contNou;
        if(tip==TipCont.DEBIT){
            contNou=new ContDebitor(ibanNou,client,moneda);
        }else {
            contNou=new ContCreditor(ibanNou,client,moneda,LIMITA_STANDARD_CREDIT);
        }
        conturi.put(ibanNou,contNou);
        System.out.println("Cont "+tip+" creat cu succes!");
    }
    /**
     * Metodă privată utilitară pentru a genera un IBAN unic.
     * Combină un prefix, un număr secvențial și o valoare aleatorie și verifică unicitatea
     * în harta de conturi existentă.
     *
     * @return Un String reprezentând noul IBAN unic.
     */
    private String genereazaIbanUnic() {
        String iban;
        do{
            long numeric=System.currentTimeMillis()%1000+(long)(Math.random()*100000);
            iban="RO"+String.format("%02d",(conturi.size()+1))+"BANK"+String.format("%02d",numeric);
        }while (conturi.containsKey(iban));
        return iban;
    }

    /**
     * Metoda va genera o matrice de statistici pentru clientul autentificat.
     *
     * @param clientAutentificat clientul pentru care se genereaza raportul.
     * @param ibanSelectat       IBAN-ul contului analizat.
     * @return o matrice double[4][2] unde:
     * [0][0]= suma totala depuneri, [0][1]=numar depuneri
     * [1][0]= suma totala retrageri, [1][1]=numar retrageri
     * [2][0]= suma totala transferuri trimise, [2][1]=numar transferuri trimise
     * [3][0]= suma totala transferuri primite, [3][1]=numar transferuri primite
     * @throws ExceptieContInexistent     dacă IBAN-ul nu este găsit.
     * @throws ExceptiePermisiuneRespinsa dacă contul nu aparține clientului autentificat.
     */
    public double[][] genereazaStatisticiContClient(Client clientAutentificat, String ibanSelectat) throws ExceptieContInexistent, ExceptiePermisiuneRespinsa {
        Cont cont=conturi.get(ibanSelectat);
        if(cont==null){
            throw new ExceptieContInexistent("Contul "+ibanSelectat+" nu exista!");
        }
        if(!cont.getClient().getClientId().equals(clientAutentificat.getClientId())){
            throw  new ExceptiePermisiuneRespinsa("Acces refuzat! Contul "+ ibanSelectat+ "nu va apartine!");
        }

        double[][] statisticiContClient=new double[4][2];

        for(Tranzactie t: istoricTranzactii){
            String ibanSursa=t.getIbanSursa();
            String ibanDestinatie=t.getIbanDestinatie();
            if(t.getTipTransfer().equals("Depunere")&& ibanSelectat.contains(ibanDestinatie)){
                statisticiContClient[0][0]+=t.getSuma();
                statisticiContClient[0][1]++;
            } else if(t.getTipTransfer().equals("Retragere")&& ibanSelectat.contains(ibanSursa)){
                statisticiContClient[1][0]+=t.getSuma();
                statisticiContClient[1][1]++;
            } else if(t.getTipTransfer().equals("Transfer")){
                if(ibanSelectat.equals(ibanSursa)){
                    statisticiContClient[2][0]+=t.getSuma();
                    statisticiContClient[2][1]++;
                } else if(ibanSelectat.equals(ibanDestinatie)){
                    statisticiContClient[3][0]+=t.getSuma();
                    statisticiContClient[3][1]++;
                }
            }
        }
        return statisticiContClient;
    }

    /**
     * Metoda care se ocupa cu generarea unui extras de cont.
     *
     * @param clientAutentificat Clientul care face cererea.
     * @param ibanSelectat       IBAN-ul contului țintă.
     * @param luniInUrma         Numărul de luni în urmă pentru care se generează extrasul.
     * @return Calea completă a fișierului text generat.
     * @throws ExceptieContInexistent     Dacă IBAN-ul nu e găsit.
     * @throws ExceptiePermisiuneRespinsa Dacă contul nu aparține clientului.
     * @throws IOException                Dacă apare o eroare la crearea/scrierea fișierului.
     */
    public String genereazaExtrasDeCont(Client clientAutentificat, String ibanSelectat, int luniInUrma)
            throws ExceptieContInexistent, ExceptiePermisiuneRespinsa, IOException {

        Cont cont = conturi.get(ibanSelectat);
        if (cont == null) {
            throw new ExceptieContInexistent("Contul " + ibanSelectat + " nu exista.");
        }
        if (!cont.getClient().getClientId().equals(clientAutentificat.getClientId())) {
            throw new ExceptiePermisiuneRespinsa("Acces refuzat. Contul " + ibanSelectat + " nu va apartine.");
        }

        File director = new File(FOLDER_EXTRASE);
        if (!director.exists()) {
            director.mkdir();
        }

        String numeFisier = "Extras_" + ibanSelectat + "_" + LocalDate.now() + ".txt";
        String caleCompleta = FOLDER_EXTRASE + File.separator + numeFisier;
        LocalDate dataInceput = null;
        String perioadaRaport;
        if (luniInUrma > 0) {
            dataInceput = LocalDate.now().minusMonths(luniInUrma);
            perioadaRaport = "Ultimele " + luniInUrma + " luni";
        } else {
            perioadaRaport = "Istoric complet";
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(caleCompleta))) {
            writer.println("--- EXTRAS DE CONT ---");
            writer.println("Client: " + clientAutentificat.getNume() + " " + clientAutentificat.getPrenume());
            writer.println("Cont IBAN: " + ibanSelectat);
            writer.println("Moneda: " + cont.getMoneda());
            writer.println("Data generării: " + LocalDate.now());
            writer.println("Perioada raport: " + perioadaRaport);
            writer.println("----------------------------------------------------------------------");
            writer.printf("%-12s | %-15s | %-15s | %-30s\n", "Data", "Tip", "Suma (" + cont.getMoneda() + ")", "Detalii");
            writer.println("----------------------------------------------------------------------");

            for (Tranzactie t : istoricTranzactii) {
                if (dataInceput != null && t.getData().isBefore(dataInceput)) {
                    continue;
                }
                String detalii = "";
                String sumaFormatata = "";

                if (ibanSelectat.equals(t.getIbanSursa())) {
                    sumaFormatata = String.format("-%,.3f", t.getSuma());
                    if (t.getTipTransfer().equals("Retragere")) {
                        detalii = "Retragere numerar";
                    } else {
                        detalii = "Transfer către: " + t.getIbanDestinatie();
                    }
                } else if (ibanSelectat.equals(t.getIbanDestinatie())) {
                    sumaFormatata = String.format("+%,.3f", t.getSuma());
                    if (t.getTipTransfer().equals("Depunere")) {
                        detalii = "Depunere numerar";
                    } else {
                        detalii = "Transfer de la: " + t.getIbanSursa();
                    }
                } else {
                    continue;
                }
                writer.printf("%-12s | %-15s | %15s | %-30s\n",
                        t.getData().toString(),
                        t.getTipTransfer(),
                        sumaFormatata,
                        detalii
                );
            }
            writer.println("----------------------------------------------------------------------");
        }
        return caleCompleta;
    }

    /**
     * Metoda generează statisticile zilnice (intrări și ieșiri) pentru ultimele N zile, pentru un cont specific.
     * Indexul 0 al array-urilor corespunde zilei curente.
     *
     * @param ibanContSelectat IBAN-ul contului pentru care se generează statisticile.
     * @param zileInUrma       Numărul de zile pentru istoric.
     * @return O Map&lt;String, double[]&gt; unde cheia "intrari" are un vector al încasărilor zilnice,
     * iar cheia "iesiri" are un vector al plăților zilnice.
     * @throws ExceptieContInexistent Dacă IBAN-ul nu există.
     */
    public Map<String, double[]> genereazaStatisticiZilnice(String ibanContSelectat, int zileInUrma)
            throws ExceptieContInexistent {

        if (!conturi.containsKey(ibanContSelectat)) {
            throw new ExceptieContInexistent("Contul cu IBAN-ul " + ibanContSelectat + " nu există.");
        }

        double[] intrariZilnice = new double[zileInUrma];
        double[] iesiriZilnice = new double[zileInUrma];
        LocalDate azi = LocalDate.now();

        for (Tranzactie t : istoricTranzactii) {
            LocalDate dataTranzactie = t.getData();

            if (dataTranzactie.isBefore(azi.minusDays(zileInUrma - 1))) {
                continue;
            }
            if (dataTranzactie.isAfter(azi)) {
                continue;
            }
            long zileDiferenta = dataTranzactie.until(azi).getDays();
            if (zileDiferenta >= 0 && zileDiferenta < zileInUrma) {

                if (ibanContSelectat.equals(t.getIbanDestinatie())) {
                    intrariZilnice[(int) zileDiferenta] += t.getSuma();
                }

                else if (ibanContSelectat.equals(t.getIbanSursa())) {
                    iesiriZilnice[(int) zileDiferenta] += t.getSuma();
                }

            }
        }
        Map<String, double[]> rezultate = new HashMap<>();
        rezultate.put("intrari", intrariZilnice);
        rezultate.put("iesiri", iesiriZilnice);
        return rezultate;
    }
}
