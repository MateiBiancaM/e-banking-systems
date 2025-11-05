package ro.ase.proiect.ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Fereastră GUI (JDialog Modal) pentru meniul principal.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 4.11.2025
 */
public class FereastraMeniuPrincipal extends JDialog implements ActionListener {
    private int optiuneAleasa = -1;

    public FereastraMeniuPrincipal(JFrame owner, String numeClient) {
        super(owner, "Meniu Principal: " + numeClient, true); // MODAL

        setLayout(new GridLayout(0, 1, 10, 10)); // O coloană
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        add(new JLabel("Alegeți operațiunea:", SwingConstants.CENTER));
        add(creeazaButonMeniu("1. Efectuează o depunere", "1"));
        add(creeazaButonMeniu("2. Efectuează o retragere", "2"));
        add(creeazaButonMeniu("3. Efectuează un transfer", "3"));
        add(creeazaButonMeniu("4. Vizualizează conturile mele", "4"));
        add(creeazaButonMeniu("5. Deschide un cont nou", "5"));
        add(creeazaButonMeniu("6. Vezi raportul de activitate", "6"));
        add(creeazaButonMeniu("7. Generează extras de cont", "7"));
        add(creeazaButonMeniu("8. Vezi grafic flux financiar (30 zile)", "8"));
        add(creeazaButonMeniu("0. Deconectare", "0"));
    }

    private JButton creeazaButonMeniu(String text, String actionCommand) {
        JButton buton = new JButton(text);
        buton.setActionCommand(actionCommand);
        buton.addActionListener(this);
        return buton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.optiuneAleasa = Integer.parseInt(e.getActionCommand());
        this.dispose();
    }

    public int afiseazaSiAsteaptaAlegere() {
        setVisible(true);
        return this.optiuneAleasa;
    }
}