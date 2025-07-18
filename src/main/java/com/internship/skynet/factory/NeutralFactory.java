package com.internship.skynet.factory;

import com.internship.skynet.model.Part;
import com.internship.skynet.model.PartType;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class NeutralFactory {

    private static final int MAX_PARTS_PER_DAY = 10;
    private final BlockingQueue<Part> partSupply;
    private int totalPartsProduced = 0;

    public NeutralFactory(BlockingQueue<Part> partSupply) {
        this.partSupply = partSupply;
        System.out.println("NeutralFactory initialized.");
    }

    public void producePartsForDay() {
        int partsToProduce = ThreadLocalRandom.current().nextInt(1, MAX_PARTS_PER_DAY + 1);
        System.out.println("NeutralFactory producing " + partsToProduce + " parts...");

        for (int i = 0; i < partsToProduce; i++) {
            PartType type = PartType.getRandomPartType();
            Part newPart = new Part(type);

            try {
                partSupply.put(newPart);
                totalPartsProduced++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("NeutralFactory production for day interrupted.");
                return;
            }
        }
        System.out.println("NeutralFactory finished day's production. Parts on stock: " + partSupply.size() + " parts.");
    }

    public int getPartsCountInSupply() {
        return partSupply.size();
    }

    public int getTotalPartsProduced() {
        return totalPartsProduced;
    }
}