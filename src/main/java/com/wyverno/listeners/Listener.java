package com.wyverno.listeners;

import com.wyverno.Main;
import com.wyverno.request.RequestRole;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        logger.info("Got message");
        logger.info("Message: " + event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());
        if (event.getChannel().getIdLong() == Main.CHAT_REQUEST_ROLE) {
            logger.info("Got new request Role");
            RequestRole.requestRoleComplete(event.getMessage(),event.getMember());
            logger.info("Finish complete request role");
        }
    }
}
