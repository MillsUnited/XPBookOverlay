package com.mills.xpbookoverlay.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MainClient implements ClientModInitializer {

    private final static Identifier LAYER_ID = Identifier.of("mills", "xp_book_overlay");
    public static int playerXP;

    // book xp cost
    private final int simpleBookXpCost = 400;
    private final int uniqueBookXpCost = 800;
    private final int eliteBookXpCost = 2500;
    private final int ultimateBookXpCost = 5000;
    private final int legendaryBookXpCost = 25000;
    private final int fabledBookXpCost = 50000;

    // book colors
    private final int simpleBookColor  = 0xFFFFFF;
    private final int uniqueBookColor  = 0x00FF00;
    private final int eliteBookColor = 0x0000FF;
    private final int ultimateBookColor = 0xFFFF00;
    private final int legendaryBookColor = 0xFFA500;
    private final int fabledBookColor = 0x800080;

    @Override
    public void onInitializeClient() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
            layeredDrawer.attachLayerAfter(
                    IdentifiedLayer.MISC_OVERLAYS,
                    LAYER_ID,
                    (context, tickCounter) -> drawHudText(context)
            );
        });

        // every tick updates players current xp
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            playerXP = client.player.totalExperience;
        });
    }

    private void drawHudText(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        String xpMessage = format(playerXP);
        int textWidth = client.textRenderer.getWidth(xpMessage);

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int xpMessageX = (screenWidth / 2) - (textWidth / 2);
        int xpMessageY = screenHeight - 65;

        int numBooks = 6;
        int spacing = 25;

        int totalWidth = (numBooks - 1) * spacing;
        int startX = (screenWidth / 2) - (totalWidth / 2);

        int bookY = xpMessageY - 25;
        int bookTextY = bookY - 7;

        context.drawText(client.textRenderer, Text.literal("XP: " + xpMessage),
                xpMessageX, xpMessageY, simpleBookColor, false);

        int[] xpCosts = {
                simpleBookXpCost,
                uniqueBookXpCost,
                eliteBookXpCost,
                ultimateBookXpCost,
                legendaryBookXpCost,
                fabledBookXpCost
        };

        int[] colors = {
                simpleBookColor,
                uniqueBookColor,
                eliteBookColor,
                ultimateBookColor,
                legendaryBookColor,
                fabledBookColor
        };

        for (int i = 0; i < numBooks; i++) {
            int x = startX + (i * spacing);
            String amount = format(getAmountInNumber(playerXP, xpCosts[i]));
            context.drawText(client.textRenderer, Text.literal(amount), x, bookTextY, colors[i], false);
            context.drawItem(new ItemStack(Items.BOOK), x + 3, bookY);
        }
    }

    private int getAmountInNumber(int total, int divider) {
        return total / divider;
    }

    public static String format(double value) {
        String[] suffixes = {
                "",     // 10^0
                "K",    // 10^3 - Thousand
                "M",    // 10^6 - Million
                "B",    // 10^9 - Billion
                "T",    // 10^12 - Trillion
                "Qa",   // 10^15 - Quadrillion
                "Qi",   // 10^18 - Quintillion
                "Sx",   // 10^21 - Sextillion
                "Sp",   // 10^24 - Septillion
                "Oc",   // 10^27 - Octillion
                "No",   // 10^30 - Nonillion
                "Dc",   // 10^33 - Decillion
                "Ud",   // 10^36 - Undecillion
                "Dd",   // 10^39 - Duodecillion
                "Td",   // 10^42 - Tredecillion
                "Qad",  // 10^45 - Quattuordecillion
                "Qid",  // 10^48 - Quindecillion
                "Sxd",  // 10^51 - Sexdecillion
                "Spd",  // 10^54 - Septendecillion
                "Ocd",  // 10^57 - Octodecillion
                "Nod",  // 10^60 - Novemdecillion
                "Vg",   // 10^63 - Vigintillion
                "Uvg",  // 10^66 - Unvigintillion
                "Dvg",  // 10^69 - Duovigintillion
                "Tvg",  // 10^72 - Tresvigintillion
                "Qavg", // 10^75 - Quattuorvigintillion
                "Qivg", // 10^78 - Quinvigintillion
                "Sxvg", // 10^81 - Sexvigintillion
                "Spvg", // 10^84 - Septenvigintillion
                "Ocvg", // 10^87 - Octovigintillion
                "Novg", // 10^90 - Novemvigintillion
                "Tg",   // 10^93 - Trigintillion
                "Qg",   // 10^120 - Quadragintillion
                "Qng",  // 10^153 - Quinquagintillion
                "Sxg",  // 10^180 - Sexagintillion
                "Sptg", // 10^210 - Septuagintillion
                "Ocg",  // 10^240 - Octogintillion
                "Nog",  // 10^270 - Nonagintillion
                "C"     // 10^303 - Centillion
        };
        int index = 0;

        while (value >= 1000 && index < suffixes.length - 1) {
            value /= 1000.0;
            index++;
        }

        String formatted = String.format("%.2f", value);

        if (formatted.endsWith(".00")) {
            formatted = formatted.substring(0, formatted.length() - 3);
        }

        return formatted + suffixes[index];
    }
}
