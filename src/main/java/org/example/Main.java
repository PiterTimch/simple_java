package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        int answer = 0;
        Scanner s = new Scanner(System.in);

        Map<String, Integer> stats = new HashMap<String, Integer>();

        stats.put("Correct", 0);
        stats.put("Wrong", 0);

        do {
             int a = GenerateNumber(1, 9);
             int b = GenerateNumber(1, 9);

            System.out.println(a + " * " + b + " = ?");

             answer = Integer.parseInt(s.nextLine());

            if (IsCorrectAnswer(answer, a, b)) {
                stats.put("Correct", stats.get("Correct") + 1);
            } else if (answer != 0) {
                stats.put("Wrong", stats.get("Wrong") + 1);
            }

        } while (answer != 0);

        System.out.println("Correct: " + stats.get("Correct"));
        System.out.println("Wrong: " + stats.get("Wrong"));

    }

    public static int GenerateNumber(int min, int max) {
        Random r = new Random();

        return r.nextInt(max - min + 1) + min;
    }

    public static boolean IsCorrectAnswer(int userInput, int a, int b) {
        return userInput == a * b;
    }
}