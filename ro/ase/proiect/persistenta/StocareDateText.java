package ro.ase.proiect.persistenta;

import ro.ase.proiect.exceptii.ExceptieDateInvalide;
import ro.ase.proiect.model.cont.*;
import ro.ase.proiect.model.tranzactii.Tranzactie;
import ro.ase.proiect.model.utilizator.Client;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
/**
 *Implementarea concreta a interfetei {@link StocareDate} ce se ocupa de gestionarea datelor in fisiere text.
 *
 * @author Matei Maria-Bianca
 * @version 1.3
 * @since 4.11.2025
 */
public class StocareDateText implements StocareDate{
    private String caleFisierClienti;
    private String caleFisierConturi;
    private String caleFisierTranzactii;

    private static final String DELIMITATOR=";";
    private static final String VALOARE_NULL="null";

    public StocareDateText(String caleFisierClienti, String caleFisierConturi, String caleFisierTranzactii) {
        this.caleFisierClienti = caleFisierClienti;
        this.caleFisierConturi = caleFisierConturi;
        this.caleFisierTranzactii = caleFisierTranzactii;
    }

    @Override
    public Map<String, Client> incarcaClienti() throws ExceptieDateInvalide {
        Map<String, Client> clientiIncarcati = new HashMap<>();
        File fisier=new File(this.caleFisierClienti);
        if(!fisier.exists()){
            System.out.println("Fisierul:"+caleFisierClienti+", nu a fost gasit! Numar clienti:0!");
            return clientiIncarcati;
        }
        try(BufferedReader br=new BufferedReader(new FileReader(fisier))){
            String linie;
            while((linie=br.readLine())!=null){
                String[] date=linie.split(DELIMITATOR);
                //clientId, nume, prenume, cnp
                if(date.length!=4){
                    throw new ExceptieDateInvalide("Format invalid pentru client:"+linie);
                }
                Client client=new Client(date[0],date[1],date[2],date[3]);
                clientiIncarcati.put(client.getClientId(),client);
            }
        } catch (IOException e) {
            throw new ExceptieDateInvalide("Eroare citire fisier:"+caleFisierClienti+" -->"+e.getMessage());
        }
        return clientiIncarcati;
    }

    @Override
    public Map<String, Cont> incarcaConturi(Map<String, Client> clientiExistenti) throws ExceptieDateInvalide {
        Map<String, Cont> conturiIncarcate = new HashMap<>();
        File fisier = new File(caleFisierConturi);
        if(!fisier.exists()){
            System.out.println("Fisierul:"+caleFisierConturi+", nu a fost gasit! Numar conturi:0!");
            return conturiIncarcate;
        }
        LocalDate dataCurenta=LocalDate.now();
        try (BufferedReader br = new BufferedReader(new FileReader(fisier))) {
            String linie;
            while((linie=br.readLine())!=null){
                String[] date = linie.split(DELIMITATOR);
                if (date.length < 6) {
                    throw new ExceptieDateInvalide("Format invalid pentru cont:"+linie);
                }
                String tipCont = date[0];
                ContBancar cont;
                switch (tipCont) {
                    case "CREDIT":
                        // credit,iban,clientId,moneda,sold,limitaCredit
                        if (date.length != 6) {
                            throw new ExceptieDateInvalide("Format ContCreditor invalid (se asteptau 6 coloane)");
                        }
                        String ibanCredit = date[1];
                        Client proprietarCredit = clientiExistenti.get(date[2]);
                        TipMoneda monedaCredit = TipMoneda.valueOf(date[3].toUpperCase());
                        double soldCredit = Double.parseDouble(date[4]);
                        double limitaCredit = Double.parseDouble(date[5]);

                        if (proprietarCredit == null) {
                            throw new ExceptieDateInvalide("Client negăsit pentru ID: " + date[2]);
                        }
                        cont = new ContCreditor(ibanCredit, proprietarCredit, monedaCredit, limitaCredit);
                        cont.setSold(soldCredit);
                        break;
                    case "DEBIT":
                        // debit,iban,clientId,moneda,sold,totalRetrasAstazi,dataUltimeiRetrageri
                        if (date.length != 7) {
                            throw new ExceptieDateInvalide("Format ContDebitor invalid (se asteptau 7 coloane)");
                        }
                        String ibanDebit = date[1];
                        Client proprietarDebit = clientiExistenti.get(date[2]);
                        TipMoneda monedaDebit = TipMoneda.valueOf(date[3].toUpperCase());
                        double soldDebit = Double.parseDouble(date[4]);
                        double totalRetras = Double.parseDouble(date[5]);
                        LocalDate dataRetragere;
                        if (VALOARE_NULL.equals(date[6])) {
                            dataRetragere = null;
                        } else {
                            dataRetragere = LocalDate.parse(date[6]);
                        }
                        if (proprietarDebit == null) {
                            throw new ExceptieDateInvalide("Client negăsit pentru ID: " + date[2]);
                        }
                        cont = new ContDebitor(ibanDebit, proprietarDebit, monedaDebit);
                        cont.setSold(soldDebit);
                        if(dataCurenta.equals(dataRetragere)){
                            ((ContDebitor) cont).setStareZilnica(totalRetras, dataRetragere);
                        }else{
                            ((ContDebitor) cont).setStareZilnica(0.0, dataCurenta);
                        }

                        break;
                    default:
                        throw new ExceptieDateInvalide("Tip cont necunoscut:" + tipCont);
                }
                conturiIncarcate.put(cont.getIban(), cont);
            }
        } catch (IOException | DateTimeParseException | IllegalArgumentException e) {
            throw new ExceptieDateInvalide("Eroare citire fisier:"+caleFisierConturi+" -->"+e.getMessage());
        }
        return conturiIncarcate;
    }

