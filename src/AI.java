import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.Random;

public class AI {

    GUI gui;
    Player player;
    public String mode = "easy"; // difficulty level
    public AI(GUI MasterGui, Player MasterPlayer) {
        gui = MasterGui;
        player = MasterPlayer;
        System.out.println(player.getPlayerName());
    }

    //Global variables for easy access within methods
    /**
     * Used in the GUI class:
     * @see GUI#createMainScreen()
     * @param mode: the mode of difficulty to set the AI to
     */
    public void setMode(String mode) {
        this.mode = mode;
    }


    /**
     * @Function: Generates Random Code For User To Guess
     * Uses a method from the GUI class
     * @see GUI#convertStringToColor(String, Color[])
     */

    //make a code into a circles array for easy usage by the GUI
    public Circle[] ConvertCodeToCircles(String code) {
        Circle circles[] = new Circle[code.length()];
        for(int i=0; i<code.length(); i++) {
            circles[i] = new Circle(20, gui.convertStringToColor(code.charAt(i)+"", player.FXcolors));
        }
        return circles;
    }

    //scramble orders to prevent user from guessing algorithm easily
    public String Scramble(String original) {
        //make list of the colors in code
        ArrayList<Character> colors = new ArrayList<>();
        for(int i = 0;i<original.length();i++) {
            colors.add(original.charAt(i));
        }
        //randomly pull colors from list and shorten list until none are left
        String scramble ="";
        while(colors.size()>0) {
            int RandomColorIndex = (new Random()).nextInt(colors.size());
            scramble+=colors.get(RandomColorIndex);
            colors.remove(RandomColorIndex);
        }
        return scramble;
    }

    public String NoDuplicates(int length, char exclusion) {
        String permutation ="";

        //randommize order of colors and store in string to run scramble
        String colors ="";
        for(String color : player.colors) {
            colors+=color;
        }
        colors = Scramble(colors);
        //get the first "length" colors, excluding the exclusion color
        for(int i=0;i<length;i++) {
            if(exclusion != colors.charAt(i)) {
                permutation += colors.charAt(i)+"";
            }
        }
        return permutation;//return the colors
    }


    //make code
    public String generateCode() {

        String code="";
        int duplications=-1;//variable used in Medium and Easy codes
        switch(mode) {
            case "hard":{//Hard mode is full on no duplicates (do as many colors as possible)
                code = NoDuplicates(Math.min(player.CodeLength, player.ColorLength),' ');
                break;
            }
            case "medium":{//medium is 50% one color
                duplications = (int)Math.round(player.CodeLength/2.0);
                //notice no break to run code in easy case to actually form code
            }
            case "easy":{
                if(duplications == -1) {//if it's not medium, set to 75% one color
                    duplications = (int)Math.round(player.CodeLength *(3.0/4.0));
                }
                String duplication = generateRandomCode(1);//get a random color for the duplicated color
                //Add that color to 75/50 % of code
                for(int i=0;i<duplications;i++) {
                    code+=duplication;
                }
                //No duplicate remaining 25/50% of code (or as far as colors stretch)
                code+= NoDuplicates(Math.min(player.CodeLength-code.length(), player.ColorLength),duplication.charAt(0));
                break;
            }
        }
        //randomly generate rest of code (if any remains)
        code+=generateRandomCode(player.CodeLength-code.length());
        return Scramble(code);//scramble before returning
    }

    public String generateRandomCode(int length) {//Make a completely random code of specified length
        String code = "";
        for(int i=0; i<length; i++) {
            code+= player.colors[new Random().nextInt(player.colors.length)];
        }
        return code;
    }

    public String CurrentCodeSolveState() {
        String codes =CurrentMasterCode+"\n";
        for(String code:PossibleCodes) {
            codes+=code+"\n";
        }
        return codes;
    }

    public void ResumeSolving(ArrayList<String> remainingCodes) {
        CurrentMasterCode = remainingCodes.get(0);
        remainingCodes.remove(0);
        PossibleCodes = remainingCodes;
    }

