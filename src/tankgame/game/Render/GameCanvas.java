package tankgame.game.Render;

import java.util.ArrayList;

import tankgame.game.projectile.Projectile;

import javax.imageio.ImageIO;
import java.io.IOException;

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
import java.awt.Font;

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
    BufferedImage selftank;
    BufferedImage bullet;
    BufferedImage debug;
    BufferedImage bgImage;
    public boolean inDebug = false;
    private final UUID id;
    private boolean quitting = false;

    public static final int WALL_SIZE = 10;

    BufferStrategy buffer;

    GraphicsEnvironment ge;
    GraphicsDevice gd;

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

    private double tileHeight;
    private double tileWidth;
    private boolean[][] map;

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

        //set rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        //load images
        try {
            tank = resizeImage(60, 80, ImageIO.read(ClassLoader.getSystemResource("images/tank.png")));
            selftank = resizeImage(60, 80, ImageIO.read(ClassLoader.getSystemResource("images/selftank.png")));
            bullet = resizeImage(20, 20, ImageIO.read(ClassLoader.getSystemResource("images/bullet.png")));
            debug = resizeImage(1920, 1080, ImageIO.read(ClassLoader.getSystemResource("images/whoisthat.jpg")));
            bgImage = resizeImage(1920, 1080, ImageIO.read(ClassLoader.getSystemResource("images/background.PNG")));
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

    public void renderMap() {
        //render shi
        g2d.setColor(Color.BLACK);
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                //if its a wall
                if (map[y][x]) {
                    //if odd x then it is thin
                    //if odd y then it is short
                    //(corner pieces are at odd,odd)
                    int h = (y % 2 == 0) ? (int) Math.round(WALL_SIZE) : (int) Math.round(tileHeight + 1);
                    int w = (x % 2 == 0) ? (int) (WALL_SIZE) : (int) (tileWidth + 1);
                    int curY = (int) (Math.round(tileHeight * (y / 2) + WALL_SIZE * ((y + 1) / 2)));
                    int curX = (int) (Math.round(tileWidth * (x / 2) + WALL_SIZE * ((x + 1) / 2)));
                    g2d.fillRect(curX, curY, w, h);
                }
            }
        }
    }

    public void render(Snapshot s1, Snapshot s2, double time, boolean isLocal) {
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
            boolean isSelf = false;
            if (playerArray1[i] instanceof ServerPlayer && !gf.getUsername().contains("debug")) {
                ServerPlayer sp = (ServerPlayer) playerArray1[i];
                if (sp.getID().equals(id)) {
                    isSelf = true;
                }
            }
            //isSelf = false;
            if (!playerArray2[i].getIsDead()) {
                if (!isSelf) {
                    x1 = playerArray1[i].getX();
                    x2 = playerArray2[i].getX();
                    y1 = playerArray1[i].getY();
                    y2 = playerArray2[i].getY();
                    a1 = playerArray1[i].getAngle();
                    a2 = playerArray2[i].getAngle();
                    if (isLocal) {
                        drawImageAtRot(selftank, lerp(x2, x1, time), lerp(y2, y1, time), lerp(a2, a1, time) + Math.PI / 2);
                    } else {
                        drawImageAtRot(tank, lerp(x2, x1, time), lerp(y2, y1, time), lerp(a2, a1, time) + Math.PI / 2);
                    }

                    //draw points on tank corners (60x80)
                    //display hitbox code
                    if (gf.getUsername().contains("debug")) {
                        double xl = lerp(x2, x1, time);
                        double yl = lerp(y2, y1, time);
                        double al = lerp(a2, a1, time);
                        double dx1 = (20 * Math.cos(al)) - (10 * Math.sin(al)) + xl;
                        double dy1 = (20 * Math.sin(al)) + (10 * Math.cos(al)) + yl;

                        double dx2 = (-35 * Math.cos(al)) - (10 * Math.sin(al)) + xl;
                        double dy2 = (-35 * Math.sin(al)) + (10 * Math.cos(al)) + yl;

                        double dx3 = (20 * Math.cos(al)) - (-10 * Math.sin(al)) + xl;
                        double dy3 = (20 * Math.sin(al)) + (-10 * Math.cos(al)) + yl;

                        double dx4 = (-35 * Math.cos(al)) - (-10 * Math.sin(al)) + xl;
                        double dy4 = (-35 * Math.sin(al)) + (-10 * Math.cos(al)) + yl;

                        double dx5 = (20 * Math.cos(al)) + xl;
                        double dy5 = (20 * Math.sin(al)) + yl;

                        double dx6 = (-35 * Math.cos(al)) + xl;
                        double dy6 = (-35 * Math.sin(al)) + yl;
                        g2d.setColor(Color.WHITE);
                        g2d.draw(new java.awt.geom.Ellipse2D.Double(dx1, dy1, 5, 5));
                        g2d.draw(new java.awt.geom.Ellipse2D.Double(dx2, dy2, 5, 5));
                        g2d.draw(new java.awt.geom.Ellipse2D.Double(dx3, dy3, 5, 5));
                        g2d.draw(new java.awt.geom.Ellipse2D.Double(dx4, dy4, 5, 5));
                        g2d.draw(new java.awt.geom.Ellipse2D.Double(dx5, dy5, 5, 5));
                        g2d.draw(new java.awt.geom.Ellipse2D.Double(dx6, dy6, 5, 5));
                    }
                }
            }
        }
        Projectile[] projArray1 = s1.getProjectileArray();
        Projectile[] projArray2 = s2.getProjectileArray();
        for (int i = 0; i < s2.getProjectileArray().length; i++) {
            boolean isSelf = false;
            if (!isLocal && id.equals(projArray2[i].getOwner()) && !gf.getUsername().contains("debug")) {
                isSelf = true;
            }
            //isSelf = false;
            if (!isSelf && !projArray2[i].getIsDead()) {
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

                    if (gf.getUsername().contains("debug")) {
                        double xl = lerp(x2, x1, time);
                        double yl = lerp(y2, y1, time);
                        g2d.setColor(Color.WHITE);
                        g2d.draw(new java.awt.geom.Ellipse2D.Double(xl, yl, 5, 5));
                    }
                }
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
        } else {
            g2d.drawImage(bgImage, 0, 0, null);
        }
        renderMap();
        if (local) {
            long t1 = localSnapshots.get(0).getTime();
            long t2 = localSnapshots.get(1).getTime();
            long t3 = System.currentTimeMillis() - 33;
            double time = (double) (t3 - t2) / (double) (t1 - t2);
            time = Math.max(0.0, Math.min(1.0, time));
            render(localSnapshots.get(0), localSnapshots.get(1), time, true);
        }

        //render server snapshots in past (100ms in past)
        if (serverSnapshots.size() > 1) {
            long renderTime = System.currentTimeMillis() - 133;
            int snapshot1 = -1;
            for (int i = 0; i < serverSnapshots.size() - 1; i++) {
                if (serverSnapshots.get(i).getTime() < renderTime) {
                    snapshot1 = i;
                    break;
                }
            }
            if (snapshot1 <= 0) {
                render(serverSnapshots.get(serverSnapshots.size() - 1), serverSnapshots.get(serverSnapshots.size() - 1), 0, false);
            } else {
                long t1 = serverSnapshots.get(snapshot1 - 1).getTime();
                long t2 = serverSnapshots.get(snapshot1).getTime();
                double time = (double) (renderTime - t2) / (double) (t1 - t2);
                time = Math.max(0.0, Math.min(1.0, time));
                render(serverSnapshots.get(snapshot1 - 1), serverSnapshots.get(snapshot1), time, false);
            }
        }
        //pause menu overlay made by koorosh the goat
        if (kb.isPaused()) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, 1920, 1080);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Comic Sans", Font.BOLD, 60));
            g2d.drawString("PAUSED", 870, 450);
            g2d.setFont(new Font("Comic Sans", Font.PLAIN, 30));
            g2d.drawString("ESC - resume", 870, 540);
            g2d.drawString("Q - quit", 870, 590);
        }

        //dead players spectate
        if (gh.isSelfDead()) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Comic Sans", Font.PLAIN, 40));
            g2d.drawString("you are dead - now spectating", 700, 100);
        }

        if (kb.getiQUIT() && !quitting) {
            quitting = true;
            gf.quitToMenu();
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

    public void setMap(boolean[][] map) {
        this.map = map;

        //calculate tile sizes in pixels using the number of tiles
        double wallHeight = WALL_SIZE * ((map.length + 1d) / 2d);
        this.tileHeight = (1080d - wallHeight) / ((map.length) / 2);

        double wallWidth = WALL_SIZE * ((map[0].length + 1d) / 2d);
        this.tileWidth = (1920d - wallWidth) / ((map[0].length) / 2);
    }
}
