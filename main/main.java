package main;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class main {
    public static void main(String[] args) {
        JFrame window = new JFrame("CaturUmgPBO Update");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH); // fullscreen tapi tetap ada title bar
        window.setVisible(true);
        // Set icon aplikasi
        ImageIcon icon = new ImageIcon("img/chess.png"); // gunakan versi besar
        window.setIconImage(icon.getImage());

        panelgame gp = new panelgame();
        window.add(gp);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
