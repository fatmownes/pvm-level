package com.pvmscore.overlay;

import com.pvmscore.PvmScore;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pvmscore.PvmScore.DEFAULT_POINTS;
import static com.pvmscore.PvmScore.HARD_POINTS;

public class BossPointsOverlay extends Overlay {

    private int points = -1;

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
        Point textLocation = client.getLocalPlayer()
                .getCanvasTextLocation(graphics, "test", client.getLocalPlayer().getLogicalHeight() + 25);

        OverlayUtil.renderTextLocation(graphics, textLocation, "test", Color.RED);
        return null;
    }

    public void notifyNotKill() {
        points = -1;
    }

    public void notifyKill(NPC npc) {
        AtomicInteger pts = new AtomicInteger(-1);
        AtomicBoolean found = new AtomicBoolean(false);



        if (testing) {
            points = 1;
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

        //update something for the render?
        points = pts.get();
    }

}
