package com.internship.skynet.model.faction;

import com.internship.skynet.model.Part;
import com.internship.skynet.model.faction.abstractfaction.Faction;

import java.util.concurrent.BlockingQueue;

public class WorldFaction extends Faction {
    public WorldFaction(BlockingQueue<Part> factorySupply) {
        super("World", factorySupply);
    }
}