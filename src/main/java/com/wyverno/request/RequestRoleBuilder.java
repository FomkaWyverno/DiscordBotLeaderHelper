package com.wyverno.request;

import com.wyverno.request.exceptions.BadFractionRequestException;
import com.wyverno.request.exceptions.BadNicknameRequestException;
import com.wyverno.request.exceptions.BadRankRequestException;
import com.wyverno.request.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestRoleBuilder {

    private static final Logger logger = LoggerFactory.getLogger(RequestRoleBuilder.class);

    private static final String[] FORM = {
            "Ник-нейм:",
            "Организация:",
            "Ваш ранг:",
            "Ваше реальное имя(по желанию):"
    };

    private String nickname;
    private Fraction fraction;
    private int rank;
    private String name;

    public RequestRole build(String message) throws BadRequestException {

        String[] args = new String[FORM.length];
        String[] splitMessage = message.split("\n");

        if (splitMessage.length != args.length) throw new BadRequestException();


        for (int i = 0; i < args.length; i++) {
            if (splitMessage[i].contains(FORM[i])) {
                args[i] = new StringBuilder(splitMessage[i])
                        .delete(0,(splitMessage[i].indexOf(FORM[i]) + FORM[i].length()))
                        .toString()
                        .trim();
            } else {
                args[i] = "";
            }
        }

        checkCorrectForm(args);

        return new RequestRole(nickname, fraction, rank, name);
    }

    private boolean checkCorrectForm(String[] args) throws BadNicknameRequestException, BadFractionRequestException, BadRankRequestException {
        logger.trace("Checking correction request");

        if (args[0] == null || args[0].isEmpty()) {
            logger.debug("Nickname is not OK");
            logger.debug("Nickname is -> " + args[0]);
            throw new BadNicknameRequestException();
        }
        nickname = args[0];

        logger.debug("Nickname is OK");

        try {
            fraction = Fraction.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.debug("Fraction is not OK");
            logger.debug("Fraction name: " + args[1]);
            throw new BadFractionRequestException();
        }

        logger.debug("Fraction is OK");

        try {
            rank = Integer.parseInt(args[2]);
            if (rank > 9 || rank < 1) {
                throw new BadRankRequestException();
            }
        } catch (NumberFormatException e) {
            logger.debug("Rank is not OK");
            logger.debug("Rank -> " + args[2]);
            throw new BadRankRequestException();
        }

        logger.debug("Rank is OK");

        name = args[3];

        return true;
    }
}
