package org.example;

import java.util.Random;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        One();
        // boolean, short, double, float, char, long, String
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введи розмір масиву: ");
        int n = Integer.parseInt(scanner.nextLine());

        int [] array = new int[n];

        for (int i = 0; i < n; i++) {
            array[i] = GetRandom(0, 100);
        }

        for (var item : array) {
            System.out.print(item + "\t");
        }

    }

    public static void One() {
        int age;

        System.out.println("Слава Україні");
        System.out.println("Скіки років:");

        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();

        age = Integer.parseInt(str);

        System.out.println("Вам зараз " + str);
    }

    public static int GetRandom(int min, int max) {
        Random random = new Random();

        return random.nextInt(max - min + 1) + min;
    }
}