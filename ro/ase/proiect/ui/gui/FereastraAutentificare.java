package ro.ase.proiect.ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Fereastră GUI (JDialog Modal) pentru meniul de autentificare. Blochează consola până când utilizatorul alege o optiune.
 *
 * @author Matei Maria-Bianca
 * @version 1.0
 * @since 4.11.2025
 */
public class FereastraAutentificare extends JDialog implements ActionListener {
    /**
     * Stochează acțiunea aleasă de utilizator ("LOGIN", "REGISTER", sau "EXIT").
     * Valoarea implicită este "EXIT" în cazul închiderii ferestrei prin 'X'.
     */
    private String actiuneAleasa = "EXIT";
    /**
     * Constructor pentru fereastra de autentificare.
     * Inițializează și configurează fereastra ca fiind modală,
     * setează layout-ul și adaugă butoanele pentru Login, Register și Exit.
     *
     * @param owner Fereastra părinte (poate fi null) peste care acest dialog va fi centrat.
     */
    public FereastraAutentificare(JFrame owner) {
        super(owner, "Bine ai venit! Autentificare E-Banking", true);

        setLayout(new GridLayout(3, 1, 10, 10));
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JButton btnLogin = new JButton("1. Autentificare (Login)");
        btnLogin.setActionCommand("LOGIN");
        btnLogin.addActionListener(this);

        JButton btnRegister = new JButton("2. Înregistrare (Client nou)");
        btnRegister.setActionCommand("REGISTER");
        btnRegister.addActionListener(this);

        JButton btnExit = new JButton("0. Ieșire din aplicație");
        btnExit.setActionCommand("EXIT");
        btnExit.addActionListener(this);

        add(btnLogin);
        add(btnRegister);
        add(btnExit);
    }
    /**
     * Gestionează evenimentele de click pe butoane.
     * Când un buton este apăsat, salvează {@code actionCommand}-ul său
     * în câmpul {@link #actiuneAleasa} și închide fereastra (dispose).
     *
     * @param e Evenimentul de acțiune generat de click-ul pe buton.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.actiuneAleasa = e.getActionCommand();
        this.dispose();
    }
    /**
     * Afișează fereastra de dialog și blochează execuția (fiind modală)
     * până când utilizatorul alege o opțiune (sau închide fereastra).
     *
     * @return Un String reprezentând acțiunea aleasă: "LOGIN", "REGISTER", sau "EXIT".
     */
    public String afiseazaSiAsteaptaAlegere() {
        setVisible(true);
        return this.actiuneAleasa;
    }

}
