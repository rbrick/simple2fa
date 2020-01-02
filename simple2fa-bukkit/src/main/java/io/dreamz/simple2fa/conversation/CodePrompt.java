package io.dreamz.simple2fa.conversation;

import io.dreamz.simple2fa.Simple2FA;
import io.dreamz.simple2fa.events.PlayerAuthenticatedEvent;
import io.dreamz.simple2fa.session.Session;
import io.dreamz.simple2fa.session.UserSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class CodePrompt implements Prompt {

    @Override
    public String getPromptText(ConversationContext context) {
        if (context.getAllSessionData().containsKey("verified")) {
            return ChatColor.GREEN + "Successfully authenticated.";
        }
        return ChatColor.RED + "Please enter your 2FA code.";
    }

    @Override
    public boolean blocksForInput(ConversationContext context) {
        return !context.getAllSessionData().containsKey("verified");
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        final UUID id = (UUID) context.getSessionData("uuid");
        final Session session = Simple2FA.getInstance().getSessions().get(id);
        final String key = Simple2FA.getInstance().getKeyCache().get(id);
        if (session instanceof UserSession) {
            if (key == null) {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Please wait while we load your session...");
                return this;
            }
            if (context.getAllSessionData().containsKey("verified")) {
                return END_OF_CONVERSATION;
            }

            if (context.getAllSessionData().containsKey("attempts") &&
                    context.getAllSessionData().get("attempts") instanceof Integer) {
                int attempts = (Integer) context.getAllSessionData().get("attempts");
                if (attempts >= 5) {
                    ((UserSession) session).getPlayer().kickPlayer(ChatColor.RED + "Too many attempts!");
                    return END_OF_CONVERSATION;
                }
            }


            if (!((UserSession) session).authenticate(key, input)) {
                context.getAllSessionData().compute("attempts", (k, v) -> {
                    if (v instanceof Integer) {
                        return ((Integer) v) + 1;
                    }
                    return 1;
                });
                // send attempt count
                context.getForWhom().sendRawMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&cInvalid code! (Attempts: &e%d&c)", (Integer) context.getAllSessionData().get("attempts"))
                ));
            } else {
                context.getAllSessionData().put("verified", true);
                Bukkit.getPluginManager().callEvent(new PlayerAuthenticatedEvent((Player) context.getForWhom(), session));
            }

            return this;
        }

        return END_OF_CONVERSATION;
    }
}
