/**
 * @Assignment: Code-Breaker/Mastermind Project
 * @Name: Peter Wang, James Su and Lakshy Gupta
 * @Date: 14 November, 2019 --> 5 December, 2019
 * @Teacher: Mr.Anadarajan
 * @Course: ICS4U (Grade 12 Computer Science)
 * @Purpose: The purpose of this program is to create the boardgame Mastermind using Javafx
 */
import java.util.Scanner;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import java.util.Random;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.File;

public class GUI extends Application {

    /*
        Declaration
     */
    Player player; // Referencing Player class
    public AI computer; // Referencing AI class

    int MAX_ROW_SIZE = 10; // Maximum number of rows for the gameboard
    int MAX_COL_SIZE = 4; // Maximum number of columns for the gameboard

    final int WINDOW_WIDTH = 1200; // Height of the mainscreen
    final int WINDOW_HEIGHT = 900; // Width of the mainscreen

    private int currentRowIndex = MAX_ROW_SIZE - 1; // Track the current row index as the player make more guesses, this number will increment by one
    private int currentPointer = 0; // Keeps track of the current peg
    private int fakeCurrentPointer = 0; // Counter for the rules page
    private int HighLightedColor = -1; // Keeps track of the highlighted colors

    public Stage gameScreen; // Global variable gameScreen

    private String AICodeStr; // The guess that the AI will make

    private Circle gameGrid[][] = new Circle[MAX_ROW_SIZE][8]; // the game board
    private Circle playerCode[] = new Circle[8]; // the player's guess
    private Rectangle playerPegs[] = new Rectangle[8]; // the player's input
    private Circle AICode[] = new Circle[8]; // the computer's randomly generated code
    private Rectangle gamePegs[][] = new Rectangle[MAX_ROW_SIZE][8];

    //Variables for the file loading methods to modify
    public Circle[][] placeHolderGrid = new Circle[MAX_ROW_SIZE][8]; // Create circles for the game screen
    public Rectangle[][] placeHolderPegs = new Rectangle[MAX_ROW_SIZE][8]; // Ceate rectangles for the game screen

    private Color pegColors[] = {Color.RED, Color.WHITE, Color.BLACK}; // The colors that the code-maker will provide as feedback for the guesser
    private String Code; // The user's guess code


    FileChooser GetFiles = new FileChooser(); // File IO for highscores
    Random rng = new Random(); // Random number generator

    // timer stuff
    Timeline timeline = new Timeline();
    IntegerProperty timeSeconds = new SimpleIntegerProperty();

    Image image;
    ImageView AIVSPlayerIcon;
    ImageView PlayerVsAIIcon;
    ImageView CustomMenuIcon;
    ImageView RulesMenuIcon;
    ImageView SavedMenuIcon;
    ImageView ExitMenuIcon;
    /**
     * @function: The actual GUI program
     */
    public GUI() {
        player = new Player("");
        computer = new AI(this, player);
        gameScreen = new Stage();
        gameScreen.setResizable(false);
        image = new Image("https://media.istockphoto.com/photos/abstract-blurred-bokeh-light-for-background-picture-id855428876?k=6&m=855428876&s=612x612&w=0&h=merWkZ7jD7xTJmj3gXiwD_ucQPZA--ZO4TjhtQtiPAE=",
                WINDOW_WIDTH, WINDOW_HEIGHT, false, true); // background image
        AIVSPlayerIcon = new ImageView("https://cdn0.iconfinder.com/data/icons/artificial-intelligence-1-6/66/59-512.png");
        PlayerVsAIIcon = new ImageView("https://cdn2.iconfinder.com/data/icons/player-rounded-set/154/user-login-player-function-name-avatar-512.png");
        CustomMenuIcon = new ImageView("https://cdn1.iconfinder.com/data/icons/flat-web-browser/100/settings-512.png");;
        RulesMenuIcon = new ImageView("https://image.flaticon.com/icons/png/512/130/130304.png");
        SavedMenuIcon = new ImageView("https://static.thenounproject.com/png/10092-200.png");
        ExitMenuIcon = new ImageView("https://cdn3.iconfinder.com/data/icons/unicons-vector-icons-pack/32/exit-512.png");
    }

    @Override
    public void start(Stage primaryStage) {
        createStartScreen();
    }

    /**
     * helper method:
     *
     * @param colors:                Amount of colors to create on the wheel
     * @param WheelChangeComponents: Array of Items to change based on highlighted Color in the wheel
     * @return Group: The color Wheel in group form, to be drawn
     * @see #createAIGameScreen(boolean)
     * @see #createAIHowToPlayScreen()
     * @see #createPlayerHowToPlayScreen()
     * @see #createPlayerGameScreen(boolean)
     */
    private Group getColorWheel(Color[] colors, Node[] WheelChangeComponents) {
        Arc[] ArcColors = new Arc[colors.length];

        //initialize the Arcs
        for (int i = 0; i < ArcColors.length; i++) {
            ArcColors[i] = new Arc(200.0, 200.0, 100.0, 100.0, i * (360.0 / colors.length), 360.0 / colors.length);
            ArcColors[i].setType(ArcType.ROUND);
        }
        //Arc colors

        for (int i = 0; i < ArcColors.length; i++) {
            ArcColors[i].setFill(colors[i]);
        }

        //Line to indicate chosen color
        Line line = new Line(200, 195, 200, 105);
        line.setStrokeWidth(10.0);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setStroke(Color.SLATEGREY);

        //Create event handler to register scrolling
        EventHandler<ScrollEvent> eventHandler = new EventHandler<ScrollEvent>() {
            //Change the handle method to do what I want it to
            @Override
            public void handle(ScrollEvent e) {
                // System.out.println("Scroll"); // DEBUG
                // System.out.println(e.getDeltaY()); // DEBUG

                double DegreesChange = 15.0;//Create variable for the degrees to scroll

                if (e.getDeltaY() < 0) {//If the scrolling is backwards, negate scroll degrees
                    DegreesChange *= -1;
                }

                for (int i = 0; i < ArcColors.length; i++) { //Loop through arcs and increment their angles
                    ArcColors[i].setStartAngle(ArcColors[i].getStartAngle() + DegreesChange);
                    if (ArcColors[i].contains(200, 190)) {//set highlighted color if this arc is at middle
                        HighLightedColor = i;
                    }
                }
                ((Shape) WheelChangeComponents[currentPointer]).setFill(colors[HighLightedColor]);
            }
        };

        //Add transparent node on top to register scroll
        Rectangle rectangle = new Rectangle(100, 100, 210, 200);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.addEventFilter(ScrollEvent.SCROLL, eventHandler);

        //idk what this is it says I have to use "group" lmao
        Group group = new Group();
        for (int i = 0; i < ArcColors.length; i++) {
            group.getChildren().add(ArcColors[i]);
        }
        group.getChildren().add(line);
        group.getChildren().add(rectangle);
        return group;
    }

    /**
     * helper method:
     *
     * @Function: Generates a random code for the player vs AI option, where AI creates the code
     * @see #createPlayerGameScreen(boolean)
     */
    private void initAICode() {
        AICodeStr = computer.generateCode();
        AICode = computer.ConvertCodeToCircles(AICodeStr);
    }

