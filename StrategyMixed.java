import java.util.Arrays;
import java.util.Random;

/**
 * Created by D on 3/7/2016.
 * Uses history of game to index into an array of doubles which represent the probability of cooperation.
 * Plays tit-for-tit if it doesn't have enough history yet.
 * The array of doubles is what the GA has to find.
 */
public class StrategyMixed extends Strategy{

    int currentIteration = 0;
    int numberOfStrategyIndices;
    int[] history;
    double[] strategy;

    // added for analysis
    private int[] strategyIndexFrequency;

    public StrategyMixed(int numberOfIterationsRemembered){
        name = "Mixed Strategy";

        // "* 2" because each iterations has a move for player1 and player2
        history = new int[numberOfIterationsRemembered * 2];
        numberOfStrategyIndices = (int)Math.pow(2,history.length);
        // 0 = defect, 1 = cooperate
        opponentLastMove = 1;

        // TODO: replace with GA found strategy
        //initializeWithRandomMixedStrategy();

        strategyIndexFrequency = new int[numberOfStrategyIndices];

        //System.out.println("Strategy: " + Arrays.toString(strategy));
        //System.out.println("History: " + Arrays.toString(history));
    }


    // This is what the GA has to find, let the GA call this function to set strategy
    public void setStrategy(double[] s){

        // each entry in the history is a 0 or 1, making the number of possible histories 2^|h|
        // we should have a strategy for history
        if(s.length != numberOfStrategyIndices){
            System.out.println("Invalid strategy based on history length");
            System.out.println("History length: " + history.length);
            System.out.println("Strategy length: " + s.length);
            return;
        }
        strategy = s;
    }

    public int nextMove(){

        //System.out.println("\nIter: "+currentIteration);
        //System.out.println("History: "+ Arrays.toString(history));

        // play tit for tat if we don't have enough history
        if(currentIteration*2 < history.length){

            currentIteration++;
            return opponentLastMove;

        } else {
            // get strategy index from current history of game
            int index = getIndex();
            //System.out.println("index: "+ index);

            // added for analysis
            strategyIndexFrequency[index]++;

            // draw random double
            Random rand = new Random();
            double randomDraw = rand.nextDouble();

            // if lower or equal to cooperation strategy probability, then cooperate
            int move = (randomDraw <= strategy[index]) ? 1 : 0;

            //System.out.println("randomDraw: " + randomDraw + " Pr: " + strategy[index]);
            currentIteration++;
            return move;
        }
    }

    /*
    Converts history array to a number.
    Works as follows, from the starting index, the values are read from left to right
    and put into a binary number from right to left. Wrap around as needed. For example

    startIdx = 4,
                           |<- start here
    history = [0, 1, 0, 0, 1, 1]  becomes in binary 001011
    which is 11 in base 10
     */
    private int getIndex(){
        int index = 0;
        int startIdx = (currentIteration*2  % history.length);
        //System.out.println("History start idx: "+ startIdx);
        for(int i = 0; i < history.length; i++){
            index += history[(i + startIdx) % history.length] * (int)Math.pow(2,i);
        }
        return index;
    }

    @Override
    public void saveOpponentMove(int move)  {
        opponentLastMove = move;
        // this goofy indexing(the "- 1") is because the currentIteration is incremented immediately after move
        // but before move gets saved
        history[(currentIteration*2 - 1) % history.length] = move;
    }

    @Override
    public void saveMyMove(int move)  {
        myLastMove = move;
        // this goofy indexing(the " - 2") is because the currentIteration is incremented immediately after move
        // but before move gets saved
        history[(currentIteration*2 - 2) % history.length] = move;
    }

    public void initializeWithRandomMixedStrategy(){
        strategy = new double[numberOfStrategyIndices];
        Random rand = new Random();

        for(int i = 0; i < strategy.length; i++){
            strategy[i] = rand.nextDouble();
        }
    }

    public int getModeStrategyIndex(){
        int topIndex = 0;
        for(int i = 0; i < numberOfStrategyIndices; i++){
            if(strategyIndexFrequency[i] > strategyIndexFrequency[topIndex]){
                topIndex = i;
            }
        }
        return topIndex;
    }

    public int[] getStrategyIndexFrequencies(){
        return Arrays.copyOf(strategyIndexFrequency, strategyIndexFrequency.length);
    }
}
