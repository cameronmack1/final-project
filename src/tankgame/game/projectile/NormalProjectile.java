/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tankgame.game.projectile;

import java.util.UUID;

/**
 *
 * @author layne
 */
public class NormalProjectile extends Projectile {
 public NormalProjectile(double x, double y, double angle, double velocity, UUID owner) {
        super(x, y, angle, velocity, owner);
        this.velocity = velocity / 2 + DEFAULT_SPEED;
    }

    @Override
    public void move() {
        if (isNew) {
            isNew = false;
        }

        y += Math.sin(angle) * velocity;
        x += Math.cos(angle) * velocity;
    }
}
    

