package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorSelectionDialog extends JDialog {
    private JButton whiteButton;
    private JButton blackButton;
    private Boolean isWhiteSelected = null; // null means no selection yet

    public ColorSelectionDialog(Frame parent) {
        super(parent, "Pilih Warna", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        whiteButton = new JButton("Main sebagai Putih");
        whiteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isWhiteSelected = true;
                dispose();
            }
        });

        blackButton = new JButton("Main sebagai Hitam");
        blackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isWhiteSelected = false;
                dispose();
            }
        });

        panel.add(whiteButton);
        panel.add(blackButton);

        add(panel);
    }

    public Boolean isWhiteSelected() {
        return isWhiteSelected;
    }
}