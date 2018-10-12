
import java.util.*;
import java.util.Scanner;

public class Game {

    // Random state generated (used in Simulated Annealing and Genetic Algorithm)
    public static int[] generateRandomState(int n) {

        int[] newState = new int[n];

        //assign random value from 0-(n-1) to each column.
        for (int i = 0; i < newState.length; i++)
            newState[i] = (int) (Math.random() * newState.length);

        return newState;
    }

    // Number of attacking queen pairs.  (SA only)
    public static int computeCost(int[] arr) {
        int totalCost = 0;

        // Determine if queen is in same row or same diagonal.
        for (int i = 0; i < arr.length; i++)
            for (int j = i + 1; j < arr.length; j++)
                if (arr[i] == arr[j] || Math.abs(arr[i] - arr[j]) == j - i)
                    totalCost += 1;

        return totalCost;
    }

    public static void main(String[] args) {

        try {

            Scanner kb = new Scanner(System.in);
            boolean done = false;

            do {
                int SASolved = 0;
                int GASolved = 0;
                long SAAvg = 0;
                long GAAvg = 0;
                long SATotal = 0;
                long GATotal = 0;
                int generationSum = 0;
                int SAmovesSum = 0;


                System.out.println("N-Queens Solver");
                System.out.println("---------------");
                System.out.println("1. Enter number of runs.");
                System.out.println("2. Print a single solution.");
                System.out.println("3. Exit.");
                int choice = kb.nextInt();
                switch (choice){
                    case 1:
                        System.out.println("Please specify how many solutions to generate: ");
                        Scanner runs = new Scanner(System.in);
                        int numruns = runs.nextInt();

                        for (int i = 0; i < numruns; i++) {
                            SimulatedAnnealing s = new SimulatedAnnealing();

                            long startTime = System.nanoTime();
                            int[] answer1 = s.solve(21, 100000, 9900, 0.5);
                            long stopTime = System.nanoTime();
                            SAmovesSum += s.numberOfMoves;
                            long elapsed = stopTime - startTime;
                            if (answer1 != null) {
                                SASolved++;
                                SATotal += elapsed;
                            }


                            GeneticAlgorithm g = new GeneticAlgorithm();

                            long startTime2 = System.nanoTime();
                            int[] answer2 = g.solve(21, 50, 0.5, 30000);
                            System.out.println("Genetic Algorithm:");
                            System.out.println("------------------");
                            g.printArray(answer2);
                            //System.out.println(g.generationSum);
                            generationSum += g.generationSum;
                            long stopTime2 = System.nanoTime();
                            long elapsed2 = stopTime2 - startTime2;

                            if (answer2 != null) {
                                GASolved++;
                                GATotal += elapsed2;
                            }
                        }

                        GAAvg = GATotal / numruns;
                        SAAvg = SATotal / numruns;
                        int generationAvg = generationSum / numruns;

                        System.out.println("\nResults");
                        System.out.println("---------");
                        System.out.println("Simulated Annealing: ");
                        System.out.println("Total Solved: " + SASolved);
                        System.out.println("Avg Time Taken: " + SAAvg / 1000000 + " ms");
                        System.out.println("Avg States Generated: " + SAmovesSum/numruns + "\n");
                        System.out.println("Genetic Algorithm: ");
                        System.out.println("Total Solved: " + GASolved);
                        System.out.println("Avg Time Taken: " + GAAvg / 1000000 + " ms");
                        System.out.println("Solution generally found at generation: " + generationAvg + "\n");
                        break;

                    case 2:
                        SimulatedAnnealing s = new SimulatedAnnealing();

                        long startTime = System.nanoTime();
                        int[] answer1 = s.solve(21, 100000, 9900, 0.5);
                        long stopTime = System.nanoTime();
                        long elapsed = stopTime - startTime;


                        GeneticAlgorithm g = new GeneticAlgorithm();
                        int solvedGeneration = 0;
                        long startTime2 = System.nanoTime();
                        int[] answer2 = g.solve(21, 50, 0.5, 30000);
                        System.out.println("Genetic Algorithm:");
                        System.out.println("------------------");
                        g.printArray(answer2);
                        long stopTime2 = System.nanoTime();
                        long elapsed2 = stopTime2 - startTime2;


                        
                        System.out.println("\nResults");
                        System.out.println("---------");
                        System.out.println("Simulated Annealing: ");
                        System.out.println("Time Taken: " + elapsed / 1000000 + " ms");
                        System.out.println("States Generated: " + s.numberOfMoves + "\n");
                        System.out.println("Genetic Algorithm: ");
                        System.out.println("Time Taken: " + elapsed2 / 1000000 + " ms");
                        System.out.println("Solution found at generation: " + g.generationSum + "\n");
                        break;

                    case 3:
                        System.exit(0);
                }


            } while(!done);
        }
        catch(InputMismatchException e){
            System.out.println("Wrong choice, please try again.");
        }


        //System.out.println(computeCost(test));
    }

}

class SimulatedAnnealing {

    public static int numberOfMoves;

