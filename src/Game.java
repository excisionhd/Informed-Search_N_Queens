import javafx.collections.transformation.SortedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Game {

    private int[] queens;
    private int numQueens;

    public Game(int numQueens){
        queens = new int[numQueens];
        this.numQueens = numQueens;
    }

    // Generate state that all queens have row # 0
    public static int[] generateAllOneState(int n) {

        return new int[n];
    }

    // Randomizes state
    public static int[] randomizeState(int[] r) {

        for (int i = 0; i < r.length; i++)
            r[i] = (int) (Math.random() * r.length);

        return r;
    }

    // generates random initial state
    public static int[] generateRandomState(int n) {

        return randomizeState(generateAllOneState(n));
    }

    // Returns heuristic cost
    public static int computeCost(int[] r) {
        int cost = 0;

        // increment cost if two queens are in same row or in same diagonal.
        for (int i = 0; i < r.length; i++)
            for (int j = i + 1; j < r.length; j++)
                if (r[i] == r[j] || Math.abs(r[i] - r[j]) == j - i)
                    cost += 1;

        return cost;
    }

    public static void main(String[] args) {
        SimulatedAnnealing s = new SimulatedAnnealing();
        s.solve(4,100000,9900,0.5);
    }

}

class SimulatedAnnealing {

    public int[] solve(int n, int maxNumOfIterations, double temperature, double coolingFactor) {
        int[] r = Game.generateRandomState(n);

        int costToBeat = Game.computeCost(r);

        // terminate when it reaches max num of iterations or problem is solved.
        for (int x = 0; x < maxNumOfIterations && costToBeat > 0; x++) {
            r = makeMove(r, costToBeat, temperature);
            costToBeat = Game.computeCost(r);
            temperature = Math.max(temperature * coolingFactor, 0.01);
        }

        if (costToBeat == 0){
            for (int i = 0; i<r.length;i++){
                System.out.print(r[i] + " ");
            }
            System.out.println();

            for (int i = 0; i<r.length;i++){
                for (int j = 0; j<r.length;j++){
                    if (r[i] == j){
                        System.out.print(" Q ");
                    }
                    else{
                        System.out.print(" - ");
                    }
                }
                System.out.println();
            }
            return r;

        }
        else{
            return null;
        }
    }

    private int[] makeMove(int r[], int costToBeat, double temp) {
        int n = r.length;

        while (true) {
            int nCol = (int) (Math.random() * n);
            int nRow = (int) (Math.random() * n);
            int tmpRow = r[nCol];
            r[nCol] = nRow;


            //gradient descent, return lower cost successor, condition will skip if cost is greater
            int cost = Game.computeCost(r);
            if (cost < costToBeat)
                return r;

            int dE = costToBeat - cost;
            double acceptProb = Math.min(1, Math.exp(dE / temp));

            if (Math.random() < acceptProb)
                return r;

            r[nCol] = tmpRow;
        }


    }

}


