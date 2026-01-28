package com.pvmscore.overlay;

import com.pvmscore.PvmScore;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pvmscore.PvmScore.DEFAULT_POINTS;

public class BossPointsOverlay extends Overlay {

    private int points = -1;
    private int yOffset = 0;  // Track vertical offset
    private static final int MOVE_SPEED = 2;  // Pixels to move up per render

    private final Client client;

    public BossPointsOverlay(Client client) {
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        return drawSomething(graphics);
    }

    private Dimension drawSomething(Graphics2D graphics) {
        if (points == -1) {
            return null;
        }

        String text = String.format("+%d", points);

        Point textLocation = client.getLocalPlayer()
                .getCanvasTextLocation(graphics, text, client.getLocalPlayer().getLogicalHeight() + 100 + yOffset);

        OverlayUtil.renderTextLocation(graphics, textLocation, text, ColorScheme.TEXT_COLOR);

        // Move up for next render
        yOffset += MOVE_SPEED;
        return null;
    }

    public void notifyNotKill() {
        points = -1;
        yOffset = 0;
    }

    public void notifyKill(int points) {
        this.points = points;
    }
}