    /**
     * @Function: the purpose  of this method is to create a start screen
     */
    private void createStartScreen() {
        Stage stage = new Stage();
        stage.setTitle("Welcome!");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        GridPane gridPane = new GridPane();
        TextField name = new TextField("");
        name.setStyle("-fx-border-color: #BABABA, black;" +
                "-fx-border-style: solid;" +
                "-fx-border-radius: 1.0em, 0;" +
                "-fx-border-insets: 0px, -2px;" +
                "-fx-border-width: 1px, 1px;" +
                "-fx-background-color: white;" +
                "-fx-background-radius: 1.0em");

        Label namePrompt = new Label("Please Enter Your Name");
        namePrompt.setStyle("-fx-font-family: Cambria;" +
                "-fx-font-weight: bold");
        name.setStyle("-fx-font-family: Lato;");
        Button submit = new Button("Submit");
        submit.setStyle("-fx-background-color: #F4F3EF;" +
                "-fx-font-family: Cambria;" +
                "-fx-font-weight: bold;" +
                "-fx-border-width: 2px;" +
                "-fx-border-color: #87CEEB;" +
                "-fx-border-radius: 2");
        submit.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                submit.setStyle("-fx-background-color: #87CEEB;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #87CEEB;" +
                        "-fx-border-radius: 2");
            }
        });
        submit.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                submit.setStyle("-fx-background-color: #F4F3EF;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #87CEEB;" +
                        "-fx-border-radius: 2");
            }
        });
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox, 600, 200);
        name.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    if (name.getText().trim().equals("") || name.getText().length() > 20) { // if the textField is blank, or if name is too long
                        String messageContent = "Please Enter Your Name Inorder to Continue";
                        if (name.getText().length() > 20) {
                            messageContent = "Please Enter A Name That's Less Than 20 Characters in Length";
                        }
                        createPopUpWindow(messageContent, "Attention");
                        event.consume();
                        return;
                    }
                    player.setPlayerName(name.getText()); // get the name
                    if (!player.getPlayerName().trim().equals("") && player.getPlayerName() != null) { // start the main screen
                        createMainScreen();
                        stage.close(); // close this window
                    }
                }
            }
        });

        submit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (name.getText().trim().equals("") || name.getText().length() > 20) { // if the textField is blank, or if name is too long
                    String messageContent = "Please Enter Your Name Inorder to Continue";
                    if (name.getText().length() > 20) {
                        messageContent = "Please Enter A Name That Is \nLess Than Or Equal To 20 Characters";
                    }
                    createPopUpWindow(messageContent, "Attention");
                    event.consume();
                    return;
                }

                player.setPlayerName(name.getText()); // get the name
                if (!player.getPlayerName().trim().equals("") && player.getPlayerName() != null) { // start the main screen
                    createMainScreen();
                    stage.close(); // close this window
                }
            }
        });


        //-------------Adding Stuff to the gridPane------------//
        gridPane.add(namePrompt, 0, 0);
        gridPane.add(name, 1, 0);
        gridPane.add(submit, 2, 0);
        //-----------------------------------------------------//

        //-----------gridPane adjustments/styles---------------//
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setPadding(new Insets(20));
        //----------------------------------------------------//

        //---------------------TRANSITION STUFF-------------------------//
        TranslateTransition ttName = new TranslateTransition(Duration.millis(1500), namePrompt),
                ttTextField = new TranslateTransition(Duration.millis(1500), name),
                ttSubmit = new TranslateTransition(Duration.millis(1500), submit);

        ttName.setToX(0);
        ttName.setToY(55);
        ttTextField.setToX(0);
        ttTextField.setToY(55);
        ttSubmit.setToX(0);
        ttSubmit.setToY(55);
        ttName.play();
        ttTextField.play();
        ttSubmit.play();
        //--------------------------------------------------------------//

        vbox.getChildren().add(gridPane);
        stage.setScene(scene);
        stage.showAndWait();

    }

    /**
     * helper method:
     *
     * @Function: The purpose of this method is to create a window that allows the user to change the length of the code
     * @see #createMainScreen()
     */
    private void createCodeChangeScreen() {
        Stage stage = new Stage();
        stage.setTitle("Change Length of Code");
        GridPane gridPane = new GridPane();
        TextField enterField = new TextField("");
        enterField.setPrefSize(50, 10);

        Label prompt = new Label("Please enter the length of the new code: \n(Please note that code length increase will result in exponential AI slowdown)");
        prompt.setPrefSize(300, 200);
        prompt.setWrapText(true);
        Button submit = new Button("Submit Custom Code Length");
        BackgroundImage backGroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backGroundImage);
        VBox vbox = new VBox();
        vbox.setBackground(background);
        Scene scene = new Scene(vbox, 600, 200);

        submit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!(new Scanner(enterField.getText()).hasNextInt()) || (new Scanner(enterField.getText()).nextInt()) <= 0
                        || (new Scanner(enterField.getText()).nextInt()) > 8) { // if the textField is blank, or if the number is out of bounds
                    createPopUpWindow("Please Enter a number from 1-8 to continue", "Attention");
                    event.consume();
                    return;
                }

                player.setCodeLength(Integer.parseInt(enterField.getText())); // get the number
                MAX_COL_SIZE = player.getCodeLength();
                stage.close(); // close this window
            }
        });

        //-------------Adding Stuff to the gridPane------------//
        gridPane.add(prompt, 0, 0);
        gridPane.add(enterField, 1, 0);
        gridPane.add(submit, 0, 1);
        //-----------------------------------------------------//

        //-----------gridPane adjustments/styles---------------//
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setPadding(new Insets(20));
        //----------------------------------------------------//

        vbox.getChildren().add(gridPane);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * helper method:
     *
     * @Function: The purpose of this method is to create a window that allows the user to change the number of colors
     * @see #createMainScreen()
     */
    private void createColorChangeScreen() {
        Stage stage = new Stage();
        stage.setTitle("Setting Up");

        GridPane gridPane = new GridPane();
        TextField enterField = new TextField("");
        enterField.setPrefSize(50, 10);

        Label prompt = new Label("Enter The new Number of colors (1-8)");
        Button submit = new Button("Submit Custom Number");
        BackgroundImage backGroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backGroundImage);
        VBox vbox = new VBox();
        vbox.setBackground(background);
        Scene scene = new Scene(vbox, 600, 200);

        submit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!(new Scanner(enterField.getText()).hasNextInt()) || (new Scanner(enterField.getText()).nextInt()) <= 0
                        || (new Scanner(enterField.getText()).nextInt()) > 8) { // if the textField is blank, or if the number is invalid
                    createPopUpWindow("Please Enter a number from 1-8 to continue", "Attention");
                    event.consume();
                    return;
                }

                player.setColorLength(Integer.parseInt(enterField.getText())); // get the name
                stage.close(); // close this window
            }
        });

        //-------------Adding Stuff to the gridPane------------//
        gridPane.add(prompt, 0, 0);
        gridPane.add(enterField, 1, 0);
        gridPane.add(submit, 0, 1);
        //-----------------------------------------------------//

        //-----------gridPane adjustments/styles---------------//
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setPadding(new Insets(20));
        //----------------------------------------------------//

        vbox.getChildren().add(gridPane);
        vbox.setBackground(background);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * One of the main screens for user to navigate, interacts with the following methods
     *
     * @Function: Creates the main screen of the game, the allows the user to navigate to all the other smaller sub-screens
     * @see #createStartScreen()
     * @see #createPlayerGameScreen(boolean) (0
     * @see #createAIGameScreen(boolean)
     * @see #playerGameOver(boolean, boolean)
     * @see #AIGameOver(String)
     */
    public void createMainScreen() {

        player.playAgain("Player");
        player.playAgain("AI");
        player.tries = 0; // reset tries
        //----------MENU STUFF_-----------------------//
        MenuBar menu = new MenuBar();
        //------CREATING MENU ITEMS------------//
        AIVSPlayerIcon.setFitWidth(30);
        AIVSPlayerIcon.setFitHeight(30); // set size

        Menu AIVsPlayer = new Menu("AI vs Player");  // set the menu
        AIVsPlayer.setGraphic(AIVSPlayerIcon); // set the icon

        MenuItem easy = new MenuItem("Easy AI");
        easy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createAIGameScreen(false);
                if (player.getAIplayTimes() == 1) {
                    createAIHowToPlayScreen();
                }
                computer.setMode("easy");
                event.consume();
            }
        });

        MenuItem medium = new MenuItem("Medium AI");
        medium.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createAIGameScreen(false);
                if (player.getAIplayTimes() == 1) {
                    createAIHowToPlayScreen();
                }
                computer.setMode("medium");
                event.consume();
            }
        });

        MenuItem hard = new MenuItem("Hard AI");
        hard.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createAIGameScreen(false);
                if (player.getAIplayTimes() == 1) {
                    createAIHowToPlayScreen();
                }
                computer.setMode("hard");
                event.consume();
            }
        });

        //--------------------------PLAYER VS AI------------------------------//
        Menu PlayerVsAIMenu = new Menu("Player vs AI");
        PlayerVsAIIcon.setFitHeight(30);
        PlayerVsAIIcon.setFitWidth(30);
        PlayerVsAIMenu.setGraphic(PlayerVsAIIcon);

        MenuItem easyCode = new MenuItem("Easy Code");
        easyCode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Set mode: ez");
                computer.setMode("easy");
                if (player.getPlayerPlayTimes() == 1) {
                    createPlayerHowToPlayScreen();
                }
                createPlayerGameScreen(false);

                event.consume();
            }
        });

        MenuItem mediumCode = new MenuItem("Medium Code");
        mediumCode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Set mode: mz");
                computer.setMode("medium");
                if (player.getPlayerPlayTimes() == 1) {
                    createPlayerHowToPlayScreen();
                }
                createPlayerGameScreen(false);

                event.consume();
            }
        });

        MenuItem hardCode = new MenuItem("Hard Code");
        hardCode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Set mode: hard");
                if (player.getPlayerPlayTimes() == 1) {
                    createPlayerHowToPlayScreen();
                }
                computer.setMode("hard");
                createPlayerGameScreen(false);

                event.consume();
            }
        });
        //--------------------------------------------------------------------//

        //-------------------------CUSTOM MENU-----------------------------------//
        CustomMenuIcon.setFitHeight(30);
        CustomMenuIcon.setFitWidth(30);

        Menu CustomMenu = new Menu("Game Customization");
        CustomMenu.setGraphic(CustomMenuIcon);

        MenuItem changeColor = new MenuItem("Change Number of Colors");
        changeColor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createColorChangeScreen();
                event.consume();
            }
        });

        MenuItem changeCode = new MenuItem("Change Length of Code");
        changeCode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createCodeChangeScreen();
                event.consume();
            }
        });

        //--------------------------------------------------------------------//

        //--------------------------RULES MENU--------------------------------//
        RulesMenuIcon.setFitWidth(30);
        RulesMenuIcon.setFitHeight(30);

        Menu RulesMenu = new Menu("Rules");
        RulesMenu.setGraphic(RulesMenuIcon);

        MenuItem RulesItem = new MenuItem("Rules");
        RulesItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createRulesPage();
                event.consume();
            }
        });

        MenuItem RulesItem2 = new Menu("How To Play As Guesser");
        RulesItem2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createPlayerHowToPlayScreen();
            }
        });

        MenuItem RulesItem3 = new Menu("How To Play As CodeMaker");
        RulesItem3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createAIHowToPlayScreen();
            }
        });
        //--------------------------------------------------------------------//

        //-------------------SAVE FILE MENU-----------------------------------//
        SavedMenuIcon.setFitWidth(30);
        SavedMenuIcon.setFitHeight(30);
        Menu SaveFileMenu = new Menu("Saved Files");
        SaveFileMenu.setGraphic(SavedMenuIcon);

        MenuItem load = new MenuItem("Load saved data");
        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GetFiles.setTitle("Choose a file to open your game From");
                File file; // Open the file from user input
                do {//if they decide not to enter anything like snakes, pop-up spam them
                    file = GetFiles.showOpenDialog(gameScreen);
                    if (file == null) {
                        return;
                    }
                    if (!file.getName().contains(".txt")) {
                        createPopUpWindow("Not a Valid File, Please try again", "Invalid Input");
                    }
                } while (!file.getName().contains(".txt"));

                try {
                    Scanner reader = new Scanner(file);
                    String Game = reader.next();//game type
                    computer.setMode(reader.next());//mode
                    //code length
                    int codelength = reader.nextInt();
                    MAX_COL_SIZE = codelength;
                    player.setCodeLength(codelength);
                    player.setColorLength(reader.nextInt());//number of colors
                    currentRowIndex = reader.nextInt();//start where left off
                    setGameGrid(reader);//create game
                    if (Game.equals("AI")) {
                        //Get the AI up to speed
                        ArrayList<String> PossibleCodes = new ArrayList<>();
                        while (reader.hasNext()) {
                            PossibleCodes.add(reader.next());
                        }
                        computer.ResumeSolving(PossibleCodes);
                        createAIGameScreen(true);//start
                    } else {
                        AICodeStr = reader.next();
                        System.out.println("Read: " + AICodeStr);
                        AICode = computer.ConvertCodeToCircles(AICodeStr);
                        createPlayerGameScreen(true);//start
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        ExitMenuIcon.setFitWidth(30);
        ExitMenuIcon.setFitHeight(30);
        Menu QuitMenu = new Menu("Quit Program");
        QuitMenu.setGraphic(ExitMenuIcon);
        MenuItem quit = new MenuItem("Quit");
        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createPopUpWindow("We hope you had fun playing Mastermind!", "Goodbye :)");
                System.exit(0);
            }
        });

        //------------------------------------------------------------------//
        //----------------------END OF MENU INITIALIZATION----------------------------------//

        //---------------------MENU STUFF + STYLING-----------------------//
        AIVsPlayer.getItems().addAll(easy, medium, hard);
        PlayerVsAIMenu.getItems().addAll(easyCode, mediumCode, hardCode);
        RulesMenu.getItems().addAll(RulesItem, RulesItem2, RulesItem3);
        CustomMenu.getItems().addAll(changeCode, changeColor);
        SaveFileMenu.getItems().add(load);
        QuitMenu.getItems().add(quit);

        AIVsPlayer.setStyle("-fx-background-color: #54C6BE;" +
                "-fx-font: 18 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 5 5 5 5;" +
                "-fx-background-radius: 5 5 5 5;");
        PlayerVsAIMenu.setStyle("-fx-background-color: #F7B15C;" +
                "-fx-font: 18 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-border-radius: 5 5 5 5;" +
                "-fx-background-radius: 5 5 5 5;");
        RulesMenu.setStyle("-fx-background-color: #F65C51;" +
                "-fx-font: 18 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-border-radius: 5 5 5 5;" +
                "-fx-background-radius: 5 5 5 5;");
        CustomMenu.setStyle("-fx-background-color: #E5243F;" +
                "-fx-font: 18 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-border-radius: 5 5 5 5;" +
                "-fx-background-radius: 5 5 5 5;");

        SaveFileMenu.setStyle("-fx-background-color: #3246DE;" +
                "-fx-font: 18 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 5 5 5 5;" +
                "-fx-background-radius: 5 5 5 5;");

        QuitMenu.setStyle("-fx-background-color: #FF00DC;" +
                "-fx-font: 18 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 5 5 5 5;" +
                "-fx-background-radius: 5 5 5 5;");

        menu.getMenus().addAll(AIVsPlayer, PlayerVsAIMenu, RulesMenu, CustomMenu, SaveFileMenu, QuitMenu);
        menu.setPadding(new Insets(15));
        //---------------------END OF MENU STYLING------------------------------//

        GridPane fakeGameGrid = new GridPane(); // a fake board to look good
        fakeGameGrid.setVgap(10);
        fakeGameGrid.setPadding(new Insets(30, 0, 0, 0));
        fakeGameGrid.setAlignment(Pos.CENTER);
        int randomRows = rng.nextInt(MAX_ROW_SIZE / 2 - 1);
        for (int r = 1; r <= MAX_ROW_SIZE; r++) { // loop through rows
            HBox row = new HBox();
            row.setSpacing(30.0);
            for (int i = 0; i < 4; i++) { // loop through columns
                Circle c = new Circle(20.0);
                if (r >= randomRows) { // for how many rules to fill
                    c.setFill(player.FXcolors[rng.nextInt(player.FXcolors.length)]);
                }
                row.getChildren().add(c); // add to box
            }
            Separator s = new Separator();
            s.setOrientation(Orientation.VERTICAL); // make a separator
            row.getChildren().add(s); // add to row box
            for (int i = 0; i < 4; i++) { // loop through rows
                Rectangle rect = new Rectangle(20, 20, Color.GRAY);
                rect.setArcWidth(10);
                rect.setArcHeight(10);
                if (r >= randomRows) { // for how many rows to fill
                    rect.setFill(pegColors[rng.nextInt(pegColors.length)]);
                }
                row.getChildren().add(rect); // add to box
            }
            fakeGameGrid.add(row, 0, r); // add to grid
        }

        Label title = new Label("Welcome To Code-Breaker!");
        title.setFont(new Font("Nunito", 41));
        title.setAlignment(Pos.CENTER);

        fakeGameGrid.add(title, 0, 0);
        VBox vbox = new VBox();

        //----------------------BACKGROUND----------------------//
        BackgroundImage backGroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backGroundImage);
        //--------------------------------------------------------------//

        vbox.setBackground(background); // set background

        vbox.getChildren().addAll(menu, fakeGameGrid); // add components
