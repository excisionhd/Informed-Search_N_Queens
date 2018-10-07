import com.sun.tools.javah.Gen;
import javafx.collections.transformation.SortedList;

import java.util.*;

public class Game {

    // generates random initial state
    public static int[] generateRandomState(int n) {

        int[] newState = new int[n];

        for (int i = 0; i < newState.length; i++)
            newState[i] = (int) (Math.random() * newState.length);

        return newState;
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
        s.solve(21,100000,9900,0.5);


        int[] test = {16,12,8,18,2,7,1,4,20,10,17,14,3,13,19,5,15,0,6,11,9};

        GeneticAlgorithm g = new GeneticAlgorithm();
        g.solve(21,100,0.1,100, 3);
        //System.out.println(computeCost(test));
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

class GeneticAlgorithm{

    public static ArrayList<int[]> generatePopulation(int boardSize, int pSize){

        ArrayList<int[]> p = new ArrayList<>();
        for (int i = 0; i<pSize;i++){
            int[] board;
            board = Game.generateRandomState(boardSize);
            p.add(board);
            //printArray(board);
            //System.out.print(Game.computeCost(board) + "\n");
            //printArray(board);
        }

        return  p;

    }

    public static void printArray(int[] array){
        for (int i = 0;i<array.length;i++){
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }

    public static int computeFitness(int[] array){


        int maxAttackingPairs = (array.length * (array.length - 1))/2;
        int numAttackingPairs = computeAttackingPairs(array);

        return maxAttackingPairs - numAttackingPairs;

    }

    public static int computeAttackingPairs(int[] r) {
        int h = 0;

        // increment cost if two queens are in same row or in same diagonal.
        for (int i = 0; i < r.length; i++)
            for (int j = i + 1; j < r.length; j++)
                if (r[i] == r[j] || Math.abs(r[i] - r[j]) == j - i)
                    h += 1;

        return h;
    }

    public static ArrayList<int[]> getSortedPopulation(ArrayList<int[]> population){
        PriorityQueue<int[]> sortedPop = new PriorityQueue<>(population.size(), new SortByFitness());

        for (int i = 0; i<population.size();i++){
            sortedPop.add(population.get(i));
        }

        ArrayList<int[]> sortedPopulation = new ArrayList<>();

        for (int j = 0; j<sortedPop.size();j++){
            int[] curr;
            curr = sortedPop.poll();
            sortedPopulation.add(curr);
        }


        for (int i = 0; i<sortedPopulation.size();i++){
            //printArray(sortedPopulation.get(i));
            //System.out.println(computeFitness(sortedPopulation.get(i)));
        }

        return sortedPopulation;


    }

    public static int[] solve(int boardSize, int populationSize, double mutationChance, int numGenerations, int kWay){
        ArrayList<int[]> population = generatePopulation(boardSize, populationSize);
        ArrayList<int[]> newPopulation = new ArrayList<>();
        //ArrayList<int[]> sorted = getSortedPopulation(population);
        int bestFit = (boardSize * (boardSize -1))/2;

        for (int i = 0;i <population.size();i++){
            if (computeFitness(population.get(i))== bestFit){
                return population.get(i);
            }
        }

        for (int i = 0; i<numGenerations;i++){

            newPopulation.clear();
            for (int pop = 0; pop<populationSize;pop++){
                ArrayList<int[]> selectedParents = new ArrayList<>();

                //select 3 random from the population
                for (int j = 0; j<kWay;j++){
                    int randomChild = (int) (Math.random() * populationSize);
                    selectedParents.add(population.get(randomChild));
                }

                //sort the parents and get top 2 fitness
                ArrayList<int[]> sortedParents = getSortedPopulation(selectedParents);
                int[] parent1, parent2;
                parent1 = sortedParents.get(0);
                parent2 = sortedParents.get(1);

                //crossover
                ArrayList<int[]> crossChildren = crossover(parent1, parent2);

                newPopulation.add(crossChildren.get(0));
                newPopulation.add(crossChildren.get(1));

            }

        }


        return null;
    }

    public static ArrayList<int[]> crossover(int[] a, int[] b){
        int size = a.length;
        int crossPos = (int) (Math.random() * size);
        int[] child1 = new int[size];
        int[] child2 = new int[size];
        ArrayList<int[]> children = new ArrayList<>();

        System.arraycopy(a, 0, child1, 0, crossPos);
        System.arraycopy(b, crossPos, child1, crossPos, size-crossPos);
        System.arraycopy(b, 0, child2, 0, crossPos);
        System.arraycopy(a, crossPos, child2, crossPos, size-crossPos);

        children.add(child1);
        children.add(child2);

        return children;
    }

}

class SortByFitness implements Comparator<int[]>{
    public int compare(int[] a, int[] b){
        if (GeneticAlgorithm.computeFitness(a) < GeneticAlgorithm.computeFitness(b)){
            return 1;

        }
        else if (GeneticAlgorithm.computeFitness(a) > GeneticAlgorithm.computeFitness(b)){
            return -1;
        }
        return 0;
    }
}


