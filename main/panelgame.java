package main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
public class panelgame extends JPanel implements Runnable{
    public static final int WIDTH =1200;
    public static final int HEIGHT =800;
    final int FPS = 60;
    Thread gameThread;
    public panelgame () {
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.black);
    }

    public void launchgame (){
        gameThread = new Thread(this);
        gameThread.start();
    }
    private void update () {

    }
    public void painComponent (Graphics g){
        super.paintComponent(g);
    }
    @Override
    public void run () {

    }
}