//        FadeTransition fd = new FadeTransition(Duration.millis(1000), vbox);
//        fd.setFromValue(0); fd.setToValue(1);
//        fd.play();
        Scene mainScene = new Scene(vbox, WINDOW_WIDTH, WINDOW_HEIGHT); // set scene and set width + height

        gameScreen.setScene(mainScene); // set scene to stage
        gameScreen.show();
    }


    /**
     * interacts with other methods, the main AI game screen
     *
     * @param loading: a boolean value if the user is using a saved file or not
     * @see #createMainScreen()
     */
    public void createAIGameScreen(boolean loading) {
        BackgroundImage backGroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backGroundImage);
        BorderPane gamePane = new BorderPane();
        currentPointer = 0;
        if (!loading) { // if the user is not using a saved file
            currentRowIndex = MAX_ROW_SIZE - 1;
        }
        for (int i = 0; i < MAX_COL_SIZE; i++) {
            playerPegs[i] = new Rectangle(40, 40);
            playerPegs[i].setFill(Color.GRAY);
            playerPegs[i].setArcHeight(20);
            playerPegs[i].setArcWidth(20);
        }

        gameScreen.setTitle("Code Breaker"); // set title
        FadeTransition fd = new FadeTransition(Duration.millis(500), gamePane);
        fd.setFromValue(0);
        fd.setToValue(1);
        gamePane.setBackground(background);
        Scene gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT); // initialize the AI randomly generated code
        //Menu Stuffs
        MenuBar PauseMenu = new MenuBar();

        Menu save = new Menu("Save Game");
        MenuItem saveButton = new MenuItem("Save to File");
        saveButton.setOnAction(new EventHandler<ActionEvent>() { // if user clicks on it
            @Override
            public void handle(ActionEvent event) {
                //Write saveData to the file
                GetFiles.setTitle("Choose a Place to save your file (remember to end your file name with \".txt\"");
                File saveData; // Open the file from user input
                do {//if they decide not to enter anything like snakes, pop-up spam them
                    saveData = GetFiles.showSaveDialog(gameScreen);
                    if (saveData == null) { //cancel button pressed, cancel the code
                        return;
                    }
                    if (!saveData.getName().contains(".txt")) {
                        createPopUpWindow("Invalid File, Please Try again.", "invalid input");
                    }
                } while (!saveData.getName().contains(".txt"));

                try {
                    saveData.createNewFile();
                    PrintWriter writeData = new PrintWriter(saveData);
                    writeData.println("AI " + computer.mode + " " + MAX_COL_SIZE + " " + player.getColorLength() + " " + currentRowIndex);
                    writeData.println(GameGridString());
                    writeData.println(computer.CurrentCodeSolveState());
                    writeData.close();
                    createPopUpWindow("File saved Successfully at " + saveData.getName(), "Success!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        save.getItems().add(saveButton);

        Menu Quit = new Menu("Quit Game");
        MenuItem Exit = new MenuItem("Exit to menu");
        Exit.setOnAction(new EventHandler<ActionEvent>() { // if user clicks on it
            @Override
            public void handle(ActionEvent event) {
                createMainScreen();
            }
        });
        Quit.getItems().add(Exit);

        Menu colorBoxMenu = new Menu("Colors Available");
        MenuItem colorBoxItem = new MenuItem("Colors");
        colorBoxItem.setOnAction(event -> {
            createColorAvailable();
        });
        colorBoxMenu.getItems().add(colorBoxItem);
        save.setStyle("-fx-background-color: #C70039;" +
                "-fx-font: 14 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 3 3 3 3;" +
                "-fx-background-radius: 3 3 3 3;");
        Quit.setStyle("-fx-background-color: #FF5733;" +
                "-fx-font: 14 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 3 3 3 3;" +
                "-fx-background-radius: 3 3 3 3;");
        colorBoxMenu.setStyle("-fx-background-color: #FF5733;" +
                "-fx-font: 14 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 3 3 3 3;" +
                "-fx-background-radius: 3 3 3 3;");
        PauseMenu.getMenus().addAll(save, Quit, colorBoxMenu);
        PauseMenu.setPadding(new Insets(5, 10, 5, 10));
        //--------------------SETTING UP--------------------------//
        Button checkButton = new Button("Check"); // create a checkButton
        checkButton.setStyle("-fx-background-color: #F4F3EF;" +
                "-fx-font-family: Cambria;" +
                "-fx-font-weight: bold;" +
                "-fx-border-width: 2px;" +
                "-fx-border-color: #87CEEB;" +
                "-fx-border-radius: 5;" +
                "-fx-font-size: 20");
        checkButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                checkButton.setStyle("-fx-background-color: #87CEEB;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #87CEEB;" +
                        "-fx-border-radius: 5;" +
                        "-fx-font-size: 20");
            }
        });
        checkButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                checkButton.setStyle("-fx-background-color: #F4F3EF;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #87CEEB;" +
                        "-fx-border-radius: 5;" +
                        "-fx-font-size: 20");
            }
        });


        HBox guessBox = new HBox();
        guessBox.setSpacing(30);
        for (int i = 0; i < MAX_COL_SIZE; i++) {
            guessBox.getChildren().add(playerPegs[i]);
        }
        Separator s = new Separator();
        s.setOrientation(Orientation.VERTICAL);
        checkButton.setAlignment(Pos.CENTER_LEFT);

        guessBox.getChildren().addAll(s, checkButton);
        //-------------------------------------------------------------------//

        checkButton.setOnMouseClicked(new EventHandler<MouseEvent>() { // if user clicks on it
            @Override
            public void handle(MouseEvent event) {
                if (!checkRowIsFinishedAI()) { // if there is still pegs to be filled
                    createPopUpWindow("Please Fill Out The Entire Row", "Attention"); // create window
                    event.consume(); // consume the event
                    return;
                }
                player.tries++; // increment one guess for computer
                String input = "";
                int BlackCount = 0, WhiteCount = 0;

                for (int i = 0; i < MAX_COL_SIZE; i++) {
                    input += convertColorToString((Color) playerPegs[i].getFill(), pegColors);
                    if (playerPegs[i].getFill().equals(Color.RED)) { // using red because black means no peg
                        BlackCount++;
                    } else if (playerPegs[i].getFill().equals(Color.WHITE)) {
                        WhiteCount++;
                    }
                }

                String sortedInput = "";
                for (int i = 0; i < BlackCount; i++) {
                    sortedInput += "B";
                }
                for (int i = 0; i < WhiteCount; i++) {
                    sortedInput += "W";
                }
                if (!sortedInput.equals(player.EndPegs)) {
                    System.out.println("SENT: " + sortedInput);
                    computer.sendPegs(sortedInput);
                }

                AIChangeRow(input);

                GridPane gameGridPane = getGameGrid();
                s.setOrientation(Orientation.HORIZONTAL);
                gameGridPane.add(s, 0, MAX_ROW_SIZE);
                gameGridPane.add(guessBox, 0, MAX_ROW_SIZE + 1);
                gameGridPane.setPadding(new Insets(30));
                gamePane.setCenter(gameGridPane);


                if (sortedInput.equals(player.EndPegs)) { // if player enters "BBBB" or similar
                    System.out.println("WIN " + player.EndPegs + " " + sortedInput);
                    AIGameOver("The AI has won the game in " + (player.tries) + " guesses!"); // create screen for end
                    return; // stop executing the rest of the method
                }
                if (currentRowIndex < 0) { // if there are no more guesses left
                    AIGameOver("The AI has lost the game!"); // check if player lost or won, and display the window
                }
            }
        });
        checkButton.setAlignment(Pos.CENTER); // style

        if (!loading) {
            initGameGridAndPegs(); // initialize the game grid and pegs
            computer.BustCode(); // start busting code
            currentRowIndex--; // because the computer already makes the first guess
        } else {
            gameGrid = placeHolderGrid;
            gamePegs = placeHolderPegs;
        }
        //-------------------SETTING UP + STYLES---------------------------------//
        GridPane gameGridPane = getGameGrid();
        Group colorWheel = getColorWheel(pegColors, playerPegs);

        HBox wheelBox = new HBox();
        wheelBox.getChildren().add(colorWheel);
        wheelBox.setAlignment(Pos.CENTER);
        wheelBox.setPadding(new Insets(30));
        s.setOrientation(Orientation.HORIZONTAL);
        gameGridPane.add(s, 0, MAX_ROW_SIZE);
        gameGridPane.add(guessBox, 0, MAX_ROW_SIZE + 1);
        gameGridPane.setPadding(new Insets(30));

        gamePane.setTop(PauseMenu);
        gamePane.setCenter(gameGridPane);
        gamePane.setBottom(wheelBox);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.BLACK);
        playerPegs[currentPointer].setEffect(dropShadow);
        gamePane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                playerPegs[currentPointer].setEffect(null);
                if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                    if (currentPointer == 0) currentPointer = MAX_COL_SIZE - 1;
                    else currentPointer = (currentPointer - 1) % MAX_COL_SIZE;
                }
                if (event.getCode() == KeyCode.D  || event.getCode() == KeyCode.RIGHT) {
                    currentPointer = (currentPointer + 1) % MAX_COL_SIZE;
                }
                playerPegs[currentPointer].setEffect(dropShadow);
                event.consume();
            }
        });


        //---------------------------------------------------------------------//
        fd.play();
        gameScreen.setScene(gameScene);
        gameScreen.show();
    }

    /**
     * helper method:
     *
     * @param grid: the scanner (grid) to read the grid of the saved file
     * @see #createMainScreen()
     */
    public void setGameGrid(Scanner grid) {
        for (int i = 0; i < 10; i++) {//Read the grid, and initialize the circles
            for (int v = 0; v < MAX_COL_SIZE; v++) {
                String color = grid.next();
                placeHolderGrid[i][v] = new Circle(20);
                if (!color.equals("null")) {//If they has contents, create thtem
                    placeHolderGrid[i][v].setFill(convertStringToColor(color, player.FXcolors));
                }
            }
        }
        //repeat for the pegs
        for (int i = 0; i < 10; i++) {
            for (int v = 0; v < MAX_COL_SIZE; v++) {
                String color = grid.next();
                placeHolderPegs[i][v] = new Rectangle(20, 20, Color.GRAY);
                placeHolderPegs[i][v].setArcWidth(10);
                placeHolderPegs[i][v].setArcWidth(10);
                if (!color.equals("null")) {
                    placeHolderPegs[i][v].setFill(convertStringToColor(color, player.pegColors));
                }
            }
        }
    }

    /**
     * helper method:
     *
     * @return The current game's state in string form for writing to file
     * @see #createPlayerGameScreen(boolean)
     * @see #createAIGameScreen(boolean)
     */
    public String GameGridString() {
        String returnString = "";
        for (int i = 0; i < 10; i++) {//loop through guesses and write em
            for (int v = 0; v < MAX_COL_SIZE; v++) {
                returnString += convertColorToString((Color) gameGrid[i][v].getFill(), player.FXcolors) + "\n";
            }
        }
        for (int i = 0; i < 10; i++) {//loop through pegs and write em
            for (int v = 0; v < MAX_COL_SIZE; v++) {
                returnString += convertColorToString((Color) gamePegs[i][v].getFill(), player.pegColors) + "\n";
            }
        }
        return returnString;
    }

    /**
     * helper method to help AI go to the next row/guess during the AI vs player mode
     *
     * @param code: the code to be displayed
     * @see #createAIGameScreen(boolean)
     */
    public void AIChangeRow(String code) {
        //System.out.println(currentRowIndex);
        for (int j = 0; j < MAX_COL_SIZE; j++) { // loop through the game pegs
            //System.out.println(j);
            gamePegs[currentRowIndex + 1][j] = new Rectangle(20, 20); // make a new rectangle
            gamePegs[currentRowIndex + 1][j].setFill(convertStringToColor(code.charAt(j) + "", pegColors)); // set it to the peg colors
        }
        //System.out.println("Show not go here");
        currentRowIndex--; // go to the new row
    }

    /**
     * helper method:
     *
     * @Function: to create a window telling the user the rules of code-breaker
     * @see #createMainScreen()
     * @see #createPlayerHowToPlayScreen()
     * @see #createAIHowToPlayScreen()
     */
    public void createRulesPage() {
        Stage rulesPage = new Stage();
        GridPane gridPane = new GridPane();
        TextArea ruleContent = new TextArea();

        Label header1 = new Label("How To Play Codebreaker:");
        header1.setAlignment(Pos.CENTER);
        header1.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        ruleContent.setText("Code breaker, commonly known as mastermind, is a difficult puzzle game where one player creates a code (1 - 8 pegs in length)" +
                "and consisting of up to 9 colors, for the other player to guess. \n\nEach turn, the guesser will make a prediction on what they think" +
                "the hidden code will be and the other player will give feedback based on their guess." +
                "The feedback that the guesser recieves will either be a red peg, white peg or black peg. Red pegs denotes that one of the pegs is in" +
                "the correct location and of the correct color. White pegs symbolizes that one of the pegs is of the correct color, but is not in the " +
                "correct position. Black pegs symbolizes that one of the pegs is not in the correct order or color. Note that the order of feedback " +
                "does not matter. The goal of the guesser is to guess the hidden code in the fewest number of turns" +
                "while the person who creates the code will try to make the code difficult to guess. " +
                "\n\nIn our version of Mastermind, there are two game modes, Player VS AI " +
                "and AI VS Player. In the Player VS AI game mode, the Player will be creating the hidden code (But will not let the computer know" +
                "to ensure competitive integrity) and the computer will try to guess the code. In the latter game mode, the Player will be guessing a random" +
                "code that the computer creates. \n\nNotes: \n * Guesser (whether it is the computer or you) will have at most 10 guesses to figure out " +
                "the hidden code. \n * Computer will not know the code that you created, so make sure you don't give false information to the computer, " +
                "They have feelings too! \n * You can set a timer for yourself for the Player vs AI game mode for an amount of seconds between 1 - 600. " +
                "This is used to keep you motivated and focused when you are playing the game. When the timer runs out, you will lose the game. The timer " +
                "for the entire game and does not reset after every guess. If you do not want to deal with the stress of a tick clock, you can set the timer " +
                "equal to zero and you will have as much time as you want.");
        ruleContent.setStyle("-fx-font-family: Lato;" +
                "-fx-font-size: 18");
        ruleContent.setWrapText(true);
        ruleContent.setEditable(false);
        ruleContent.setFont(new Font(24));
        ruleContent.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT / 1.5);

        gridPane.setPadding(new Insets(30));
        gridPane.setVgap(30);
        gridPane.add(header1, 0, 0);
        gridPane.add(ruleContent, 0, 1);

        Button closeBtn = new Button("I Got This");
        closeBtn.setStyle("-fx-background-color: #F4F3EF;" +
                "-fx-font-family: Cambria;" +
                "-fx-font-weight: bold;" +
                "-fx-border-width: 2px;" +
                "-fx-border-color: #24FF00;" +
                "-fx-border-radius: 2");
        closeBtn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                closeBtn.setStyle("-fx-background-color: #24FF00;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #24FF00;" +
                        "-fx-border-radius: 2");
            }
        });
        closeBtn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                closeBtn.setStyle("-fx-background-color: #F4F3EF;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #24FF00;" +
                        "-fx-border-radius: 2");
            }
        });
        closeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rulesPage.close();
                event.consume();
            }
        });

        gridPane.add(closeBtn, 0, 2);

        Scene scene = new Scene(gridPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        rulesPage.setScene(scene);
        rulesPage.showAndWait();
    }

    /**
     * It is a helper method
     *
     * @function create game screen
     * @see #createAIGameScreen(boolean)
     */
    public void initGameGridAndPegs() {
        for (int r = 0; r < MAX_ROW_SIZE; r++) { // loop through rows
            for (int i = 0; i < MAX_COL_SIZE; i++) { // loop through columns
                Circle c = new Circle(20.0); // create a circle
                Rectangle rect = new Rectangle(20, 20, Color.GRAY); // create a rectangle
                rect.setArcWidth(10);
                rect.setArcHeight(10); // styles
                //---------initialize----------//
                gameGrid[r][i] = c;
                gamePegs[r][i] = rect;
                //----------------------------//
            }
        }
    }

    /**
     * @function creates a rulesbook for the AI vs player
     */
    public void createAIHowToPlayScreen() {
        Stage mainScreen = new Stage();
        mainScreen.setTitle("How To Play");
        mainScreen.initModality(Modality.APPLICATION_MODAL);
        BackgroundImage backGroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backGroundImage);
        Pagination pagination = new Pagination();

        //----------------CONTENT------------------//
        HBox fakePegBox = new HBox(); // for the fake circle box interaction
        fakePegBox.setSpacing(30);

        TextArea textArea1 = new TextArea(); // telling player how to use change color
        textArea1.setText("You use the the arrow keys (left and right) or the letters A and D to change the selected rectangle." +
                "The rectangle that you have chosen will be highlighted. Try it on the sample to the right");
        textArea1.setEditable(false);
        textArea1.setWrapText(true);
        textArea1.setFont(new Font(24));
        textArea1.setPrefSize(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 3);

        Rectangle fakePlayerPegs[] = new Rectangle[4]; // create circles for the fake codes
        for (int i = 0; i < 4; i++) {
            fakePlayerPegs[i] = new Rectangle(40, 40, Color.GRAY); // set width and height and style
            fakePlayerPegs[i].setArcHeight(20);
            fakePlayerPegs[i].setArcWidth(20); // make it round
            fakePegBox.getChildren().add(fakePlayerPegs[i]); // add to fake circle box
        }


        HBox fakeColorWheelBox = new HBox(); // for the fake color wheel
        TextArea textArea2 = new TextArea();
        textArea2.setText("You use the mouse and scroll to change the circle, scroll on the circle to change the colour. However, if you do not " +
                "have a scroll wheel, you can always use two fingers and swipe up or down to change the direction. Try it out on the sample circle" +
                "to the right.");
        textArea2.setEditable(false);
        textArea2.setWrapText(true);
        textArea2.setFont(new Font(24));
        textArea2.setPrefSize(500, 300);

        Group fakeColorWheel = getColorWheel(player.pegColors, fakePlayerPegs); // get the fake color wheel
        fakeColorWheelBox.getChildren().add(fakeColorWheel); // add it to the fake color wheel box


        TextArea textArea3 = new TextArea();
        textArea3.setText("After you are done making your guess, press the check button and the AI will display its guess on the board " +
                "If you are confused with the rules of code breaker, I suggest your to read over our rules page. You can click the button " +
                "to open up the rules page which is on the right");
        textArea3.setEditable(false);
        textArea3.setWrapText(true);
        textArea3.setFont(new Font(24));
        textArea3.setPrefSize(500, 300);


        Button rulesPageBtn = new Button("Go To Rules Page"); // create button for user to go to rules page
        rulesPageBtn.setStyle("-fx-background-color: #F4F3EF;" +
                "-fx-font-family: Cambria;" +
                "-fx-font-weight: bold;" +
                "-fx-border-width: 2px;" +
                "-fx-border-color: #87CEEB;" +
                "-fx-border-radius: 2");
        rulesPageBtn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rulesPageBtn.setStyle("-fx-background-color: #87CEEB;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #87CEEB;" +
                        "-fx-border-radius: 2");
            }
        });
        rulesPageBtn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rulesPageBtn.setStyle("-fx-background-color: #F4F3EF;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #87CEEB;" +
                        "-fx-border-radius: 2");
            }
        });
        rulesPageBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                createRulesPage();
                event.consume();
            }
        });

        //---------------------------------------------------------------------//


        //----------FOR CIRCLE HIGHLIGHTING----------//
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.BLACK);
        fakePlayerPegs[currentPointer].setEffect(dropShadow);
        //--------------------------------------------//

        Button skip = new Button("I Got This!"); // if the user is too good and just want to start the game
        skip.setStyle("-fx-background-color: #F4F3EF;" +
                "-fx-font-family: Cambria;" +
                "-fx-font-weight: bold;" +
                "-fx-border-width: 2px;" +
                "-fx-border-color: #24FF00;" +
                "-fx-border-radius: 2");
        skip.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                skip.setStyle("-fx-background-color: #24FF00;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #24FF00;" +
                        "-fx-border-radius: 2");
            }
        });
        skip.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                skip.setStyle("-fx-background-color: #F4F3EF;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #24FF00;" +
                        "-fx-border-radius: 2");
            }
        });
        skip.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
                mainScreen.close();
            }
        });

        pagination.setPageFactory((pageIndex) -> {
            GridPane gridPane = new GridPane(); // create grid pane/page

            //--------STYLING---------//
            gridPane.setHgap(100);
            gridPane.setVgap(40);
            gridPane.setPadding(new Insets(30));
            //------------------------//

            if (pageIndex == 0) { // first page
                for (int i = 0; i < 4; i++) {
                    fakePlayerPegs[i].setFill(Color.GRAY);
                }
                gridPane.add(textArea1, 0, 0);
                gridPane.add(fakePegBox, 1, 0);
                return gridPane;
            } else if (pageIndex == 1) { // second page
                gridPane.add(textArea2, 0, 0);
                gridPane.add(fakeColorWheelBox, 1, 0);
                return gridPane;
            }

            // third page
            //----------EXTRA GRIDPANE-----------//
            // for styling:
            GridPane gridPane2 = new GridPane();
            gridPane2.setVgap(30);
            gridPane2.setPadding(new Insets(100, 0, 0, 0));
            gridPane2.add(rulesPageBtn, 0, 0);
            gridPane2.add(skip, 0, 1);
            //------------------------------------//

            gridPane.add(textArea3, 0, 0);
            gridPane.add(gridPane2, 1, 0);
            return gridPane;
        }); // set up pages

        pagination.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                fakePlayerPegs[fakeCurrentPointer].setEffect(null);
                if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                    if (fakeCurrentPointer == 0) fakeCurrentPointer = 3;
                    else fakeCurrentPointer = (fakeCurrentPointer - 1) % 4;
                }
                if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                    fakeCurrentPointer = (fakeCurrentPointer + 1) % 4;
                }
                fakePlayerPegs[fakeCurrentPointer].setEffect(dropShadow);
                event.consume();
            }
        }); // adding key events for the interaction of fakePlayerCode

        //-------------PAGE SETUP---------------//
        pagination.setPageCount(3);
        pagination.setCurrentPageIndex(0);
        pagination.setMaxPageIndicatorCount(3);
        pagination.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        pagination.setStyle("-fx-font: 28 arial;");
        //---------------------------------------//

        VBox vbox = new VBox(pagination);
        vbox.setBackground(background);
        Scene mainScene = new Scene(vbox, WINDOW_WIDTH, WINDOW_HEIGHT);
        mainScreen.setScene(mainScene);
        mainScreen.showAndWait();
    }

    /**
     * Is a helper method:
     *
     * @Function: creating the how to play screen for players for do not know how to use the GUI
     * @see #createMainScreen()
     */
    public void createPlayerHowToPlayScreen() { // the source, eg player vs AI, AI vs player etc
        Stage mainScreen = new Stage();
        mainScreen.setTitle("How To Play");
        mainScreen.initModality(Modality.APPLICATION_MODAL); // so player has to interact with this screen first
        BackgroundImage backGroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backGroundImage);
        Pagination pagination = new Pagination(); // creating a page style book

        //----------------CONTENT------------------//
        HBox fakeCircleBox = new HBox(); // for the fake circle box interaction
        fakeCircleBox.setSpacing(30);

        TextArea textArea1 = new TextArea(); // telling player how to use change color
        textArea1.setText("You can use the the arrow keys (right and left) or the letters A and D to change the selected circle. The circle" +
                "that you have chosen will be highlighted. Try it on the sample to the right");
        textArea1.setEditable(false);
        textArea1.setWrapText(true);
        textArea1.setFont(new Font(24));
        textArea1.setPrefSize(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 3);

        Circle fakePlayerCode[] = new Circle[4]; // create circles for the fake codes
        for (int i = 0; i < 4; i++) {
            fakePlayerCode[i] = new Circle(20, Color.GRAY); // set radius and style
            fakeCircleBox.getChildren().add(fakePlayerCode[i]); // add to fake circle box
        }

        HBox fakeColorWheelBox = new HBox(); // for the fake color wheel
        TextArea textArea2 = new TextArea();
        textArea2.setText("You use the mouse and scroll to change the circle, scroll on the circle to change the colour. If you do not have mouse with " +
                "a scroll wheel, you can swip on your track pad with two fingers inorder to turn the circle. Test out how to " +
                "scroll with the sample color wheel on the right.");
        textArea2.setEditable(false);
        textArea2.setWrapText(true);
        textArea2.setFont(new Font(24));
        textArea2.setPrefSize(500, 300);

        Group fakeColorWheel = getColorWheel(player.FXcolors, fakePlayerCode); // get the fake color wheel
        fakeColorWheelBox.getChildren().add(fakeColorWheel); // add it to the fake color wheel box


        TextArea textArea3 = new TextArea();
        textArea3.setText("After you are done making your guess, press the check button and the AI will give you feedback with pegs. " +
                "If you are confused with the rules of code breaker, I suggest your to read over our rules page. You can click the button " +
                "to open up the rules page which is on the right");
        textArea3.setEditable(false);
        textArea3.setWrapText(true);
        textArea3.setFont(new Font(24));
        textArea3.setPrefSize(500, 300);

        TextArea textArea4 = new TextArea();
        textArea4.setText("For this gamemode, there will be a timer which you can set by pressing the set-time function in the menu at the top left " +
                "of the screen. After you set the timer and have made your first guess, the timer will start. Remember, the timer is for the entire game and does not reset" +
                "after every guess. Furthermore, the timer will be saved and when you load a saved file, the timer will remaint the same. There will also be a pause and " +
                "resume timer function that allows you to pause the time and resume the time if you had to be away from the game.");
        textArea4.setEditable(false);
        textArea4.setWrapText(true);
        textArea4.setFont(new Font(24));
        textArea4.setPrefSize(500, 550);

        Button rulesPageBtn = new Button("Go To Rules Page"); // create button for user to go to rules page
        rulesPageBtn.setStyle("-fx-background-color: #F4F3EF;" +
                "-fx-font-family: Cambria;" +
                "-fx-font-weight: bold;" +
                "-fx-border-width: 2px;" +
                "-fx-border-color: #87CEEB;" +
                "-fx-border-radius: 2");
        rulesPageBtn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rulesPageBtn.setStyle("-fx-background-color: #87CEEB;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #87CEEB;" +
                        "-fx-border-radius: 2");
            }
        });
        rulesPageBtn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rulesPageBtn.setStyle("-fx-background-color: #F4F3EF;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #87CEEB;" +
                        "-fx-border-radius: 2");
            }
        });
        rulesPageBtn.setOnMouseClicked(new EventHandler<MouseEvent>() { // TODO add rules screen
            @Override
            public void handle(MouseEvent event) {
                createRulesPage();
                event.consume();
            }
        });

        //---------------------------------------------------------------------//


        //----------FOR CIRCLE HIGHLIGHTING----------//
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.BLACK);
        fakePlayerCode[currentPointer].setEffect(dropShadow);
        //--------------------------------------------//

        Button skip = new Button("I Got This!"); // if the user is too good and just want to start the game
        skip.setStyle("-fx-background-color: #F4F3EF;" +
                "-fx-font-family: Cambria;" +
                "-fx-font-weight: bold;" +
                "-fx-border-width: 2px;" +
                "-fx-border-color: #24FF00;" +
                "-fx-border-radius: 2");
        skip.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                skip.setStyle("-fx-background-color: #24FF00;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #24FF00;" +
                        "-fx-border-radius: 2");
            }
        });
        skip.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                skip.setStyle("-fx-background-color: #F4F3EF;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #24FF00;" +
                        "-fx-border-radius: 2");
            }
        });
        skip.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
                mainScreen.close();
            }
        });

        pagination.setPageFactory((pageIndex) -> {
            GridPane gridPane = new GridPane(); // create grid pane/page

            //--------STYLING---------//
            gridPane.setHgap(100);
            gridPane.setVgap(40);
            gridPane.setPadding(new Insets(30));
            //------------------------//

            if (pageIndex == 0) { // first page
                for (int i = 0; i < 4; i++) {
                    fakePlayerCode[i].setFill(Color.GRAY);// set radius and style
                }
                gridPane.add(textArea1, 0, 0);
                gridPane.add(fakeCircleBox, 1, 0);
                return gridPane;
            } else if (pageIndex == 1) { // second page
                gridPane.add(textArea2, 0, 0);
                gridPane.add(fakeColorWheelBox, 1, 0);
                return gridPane;
            } else if (pageIndex == 2) {
                gridPane.add(textArea3, 0, 0);
                return gridPane;
            }

            // third page
            //----------EXTRA GRIDPANE-----------//
            // for styling:
            GridPane gridPane2 = new GridPane();
            gridPane2.setVgap(30);
            gridPane2.setPadding(new Insets(100, 0, 0, 0));
            gridPane2.add(rulesPageBtn, 0, 0);
            gridPane2.add(skip, 0, 1);
            //------------------------------------//

            gridPane.add(textArea4, 0, 0);
            gridPane.add(gridPane2, 1, 0);
            return gridPane;
        }); // set up pages

        pagination.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                fakePlayerCode[fakeCurrentPointer].setEffect(null);
                if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                    if (fakeCurrentPointer == 0) fakeCurrentPointer = 3;
                    else fakeCurrentPointer = (fakeCurrentPointer - 1) % 4;
                }
                if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                    fakeCurrentPointer = (fakeCurrentPointer + 1) % 4;
                }
                fakePlayerCode[fakeCurrentPointer].setEffect(dropShadow);
                event.consume();
            }
        }); // adding key events for the interaction of fakePlayerCode

        //-------------PAGE SETUP---------------//
        pagination.setPageCount(4);
        pagination.setCurrentPageIndex(0);
        pagination.setMaxPageIndicatorCount(4);
        pagination.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        pagination.setStyle("-fx-font: 28 arial;");
        //---------------------------------------//

        VBox vbox = new VBox(pagination);
        vbox.setBackground(background);
        Scene mainScene = new Scene(vbox, WINDOW_WIDTH, WINDOW_HEIGHT);
        mainScreen.setScene(mainScene);
        mainScreen.showAndWait();
    }

    /**
     * A helper method:
     *
     * @Function: the purpose of this method is to draw the code that the AI guesses on to the GUI
     * @see AI#BustCode()
     * @see #drawCode()
     */
    public void drawCode() {
        // System.out.println("Drawing "+Code+" at row "+currentRowIndex);
        // System.out.println("Player code length: " + player.getCodeLength());
        for (int j = 0; j < player.CodeLength; j++) { // loop through the player's code
            gameGrid[currentRowIndex][j] = new Circle(gameGrid[currentRowIndex][j].getRadius(),
                    convertStringToColor(Code.charAt(j) + "", player.FXcolors)); // draw the new circles
        }
        //System.out.println("Drew "+Code+" at row "+currentRowIndex);
    }

    /**
     * Interacts/helps another method
     *
     * @param code: the code which is being sent
     * @Function: The purpose of this method is to send the code from the AI class to the GUI class for it to draw onto the GUI
     * @see #drawCode()
     * @see AI#BustCode()
     */
    public void sendCode(String code) {
        Code = code; // set the new code
        if (Code.equals("invalid")) { // if its a invalid code
            AIGameOver("It seems like you've entered some invalid input, as the AI is unable to come up with a guess that matches your entries...");
        }
        drawCode(); // drawing the code onto game grid (not feed back pegs) // draw the code
        System.out.println(code + " Recieved"); // DEBUG
    }

    /**
     * @Function: the purpose of this method is to create the player vs AI screen, where the AI creates a code, and the user guesses
     * Uses the following methods:
     * @see #initAICode()
     * @see #playerChangeRow()
     * @see #getGameGrid()
     * @see #createPopUpWindow(String, String)
     * @see #checkPlayerWin()
     */
    private void createPlayerGameScreen(boolean loading) {
        player.usingTimer = false; // player is assumed to not use the timer at the start
        BackgroundImage backGroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backGroundImage);

        BorderPane gamePane = new BorderPane();
        gamePane.setBackground(background);
        //-----------Initialize Important Variables-------------//
        currentPointer = 0;
        if (!loading) {
            currentRowIndex = MAX_ROW_SIZE - 1;
            initAICode();
        }
        for (int i = 0; i < MAX_COL_SIZE; i++) {
            playerCode[i] = new Circle(20, Color.GRAY);
        }
        //-----------------------------------------------------//

        gameScreen.setTitle("Code Breaker"); // set title
        Scene gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);


        //Menu Stuffs
        MenuBar PauseMenu = new MenuBar();

        Menu save = new Menu("Save Game");
        MenuItem saveButton = new MenuItem("Save to File");
        saveButton.setOnAction(new EventHandler<ActionEvent>() { // if user clicks on it
            @Override
            public void handle(ActionEvent event) {
                timeline.pause(); // DEBUG
                //Write saveData to the file
                GetFiles.setTitle("Choose a Place to save your file (remember to end your file name with \".txt\"");
                File saveData; // Open the file from user input
                do {//if they decide not to enter anything like snakes, pop-up spam them
                    saveData = GetFiles.showSaveDialog(gameScreen);
                    if (saveData == null) {
                        return;
                    }
                    if (!saveData.getName().contains(".txt")) {
                        createPopUpWindow("Not a valid File.", "Invalid Input");
                    }
                } while (!saveData.getName().contains(".txt"));

                try {
                    saveData.createNewFile();
                    PrintWriter writeData = new PrintWriter(saveData);
                    //Write stuffs
                    writeData.println("Player " + computer.mode + " " + MAX_COL_SIZE + " " + player.getColorLength() + " " + currentRowIndex);
                    writeData.println(GameGridString());
                    writeData.println(AICodeStr);
                    System.out.println("Wrote: " + AICodeStr);
                    writeData.close();
                    createPopUpWindow("File saved Successfully at " + saveData.getName(), "Success!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        save.getItems().add(saveButton);

        Menu Quit = new Menu("Quit Game");
        MenuItem Exit = new MenuItem("Exit to menu");
        Exit.setOnAction(new EventHandler<ActionEvent>() { // if user clicks on it
            @Override
            public void handle(ActionEvent event) {
                timeline.stop();
                timeSeconds.set(120); // setting default seconds
                createMainScreen();
            }
        });
        Quit.getItems().add(Exit);

        Menu timerMenu = new Menu("Set Timer");
        MenuItem timerItem = new Menu("Set Timer");
        timerItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage timerWindow = new Stage();
                HBox hbox = new HBox();
                timerWindow.setTitle("Setting Up Timer");
                timerWindow.initModality(Modality.APPLICATION_MODAL);

                Label message = new Label("Please enter a time in seconds (1-600):");
                message.setWrapText(true);
                message.setStyle("-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: Lato;");
                message.setPrefSize(300, 10);
                TextField textField = new TextField();
                textField.setPrefSize(50, 10);
                Button submit = new Button("Set");
                submit.setPrefSize(50, 10);
                submit.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (textField.getText() == null || !(new Scanner(textField.getText()).hasNextInt()) ||
                                new Scanner(textField.getText()).nextInt() <= 0 || new Scanner(textField.getText()).nextInt() > 600) {
                            createPopUpWindow("Invalid input. Please Re-enter", "Attention");
                            event.consume();
                            return;
                        }
                        timeSeconds.set(Integer.parseInt(textField.getText()));
                        player.setSeconds(Integer.parseInt(textField.getText()));
                        createPopUpWindow("Timer Set!", "Note");
                        player.usingTimer = true;
                    }
                });

                hbox.getChildren().addAll(message, textField, submit);
                hbox.setPadding(new Insets(30));
                hbox.setSpacing(10);
                Scene scene = new Scene(hbox, 500, 100);
                timerWindow.setScene(scene);
                timerWindow.showAndWait();
            }
        });
        save.setStyle("-fx-background-color: #DBB2D1;" +
                "-fx-font: 18 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 3 3 3 3;" +
                "-fx-background-radius: 3 3 3 3;");
        Quit.setStyle("-fx-background-color: #6CA0DC;" +
                "-fx-font: 18 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 3 3 3 3;" +
                "-fx-background-radius: 3 3 3 3;");
        timerMenu.setStyle("-fx-background-color: #645394;" +
                "-fx-font: 18 arial;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 3 3 3 3;" +
                "-fx-background-radius: 3 3 3 3;");
        timerMenu.getItems().add(timerItem);

        PauseMenu.getMenus().addAll(save, Quit, timerMenu);
        PauseMenu.setPadding(new Insets(5, 10, 5, 10));
        Button checkButton = new Button("Check"); // create a checkButton

        //---------------CREATE THE GUESS BOX------------------------//
        HBox guessBox = new HBox();
        guessBox.setSpacing(30);
        for (int i = 0; i < MAX_COL_SIZE; i++) {
            guessBox.getChildren().add(playerCode[i]);
        }
        Separator s = new Separator();
        s.setOrientation(Orientation.VERTICAL);
        checkButton.setAlignment(Pos.CENTER_LEFT);

        guessBox.getChildren().addAll(s, checkButton);
        //-----------------------------------------------------------//

        checkButton.setStyle("-fx-background-color: #F4F3EF;" +
                "-fx-font-family: Cambria;" +
                "-fx-font-weight: bold;" +
                "-fx-border-width: 2px;" +
                "-fx-border-color: #87CEEB;" +
                "-fx-border-radius: 5;" +
                "-fx-font-size: 20");
        checkButton.setOnMouseClicked(new EventHandler<MouseEvent>() { // if user clicks on it
            @Override
            public void handle(MouseEvent event) {
                if (!checkRowIsFinished()) { // if the row is yet to be completely filled
                    createPopUpWindow("Please Fill Out The Entire Row", "Attention"); // create pop up window
                    return;
                }
                if (currentRowIndex == MAX_ROW_SIZE - 1 && player.usingTimer) {
                    timeline = new Timeline();
                    timeSeconds.set(player.seconds);
                    timeline = new Timeline();
                    timeline.getKeyFrames().add(
                            new KeyFrame(Duration.seconds(player.seconds + 1),
                                    new KeyValue(timeSeconds, 0)));
                    timeline.play();
                    timeline.setOnFinished(end -> {
                        timeline.stop();
                        playerGameOver(checkPlayerWin(), true);
                        System.out.println("Countdown finished");
                    });
                }

                playerChangeRow(); // change the row
                player.tries++;
                //------------GETTING NEW GRID PANE-----------------//
                GridPane gameGridPane = getGameGrid();
                s.setOrientation(Orientation.HORIZONTAL);
                gameGridPane.add(s, 0, MAX_ROW_SIZE);
                gameGridPane.add(guessBox, 0, MAX_ROW_SIZE + 1);
                gameGridPane.setPadding(new Insets(30));
                //---------------------------------------------------//

                gamePane.setCenter(gameGridPane); // put the new grid pane on the screen

                if (checkPlayerWin()) { // if player has guessed the code
                    playerGameOver(true, player.usingTimer); // create screen
                    return; // stop executing the rest of the method
                }
                if (currentRowIndex < 0) { // if there are no more guesses left
                    playerGameOver(checkPlayerWin(), player.usingTimer); // check if player lost or won, and display the window
                }
            }
        });
        checkButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                checkButton.setStyle("-fx-background-color: #87CEEB;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #87CEEB;" +
                        "-fx-border-radius: 5;" +
                        "-fx-font-size: 20");
            }
        });
        checkButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                checkButton.setStyle("-fx-background-color: #F4F3EF;" +
                        "-fx-font-family: Cambria;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-color: #87CEEB;" +
                        "-fx-border-radius: 5;" +
                        "-fx-font-size: 20");
            }
        });

        checkButton.setAlignment(Pos.CENTER); // style

        if (!loading) {
            initGameGridAndPegs(); // initialize the game grid and pegs
        } else {
            gameGrid = placeHolderGrid;
            gamePegs = placeHolderPegs;
        }

        GridPane gameGridPane = getGameGrid(); // get the game grid
        Group colorWheel = getColorWheel(player.FXcolors, playerCode); // get the color wheel

        //-------------------CREATING WHEEL BOX------------------//
        HBox wheelBox = new HBox();
        wheelBox.getChildren().add(colorWheel);
        wheelBox.setAlignment(Pos.CENTER);
        wheelBox.setPadding(new Insets(30));
        //-------------------------------------------------------//

        s.setOrientation(Orientation.HORIZONTAL);
        gameGridPane.add(s, 0, MAX_ROW_SIZE);
        gameGridPane.add(guessBox, 0, MAX_ROW_SIZE + 1);
        gameGridPane.setPadding(new Insets(30));

        gamePane.setTop(PauseMenu);
        gamePane.setCenter(gameGridPane);
        gamePane.setBottom(wheelBox);

        //----------------TIMER--------------------//
        GridPane timerBox = new GridPane();
        Label countDown = new Label();
        Label emptySpace = new Label();

        timeSeconds.set(120); // default time
        Button resumeTimer = new Button("Resume Timer");

        resumeTimer.setOnMouseClicked(event -> {
            timeline.play();
        });

        Button pauseTimer = new Button("Pause Timer");
        pauseTimer.setOnMouseClicked(event -> {
            timeline.pause();
        });

        countDown.textProperty().bind(timeSeconds.asString());

        countDown.setStyle("-fx-font-family: 'Digital-7 Mono';" +
                "-fx-font-size: 30;" +
                "-fx-text-fill: red;");
        timerBox.add(emptySpace, 10, 0);
        timerBox.add(countDown, 1, 0);
        timerBox.add(pauseTimer, 0, 1);
        timerBox.add(resumeTimer, 1, 1);

        timerBox.setPadding(new Insets(100));


        timerBox.setHgap(10);

        gamePane.setRight(timerBox);
        //-----------------------------------------------------------------//

        gamePane.setPadding(new Insets(0, 0, 30, 0)); // styles
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.BLACK);
        playerCode[currentPointer].setEffect(dropShadow);
        gamePane.setOnKeyPressed(new EventHandler<KeyEvent>() { // for playerCode interaction
            @Override
            public void handle(KeyEvent event) {
                playerCode[currentPointer].setEffect(null); // erase the previous codes highlighting
                if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) { // if the user is going left
                    if (currentPointer == 0) currentPointer = MAX_COL_SIZE - 1; // so it doesn't go negative
                    else currentPointer = currentPointer - 1;
                }
                if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) { // if the user is going right
                    currentPointer = (currentPointer + 1) % MAX_COL_SIZE; // so it doesn't go over the MAX_COL_SIZE
                }
                playerCode[currentPointer].setEffect(dropShadow); // set the highlight to the new circle
                event.consume(); // consume the event
            }
        });


        gameScreen.setScene(gameScene);
        gameScreen.show();
    }

    /**
     * helper method:
     *
     * @return true: if the row is finished, false otherwise
     * @Function: the purpose of this method is to check if the user has filled in his row for the player vs AI mode
     * @see #createPlayerGameScreen(boolean)
     */
    private boolean checkRowIsFinished() {
        for (int i = 0; i < MAX_COL_SIZE; i++) { // loop through the player's Code
            if (playerCode[i].getFill().equals(Color.GRAY)) { // if there are spots left to fill
                return false; // not completed
            }
        }
        return true; // completed
    }

    /**
     * helper method:
     *
     * @return true: if the player peg is all filled, false otherwise
     * @Function: the purpose of this method is to check if the user has filled in his row for the AI vs player mode
     * @see #createAIGameScreen(boolean)
     */
    private boolean checkRowIsFinishedAI() {
        for (int i = 0; i < MAX_COL_SIZE; i++) { // loop through the player pegs
            if (playerPegs[i].getFill().equals(Color.GRAY)) { // if there is still pegs to be filled
                return false; // not ocmpleted
            }
        }
        return true; // completed
    }

    /**
     * helper method:
     *
     * @param messageContent: the content of the message
     * @param windowTitle:    the title of the window
     * @Functino: the purpose of this method is to create a pop up window to tell the user for things such as invalid input
     * @see #createStartScreen()
     * @see #createAIGameScreen(boolean)
     * @see #createPlayerGameScreen(boolean)
     */
    private void createPopUpWindow(String messageContent, String windowTitle) {
        Stage popUpStage = new Stage();
        popUpStage.setTitle(windowTitle); // set title
        popUpStage.initModality(Modality.APPLICATION_MODAL); // so the user has to interact with this window first
        BackgroundImage backGroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background = new Background(backGroundImage);

        Label message = new Label(messageContent); // set the message
        message.setWrapText(true);
        message.setAlignment(Pos.TOP_LEFT); // wrapping text and setting the message to the center of the screen
        message.setStyle("-fx-font-family: Lato;" +
                "-fx-font-size: 14;");
        TextField tf = new TextField(messageContent);
        tf.setAlignment(Pos.CENTER);
        tf.setStyle("-fx-font-family: Lato;" +
                "-fx-font-size: 14;");
        tf.setBackground(background);
        Scene popUpScene = new Scene(tf, 400, 100);
        popUpStage.setScene(popUpScene);
        popUpStage.showAndWait();
    }

    /**
     * @Function: The purpose of this method is to change the guess pegs to match the players guess and display it on the screen
     * Uses a method:
     * @see #changeComputerFeedbackPegs()
     */
    private void playerChangeRow() {
        for (int j = 0; j < MAX_COL_SIZE; j++) { // loop through the row
            gameGrid[currentRowIndex][j] = new Circle(playerCode[j].getRadius(), playerCode[j].getFill()); // change the circle to the player's code
        }
        changeComputerFeedbackPegs(); // change the feedback pegs
        currentRowIndex--; // move to the next row/guess
    }


    /**
     * @Function: the purpose of this method is to change the feedback pegs after a player has enter a guess
     */
    private void changeComputerFeedbackPegs() {
        int rightColor = 0, rightColorAndPosition = 0; // right color, and right color and position counters
        boolean[] correctIndexes = new boolean[player.CodeLength];
        boolean[] usedIndexes = new boolean[player.CodeLength];

        for (int i = 0; i < MAX_COL_SIZE; i++) { // loop through the AI's code
            if (playerCode[i].getFill().equals(AICode[i].getFill())) { // if same color and position
                rightColorAndPosition++; // increment
                correctIndexes[i] = true; // set correctIndexed to true, so we don't overlap in our next for loop
            }
        }

        for (int i = 0; i < MAX_COL_SIZE; i++) { // loop through the colors again, needs a second for loop for logic reasons
            if (!correctIndexes[i]) { // if we havne't mark this index has a correct position and color peg yet
                for (int j = 0; j < MAX_COL_SIZE; j++) {
                    // check if the index has not been just yet, and the colors match, and if it is ONLY right color
                    if (!usedIndexes[j] && playerCode[i].getFill().equals(AICode[j].getFill()) && !correctIndexes[j]) {
                        usedIndexes[j] = true; // set used to true
                        rightColor++; // increment rightColor accumulator
                        break; // move to next color
                    }
                }
            }
        }
        int idx = 0; // column index
        for (int i = 0; i < rightColorAndPosition; i++) { // for the right color and position
            gamePegs[currentRowIndex][idx] = new Rectangle(20, 20, Color.RED); // make a new rectangle
            gamePegs[currentRowIndex][idx].setArcHeight(10);
            gamePegs[currentRowIndex][idx].setArcWidth(10); // rounded corners
            idx++; // increment index
        }
        for (int i = 0; i < rightColor; i++) { // for the right color
            gamePegs[currentRowIndex][idx] = new Rectangle(20, 20, Color.WHITE); // make a new rectangle
            gamePegs[currentRowIndex][idx].setArcHeight(10);
            gamePegs[currentRowIndex][idx].setArcWidth(10); // rounded corners
            idx++; // increment index
        }
    }


    /**
     * @Function: the purpose of this method is to get the game grid in the form of an GridPane
     * @return: the gameGameGrid (GridPane)
     */
    public GridPane getGameGrid() {
        GridPane gameGridPane = new GridPane(); // the game grid
        for (int r = 0; r < MAX_ROW_SIZE; r++) { // loop through the rows
            HBox row = new HBox();
            row.setSpacing(30.0); // make a horizontal box and set spacing for the items
            for (int c = 0; c < MAX_COL_SIZE; c++) { // loop through the columns
                row.getChildren().add(gameGrid[r][c]); // add the guess circles
            }
            Separator s = new Separator();
            s.setOrientation(Orientation.VERTICAL); // make a vertical separator
            row.getChildren().add(s); // add it to the row
            for (int c = 0; c < MAX_COL_SIZE; c++) { // loop through the columns
                row.getChildren().add(gamePegs[r][c]); // add the feedback pegs
            }
            gameGridPane.add(row, 0, r); // put the row onto the game grid
        }
        //-----------Styles------------//
        gameGridPane.setVgap(10.0);
        gameGridPane.setAlignment(Pos.CENTER);
        //-----------------------------//

        return gameGridPane; // return the gameGrid
    }

    /**
     * helper method:
     *
     * @return true: if the player has won, and false when the player has not won yet
     * @Function: The purpose of this method is to check if the player has won
     * @see #createPlayerGameScreen(boolean)
     */
    private boolean checkPlayerWin() {
        for (int i = 0; i < MAX_COL_SIZE; i++) { // loop through the Code
            if (!playerCode[i].getFill().equals(AICode[i].getFill())) { // check to see if all colors match
                return false; // return false
            }
        }
        return true; //return true;
    }

    /**
     * helper method:
     *
     * @param color:  the color we want to convert to a string
     * @param colors: the array of colors that correspond to the string (the color)
     * @return String: the string that corresponds to the color that we want to convert
     * @see #createAIGameScreen(boolean)
     */
    private String convertColorToString(Color color, Color[] colors) {
        for (int i = 0; i < colors.length; i++) { // loop through the colors
            if (color.equals(colors[i])) { // if its the same color
                return player.colors[i]; // return that color in form of string
            }
        }
        return null; // see if there is an error
    }

    /**
     * Helper method in AI class
     *
     * @param colorChar: The color in form a character in of the code
     * @return circleColors[idx]: the color that corresponds to the color
     * @Fuction: convert a character in form of string to convert to its corresponding color
     * @see AI#ConvertCodeToCircles(String)
     */
    public Color convertStringToColor(String colorChar, Color[] Color) {
        for (int i = 0; i < Color.length; i++) { // loop through the colors
            if (player.colors[i].equals(colorChar)) { // if we found a color that matches this color
                return Color[i]; // return that color
            }
        }
        return null; // return null in case anything bad happens
    }

    /**
     * helper method:
     *
     * @param win: a boolean variable that is set to true if the player has won, and false otherwise
     * @Function: To create a window screen for the scenarios where the player lost or guessed the code
     * @see #createPlayerGameScreen(boolean)
     */
    private void playerGameOver(boolean win, boolean timer) { // TODO make stuff look nice
        Stage popUpWindow = new Stage(); // create new window
        popUpWindow.initModality(Modality.APPLICATION_MODAL); // so user has to interact with this window before using other windows
        popUpWindow.setOnCloseRequest(new EventHandler<WindowEvent>() { // if they close this window
            @Override
            public void handle(WindowEvent event) { // if the user closes this window
                event.consume();
                System.exit(0); // terminate the program
            }
        });

        GridPane gridPane = new GridPane(); // create pane
        timeline.stop();
        // set base message
        Label message = new Label("Player " + player.getPlayerName() + " Has Guessed the code! in " + player.tries + " tries!");

        message.setAlignment(Pos.CENTER); // put it to the CENTER
        HBox codeBox = new HBox();
        codeBox.setSpacing(30); // set spacing
        if (!win) { // if the player has not won
            if (!timer) {
                message.setText("Player " + player.getPlayerName() + " Has Lost! The Code Was:"); // set message
            } else {
                message.setText("The time is up! Player " + player.getPlayerName() + " Has Lost! The Code Was:");
            }
            message.setFont(new Font("Arial", 18)); // set font
            for (Circle circle : AICode) { // get code
                codeBox.getChildren().add(circle); // add to hbox
            }
            gridPane.add(codeBox, 0, 1); // add it to gridpane
        }
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20); // set buttons box and spacing
        Button playAgainBtn = new Button("Play Again? (Go To Menu)"); // set message for button
        playAgainBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { // if user wants to playagain
                player.playAgain("Player"); // the user wants to play again
                popUpWindow.close();
                createMainScreen(); // go to the main screen
            }
        });
        Button exitBtn = new Button("Quit Program"); // quit button
        exitBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.exit(0); // terminate the program
            }
        });
        buttonBox.getChildren().addAll(playAgainBtn, exitBtn); // add to box
        gridPane.add(message, 0, 0); // add to first row of pane
        gridPane.add(buttonBox, 0, 2); // add to third row of pane
        gridPane.setVgap(10); // set vertical gapes
        gridPane.setAlignment(Pos.CENTER); // algin to center

        Scene popUpScene = new Scene(gridPane, 600, 200);
        popUpWindow.setScene(popUpScene);
        popUpWindow.show();
    }

    /**
     * @Function: creates the colors that are avaliable to you when the user creates the code
     */
    private void createColorAvailable() {
        Stage popUp = new Stage();
        popUp.setTitle("Colors Available");

        Label message = new Label("Colors that are available to you");
        GridPane colorBox = new GridPane();
        colorBox.setPadding(new Insets(30));

        HBox hbox1 = new HBox();
        hbox1.setSpacing(30);
        for (int i = 0; i < player.ColorLength; i++) {
            Circle circle = new Circle(20);
            circle.setFill(player.FXcolors[i]);
            hbox1.getChildren().add(circle);
        }


        colorBox.add(message, 0, 0);
        colorBox.add(hbox1, 0, 1);
        colorBox.setVgap(20);

        Scene scene = new Scene(colorBox, 500, 200);
        popUp.setScene(scene);
        popUp.show();
    }

    /**
     * helper method:
     *
     * @param msg: the message in string that the window is displaying
     * @Function: the purpose of this method is to provide a gameOver screen for the AI vs player mode
     * @see #createAIGameScreen(boolean)
     * @see #sendCode(String)
     */
    private void AIGameOver(String msg) { // TODO make stuff look nice
        Stage popUpWindow = new Stage(); // create new window
        popUpWindow.initModality(Modality.APPLICATION_MODAL); // so user has to interact with this window before using other windows
        popUpWindow.setOnCloseRequest(new EventHandler<WindowEvent>() { // if they close this window
            @Override
            public void handle(WindowEvent event) { // if the user closes this window
                event.consume();
                System.exit(0); // terminate the program
            }
        });
        GridPane gridPane = new GridPane(); // create pane
        Label message = new Label(msg); // set base message
        message.setWrapText(true);
        message.setFont(new Font("Arial", 18)); // set font
        message.setAlignment(Pos.CENTER); // put it to the CENTER

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20); // set buttons box and spacing
        Button playAgainBtn = new Button("Play Again? (Go To Menu)"); // set message for button
        buttonBox.setPadding(new Insets(10));
        message.setPadding(new Insets(20));
        playAgainBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { // if user wants to playagain
                popUpWindow.close();
                createMainScreen(); // go to the main screen
            }
        });
        Button exitBtn = new Button("Quit Program"); // quit button
        exitBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.exit(0); // terminate the program
            }
        });
        buttonBox.getChildren().addAll(playAgainBtn, exitBtn); // add to box
        gridPane.add(message, 0, 0); // add to first row of pane
        gridPane.add(buttonBox, 0, 2); // add to third row of pane
        gridPane.setVgap(10); // set vertical gapes
        gridPane.setAlignment(Pos.CENTER); // algin to center

        Scene popUpScene = new Scene(gridPane, 600, 200);
        popUpWindow.setScene(popUpScene);
        popUpWindow.show();
    }

    /**
     * main method
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}