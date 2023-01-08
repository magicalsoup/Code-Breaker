import java.awt.*;
import java.util.Arrays;
import javafx.scene.paint.Color;

public class Player {
    boolean PlayerWon;
    String playerName;
    String userCode;
    int CodeLength;
    int ColorLength;
    String EndPegs;
    String[] maxColors = {"R","B","Y","G","O","P","V","T",""};
    Color circleColors[] = {Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.ORANGE, Color.HOTPINK, Color.PURPLE, Color.TURQUOISE, Color.BLACK};
    Color pegColors[] = {Color.BLACK, Color.WHITE, Color.RED};
    Color FXcolors[];
    String[] colors;
    int PlayerPlayTimes; // number of times the player has played the game
    int AIPlayTimes;
    int tries;
    int seconds;
    boolean usingTimer;
    public Player(String name) {
        playerName = name;
        CodeLength = 4;
        ColorLength = 6;
        PlayerPlayTimes = 0;
        AIPlayTimes = 0;
        tries = 0;
        seconds = 120; // DEBUG
        usingTimer = false;
        String[] EndPegPatterns= {"","B","BB","BBB","BBBB","BBBBB","BBBBBB","BBBBBBB","BBBBBBBB"};
        EndPegs = EndPegPatterns[CodeLength];
        colors = Arrays.copyOfRange(maxColors, 0, ColorLength);
        FXcolors = Arrays.copyOfRange(circleColors, 0, ColorLength);
    }
    public void playAgain(String type) {
        if(type.equals("Player")) {
            PlayerPlayTimes++;
        }
        if(type.equals("AI")) {
            AIPlayTimes++;
        }
    }
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
    public int getPlayerPlayTimes() {
        return PlayerPlayTimes;
    }
    public int getAIplayTimes() {
        return AIPlayTimes;
    }
    public void setPlayerName(String name) {
        playerName = name;
    }
    public String getPlayerName() {
        return playerName;
    }
    public String getUserCode() {
        return userCode;
    }
    public boolean win() {
        return PlayerWon;
    }
    public void setCode(String code) {
        userCode = code;
    }
    public void setCodeLength(int length) {
        CodeLength = length;
        String[] EndPegPatterns= {"","B","BB","BBB","BBBB","BBBBB","BBBBBB","BBBBBBB","BBBBBBBB"};
        EndPegs = EndPegPatterns[CodeLength];
    }
    public int getCodeLength() {
        return CodeLength;
    }
    public void setColorLength(int length) {
        ColorLength = length;
        colors = Arrays.copyOfRange(maxColors, 0, ColorLength);
        FXcolors = Arrays.copyOfRange(circleColors, 0, ColorLength);
    }
    public int getColorLength() {
        return ColorLength;
    }
}
