import java.io.FileWriter;

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
    int maxNumberOfIterations = 200;

    // comes from cooperate, cooperate
    int bestScorePossible = 7;

    // number of times to repeat suite of games
    int suiteIterations = 10;

    // weights for the different game types
    int randomWeight = 1;
    int cooperateWeight = 1;
    int defectWeight = 1;
    int titForTatWeight = 10;
    int bestFoundWeight = 1;

    PrisonersTournament() { name = "Iterated Prisoner's Dilemma"; }
//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

    public void doRawFitness(Chromo X){

        X.rawFitness = 0;

        // use chromo as strategy
        StrategyMixed player1 = new StrategyMixed(iterationsRemembered);
        player1.setStrategy(X.chromo);

        //Strategy player1 = new StrategyTitForTat();

        // tournament suite
        for(int i = 0; i < suiteIterations; i++) {
            X.rawFitness += randomWeight * play(player1, new StrategyRandom());
            X.rawFitness += titForTatWeight * play(player1, new StrategyTitForTat());
            X.rawFitness += cooperateWeight * play(player1, new StrategyAlwaysCooperate());
            X.rawFitness += defectWeight * play(player1, new StrategyAlwaysDefect());

            // and play best found so far
            StrategyMixed player2 = new StrategyMixed(iterationsRemembered);
            player2.setStrategy(Search.bestOfRunChromo.chromo);
            X.rawFitness += bestFoundWeight * play(player1, player2);
        }
    }

    /*
    Number of iterations to play is chosen randomly.
    Returns an integer less than 100, giving the ratio of the points scored over the
    maximum number of number of points possible.
     */
    private int play(Strategy p1, Strategy p2){
        IteratedPD ipd = new IteratedPD(p1, p2);
        int iterations = Search.r.nextInt(maxNumberOfIterations) + 1;
        //int iterations = 100;
        ipd.runSteps(iterations);
        return (int) (100.00 * ((double)ipd.player1Score() / (double)(iterations * bestScorePossible)));
    }

}
