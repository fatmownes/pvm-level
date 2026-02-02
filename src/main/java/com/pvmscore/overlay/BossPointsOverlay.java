package com.pvmscore.overlay;

import com.pvmscore.PvmScore;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

public class BossPointsOverlay extends Overlay {

    private int points = -1;
    private HiscoreSkill currentKill = null;
    private int yOffset = 0;  // Track vertical offset
    private static final int MOVE_SPEED = 2;  // Pixels to move up per render

    private final Client client;
    private final SpriteManager spriteManager;

    public BossPointsOverlay(Client client, SpriteManager spriteManager) {
        this.client = client;
        this.spriteManager = spriteManager;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        return drawSomething(graphics);
    }

    private Dimension drawSomething(Graphics2D graphics) {
        if (points == -1) {
            return null;
        }

        String text = String.format("%d", points);

//        Point textLocation = client.getLocalPlayer()
//                .getCanvasTextLocation(graphics, text, client.getLocalPlayer().getLogicalHeight() + 100 + yOffset);

        int y = (int) (client.getCanvasHeight() - (client.getCanvasHeight() * .75));
        int x = (int) (client.getCanvasWidth() - (client.getCanvasWidth() * .25)); //TODO hehe doesnt work at all



        spriteManager.getSpriteAsync(currentKill == null ? SpriteID.SideIcons.COMBAT : currentKill.getSpriteId(), 0,
        (sprite) -> {
            final BufferedImage scaledSprite = ImageUtil.
                    resizeImage(ImageUtil.resizeCanvas(sprite, 25, 25), 20, 20);

            Point textPoint = new Point(x, y - yOffset);
            Point imagePoint = new Point(x - sprite.getTileWidth(), (int) ((y - yOffset) - (scaledSprite.getHeight() * .85)));

            OverlayUtil.renderTextLocation(graphics, textPoint, text, ColorScheme.TEXT_COLOR);
            OverlayUtil.renderImageLocation(graphics, imagePoint, scaledSprite);
        });

        // Move up for next render
        yOffset += MOVE_SPEED;
        return null;
    }

    public void notifyNotKill() {
        points = -1;
        yOffset = 0;
        currentKill = null;
    }

    public void notifyKill(HiscoreSkill dead) {
        points = PvmScore.FULL_POINT_MAPPINGS.get(dead);
        currentKill = dead;
    }
}
