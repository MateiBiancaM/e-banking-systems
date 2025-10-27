package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;
import ro.ase.proiect.exceptii.ExceptieRetragereZilnicaDepasita;

/**
 *Interfata are scopul de a defini principalele operatiuni financiare.
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 27.10.2025
 * @see ContCreditor
 * @see ContDebitor
 */
public interface OperatiuniBancare {
    /**
     * Sunt adaugate fonduri in contul de debit sau sunt reduse datoriile corespunzatoare contului de credit.
     * @param suma Reprezinta suma ce trebuie depusa/platita.
     */
   void depunere(double suma);

    /**
     * Sunt retrasi banii corespunzatori contului de debit sau sunt utilizate fonduri corespunzatoare contului de credit.
     * @param suma Reprezinta suma ce trebuie retrasa.
     * @throws ExceptieFonduriInsuficiente daca suma depaseste soldul.
     */
   void retragere(double suma) throws ExceptieFonduriInsuficiente, ExceptieRetragereZilnicaDepasita;

    /**
     * Returneaza suma ce sta la dispozitia utilizatorului
     * @return soldul/creditul disponibil
     */
   double getSoldDisponibil();
}
