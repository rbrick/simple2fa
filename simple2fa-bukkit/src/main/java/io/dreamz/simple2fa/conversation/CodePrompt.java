package io.dreamz.simple2fa.conversation;

import io.dreamz.simple2fa.Simple2FA;
import io.dreamz.simple2fa.events.PlayerAuthenticatedEvent;
import io.dreamz.simple2fa.session.Session;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CodePrompt implements Prompt {

    private AtomicBoolean verified = new AtomicBoolean(false);

    @Override
    public String getPromptText(ConversationContext context) {
        if (verified.get()) {
            return ChatColor.GREEN + "Successfully authenticated.";
        }
        return ChatColor.RED + "Please enter your 2FA code.";
    }

    @Override
    public boolean blocksForInput(ConversationContext context) {
        return !verified.get();
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

        if (this.verified.get()) {

            Bukkit.getPluginManager().callEvent(new PlayerAuthenticatedEvent((Player) context.getForWhom(), session));

            return END_OF_CONVERSATION;
        }

        session.authenticate(input, (verified) -> {
            if (!verified) {
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
            } else {
                this.verified.set(true);
            }
        });

        return this;
    }

}
