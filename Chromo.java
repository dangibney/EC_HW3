import java.util.Arrays;

/**
 * Created by J on 3/8/2016.
 */
public class Chromo {
    /*******************************************************************************
     *                            INSTANCE VARIABLES                                *
     *******************************************************************************/

    public double[] chromo;
    public double rawFitness;
    public double sclFitness;
    public double proFitness;

    // to store combined score of both players in game
    public double combinedGameRawFitness;

    private double mutationSD = .1;
    /*******************************************************************************
     *                            INSTANCE VARIABLES                                *
     *******************************************************************************/

    private static double randnum;
    /*******************************************************************************
     *                              CONSTRUCTORS                                    *
     *******************************************************************************/

    public Chromo(){

        //  Set gene values to a randum sequence of floats
        chromo = new double[Parameters.geneSize];

        for(int i =0; i < Parameters.geneSize; i++){
           chromo[i] = Search.r.nextDouble();
        }

        this.rawFitness = -1;   //  Fitness not yet evaluated
        this.sclFitness = -1;   //  Fitness not yet scaled
        this.proFitness = -1;   //  Fitness not yet proportionalized
        this.combinedGameRawFitness = -1; // Fitness not yet evaluated
    }


    /*******************************************************************************
     *                                MEMBER METHODS                                *
     *******************************************************************************/

    //  Get Alpha Represenation of a Gene **************************************

    public String getGeneAlpha(int geneID){
        int start = geneID * Parameters.geneSize;
        int end = (geneID+1) * Parameters.geneSize;
        String geneAlpha = Arrays.toString(Arrays.copyOfRange(this.chromo,start, end));
        return (geneAlpha);
    }

    //  Mutate a Chromosome Based on Mutation Type *****************************

    public void doMutation(){

        switch (Parameters.mutationType){

            case 1:     // guassian mutation to genes

                // for each gene in the chromo
                for(int i = 0; i < chromo.length; i++) {
                    randnum = Search.r.nextDouble();
                    if (randnum < Parameters.mutationRate) {
                        // modify current gene value by some amount chosen from a normal distribution
                        chromo[i] += (Search.r.nextGaussian() * mutationSD);

                        // don't allow probabilities stored in chromo to go less that 0 or greater than 1
                        if(chromo[i] < 0) chromo[i] = 0;
                        else if(chromo[i] > 1) chromo[i] = 1;
                    }
                }
            break;

            default:
                System.out.println("ERROR - No mutation method selected");
        }
    }

    /*******************************************************************************
     *                             STATIC METHODS                                   *
     *******************************************************************************/

    //  Select a parent for crossover ******************************************

    public static int selectParent(){

        double rWheel = 0;
        int j = 0;

        switch (Parameters.selectType){

            case 1:     // Proportional Selection
                randnum = Search.r.nextDouble();
                for (j=0; j<Parameters.popSize; j++){
                    rWheel = rWheel + Search.member[j].proFitness;
                    if (randnum < rWheel) return(j);
                }
                break;

            case 2:     //  Tournament Selection

                // get two possible parents
                int possibleParent1 = Search.r.nextInt(Parameters.popSize);
                int possibleParent2 = Search.r.nextInt(Parameters.popSize);

                // get their fitness level
                int fitterParent = (Search.member[possibleParent1].proFitness >=
                        Search.member[possibleParent2].proFitness)? possibleParent1:possibleParent2;

                int lessFitParent = (possibleParent1 != fitterParent)?possibleParent1:possibleParent2;

                // chose the fitter one if less than k
                double k = 1;
                randnum = Search.r.nextDouble();
                if(randnum < k){
                    return fitterParent;
                }else{
                    return lessFitParent;
                }

            case 3:     // Random Selection

                return Search.r.nextInt(Parameters.popSize);
            default:
                System.out.println("ERROR - No selection method selected");
        }
        return(-1);
    }

    //  Produce a new child from two parents  **********************************

    public static void mateParents(int pnum1, int pnum2, Chromo parent1, Chromo parent2, Chromo child1, Chromo child2){

        switch (Parameters.xoverType){

            case 1:     //  Single Point Crossover

                //  Select crossover point
                int xoverPoint1 = Search.r.nextInt(Parameters.geneSize);

                //  Create child chromosome from parental material
               for(int i = 0; i < Parameters.geneSize; i++){
                    if(i < xoverPoint1){
                        child1.chromo[i] = parent1.chromo[i];
                        child2.chromo[i] = parent2.chromo[i];
                    }else{
                        child1.chromo[i] = parent2.chromo[i];
                        child2.chromo[i] = parent1.chromo[i];
                    }
                }
                break;

            case 2:     //  Two Point Crossover

            case 3:     //  Uniform Crossover

            default:
                System.out.println("ERROR - Bad crossover method selected");
        }

        //  Set fitness values back to zero
        child1.rawFitness = -1;   //  Fitness not yet evaluated
        child1.sclFitness = -1;   //  Fitness not yet scaled
        child1.proFitness = -1;   //  Fitness not yet proportionalized
        child1.combinedGameRawFitness = -1;
        child2.rawFitness = -1;   //  Fitness not yet evaluated
        child2.sclFitness = -1;   //  Fitness not yet scaled
        child2.proFitness = -1;   //  Fitness not yet proportionalized
        child2.combinedGameRawFitness = -1;
    }

    //  Produce a new child from a single parent  ******************************

    public static void mateParents(int pnum, Chromo parent, Chromo child){

        //  Create child chromosome from parental material
        child.chromo = parent.chromo;

        //  Set fitness values back to zero
        child.rawFitness = -1;   //  Fitness not yet evaluated
        child.sclFitness = -1;   //  Fitness not yet scaled
        child.proFitness = -1;   //  Fitness not yet proportionalized
        child.combinedGameRawFitness = -1;
    }

    //  Copy one chromosome to another  ***************************************

    public static void copyB2A (Chromo targetA, Chromo sourceB){

        targetA.chromo = sourceB.chromo;

        targetA.rawFitness = sourceB.rawFitness;
        targetA.sclFitness = sourceB.sclFitness;
        targetA.proFitness = sourceB.proFitness;
        targetA.combinedGameRawFitness = sourceB.combinedGameRawFitness;
        return;
    }

    @Override
    public String toString(){
        String out = "Array form:\n"
                + Arrays.toString(chromo)
                +"\nExplicitly indexed form:\n";

        for(int i = 0; i < chromo.length; i++){
            out += i+ " : " + chromo[i] + ", ";
        }
        return out;
    }
}
