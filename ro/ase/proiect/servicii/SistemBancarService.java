package ro.ase.proiect.servicii;

import ro.ase.proiect.exceptii.*;
import ro.ase.proiect.model.cont.*;
import ro.ase.proiect.model.tranzactii.Tranzactie;
import ro.ase.proiect.model.utilizator.Client;
import ro.ase.proiect.persistenta.StocareDate;

import java.time.LocalDate;
import java.util.*;

/**
 *Clasa care gestioneaza operatiuniile si starea aplicatiei in memorie
 *
 * @author Matei Maria-Bianca
 * @version 1.4
 * @since 03.11.2025
 * @see StocareDate
 * @see Cont
 * @see Client
 * @see Tranzactie
 */
public class SistemBancarService {
    private static final double LIMITA_STANDARD_CREDIT=10000;
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
    /**
     * Metoda apeleaza menegerul de stocare a datelor pentru a salva datele din colectiile din memorie in fisiere
     * Este apelata la pornirea aplicatiei
     */
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

    public void efectueazaDepunere(String ibanDestinatie, double suma) throws ExceptieContInexistent {
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

    public void efecteazaTransfer(Client clientAutentificat, String ibanSursa, String ibanDestinatie, double suma) throws ExceptieContInexistent, ExceptieRetragereZilnicaDepasita, ExceptieFonduriInsuficiente, ExceptiePermisiuneRespinsa {
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

    public Client inregistrareClient(String nume, String prenume, String cnp) throws ExceptieClientExistent {
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
    public double[][] genereazaStatisticiClient(Client clientAutentificat){
        double[][] statisticiClient=new double[4][2];
        List<Cont> conturileMele;
        try{
            conturileMele=this.getConturiByClientId(clientAutentificat.getClientId());
        } catch (ExceptieClientInexistent e) {
            return statisticiClient;
        }
        Set<String> ibanurileMele=new HashSet<>();
        for(Cont cont:conturileMele){
            ibanurileMele.add(cont.getIban());
        }
        for(Tranzactie t: istoricTranzactii){
            String ibanSursa=t.getIbanSursa();
            String ibanDestinatie=t.getIbanDestinatie();
            if(t.getTipTransfer().equals("Depunere")&& ibanurileMele.contains(ibanDestinatie)){
                statisticiClient[0][0]+=t.getSuma();
                statisticiClient[0][1]++;
            } else if(t.getTipTransfer().equals("Retragere")&& ibanurileMele.contains(ibanSursa)){
                statisticiClient[1][0]+=t.getSuma();
                statisticiClient[1][1]++;
            } else if(t.getTipTransfer().equals("Transfer")){
                boolean sursaMea=ibanurileMele.contains(ibanSursa);
                boolean destinatiaMea=ibanurileMele.contains(ibanDestinatie);
                if(sursaMea&&!destinatiaMea){
                    statisticiClient[2][0]+=t.getSuma();
                    statisticiClient[2][1]++;
                } else if(!sursaMea&&destinatiaMea){
                    statisticiClient[3][0]+=t.getSuma();
                    statisticiClient[3][1]++;
                }
            }
        }
        return statisticiClient;
    }
}
