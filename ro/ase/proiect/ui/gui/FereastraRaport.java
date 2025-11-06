package ro.ase.proiect.ui.gui;

import ro.ase.proiect.model.cont.Cont;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Fereastra grafica (JFrame) pentru afisarea raportului de activitate a clientului sub forma de tabel.
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 4.11.2025
 */
public class FereastraRaport extends JFrame {
    /**
     * Tabelul Swing care afișează datele statistice.
     */
    private JTable tabelStatistici;

    /**
     * Containerul cu bare de derulare pentru tabel, în cazul în care conținutul este prea mare.
     */
    private JScrollPane scrollPane;

    /**
     * Constructor pentru fereastra de raport.
     * Inițializează și configurează fereastra, creează modelul de tabel
     * pe baza statisticilor primite și afișează fereastra.
     *
     * @param statistici     Matricea double[4][2] care conține datele (Suma, Număr)
     *                          pentru (Depuneri, Retrageri, Trimise, Primite).
     * @param numeClient     Numele clientului (pentru titlu).
     * @param prenumeClient  Prenumele clientului (pentru titlu).
     * @param contSelectat   Obiectul Cont pentru care se generează raportul (pentru IBAN și monedă).
     */
    public FereastraRaport(double[][] statistici, String numeClient, String prenumeClient, Cont contSelectat) {
        setTitle("Raport activitate pentru:"+ numeClient+" "+prenumeClient+" -> Cont selectat:"+contSelectat.getIban());
        setSize(600,200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);//necesar pentru a nu inchide toata aplicatia
        setLocationRelativeTo(null);//centrarea ferestrei

        String[] numeColoane={"Operatiuni","Total suma("+contSelectat.getMoneda() +")"," Numar operatiuni"};
        Object[][] dateTabel = {
                {"Depuneri",
                        String.format("%,.3f", statistici[0][0]),
                        (int) statistici[0][1]},
                {"Retrageri",
                        String.format("%,.3f", statistici[1][0]),
                        (int) statistici[1][1]},
                {"Transferuri Trimise",
                        String.format("%,.3f", statistici[2][0]),
                        (int) statistici[2][1]},
                {"Transferuri Primite",
                        String.format("%,.3f", statistici[3][0]),
                        (int) statistici[3][1]}
        };

        DefaultTableModel model = new DefaultTableModel(dateTabel,numeColoane);
        tabelStatistici = new JTable(model);
        tabelStatistici.setEnabled(false);

        scrollPane=new JScrollPane(tabelStatistici);
        add(scrollPane, BorderLayout.CENTER);
        setAlwaysOnTop(true);
        setVisible(true);
    }

}
