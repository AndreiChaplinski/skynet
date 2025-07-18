package com.internship.skynet.model.faction.abstractfaction;

import com.internship.skynet.model.Part;
import com.internship.skynet.model.PartType;
import com.internship.skynet.model.Robot;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Faction {
    protected static final int MAX_PARTS_TO_CARRY = 5;
    protected final String name;
    protected final BlockingQueue<Part> factorySupply;
    protected final Map<PartType, AtomicInteger> partsInventory;
    protected final List<Robot> assembledRobots;

    public Faction(String name, BlockingQueue<Part> factorySupply) {
        this.name = name;
        this.factorySupply = factorySupply;
        this.partsInventory = new EnumMap<>(PartType.class);
        for (PartType type : PartType.values()) {
            partsInventory.put(type, new AtomicInteger(0));
        }
        this.assembledRobots = new ArrayList<>();
    }


    public void collectParts() {
        System.out.println(name + " faction is going to collect parts. Factory supply size: " + factorySupply.size() + ". Max to carry: " + MAX_PARTS_TO_CARRY);
        int collectedThisNight = 0;
        while (collectedThisNight < MAX_PARTS_TO_CARRY) {
            Part part = factorySupply.poll();

            if (part != null) {
                partsInventory.get(part.type()).incrementAndGet();
                collectedThisNight++;
                System.out.println(name + " collected a " + part.type() + ". Total collected this night: " + collectedThisNight);
            } else {
                System.out.println(name + " found no more parts at factory (poll() returned null). Collected " + collectedThisNight + " this night so far.");
                break;
            }
        }
        System.out.println(name + " finished collecting for the night. Collected " + collectedThisNight + " parts. (New inventory: " + getCurrentInventory() + ")");
        assembleRobots();
    }


    protected void assembleRobots() {
        System.out.println(name + " faction attempting to assemble robots... Current inventory: " + getCurrentInventory());
        boolean assembledAny = false;
        while (canAssembleRobot()) {
            for (PartType type : PartType.values()) {
                partsInventory.get(type).decrementAndGet();
            }
            assembledRobots.add(new Robot());
            assembledAny = true;
            System.out.println(name + " assembled a new robot! Total robots: " + assembledRobots.size());
        }
        if (!assembledAny) {
            System.out.println(name + " could not assemble any robots this night.");
        }
    }


    protected boolean canAssembleRobot() {
        for (PartType type : PartType.values()) {
            if (partsInventory.get(type).get() == 0) {
                System.out.println(name + " cannot assemble: missing " + type);
                return false;
            }
        }
        return true;
    }

    public int getAssembledRobotsCount() {
        return assembledRobots.size();
    }

    public String getName() {
        return name;
    }


    public Map<PartType, Integer> getCurrentInventory() {
        Map<PartType, Integer> current = new EnumMap<>(PartType.class);
        partsInventory.forEach((type, count) -> current.put(type, count.get()));
        return current;
    }
}