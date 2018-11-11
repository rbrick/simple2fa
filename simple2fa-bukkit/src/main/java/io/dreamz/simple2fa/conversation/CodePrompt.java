package io.dreamz.simple2fa.conversation;

import io.dreamz.simple2fa.Simple2FA;
import io.dreamz.simple2fa.storage.StorageEngine;
import io.dreamz.simple2fa.utils.HOTP;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

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
        final HOTP instance = Simple2FA.getInstance().getTotp();
        final StorageEngine storage = Simple2FA.getInstance().getStorageEngine();

        final UUID id = (UUID) context.getSessionData("uuid");
        final byte[] secret = storage.getRawSecret(id);

        if (secret == null) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "No key found!");
            return END_OF_CONVERSATION;
        }

        if (!instance.verify(input, secret, 2)) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid code!");
            return this;
        }

        return new Prompt() {
            @Override
            public String getPromptText(ConversationContext context) {
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
