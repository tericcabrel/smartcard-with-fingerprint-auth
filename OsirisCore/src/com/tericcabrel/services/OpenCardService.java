package com.tericcabrel.services;

import opencard.core.event.CTListener;
import opencard.core.event.CardTerminalEvent;
import opencard.core.event.EventGenerator;
import opencard.core.service.CardServiceException;
import opencard.core.service.SmartCard;
import opencard.core.terminal.CardTerminal;
import opencard.core.terminal.CardTerminalException;
import opencard.core.util.OpenCardPropertyLoadingException;

public class OpenCardService implements CTListener {
    SmartCard smartCard = null;
    private CardTerminal terminal = null;
    private int slotID = 0;

    OpenCardService() {
       try {
           SmartCard.start();
       } catch (OpenCardPropertyLoadingException | ClassNotFoundException | CardServiceException | CardTerminalException e) {
           e.printStackTrace();
           System.exit(-1);
       }
   }

    public void register()
    {
        EventGenerator.getGenerator().addCTListener(this);
        try {
            EventGenerator.getGenerator().createEventsForPresentCards(this);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void unregister() {
        EventGenerator.getGenerator().removeCTListener(this);
    }

    @Override
    public void cardInserted(CardTerminalEvent cardTerminalEvent) {
        System.out.println("Card inserted!");
        if (smartCard == null) {
            try {
                smartCard = SmartCard.getSmartCard(cardTerminalEvent);
                terminal = cardTerminalEvent.getCardTerminal();
                slotID = cardTerminalEvent.getSlotID();
            } catch (CardTerminalException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cardRemoved(CardTerminalEvent cardTerminalEvent) {
        System.out.println("Card removed");
        if ((cardTerminalEvent.getSlotID() == slotID) &&(cardTerminalEvent.getCardTerminal() == terminal)) {
            smartCard = null;
            terminal = null;
            slotID = 0;
        }
    }
}