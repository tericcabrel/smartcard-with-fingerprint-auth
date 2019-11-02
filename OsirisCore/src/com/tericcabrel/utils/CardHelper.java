package com.tericcabrel.utils;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.util.List;

/**
 *
 * @author ZEGEEK
 */
public class CardHelper {
    public static Card getCard() {
        Card card = null;

        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        List<CardTerminal> cardTerminals = null;
        try {
            cardTerminals = terminalFactory.terminals().list();

            if (cardTerminals.isEmpty()) {
                System.out.println("No card terminals available");
                return null;
            }

            System.out.println("Terminals: " + cardTerminals);
            CardTerminal cardTerminal = cardTerminals.get(0);

            if (cardTerminal.isCardPresent()) {
                card = cardTerminal.connect("T=1");
                // System.out.println(card);
            }
        } catch (CardException e) {
            e.printStackTrace();
        }

        return card;
    }
}