    @Override
    public Set<Tranzactie> incarcaTranzactii() throws ExceptieDateInvalide {
        Set<Tranzactie> tranzactiiIncarcate = new TreeSet<>();
        File fisier = new File(caleFisierTranzactii);
        if(!fisier.exists()){
            System.out.println("Fisierul:"+caleFisierTranzactii+", nu a fost gasit! Numar tranzactii:0!");
            return tranzactiiIncarcate;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(fisier))) {
            String linie;
            while ((linie=br.readLine())!=null) {
                //idTranzactie,data,tip,suma,ibanSursa,ibanDestinatie
                String[] date = linie.split(DELIMITATOR);
                if (date.length != 6) {
                    throw new ExceptieDateInvalide("Format invalid pentru tranzactie:"+linie);
                }
                String id = date[0];
                LocalDate data = LocalDate.parse(date[1]);
                String tip = date[2];
                double suma = Double.parseDouble(date[3]);
                String ibanSursa = VALOARE_NULL.equals(date[4]) ? null : date[4];
                String ibanDestinatie = VALOARE_NULL.equals(date[5]) ? null : date[5];
                Tranzactie t = new Tranzactie(id, data, tip, suma, ibanSursa, ibanDestinatie);
                tranzactiiIncarcate.add(t);
            }
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            throw new ExceptieDateInvalide("Eroare citire fisier:"+caleFisierTranzactii+" -->"+e.getMessage());
        }
        return tranzactiiIncarcate;
    }

    @Override
    public void salveazaClienti(Map<String, Client> clienti) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(caleFisierClienti))) {
            for (Client client : clienti.values()) {
                String linie = String.join(DELIMITATOR,
                        client.getClientId(),
                        client.getNume(),
                        client.getPrenume(),
                        client.getCnp()
                );
                writer.println(linie);
            }
        } catch (IOException e) {
            System.err.println("Eroare scriere in " + caleFisierClienti + ": " + e.getMessage());
        }
    }

    @Override
    public void salveazaConturi(Map<String, Cont> conturi) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(caleFisierConturi))) {
            for (Cont cont : conturi.values()) {
                String linieFinala = "";
                if (cont instanceof ContCreditor) {
                    ContCreditor cc = (ContCreditor) cont;
                    //credit,iban,clientId,moneda,sold,limitaCredit
                    linieFinala = String.join(DELIMITATOR,
                            "CREDIT",
                            cc.getIban(),
                            cc.getClient().getClientId(),
                            cc.getMoneda().name(),
                            String.valueOf(cc.getSold()),
                            String.valueOf(cc.getLimitaCredit())
                    );
                } else if (cont instanceof ContDebitor) {
                    ContDebitor cd = (ContDebitor) cont;
                    String dataSalvare = (cd.getDataUltimeiRetrageri() != null)
                            ? cd.getDataUltimeiRetrageri().toString()
                            : VALOARE_NULL;
                    //debit,iban,clientId,moneda,sold_intern,totalRetrasAstazi,dataUltimeiRetrageri
                    linieFinala = String.join(DELIMITATOR,
                            "DEBIT",
                            cd.getIban(),
                            cd.getClient().getClientId(),
                            cd.getMoneda().name(),
                            String.valueOf(cd.getSold()),
                            String.valueOf(cd.getTotalRetrasAstazi()),
                            dataSalvare
                    );
                } else {
                    continue;
                }
                writer.println(linieFinala);
            }
        } catch (IOException e) {
            System.err.println("Eroare scriere in: " + caleFisierConturi + ": " + e.getMessage());
        }
    }

    @Override
    public void salveazaTranzactii(Set<Tranzactie> tranzactii) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(caleFisierTranzactii))) {
            for (Tranzactie t : tranzactii) {
                String ibanSursa = (t.getIbanSursa() != null) ? t.getIbanSursa() : VALOARE_NULL;
                String ibanDestinatie = (t.getIbanDestinatie() != null) ? t.getIbanDestinatie() : VALOARE_NULL;

                String linie = String.join(DELIMITATOR,
                        t.getIdTranzactie(),
                        t.getData().toString(),
                        t.getTipTransfer(),
                        String.valueOf(t.getSuma()),
                        ibanSursa,
                        ibanDestinatie
                );
                writer.println(linie);
            }
        } catch (IOException e) {
            System.err.println("Eroare scriere in: " + caleFisierTranzactii + ": " + e.getMessage());
        }
    }
}
