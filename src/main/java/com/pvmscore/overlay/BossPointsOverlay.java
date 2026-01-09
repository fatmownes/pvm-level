package com.pvmscore.overlay;

import com.pvmscore.PvmScore;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pvmscore.PvmScore.DEFAULT_POINTS;
import static com.pvmscore.PvmScore.HARD_POINTS;

public class BossPointsOverlay extends Overlay {

    boolean rendering = false;
    private int points = -1;
    private int yOffset = 0;  // Track vertical offset
    private static final int MOVE_SPEED = 2;  // Pixels to move up per render

    private final boolean testing = true;
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
//        yOffset = 0;  // Reset offset
    }

    public void notifyKill(NPC npc) {
        AtomicInteger pts = new AtomicInteger(-1);
        AtomicBoolean found = new AtomicBoolean(false);

        if (testing) {
            points = 1;
            yOffset = 0;  // Reset offset on new kill
            return;
        }

        for (List<HiscoreSkill> bosses: PvmScore.ALL) {
            bosses.forEach(boss -> {
                if (boss.getName().equals(npc.getName())) {
                    pts.set(DEFAULT_POINTS);
                    found.set(true);
                }
            });

            if (found.get()) {
                break;
            }
        }

        points = pts.get();
        yOffset = 0;  // Reset offset on new kill
    }
}
