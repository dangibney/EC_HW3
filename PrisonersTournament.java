import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

/**
 * Created by J on 3/8/2016.
 */
public class PrisonersTournament extends FitnessFunction{
    /*******************************************************************************
     *                                MEMBER METHODS                                *
     *******************************************************************************/

    // if you change this, change the gene size in the param file to match,
    // gene size = 2 ^ (iterationsRemembered * 2)
    int iterationsRemembered = 4;

    // iterations in a game
    int maxNumberOfIterations = 150;

    // comes from cooperate, cooperate
    int bestScorePossible = 7;
    int bestCombinedScorePossible = 10;

    // number of times to repeat suite of games
    int suiteIterations = 1;

    // weights for the different game types
    int randomWeight = 1;
    int cooperateWeight = 1;
    int defectWeight = 1;
    int titForTatWeight = 1;
    int bestFoundWeight = 1;

    PrisonersTournament() { name = "Iterated Prisoner's Dilemma"; }
//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

    public void doRawFitness(Chromo X){

        X.rawFitness = 0;

        // use chromo as strategy
        StrategyMixed player1 = new StrategyMixed(iterationsRemembered);
        player1.setStrategy(X.chromo);

        // tournament suite - modify this for different tests
        for(int i = 0; i < suiteIterations; i++) {

            double sum = 0;
            sum += randomWeight * play(player1, new StrategyRandom())[0];
            sum += titForTatWeight * play(player1, new StrategyTitForTat())[0];
            sum += titForTatWeight * play(player1, new StrategyTitForTwoTats())[0];
            sum += cooperateWeight * play(player1, new StrategyAlwaysCooperate())[0];
            sum += defectWeight * play(player1, new StrategyAlwaysDefect())[0];
            X.rawFitness += sum /(randomWeight + titForTatWeight + 2*titForTatWeight + cooperateWeight + defectWeight + (double)Parameters.popSize);


            // and play against everyone in the population


            double individualSum = 0;
            double combinedSum = 0;
            for(Chromo c : Search.member){
                StrategyMixed player3 = new StrategyMixed(iterationsRemembered);
                player3.setStrategy(c.chromo);
                double[] results = play(player1, player3);
                individualSum += results[0];
                combinedSum += results[1];
            }
            //X.rawFitness += individualSum / (double)Parameters.popSize;
            X.rawFitness += individualSum / (randomWeight + titForTatWeight + 2*titForTatWeight + cooperateWeight + defectWeight + (double)Parameters.popSize);
            X.combinedGameRawFitness += combinedSum / (double)Parameters.popSize;


            // Use this print statement to analyze the most frequently used(mode) strategy index.
            //System.out.println("suite iteration: " + i + " player1 mode strategy index: " + player1.getModeStrategyIndex());

            //
        }

        // average across suite iteration
        X.rawFitness = X.rawFitness / (double)suiteIterations;
        X.combinedGameRawFitness = X.combinedGameRawFitness / (double)suiteIterations;


        // added to record index frequencies for analysis
        int[] indexFreqs = player1.getStrategyIndexFrequencies();
        for(int i = 0; i < indexFreqs.length; i++){
            Search.indexFreq[Search.G][i] += indexFreqs[i];
        }

    }

    /*
    Number of iterations to play is chosen randomly.
    Returns an integer less than 100, giving the ratio of the points scored over the
    maximum number of number of points possible.
     */
    private double[] play(Strategy p1, Strategy p2){
        IteratedPD ipd = new IteratedPD(p1, p2);
        int iterations = Search.r.nextInt(maxNumberOfIterations) + 1;

        ipd.runSteps(iterations);
        double[] results = new double[2];
        results[0] = ipd.player1Score() / (double)iterations;
        results[1] = (ipd.player1Score() + ipd.player2Score()) / (double)iterations;
        return results;
    }

}
