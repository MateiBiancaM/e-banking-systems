package ro.ase.proiect.model.cont;

import ro.ase.proiect.exceptii.ExceptieFonduriInsuficiente;
import ro.ase.proiect.exceptii.ExceptieLimitaDepunereDepasita;
import ro.ase.proiect.exceptii.ExceptieOperatiuneInvalida;
import ro.ase.proiect.exceptii.ExceptieRetragereZilnicaDepasita;

/**
 *Interfata are scopul de a defini principalele operatiuni financiare.
 *
 * @author Matei Maria-Bianca
 * @version 1.2
 * @since 4.11.2025
 * @see ContCreditor
 * @see ContDebitor
 */
public interface OperatiuniBancare {
    /**
     * Sunt adaugate fonduri in contul de debit sau sunt reduse datoriile corespunzatoare contului de credit.
     * @param suma Reprezinta suma ce trebuie depusa/platita.
     * @throws ExceptieLimitaDepunereDepasita dacă suma depășește limita unică de tranzacție.
     * @throws ExceptieOperatiuneInvalida     dacă operațiunea este invalidă
     */
   void depunere(double suma) throws ExceptieLimitaDepunereDepasita, ExceptieOperatiuneInvalida;

    /**
     * Sunt retrasi banii corespunzatori contului de debit sau sunt utilizate fonduri corespunzatoare contului de credit.
     * @param suma Reprezinta suma ce trebuie retrasa.
     * @throws ExceptieFonduriInsuficiente      daca suma depaseste soldul/creditul disponibil.
     * @throws ExceptieRetragereZilnicaDepasita dacă suma depășește limita zilnică de retragere.
     */
   void retragere(double suma) throws ExceptieFonduriInsuficiente, ExceptieRetragereZilnicaDepasita;

    /**
     * Returneaza suma ce sta la dispozitia utilizatorului (soldul pentru ContDebitor, creditul rămas pentru ContCreditor).
     *
     * @return soldul/creditul disponibil
     */
   double getSoldDisponibil();
}
