package ro.ase.proiect.main;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;
import ro.ase.proiect.exceptii.ExceptieRetragereZilnicaDepasita;
import ro.ase.proiect.model.cont.ContCreditor;
import ro.ase.proiect.model.cont.ContDebitor;
import ro.ase.proiect.model.cont.TipMoneda;
import ro.ase.proiect.model.utilizator.Client;

/**
 * Punctul de intrare al aplicatei. Clasa este responsabila cu gestionarea fluxului principal al aplicatiei.
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 27.10.2025
 */
public class Main {
    static void main(String[] args) {
        Client client=new Client("C1","Matei","Maria-Bianca","1234567891");
        ContDebitor cd=new ContDebitor("RO012345DEBIT",client, TipMoneda.RON);
        ContCreditor cc=new ContCreditor("RO012345CREDIT",client, TipMoneda.RON);

        try{
            System.out.println("\t DEBIT");
            System.out.println("Sold debit:"+cd.getSold());
            cd.depunere(5000);
            cd.depunere(500);
            System.out.println("Sold debit:"+cd.getSold());
            cd.retragere(3000);
            //cd.retragere(1000);
            System.out.println("Sold debit:"+cd.getSold());
            //cd.retragere(2000);
        } catch (ExceptieRetragereZilnicaDepasita e) {
            throw new RuntimeException(e);
        } catch (ExceptieFonduriInsuficiente e) {
            throw new RuntimeException(e);
        }

        try {
            System.out.println("\t CREDIT");
            System.out.println("Credit Disponibil: " + cc.getSoldDisponibil());
            System.out.println("Datorie Curenta: " + cc.getDatorieCurenta());
            cc.retragere(200);
            cc.retragere(300);
            System.out.println("Credit Disponibil: " + cc.getSoldDisponibil());
            System.out.println("Datorie Curenta: " + cc.getDatorieCurenta());
            cc.depunere(500);
            System.out.println("Credit Disponibil: " + cc.getSoldDisponibil());
            System.out.println("Datorie Curenta: " + cc.getDatorieCurenta());
            cc.retragere(2000);
            cc.retragere(1500);
            System.out.println("Credit Disponibil: " + cc.getSoldDisponibil());
            System.out.println("Datorie Curenta: " + cc.getDatorieCurenta());
            //cc.retragere(40000);
        } catch (ExceptieFonduriInsuficiente e) {
            throw new RuntimeException(e);
        }

    }
}