    /**
     * Make an "imaginary guess" in which the computer asks itself for pegs using a test code and answer code
     * @param test: the code to test
     * @param code: TODO ask lakshy what this is
     * @return pegs: the peg response for a given code
     */
    private String AutoCheckCode(String test, String code){
        String pegs = ""; //Initialize string for pegs

        //Arrays to track used Positions when assigning pegs
        boolean[] CorrectIndexes = new boolean[player.CodeLength];
        boolean[] UsedIndexes = new boolean[player.CodeLength];

        if(test.length() != code.length()) { // if error
            return ""; // return empty string, and it will be removed later
        }

        //First check for Black pegs (i.e. correct color, correct index)
        for(int i=0; i<test.length(); i++) {//loop through code
            if(code.charAt(i) == test.charAt(i)) {//If the colors match at same index
                pegs += "B"; //add black peg
                CorrectIndexes[i] = true; //Mark as correct
            }
        }

        //Next, check for white pegs (Correct color, wrong index)
        for(int i=0; i<test.length(); i++) {//loop through code
            if(!CorrectIndexes[i]) {//exclude indexes with black pegs
                //for each index, loop through the others again to find a matching color
                for(int v=0; v<test.length(); v++) {
                    //if this index is not marked as white or black peg, and the colors match
                    if(!UsedIndexes[v] && test.charAt(i) == code.charAt(v) && !CorrectIndexes[v]) {
                        pegs += "W";//add a white peg
                        UsedIndexes[v] = true;//mark as white peg
                        break;//leave loop
                    }
                }
            }
        }
        return pegs;//return the total pegs
    }


    /**
     * Initialize an arrayList with all possible codes
     * @param codes: the ArrayList to be initialized
     */
    public int e =0;
    private void GetCodes (ArrayList<String> codes, int[] indexes, int CurrentIteration, String[] Colors) {
        if(CurrentIteration == indexes.length){
            String code = "";
            for(int index : indexes){
                code+=Colors[index];
            }
            codes.add(code);
            e++;
        }
        else{
            for(indexes[CurrentIteration] = 0;indexes[CurrentIteration]<player.colors.length;indexes[CurrentIteration]++){
                GetCodes(codes,indexes,CurrentIteration+1,Colors);
            }
        }
    }

    /**
     *
     * @param gui: the gui class to display the code onto the board
     * @return CurrentMasterCode: the answer code, although at this point, on hard difficulty, this would be the actual player's code
     */

    public static ArrayList<String> PossibleCodes = new ArrayList<>();
    public static String CurrentMasterCode;

    public void BustCode () { // TODO it seems okay, might need fixing
        //initialize variables for # of tries and All possible codes
        GetCodes(PossibleCodes, new int[player.CodeLength],0,player.colors);
        // System.out.println(player.CodeLength+" "+(player.CodeLength == 4)+e); // DEBUG
        if(player.CodeLength == 4 && !mode.equals("easy")){
            CurrentMasterCode = PossibleCodes.get(7); // 1122
        }
        else{
            RandomCode(PossibleCodes);
        }
        gui.sendCode(CurrentMasterCode);
    }

    public void RandomCode(ArrayList<String> codelist) {
        Random generator = new Random();
        CurrentMasterCode = codelist.get(generator.nextInt(codelist.size()));
    }

    public void miniMax() {
        int BestGuessHits = -1; // initialize the best guess hits
        String BestGuess = ""; // initialize best guess
        //-------------------------------------MINI-MAX CODE-------------------------------------//
        //Loop to find the code with the best worst case Scenario
        for (int i = 0; i < PossibleCodes.size(); i++) {//loop through each possible guess
            //Check the given guess's worst-case scenario
            //If it's the best case so far, make it the best guess
            if (HitCount(PossibleCodes, PossibleCodes.get(i)) > BestGuessHits) {
                //save the best guess and its knocked out guesses
                BestGuessHits = HitCount(PossibleCodes, PossibleCodes.get(i));
                BestGuess = PossibleCodes.get(i);
            }
        }
        CurrentMasterCode = BestGuess;
    }

