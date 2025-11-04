package ro.ase.proiect.ui.gui;

import ro.ase.proiect.model.cont.Cont;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Fereastra grafica pentru afisarea raportului de activitate a clientului sub forma de tabel.
 *
 * @author Matei Maria-Bianca
 * @version 1.1
 * @since 4.11.2025
 */
public class FereastraRaport extends JFrame {
    private JTable tabelStatistici;
    private JScrollPane scrollPane;

    public FereastraRaport(double[][] statistici, String numeClient, String prenumeClient, Cont contSelectat) {
        setTitle("Raport activitate pentru:"+ numeClient+" "+prenumeClient+" -> Cont selectat:"+contSelectat.getIban());
        setSize(600,200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);//necesar pentru a nu inchide toata aplicatia
        setLocationRelativeTo(null);//centrarea ferestrei

        String[] numeColoane={"Operatiuni","Total suma("+contSelectat.getMoneda() +")"," Numar operatiuni"};
        Object[][] data= {
                {"Depuneri", statistici[0][0], (int)statistici[0][1]},
                {"Retrageri", statistici[1][0], (int)statistici[1][1]},
                {"Transferuri Trimise", statistici[2][0], (int)statistici[2][1]},
                {"Transferuri Primite", statistici[3][0], (int)statistici[3][1]}
        };

        DefaultTableModel model = new DefaultTableModel(data,numeColoane);
        tabelStatistici = new JTable(model);
        tabelStatistici.setEnabled(false);

        scrollPane=new JScrollPane(tabelStatistici);
        add(scrollPane, BorderLayout.CENTER);
        setAlwaysOnTop(true);
        setVisible(true);
    }

}
