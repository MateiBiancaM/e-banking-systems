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

    private String actiuneAleasa = "EXIT";

    public FereastraAutentificare(JFrame owner) {
        super(owner, "Bine ai venit! -->Autentificare E-Banking<--", true);

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

    @Override
    public void actionPerformed(ActionEvent e) {
        this.actiuneAleasa = e.getActionCommand();
        this.dispose();
    }

    public String afiseazaSiAsteaptaAlegere() {
        setVisible(true);
        return this.actiuneAleasa;
    }

}
