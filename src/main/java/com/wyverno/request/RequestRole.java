package com.wyverno.request;

import com.wyverno.Main;
import com.wyverno.request.exceptions.BadFractionRequestException;
import com.wyverno.request.exceptions.BadNicknameRequestException;
import com.wyverno.request.exceptions.BadRankRequestException;
import com.wyverno.request.exceptions.BadRequestException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RequestRole {
    private static final Logger logger = LoggerFactory.getLogger(RequestRole.class);

    private String nickname;
    private Fraction fraction;
    private int rank;
    private String name;

    private static final long ROLE_LCN_ID = 1019869262682259497L;
    private static final long ROLE_YAK_ID = 1021478341771014304L;

    private static final long ROLE_LCN_AIR_ID = 1019869361181311027L;
    private static final long ROLE_YAK_AIR_ID = 1021478052204658718L;

    private static final String exampleRequest =
                    "Ник-нейм: Mimi_Conti\n" +
                    "Организация: LCN\n" +
                    "Ваш ранг: 4\n" +
                    "Ваше реальное имя(по желанию): Катя\n" +
                    "Или\n" +
                    "Ник-нейм: Kuro_Kato\n" +
                    "Организация: Yakuza\n" +
                    "Ваш ранг: 8\n" +
                    "Ваше реальное имя(по желанию):";

    protected RequestRole(String nickname, Fraction fraction, int rank, String name) {
        this.nickname = nickname;
        this.fraction = fraction;
        this.rank = rank;
        this.name = name;
    }

    public void giveRoleAndChangeNick(Member member) {
        changeNickname(member);
        giveRole(member);
    }

    private void changeNickname(Member member) {
        String formatNickname =
                String.format("%s | %s" + (!this.name.isEmpty() ? " | " + this.name : ""),
                        this.fraction.getLabel(), this.nickname);

        logger.debug("formatNickname = " + formatNickname);

        member.modifyNickname(formatNickname).complete();
    }

    private void giveRole(Member member) {
        switch (fraction) {
            case LCN: {
                logger.debug(nickname + " ADD ROLE LCN");
                member.getGuild().addRoleToMember(member,member.getGuild().getRoleById(ROLE_LCN_ID)).complete();
                if (rank >= 5) {
                    logger.debug(nickname + " ADD ROLE AIR LCN");
                    member.getGuild().addRoleToMember(member,member.getGuild().getRoleById(ROLE_LCN_AIR_ID)).complete();
                }
                break;
            }
            case YAKUZA: {
                logger.debug(nickname + "ADD ROLE YAKUZA");
                member.getGuild().addRoleToMember(member,member.getGuild().getRoleById(ROLE_YAK_ID)).complete();
                if (rank >= 5) {
                    logger.debug(nickname + " ADD ROLE AIR YAKUZA");
                    member.getGuild().addRoleToMember(member,member.getGuild().getRoleById(ROLE_YAK_AIR_ID)).complete();
                }
                break;
            }
        }
    }

    public static void requestRoleComplete(Message message,Member member) {
        String privateMessage = "";
        try {
            RequestRole requestRole = new RequestRoleBuilder().build(message.getContentDisplay());

            requestRole.giveRoleAndChangeNick(member);
            privateMessage = "Ваша заявка для выдачи роли была одобрена автоматически!";

        } catch (BadRankRequestException e) {
            privateMessage = "В вашей заявка не правильно указан ранг\n" + message.getContentDisplay() + "\n Пример:\n"+ exampleRequest;
        } catch (BadNicknameRequestException e) {
            privateMessage = "В вашей заявка не правильно указан ник-нейм\n" + message.getContentDisplay() + "\n Пример:\n"+ exampleRequest;
        } catch (BadFractionRequestException e) {
            privateMessage = "В вашей заявка ошибка в пункте организация!\n" + message.getContentDisplay() + "\n Пример:\n"+ exampleRequest;
        } catch (BadRequestException e) {
            privateMessage = "Ваша заявка составлена не по форме!\n" + message.getContentDisplay() + "\n Пример:\n"+ exampleRequest;
        }


        String finalPrivateMessage = privateMessage;
        message.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(finalPrivateMessage).queue());
        logger.info("Send private message to user " + message.getAuthor().getName());

        message.delete().complete();
        logger.debug("Message request removed!");
    }

    public static Member findMember(Guild guild, long id) {
        List<Member> members = guild.loadMembers().get();

        Member user = null;

        logger.debug("Starting find member - " + id);
        for (Member member : members) {
            if (id == member.getIdLong()) {
                logger.debug("We find member - " + id);
                user = member;
            }
        }
        return user;
    }
}
