package io.dreamz.simple2fa.conversation;

import io.dreamz.simple2fa.Simple2FA;
import io.dreamz.simple2fa.session.Session;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class CodePrompt implements Prompt {
    @Override
    public String getPromptText(ConversationContext context) {
        return ChatColor.RED + "Please enter your 2FA code.";
    }

    @Override
    public boolean blocksForInput(ConversationContext context) {
        return true;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        final UUID id = (UUID) context.getSessionData("uuid");
        final Session session = Simple2FA.getInstance().getSessions().get(id);

        if (context.getAllSessionData().containsKey("attempts") &&
                context.getAllSessionData().get("attempts") instanceof Integer) {
            int attempts = (Integer) context.getAllSessionData().get("attempts");
            if (attempts > 5) {
                session.getPlayer().kickPlayer(ChatColor.RED + "Too many attempts!");
                return END_OF_CONVERSATION;
            }
        }

        if (!session.authenticate(input)) {
            context.getAllSessionData().putIfAbsent("attempts", 0);
            context.getAllSessionData().compute("attempts", (k, v) -> {
                if (v instanceof Integer) {
                    return ((Integer) v) + 1;
                }
                return -1;
            });
            // send attempt count
            context.getForWhom().sendRawMessage(ChatColor.translateAlternateColorCodes('&',
                    String.format("&cInvalid code! (Attempts: &e%d&c)",
                            (Integer) context.getAllSessionData().get("attempts"))
            ));
            return this;
        }

        return new Prompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                final UUID playerUuid = (UUID) context.getAllSessionData().get("uuid");
                final Session session = Simple2FA.getInstance().getSessions().get(playerUuid);
                final Player player = session.getPlayer();

                if (player != null) {
                    player.getInventory().setContents(session.getInventorySnapshot().getContents());
                }

                return ChatColor.GREEN + "Successfully authenticated.";
            }

            @Override
            public boolean blocksForInput(ConversationContext context) {
                return false;
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                return END_OF_CONVERSATION;
            }
        };
    }
}