    public void sendPegs(String pegs) { // TODO sometimes the pegs are empty/null????

        System.out.println(mode);

        if(!mode.equals("easy")) {
            System.out.println("removing");
            removeUseLessCodes(PossibleCodes, CurrentMasterCode, pegs); // remove useless codes/codes that don't work with the feedback pegs

            System.out.println("validation");
            if(PossibleCodes.size()<1) {
                gui.sendCode("invalid");
                return;
            }

            //Run minimax if on hard
            if(mode.equals("hard")) {
                System.out.println("minimaxxx");
                miniMax();
            }
            else {
                RandomCode(PossibleCodes);
            }
        }
        else {
            RandomCode(PossibleCodes);
        }

        System.out.println("sending "+CurrentMasterCode);
        gui.sendCode(CurrentMasterCode);
        PossibleCodes.remove(CurrentMasterCode);
    }

    /**
     * @Explanation:
     *  Following code revolves on this principle: If the code from our guess is used as an
     *  answer code, any correct code from the list of possible codes will yield the same
     *  resulting pegs when tested against the guess as the guess did against the True answer
     *  in the user's mind. This is because the pegs from our previous guess refer to correct
     *  or partially correct segments of the sequence. If any given code yields more pegs when
     *  tested against our guess, it means that it is detecting pegs in our guess that the true
     *  answer itself didn't contain, and is therefore incorrect. On the other hand, if it yields
     *  less pegs, it is failing to detect a peg the true answer found, and is once again incorrect.
     *  If you do not completely understand the concept, research Donald knuth's algorithm here,
     *  Where a better explanation may be present: https://en.wikipedia.org/wiki/Mastermind_(board_game)
     * @param PossibleCodes: the arrayList of all the possible codes left that match the peg response
     * @param CurrentMasterCode: the current code to check which possible codes to remove
     * @param pegs: the peg response of the given code
     */
    private void removeUseLessCodes(ArrayList<String> PossibleCodes, String CurrentMasterCode, String pegs) {
        //Loop through and remove codes based on principle above, using prev. guess (mastercode) as answer code
        ArrayList<String> RemovalCodes = new ArrayList<>();//Arraylist for codes to remove
        for(String code : PossibleCodes) {//loop through all remaining codes
            //If the code doesn't satisfy principle above
            if(!AutoCheckCode(code, CurrentMasterCode).equals(pegs)) {
                //Add it to list of codes to remove: Note that it can't be removed here, since
                //we are iterating through the arrayList at this point, so it must be added to a list
                RemovalCodes.add(code);
            }
        }
        //remove the specified elements
        for(String toRemove : RemovalCodes) {
            PossibleCodes.remove(toRemove);
        }
    }

    //method to find all possible peg combinations up to a maximum combo length
    public static ArrayList<String> GetPegs(int length){
        ArrayList<String> pegs = new ArrayList<>();// create list of pegs
        if(length == 0){ // exit condition
            return pegs;//empty list for 0 pegs
        }
        else{ //To include all possible peg combinations of any length until length
            //Initialize list of pegs to include the previous lists
            pegs = new ArrayList<>(GetPegs(length-1));
            //Loop through Each value from 0-length,
            for(int i=0;i<=length;i++){
                //Find the corresponding Black-White peg ratio
                int Bcount = i;
                int Wcount = length-Bcount;
                //Based on ratio,generate string for list of pegs
                String PegCombo = "";
                for(int v = 0;v<Bcount;v++){//add Black pegs
                    PegCombo+="B";
                }
                for(int v = 0;v<Wcount;v++){//add White pegs
                    PegCombo+="W";
                }
                //Add combination to list
                pegs.add(PegCombo);
            }

        }
        return pegs;
    }

    //Given the list of codes and test code, find the worst-case for amount of codes
    //Removed if we were to guess the given code
    private int HitCount(ArrayList<String> codes, String TestCode) {
        int LowestHits = 1297;//1296 is max hits, so any list will at least be worse than this
        //List of all possible peg scenarios for each code
        ArrayList<String> PossiblePegs = GetPegs(player.CodeLength);
        //Loop through each peg scenario
        for(String pegs : PossiblePegs) {
            //Track the amount of "hit" or destroyed codes for each peg scenario
            int hits = 0;
            for(int i=0; i<codes.size(); i++) {
                //If this scenario were to knock a code out,
                if(!AutoCheckCode(codes.get(i), TestCode).equals(pegs)) {
                    hits++;//increment the "hits"
                }
            }
            LowestHits = Math.min(hits, LowestHits);
        }
        return LowestHits;//return worst case
    }
    public static void main(String args[]) {}
}
