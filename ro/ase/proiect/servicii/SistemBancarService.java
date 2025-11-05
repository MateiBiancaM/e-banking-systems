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
 *Clasa care gestioneaza operatiuniile si starea aplicatiei in memorie
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
    private static final String FOLDER_EXTRASE = "ExtraseCont";
    private static final double LIMITA_STANDARD_CREDIT=10000;
    private Map<String, Client> clienti;
    private Map<String, Cont> conturi;
    private SortedSet<Tranzactie> istoricTranzactii;
    private StocareDate managerStocare;

    /**
     * Constructorul primeste o implementare pentru interfata StocareDate.
     * @param managerStocare este responsabil cu citirea/scrierea datelor, indiferent de format.
     */
    public SistemBancarService(StocareDate managerStocare) {
        this.managerStocare = managerStocare;
        this.clienti = new HashMap<>();
        this.conturi = new HashMap<>();
        this.istoricTranzactii = new TreeSet<>();
    }

    /**
     * Metoda apeleaza menegerul de stocare a datelor pentru a incarca datele din fisiere in colectiile din memorie
     * Este apelata la pornirea aplicatiei
     */
    public void incarcareDate() throws ExceptieDateInvalide {
        this.clienti=managerStocare.incarcaClienti();
        this.conturi=managerStocare.incarcaConturi(this.clienti);
        this.istoricTranzactii= (SortedSet<Tranzactie>) managerStocare.incarcaTranzactii();
    }
    /**
     * Metoda apeleaza menegerul de stocare a datelor pentru a salva datele din colectiile din memorie in fisiere
     * Este apelata la pornirea aplicatiei
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
    private boolean esteFormatCnpValid(String cnp) {
        if (cnp == null) {
            return false;
        }
        if (cnp.length() != 13) {
            return false;
        }
        return cnp.matches("\\d+");
    }
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

    public Cont creazaContNou(Client client, TipCont tip, TipMoneda moneda){
        String ibanNou= genereazaIbanUnic();
        Cont contNou;
        if(tip==TipCont.DEBIT){
            contNou=new ContDebitor(ibanNou,client,moneda);
        }else {
            contNou=new ContCreditor(ibanNou,client,moneda,LIMITA_STANDARD_CREDIT);
        }
        conturi.put(ibanNou,contNou);
        System.out.println("Cont "+tip+" creat cu succes!");
        return contNou;
    }

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
     * @param clientAutentificat clientul pemtru care se genereaza raportul
     * @return o matrice double[4][2] unde:
     * [0][0]= suma totala depuneri, [0][1]=numar depuneri
     * [1][0]= suma totala retrageri, [1][1]=numar retrageri
     * [2][0]= suma totala transferuri trimise, [2][1]=numar transferuri trimise
     * [3][0]= suma totala transferuri primite, [3][1]=numar transferuri primite
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
     * Matoda care se ocupa cu generarea unui extras de cont.
     *
     * @param clientAutentificat Clientul care face cererea.
     * @param ibanSelectat IBAN-ul contului țintă.
     * @return Calea completă a fișierului generat (ex: "ExtraseCont\Extras_RO01...txt").
     * @throws ExceptieContInexistent Dacă IBAN-ul nu e găsit.
     * @throws ExceptiePermisiuneRespinsa Dacă contul nu aparține clientului.
     * @throws IOException Dacă apare o eroare la scrierea fișierului.
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

            // 6. Iterăm prin istoricul DEJA SORTAT (TreeSet)
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
     *
     * @param ibanContSelectat IBAN-ul contului pentru care se generează statisticile.
     * @param zileInUrma Numărul de zile pentru istoric î.
     * @return O Map<String, double[]> unde cheile sunt "intrari" și "iesiri".
     * @throws ExceptieContInexistent Dacă IBAN-ul nu există
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
