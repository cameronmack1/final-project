package tankgame.game.Render;

import java.util.ArrayList;

import tankgame.game.projectile.Projectile;

import javax.imageio.ImageIO;
import java.io.IOException;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Canvas;
import java.awt.image.BufferStrategy;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;

import java.io.File;

import tankgame.game.GameFrame;
import tankgame.game.GameHandler;
import tankgame.game.KeyHandler;
import tankgame.game.Player;
;
import tankgame.server.ServerPlayer;

import java.util.UUID;

/**
 *
 * @author Cameron
 */


public final class GameCanvas extends Canvas {

    GameHandler gh;
    GameFrame gf;
    boolean isHost;
    BufferedImage img;
    private int width;
    private int height;
    Graphics2D g2d;
    BufferedImage tank;
    BufferedImage bullet;
    BufferedImage debug;
    public boolean inDebug = false;
    private final UUID id;

    BufferStrategy buffer;

    private int projCooldown = 0;

    GraphicsEnvironment ge;
    GraphicsDevice gd;
    GraphicsConfiguration gc;

    private boolean local;

    //this should only contain the local player and local projectiles
    //so they can be predicted
    //only save 3 snapshots at a time cuz we dont care abt past
    public volatile ArrayList<Snapshot> localSnapshots = new ArrayList<>();

    //this will contain snapshots sent by the server
    //it will have ALL players and projectils
    //local renderer must figure out which ones are local from their rid
    //so they arent double rendered in the past
    //once a snapshot gets old enough we throw it out
    private volatile ArrayList<Snapshot> serverSnapshots = new ArrayList<>();

    public KeyHandler kb = new KeyHandler();

    public GameCanvas(GameFrame gf, GameHandler gh, UUID id) {
        this.gf = gf;
        this.gh = gh;
        addKeyListener(kb);

        //init a ton of stuff
        setIgnoreRepaint(true);
        width = gf.getWidth();
        height = gf.getHeight();
        setPreferredSize(new Dimension(width, height));
        img = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
        g2d = img.createGraphics();

        //graphics stuff
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gd = ge.getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();

        //set rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        //load images
        try {
            tank = resizeImage(60, 80, ImageIO.read(new File("src" + File.separator + "images" + File.separator + "tank.png")));
            bullet = resizeImage(20, 20, ImageIO.read(new File("src" + File.separator + "images" + File.separator + "bullet.png")));
            debug = resizeImage(1920, 1080, ImageIO.read(new File("src" + File.separator + "images" + File.separator + "whoisthat.jpg")));
        } catch (IOException e) {
            System.out.println("error loading file");
        } catch (NullPointerException e) {
            System.out.println("error file missing wtf did u do");
            e.printStackTrace();
        }

        this.id = id;

        setVisible(true);
    }

    public void initBuffer() {
        createBufferStrategy(2);
        buffer = getBufferStrategy();
        requestFocusInWindow();
    }

    public void addServerSnapshot(Snapshot s) {
        serverSnapshots.add(0, s);
        if (serverSnapshots.size() > 150) {
            serverSnapshots.remove(serverSnapshots.size() - 1);
        }
    }

    public void addLocalSnapshot(Snapshot s) {
        localSnapshots.add(0, s);
        if (localSnapshots.size() > 150) {
            localSnapshots.remove(localSnapshots.size() - 1);
        }
    }

    public void initLocal() {
        local = true;
    }

    public static double lerp(double val1, double val2, double alpha) {
        //val 1 is old, val2 is new
        return (val2 - val1) * alpha + val1;
    }
    
    public void renderMap(boolean[][] map){
        
    }

    public void render(Snapshot s1, Snapshot s2, double time) {
        //drawImageAtRot(tank, x,y,angle+Math.PI/2);
        //s1 is new, s2 is old
        double x1;
        double x2;
        double y1;
        double y2;
        double a1;
        double a2;
        Player[] playerArray1 = s1.getPlayerArray();
        Player[] playerArray2 = s2.getPlayerArray();
        for (int i = 0; i < playerArray1.length; i++) {
            //check if it is a server player, and if it is then check the id against self id
            if (playerArray1[i] instanceof ServerPlayer sp) {
                if (!sp.getID().equals(id)) {
                    x1 = playerArray1[i].getX();
                    x2 = playerArray2[i].getX();
                    y1 = playerArray1[i].getY();
                    y2 = playerArray2[i].getY();
                    a1 = playerArray1[i].getAngle();
                    a2 = playerArray2[i].getAngle();
                    drawImageAtRot(tank, lerp(x2, x1, time), lerp(y2, y1, time), lerp(a2, a1, time) + Math.PI / 2);
                }
            }
        }
        Projectile[] projArray1 = s1.getProjectileArray();
        Projectile[] projArray2 = s2.getProjectileArray();
        for (int i = 0; i < s2.getProjectileArray().length; i++) {
            if (projArray2[i].getIsNew()) {
                drawImageAtRot(bullet, projArray2[i].getX(), projArray2[i].getY(), projArray2[i].getAngle());
            } else {
                x1 = projArray1[i].getX();
                x2 = projArray2[i].getX();
                y1 = projArray1[i].getY();
                y2 = projArray2[i].getY();
                a1 = projArray1[i].getAngle();
                a2 = projArray2[i].getAngle();
                drawImageAtRot(bullet, lerp(x2, x1, time), lerp(y2, y1, time), lerp(a2, a1, time));
            }
        }
    }

    public void renderLoop() {
        //NO SIMULATION
        //RENDER ONLY
        //MUST KEEP SIM SEPERATE FROM RENDER
        g2d = img.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, 1920, 1080);
        if (inDebug) {
            g2d.drawImage(debug, 0, 0, null);
        }
        if (local) {
            long t1 = localSnapshots.get(0).getTime();
            long t2 = localSnapshots.get(1).getTime();
            long t3 = System.currentTimeMillis() - 33;
            double time = (double) (t3 - t2) / (double) (t1 - t2);
            time = Math.max(0.0, Math.min(1.0, time));
            render(localSnapshots.get(0), localSnapshots.get(1), time);
        }
        Graphics graphics = buffer.getDrawGraphics();
        graphics.drawImage(img.getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, null);
        g2d.dispose();
        if (!buffer.contentsLost()) {
            buffer.show();
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

    public void drawImageAtRot(BufferedImage img, double x, double y, double angle) {
        //transforms the entire base image, renders new image, and rotates it back
        AffineTransform old = g2d.getTransform();
        g2d.translate(Math.round(x), Math.round(y));
        g2d.rotate(angle);
        g2d.drawImage(img, -img.getWidth(null) / 2, -img.getHeight(null) / 2, null);
        g2d.setTransform(old);
    }
}
