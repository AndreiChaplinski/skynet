package com.internship.skynet;

import com.internship.skynet.factory.NeutralFactory;
import com.internship.skynet.model.Part;
import com.internship.skynet.model.faction.WednesdayFaction;
import com.internship.skynet.model.faction.WorldFaction;
import com.internship.skynet.model.faction.abstractfaction.Faction;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

public class Simulator {
    private static final int SIMULATION_DAYS = 100;
    private static final int FACTORY_SUPPLY_CAPACITY = 50;

    private final BlockingQueue<Part> factorySupply;
    private final NeutralFactory factory;
    private final WorldFaction worldFaction;
    private final WednesdayFaction wednesdayFaction;
    private final List<Faction> factions;

    private ExecutorService factionExecutor;


    public Simulator() {
        this.factorySupply = new ArrayBlockingQueue<>(FACTORY_SUPPLY_CAPACITY);
        this.factory = new NeutralFactory(factorySupply);
        this.worldFaction = new WorldFaction(factorySupply);
        this.wednesdayFaction = new WednesdayFaction(factorySupply);
        this.factions = Arrays.asList(worldFaction, wednesdayFaction);

        this.factionExecutor = Executors.newFixedThreadPool(factions.size());
    }


    public void startSimulation() {
        System.out.println("Starting Robot War simulation for " + SIMULATION_DAYS + " days.");


        for (int day = 1; day <= SIMULATION_DAYS; day++) {
            final int currentDay = day;

            System.out.println("\n--- Day " + currentDay + " ---");

            factory.producePartsForDay();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Simulator interrupted during day phase pause.");
                break;
            }

            System.out.println("--- Night " + currentDay + " ---");
            CountDownLatch latch = new CountDownLatch(factions.size());

            factions.forEach(faction -> {
                factionExecutor.submit(() -> {
                    faction.collectParts();
                    latch.countDown();
                });
            });

            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Simulator interrupted while waiting for factions during night.");
                break;
            }
            System.out.println("Night " + currentDay + " completed.");

            printDailyStats(currentDay);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Simulator interrupted between days.");
                break;
            }
        }

        shutdownSimulation();
        System.out.println("\n--- Simulation Finished ---");
        printFinalResults();
    }

    private void printDailyStats(int day) {
        System.out.println("\nStats at the end of Day " + day + ":");
        System.out.println("  Parts remaining at factory: " + factorySupply.size() + " of " + FACTORY_SUPPLY_CAPACITY);

        factions.forEach(faction -> {
            System.out.println("  " + faction.getName() + ":");
            System.out.println("    Robots assembled: " + faction.getAssembledRobotsCount());
            System.out.println("    Current inventory: " + faction.getCurrentInventory());
        });
        System.out.println("----------------------------------");
    }

    private void printFinalResults() {
        System.out.println("\n--- Final Results after " + SIMULATION_DAYS + " days ---");
        Faction winner = null;
        int maxRobots = -1;

        for (Faction faction : factions) {
            int robots = faction.getAssembledRobotsCount();
            System.out.println(faction.getName() + " assembled " + robots + " robots.");
            if (robots > maxRobots) {
                maxRobots = robots;
                winner = faction;
            } else if (robots == maxRobots) {
            }
        }

        if (winner != null) {
            System.out.println("\nWinner: " + winner.getName() + " with a total of " + winner.getAssembledRobotsCount() + " robots!");
        } else {
            System.out.println("\nNo robots were assembled by any faction.");
        }
        System.out.println("Total parts produced by factory: " + factory.getTotalPartsProduced());
    }

    public void shutdownSimulation() {
        System.out.println("Shutting down simulation...");


        factionExecutor.shutdownNow();

        try {
            if (!factionExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Faction executor did not terminate in time, forcing shutdown.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Shutdown process interrupted.");
        }
        System.out.println("All executors have been shut down.");
    }
}