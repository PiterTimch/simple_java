package org.example;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static final List<Attempt> attempts = new ArrayList<>();
    static final Random rnd = new Random();

    public static void main(String[] args) {
        while (true) {
            clearConsole();
            System.out.println("\u001B[32m=== Multiplication Quiz ===\u001B[0m");
            System.out.println("1. Easy (1-5)");
            System.out.println("2. Medium (1-9)");
            System.out.println("3. Hard (1-12)");
            System.out.println("4. View Statistics");
            System.out.println("0. Exit");
            System.out.print("Choose level: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> { runSession(1, 5); clearConsole(); }
                case "2" -> { runSession(1, 9); clearConsole(); }
                case "3" -> { runSession(1, 12); clearConsole(); }
                case "4" -> { showStats(); clearConsole(); }
                case "0" -> { clearConsole(); System.out.println("Goodbye."); return; }
                default -> { System.out.println("Invalid choice"); sleep(800); clearConsole(); }
            }
        }
    }

    static void runSession(int min, int max) {
        int correct = 0;
        int wrong = 0;
        List<Long> times = new ArrayList<>();
        for (;;) {
            clearConsole();
            int a = rand(min, max);
            int b = rand(min, max);
            System.out.printf("\u001B[34m%d * %d = ? (enter 0 to quit)\u001B[0m\n", a, b);
            System.out.print("Answer: ");
            long start = System.currentTimeMillis();
            Integer ans = timedInputWithCountdown(5);
            long elapsed = System.currentTimeMillis() - start;
            times.add(elapsed);
            if (ans == null) {
                System.out.println("\nTime's up!");
                wrong++;
                sleep(1200);
                continue;
            }
            if (ans == 0) break;
            if (ans == a * b) {
                correct++;
                System.out.println("Correct!");
            } else {
                wrong++;
                System.out.println("Wrong!");
            }
            sleep(700);
        }
        recordAttempt(correct, wrong, times);
    }

    static Integer timedInputWithCountdown(int seconds) {
        ExecutorService inputExec = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        Future<String> lineFuture = inputExec.submit(() -> scanner.nextLine());
        ScheduledExecutorService ticker = Executors.newSingleThreadScheduledExecutor();
        final int[] left = {seconds};
        Runnable r = () -> {
            System.out.printf("\r\u001B[36mTime left: %2ds\u001B[0m ", left[0]--);
            System.out.flush();
        };
        ticker.scheduleAtFixedRate(r, 0, 1, TimeUnit.SECONDS);
        try {
            String line = lineFuture.get(seconds, TimeUnit.SECONDS);
            ticker.shutdownNow();
            inputExec.shutdownNow();
            System.out.print("\r");
            try { return Integer.parseInt(line.trim()); } catch (Exception e) { return -1; }
        } catch (TimeoutException e) {
            lineFuture.cancel(true);
            ticker.shutdownNow();
            inputExec.shutdownNow();
            System.out.print("\r");
            System.out.println();
            resetScanner();
            return null;
        } catch (Exception e) {
            ticker.shutdownNow();
            inputExec.shutdownNow();
            System.out.print("\r");
            return -1;
        }
    }

    static void resetScanner() {
        try {
            if (System.in.available() > 0)
                System.in.skip(System.in.available());
        } catch (IOException ignored) {}
        scanner = new Scanner(System.in);
    }

    static void recordAttempt(int correct, int wrong, List<Long> times) {
        long avg = times.stream().mapToLong(Long::longValue).sum() / Math.max(1, times.size());
        int total = correct + wrong;
        int pct = total == 0 ? 0 : (int) Math.round(correct * 100.0 / total);
        attempts.add(new Attempt(Instant.now().toString(), correct, wrong, avg, pct));
        while (attempts.size() > 5) attempts.remove(0);
    }

    static void showStats() {
        clearConsole();
        System.out.println("\u001B[33m=== Last Attempts ===\u001B[0m");
        if (attempts.isEmpty()) {
            System.out.println("No attempts yet.");
            promptEnter();
            return;
        }
        for (int i = 0; i < attempts.size(); i++) {
            Attempt a = attempts.get(i);
            System.out.printf("%d) %s | Correct: %d | Wrong: %d | %%: %d | Avg: %dms\n",
                    i + 1, a.timeStamp(), a.correct(), a.wrong(), a.percentage(), a.avgTimeMs());
        }
        Attempt best = attempts.stream().max(Comparator.comparingInt(Attempt::percentage)).orElse(null);
        if (best != null) {
            System.out.println("\nBest: " + best.percentage() + "%  Avg time: " + best.avgTimeMs() + "ms");
        }
        double totalCorrect = attempts.stream().mapToInt(Attempt::correct).sum();
        double totalQ = attempts.stream().mapToInt(a -> a.correct + a.wrong).sum();
        double overallPct = totalQ == 0 ? 0 : Math.round((totalCorrect / totalQ) * 100.0);
        long overallAvg = attempts.stream().mapToLong(Attempt::avgTimeMs).sum() / Math.max(1, attempts.size());
        System.out.println("\nOverall correct: " + (int) totalCorrect + " | Overall %: " + (int) overallPct + " | Avg time: " + overallAvg + "ms");
        promptEnter();
    }

    static void promptEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    static int rand(int min, int max) {
        return rnd.nextInt(max - min + 1) + min;
    }

    static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    record Attempt(String timeStamp, int correct, int wrong, long avgTimeMs, int percentage) {}
}
