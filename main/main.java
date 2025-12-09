package main;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // Buat window
        JFrame window = new JFrame("CaturUmgPBO Update");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set icon aplikasi
        ImageIcon icon = new ImageIcon("img/chess.png");
        window.setIconImage(icon.getImage());

        // Buat panel game dan tambahkan ke window
        PanelGame gp = new PanelGame();
        window.add(gp);
        window.pack();
        window.setLocationRelativeTo(null);

        // Launch game
        gp.launchGame();

        // Tampilkan window
        window.setVisible(true);
    }
}