    public int[] solve(int n, int maxNumOfIterations, double temperature, double coolingFactor) {
        numberOfMoves = 0;
        int[] state = Game.generateRandomState(n);

        int costToBeat = Game.computeCost(state);

        // terminate when it reaches max num of iterations or problem is solved.
        for (int x = 0; x < maxNumOfIterations && costToBeat > 0; x++) {
            state = move(state, costToBeat, temperature);
            costToBeat = Game.computeCost(state);
            temperature = Math.max(temperature * coolingFactor, 0.01);
            numberOfMoves++;
        }

        if (costToBeat == 0){
            System.out.println("Simulated Annealing");
            System.out.println("-------------------");
            for (int i = 0; i<state.length;i++){
                System.out.print(state[i] + " ");
            }
            System.out.println();

            for (int i = 0; i<state.length;i++){
                for (int j = 0; j<state.length;j++){
                    if (state[i] == j){
                        System.out.print(" Q ");
                    }
                    else{
                        System.out.print(" - ");
                    }
                }
                System.out.println();
            }
            return state;

        }
        else{
            return null;
        }
    }

    //Following book pseudocode...
    private int[] move(int board[], int lowerCost, double temperature) {
        //get board size
        int size = board.length;

        //infinite loop until probability allows choice of worse state, or if cost of new state is better.
        while (true) {
            int colNum = (int) (Math.random() * size);
            int rowNum = (int) (Math.random() * size);
            int tmpRow = board[colNum];
            board[colNum] = rowNum;


            //if computed smaller cost is better than the cost to beat, then return the better state.
            int cost = Game.computeCost(board);
            if (cost < lowerCost)
                return board;

            //calculate delta E
            int dE = lowerCost - cost;

            //calculate probability of choosing worse state
            double acceptProb = Math.min(1, Math.exp(dE / temperature));

            if (Math.random() < acceptProb)
                return board;

            board[colNum] = tmpRow;
        }


    }

}

class GeneticAlgorithm{

    public static int generationSum;

    public GeneticAlgorithm(){
        generationSum = 0;
    }

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

        for (int i = 0; i<array.length;i++){
            for (int j = 0; j<array.length;j++){
                if (array[i] == j){
                    System.out.print(" Q ");
                }
                else{
                    System.out.print(" - ");
                }
            }
            System.out.println();
        }
    }

    public static int computeFitness(int[] array){


        int maxAttackingPairs = (array.length * (array.length - 1))/2;
        int numAttackingPairs = computeAttackingPairs(array);

        return maxAttackingPairs - numAttackingPairs;

    }

    public static int computeAttackingPairs(int[] array) {
        int cost = 0;

        for (int i = 0; i < array.length; i++)
            for (int j = i + 1; j < array.length; j++)
                if (array[i] == array[j] || Math.abs(array[i] - array[j]) == j - i)
                    cost += 1;

        return cost;
    }

    //Returns sorted population, no longer used.
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

        return sortedPopulation;


    }

    public static int[] solve(int boardSize, int populationSize, double mutationChance, int numGenerations){

        ArrayList<int[]> population = generatePopulation(boardSize, populationSize);
        //ArrayList<int[]> sorted = getSortedPopulation(population);
        int bestFit = (boardSize * (boardSize -1))/2;
        //if (computeFitness(sorted.get(0))== bestFit){
        //    return sorted.get(0);
        //}*/



        for (int i = 0; i<numGenerations;i++){

            double generationFitnessTotal = 0;

            ArrayList<int[]> matingPool = new ArrayList<>();

            for(int g = 0; g<population.size();g++){
                generationFitnessTotal += computeFitness(population.get(g));
            }

            for (int count = 0; count < population.size();count++){
                double freq = computeFitness(population.get(count))/generationFitnessTotal;

                double n = Math.floor(freq*100);


                for(int j = 0; j<n;j++){
                    matingPool.add(population.get(count));
                }
            }


            ArrayList<int[]> newPopulation = new ArrayList<>();

            for (int pop = 0; pop<populationSize;pop++){
                int chooseRandom1 = (int) (Math.random() * matingPool.size());
                int chooseRandom2 = (int) (Math.random() * matingPool.size());

                int[] parent1 = matingPool.get(chooseRandom1);
                int[] parent2 = matingPool.get(chooseRandom2);

                //crossover
                ArrayList<int[]> crossChildren = crossover(parent1, parent2);

                //check if children are solutions
                if(computeFitness(crossChildren.get(0)) == bestFit){
                    generationSum += i;
                    return crossChildren.get(0);

                }
                if(computeFitness(crossChildren.get(1)) == bestFit){
                    generationSum += i;
                    return crossChildren.get(1);
                }

                int[] possibleMutatedChild1 = mutate(crossChildren.get(0), mutationChance);
                int[] possibleMutatedChild2 = mutate(crossChildren.get(1), mutationChance);

                if(computeFitness(possibleMutatedChild1) == bestFit){
                    generationSum += i;
                    return crossChildren.get(0);
                }
                if(computeFitness(possibleMutatedChild2) == bestFit){
                    generationSum += i;
                    return crossChildren.get(1);
                }

                newPopulation.add(possibleMutatedChild1);
                newPopulation.add(possibleMutatedChild2);
            }

            population = newPopulation;


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

    public static int[] mutate(int[] array, double mutationProb){

        boolean done = false;
        if(mutationProb >= Math.random()){
            while(!done){
                int randomNumber = (int) (Math.random()*array.length);

                if(!Arrays.asList(array).contains(randomNumber)){
                    array[(int)(Math.random()*array.length)] = randomNumber;
                    done= true;
                }
            }

        }

        return array;
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


