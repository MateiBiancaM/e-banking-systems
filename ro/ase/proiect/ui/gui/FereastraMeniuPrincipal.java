package ro.ase.proiect.ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Fereastră GUI (JDialog Modal) pentru meniul principal.
 * Blochează consola până când utilizatorul alege o opțiune.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 4.11.2025
 */
public class FereastraMeniuPrincipal extends JDialog implements ActionListener {
    /**
     * Stochează opțiunea numerică aleasă de utilizator.
     * Valoarea implicită este -1, returnată dacă fereastra este închisă folosind butonul 'X'.
     */
    private int optiuneAleasa = -1;

    /**
     * Constructor pentru fereastra meniului principal.
     * Inițializează și configurează fereastra ca fiind modală,
     * afișează numele clientului în titlu și adaugă butoanele de meniu.
     *
     * @param owner      Fereastra părinte (poate fi null) peste care acest dialog va fi centrat.
     * @param numeClient Numele clientului autentificat, afișat în titlul ferestrei.
     */

    public FereastraMeniuPrincipal(JFrame owner, String numeClient) {
        super(owner, "Meniu Principal: " + numeClient, true); // MODAL

        setLayout(new GridLayout(0, 1, 10, 10));
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
    /**
     * Metodă ajutătoare privată pentru a crea și configura un buton de meniu.
     *
     * @param text          Textul afișat pe buton.
     * @param actionCommand Comanda numerică (String) asociată butonului.
     * @return Un obiect JButton configurat.
     */
    private JButton creeazaButonMeniu(String text, String actionCommand) {
        JButton buton = new JButton(text);
        buton.setActionCommand(actionCommand);
        buton.addActionListener(this);
        return buton;
    }
    /**
     * Gestionează evenimentele de click pe butoane.
     * Când un buton este apăsat, convertește {@code actionCommand}-ul său
     * într-un număr întreg, îl salvează în {@link #optiuneAleasa} și închide fereastra.
     *
     * @param e Evenimentul de acțiune generat de click-ul pe buton.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.optiuneAleasa = Integer.parseInt(e.getActionCommand());
        this.dispose();
    }
    /**
     * Afișează fereastra de dialog și blochează execuția (fiind modală)
     * până când utilizatorul alege o opțiune (sau închide fereastra).
     *
     * @return Un număr întreg reprezentând opțiunea aleasă (0-8), sau -1
     * dacă fereastra este închisă manual.
     */
    public int afiseazaSiAsteaptaAlegere() {
        setVisible(true);
        return this.optiuneAleasa;
    }
}