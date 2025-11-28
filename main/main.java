package main;

import javax.swing.JFrame;

public class main {
    public static void main(String[] args) {
        JFrame window = new JFrame("CaturUmgPbo");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        panelgame gp = new panelgame();
        window.add(gp);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
