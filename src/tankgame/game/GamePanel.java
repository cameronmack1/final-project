package tankgame.game;

import tankgame.server.PlayerHandler;
import tankgame.server.ServerPlayer;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;

import tankgame.menu.MainMenu;

import tankgame.game.Render.*;
import tankgame.game.projectile.Projectile;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.io.IOException;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.Toolkit;

import java.io.File;

import tankgame.client.ClientPlayer;

/**
 *
 * @author Cameron
 */
public final class GamePanel extends JPanel {
    GameFrame gf;
    int gameState;
    boolean isHost;
    BufferedImage img;
    private int width;
    private int height;
    public PlayerHandler playerHandler = new PlayerHandler();
    Graphics2D g2d;
    BufferedImage tank;

    ClientPlayer self;
    Projectile[] localProj;

    //this should only contain the local player and local projectiles
    //so they can be predicted
    //only save 3 snapshots at a time cuz we dont care abt past
    ArrayList<Snapshot> localSnapshots = new ArrayList<>();

    //this will contain snapshots sent by the server
    //it will have ALL players and projectils
    //local renderer must figure out which ones are local from their rid
    //so they arent double rendered in the past
    //once a snapshot gets old enough we throw it out
    Deque<Snapshot> serverSnapshots = new ArrayDeque<>();

    public GamePanel(GameFrame gf) {
        width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        setPreferredSize(new Dimension(width, height));
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = img.createGraphics();
        revalidate();
        this.gf = gf;

        try {
            tank = resizeImage(60, 80, ImageIO.read(new File("src" + File.separator + "images" + File.separator + "tank.png")));
        } catch (IOException e) {
            System.out.println("error loading file");
        } catch (NullPointerException e) {
            System.out.println("error file missing wtf did u do");
        }

        gameState = 10;
        this.initDebug();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
    }

    public void initLobby(boolean isHost) {

    }

    public void initMenu() {
        MainMenu menu = new MainMenu();
    }

    public void initDebug() {
        //create snapshot with only 1 player and 0 projectiles as the default snapshot to base the simulation on
        self = new ClientPlayer(0);
        localProj = new Projectile[]{};
        Snapshot defaultSnapshot = new Snapshot(new ClientPlayer[]{self}, localProj, System.currentTimeMillis());
        localSnapshots.add(defaultSnapshot);
    }

    public static double lerp(double val1, double val2, double alpha) {
        //val 1 is old, val2 is new
        return (val2 - val1) * alpha + val1;
    }

    public void render(Snapshot s1, Snapshot s2, double time) {
        //drawImageAtRot(tank, x,y,angle+Math.PI/2);
        //s1 is old, s2 is new
        double x1;
        double x2;
        double y1;
        double y2;
        double a1;
        double a2;
        for (int i = 0; i < s1.getPlayerArray().length; i++) {
            x1 = s1.getPlayerArray()[i].getX();
            x2 = s2.getPlayerArray()[i].getX();
            y1 = s1.getPlayerArray()[i].getY();
            y2 = s2.getPlayerArray()[i].getY();
            a1 = s1.getPlayerArray()[i].getAngle();
            a2 = s2.getPlayerArray()[i].getAngle();
            drawImageAtRot(tank, lerp(x1, x2, time), lerp(y1, y2, time), lerp(a1, a2, time) + Math.PI / 2);
        }
        for(int i = 0; i< s2.getProjectileArray().length; i++){
            //if s2 proj array>s1 proj array then theres a new projectile
            //if s1 proj array[0] has a different rid than s2 proj array[0]
            //then projectile went bye bye
        }
    }

    public void renderLoop() {
        //NO SIMULATION
        //RENDER ONLY
        //MUST KEEP SIM SEPERATE FROM RENDER
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        switch (gameState) {
            case 0: {//in menu

                break;
            }
            case 1: {//client in game
                break;
            }
            case 2: {//server in game

                break;
            }

            case 10: {//singleplayer debug
                long t1 = localSnapshots.get(0).getTime();
                long t2 = localSnapshots.get(1).getTime();
                long t3 = System.currentTimeMillis()-33;
                double time = (double) (t3 - t2) / (double) (t1 - t2);
                time = Math.max(0.0, Math.min(1.0, time));
                render(localSnapshots.get(0), localSnapshots.get(1), time);
                break;
            }

            case 11: {//multiplayer debug

                break;
            }
        }
        validate();
        repaint();
    }

    public void tick() {
        //DO NOT PUT ANY RENDERING IN HERE OR I WILL KILL YOU
        //THIS IS SIMULATION **ONLY**
        switch (gameState) {
            case 0: {//in menu

                break;
            }
            case 1: {//client in game
                break;
            }
            case 2: {//server in game

                break;
            }

            case 10: {//singleplayer debug
                //DEBUG USING SERVER SIMULATE
                //add new snapshot to index 0, remove from end
                self.move(gf.kb.getKeys());
                ClientPlayer[] pArr = new ClientPlayer[]{new ClientPlayer(self, 0)};
                localSnapshots.add(0, new Snapshot(pArr, localProj, System.currentTimeMillis()));
                if (localSnapshots.size() > 5) {
                    localSnapshots.remove(localSnapshots.size() - 1);
                }
                break;
            }

            case 11: {//multiplayer debug

                break;
            }
        }
    }

    public BufferedImage resizeImage(int newWidth, int newHeight, BufferedImage img) {
        //scale
        Image image2 = img.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);

        //create new bufferedimage
        BufferedImage rsImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        //use graphics2d to draw resized image on bufferedimage then dispose
        Graphics2D bg2d = rsImage.createGraphics();
        bg2d.drawImage(image2, 0, 0, null);
        bg2d.dispose();

        return rsImage;
    }

    public void drawImageAtRot(Image img, double x, double y, double angle) {
        //transforms the entire base image, renders new image, and rotates it back
        AffineTransform old = g2d.getTransform();
        g2d.translate(x, y);
        g2d.rotate(angle);
        g2d.drawImage(img, -img.getWidth(null) / 2, -img.getHeight(null) / 2, null);
        g2d.setTransform(old);
    }
}
