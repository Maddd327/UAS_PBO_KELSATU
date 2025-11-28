package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Papan {
    final int MAX_COL = 0;
    final int MAX_ROW = 0;
    public static final int KOTAK_SIZE = 100;
    public static final int HALF_KOTAK_SIZE = KOTAK_SIZE/2;
    
    public void draw (Graphics2D g2){

    int c = 0;

        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                if (c == 0){
                    g2.setColor(new Color(210,165,125));
                    c=1;
                }else {
                    g2.setColor(new Color(175,115,70));
                    c=0;
                }
                g2.fillRect(col*KOTAK_SIZE, row*KOTAK_SIZE,KOTAK_SIZE,KOTAK_SIZE);
            }
            
            if (c == 0){
                c=1;
            }else {
                c= 0;
            }
        }
    }
}
