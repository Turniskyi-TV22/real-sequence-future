package org.example;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        CopyOnWriteArrayList seaquence = new CopyOnWriteArrayList();


        int n = 20;

        ExecutorService executor = Executors.newFixedThreadPool(3);

        CompletableFuture<CopyOnWriteArrayList<Double>> sequenceFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Generating sequence...");
            Random random = new Random();
            CopyOnWriteArrayList<Double> sequence = new CopyOnWriteArrayList<>();
            for (int i = 0; i < n; i++) {
                sequence.add(random.nextDouble() * 100);
            }
            System.out.println("Generated sequence is ended");
            return sequence;
        }, executor);

        CompletableFuture<Void> displaySequenceFuture = sequenceFuture.thenAcceptAsync(sequence -> {
            for (int i = 0; i < sequence.size(); i++) {
                System.out.println("Generated #" + i + ": " + sequence.get(i));
            }
        }, executor);

        CompletableFuture<Double> resultFuture = sequenceFuture.thenApplyAsync(sequence -> {
            double minOdd = Double.MAX_VALUE;
            double maxEven = Double.MIN_VALUE;
            for (int i = 0; i < sequence.size(); i++) {
                if (i % 2 == 0) {
                    maxEven = Math.max(maxEven, sequence.get(i));
                } else {
                    minOdd = Math.min(minOdd, sequence.get(i));
                }
            }
            System.out.println("Min (odd indices): " + minOdd);
            System.out.println("Max (even indices): " + maxEven);
            return minOdd + maxEven;
        }, executor);

        CompletableFuture<Void> displayResultFuture = resultFuture.thenAcceptAsync(result -> {
            System.out.println("Result (min(odd) + max(even)): " + result);
        }, executor);

        CompletableFuture.allOf(displaySequenceFuture, displayResultFuture).thenRunAsync(() -> {
            System.out.println("All tasks completed!");
            executor.shutdown();
        }, executor);
    }
}