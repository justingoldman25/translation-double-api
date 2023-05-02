package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    Stage stage;
    Scene scene;
    VBox root;
    HBox topBox;
    HBox instructions;
    HBox term;
    HBox definition;
    HBox spanish;
    HBox french;
    HBox bottomBar;
    Button searchButton;
    TextField searchBar;


    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
        root.setPrefHeight(500);
        topBox = new HBox();
        instructions = new HBox();
        term = new HBox();
        definition = new HBox();
        spanish = new HBox();
        french = new HBox();
        bottomBar = new HBox();
        searchButton = new Button();
        searchBar = new TextField();

    } // ApiApp


    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;

        // demonstrate how to load local asset using "file:resources/"
        Image bannerImage = new Image("file:resources/readme-banner.png");
        ImageView banner = new ImageView(bannerImage);
        banner.setPreserveRatio(true);
        banner.setFitWidth(640);

        // some labels to display information
        Label notice = new Label("Modify the starter code to suit your needs.");

        // setup scene
        root.getChildren().addAll(topBox, instructions, term, definition, spanish, french);
        root.getChildren().add(bottomBar);
        scene = new Scene(root);

        //setup top bar
        Label termLabel = new Label("Term:");
        Button searchButton = new Button("Search");
        topBox.setSpacing(50);
        topBox.getChildren().addAll(termLabel, searchBar, searchButton);

        //setup instructions bar
        Label instructionsLabel = new Label("Type your term into the box and press search.");
        instructions.getChildren().add(instructionsLabel);
        // setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

} // ApiApp
