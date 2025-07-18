package com.internship.skynet.model.faction;

import com.internship.skynet.model.Part;
import com.internship.skynet.model.faction.abstractfaction.Faction;

import java.util.concurrent.BlockingQueue;

public class WednesdayFaction extends Faction {
    public WednesdayFaction(BlockingQueue<Part> factorySupply) {
        super("Wednesday", factorySupply);
    }
}