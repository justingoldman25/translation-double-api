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
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import javafx.concurrent.Task;

import java.io.IOException;
import java.lang.InterruptedException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {

    /**
       JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    Stage stage;
    Scene scene;
    VBox root;
    HBox topBox;
    HBox instructions;
    HBox termBar;
    HBox definitionBar;
    HBox spanishBar;
    HBox frenchBar;
    HBox bottomBar;
    Button searchButton;
    TextField searchBar;
    Label instructionsLabel;
    Label definitionLabel;

    String uriString;
    DictionaryResponse dictResp;




    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
        root.setPrefHeight(500);
        topBox = new HBox();
        instructions = new HBox();
        termBar = new HBox();
        definitionBar = new HBox();
        spanishBar = new HBox();
        frenchBar = new HBox();
        bottomBar = new HBox();
        searchButton = new Button();
        searchBar = new TextField();


    } // ApiApp

/**
   Private method to make a new thread and run task t in it.
   @param r the task to be run
*/

    private void runInNewThread(Runnable r) {
        Thread newThread = new Thread(r);
        newThread.setDaemon(true);
        newThread.start();
    }
/**
   Private method to build an error and show it.
   @param errorString the string that will be shown
*/

    private void showError(final String errorString) {

        Alert error = new Alert(AlertType.ERROR, errorString);
        error.show();

    }

/**
   Private method to update the GUI for the  definition search.
   @param term to update the search with
*/

    private void updateDictionarySearch(String term) {
        Platform.runLater(() -> searchButton.setDisable(true));
        DictionaryResponse response = dictionaryAPIFetch(term);
        String substringresponse = response.meanings[0].definitions[0].definition;
        String newLabel = "Now showing definition for word: '" + term + "'";
        Platform.runLater(() -> instructionsLabel.setText(newLabel));
        Platform.runLater(() -> definitionLabel.setText(substringresponse));
        Platform.runLater(() -> searchButton.setDisable(false));


    }


/**
   Goes to the disctionary and fetches a given term.
   @param term - the term that we want to search in the dictionary.
   @return a class that has the response from the dictionar
*/

    private DictionaryResponse dictionaryAPIFetch(String term) {
        try {
            uriString = "https://api.dictionaryapi.dev/api/v2/entries/en/";
            uriString += term;
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriString))
                .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
            String substringresponse = response.body().substring(1, response.body().length() - 1);
            dictResp = GSON.fromJson(substringresponse, DictionaryResponse.class);
            System.out.println(dictResp.meanings[0].definitions[0].definition);

        } catch (IOException IO) {
            String errorString = uriString + "\n" + "\n" + "\n";
            String IOstring = errorString += "Exception: " + IO.getMessage();
            final String IOString2 = IOstring;
            Platform.runLater(() -> showError(IOString2));
        } catch (InterruptedException IE) {
            String errorString = uriString + "\n" + "\n" + "\n";
            String IEString = errorString += "Exception: " + IE.getMessage();
            final String IEString2 = IEString;
            Platform.runLater(() -> showError(IEString2));
        } catch (IllegalStateException ISE) {
            String errorString = uriString + "\n" + "\n" + "\n";
            String ISEstring = errorString += "Exception :" + ISE.getMessage();
            final String ISEString2 = ISEstring;
            Platform.runLater(() -> showError(ISEString2));
        }
        return dictResp;
    }


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
        root.getChildren().addAll(topBox, instructions, termBar, definitionBar, spanishBar);
        root.getChildren().addAll(frenchBar, bottomBar);
        scene = new Scene(root);

        //setup top bar
        Label termLabel = new Label("Term:");
        Button searchButton = new Button("Search");
        Runnable task = () -> {
            updateDictionarySearch(searchBar.getText());
        };
        searchButton.setOnAction(event -> runInNewThread(task));
        topBox.setSpacing(50);
        topBox.getChildren().addAll(termLabel, searchBar, searchButton);

        //setup instructions bar
        instructionsLabel = new Label("Type your term into the box and press search.");
        instructions.getChildren().add(instructionsLabel);

        //a little with the definition / term bar


        // setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

} // ApiApp
