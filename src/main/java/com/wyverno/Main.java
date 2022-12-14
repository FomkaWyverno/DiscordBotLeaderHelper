package com.wyverno;

import com.wyverno.listeners.Listener;
import com.wyverno.request.RequestRole;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static JDA discordBot;

    public static final long CHAT_REQUEST_ROLE = 1006641203028168846L;

    public static void main(String[] args) throws InterruptedException {

        logger.info("Starting...");

        JDABuilder jdaBuilder = JDABuilder.create(args[0],
                        GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS,GatewayIntent.GUILD_MESSAGES)
                .setActivity(Activity.playing("Пошлые игры с Танаки)"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .addEventListeners(new Listener());

        discordBot = jdaBuilder.build();

        discordBot.awaitReady();

        logger.info("Connected!");

        requestHistory();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String str = reader.readLine();

                boolean isNeedBreak = false;
                switch (str) {
                    case "/stop" : {
                        logger.info("Discord Bot start to stop");
                        discordBot.shutdown();
                        isNeedBreak = true;
                        break;
                    }
                    case "/stopNow": {
                        logger.info("Discord Bot shutdown now!");
                        discordBot.shutdownNow();
                        isNeedBreak = true;
                        break;
                    }
                }
                if (isNeedBreak) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JDA getDiscordBot() {
        return discordBot;
    }

    private static void requestHistory() {
        TextChannel channel = discordBot.getTextChannelById(CHAT_REQUEST_ROLE);

        while (true) {
            List<Message> messageList = channel.getIterableHistory().complete();

            if (messageList.size() == 1) break;

            messageList.forEach(message -> {
                if (message.getAuthor().getIdLong() != message.getJDA().getSelfUser().getIdLong()) {
                    RequestRole.requestRoleComplete(message,RequestRole.findMember(message.getGuild(),message.getAuthor().getIdLong()));
                }
            });
        }


    }
}
