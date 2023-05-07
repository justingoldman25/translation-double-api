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
import javafx.scene.control.ComboBox;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.IllegalStateException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.google.gson.JsonSyntaxException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.NullPointerException;



/**
 * My app is used in order to take a word and find the definition of it. Once
 you have the definition, you then are given the choice to translate the definition into one
 of five different languages.
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
    HBox translateOptionBar;
    HBox translateBar;
    Button searchButton;
    TextField searchBar;
    Label instructionsLabel;
    Label definitionLabel;
    Label translateOptionLabel;
    ComboBox translationOptionComboBox;
    Button translationGoButton;
    Label translateBarLabel;

    String uriString;
    DictionaryResponse dictResp;
    TranslationResponse transResp;
    String translatedDefinition;

    Boolean errorThrown;
    Boolean runOnce;




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
        translateOptionBar = new HBox();
        translateBar = new HBox();
        searchButton = new Button();
        searchBar = new TextField();
        definitionLabel = new Label();
        translateOptionLabel = new Label();
        translationOptionComboBox = new ComboBox();
        translationGoButton = new Button();
        errorThrown = false;
        runOnce = false;
        translateBarLabel = new Label();


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
   Private method to create the second part of the JavaFX.
   Only called once a term has been successfully defined.
   @param term - the term that we are going to translate
   @param definition - the definition th
*/

    private void createTranslate(String term, String definition) {
        translateOptionLabel.setText("Translate?");
        translationOptionComboBox.getItems().addAll("French", "Spanish", "German");
        translationOptionComboBox.getItems().addAll("Chinese", "Japanese");
        translationOptionComboBox.setValue("Spanish");
        translationGoButton.setText("Search");
        Runnable translator = () -> {
            translationAPI(definition);
        };

        translationGoButton.setOnAction(event -> runInNewThread(translator));
        Platform.runLater(() -> translateOptionBar.getChildren().add(translateOptionLabel));
        Platform.runLater(() -> translateOptionBar.getChildren().add(translationOptionComboBox));
        Platform.runLater(() -> translateOptionBar.getChildren().add(translationGoButton));
    }
/**
   gets language code.
   @param language language string
   @return the code
*/

    private String getLanguageCode(String language) {
        String languageCode = "";
        if (language == "French") {
            languageCode = "fr";
        }
        if (language == "Spanish") {
            languageCode = "es";
        }
        if (language == "German") {
            languageCode = "de";
        }
        if (language == "Chinese") {
            languageCode = "zh";
        }
        if (language == "Japanese") {
            languageCode = "ja";
        }
        return languageCode;
    }
/**
   A private method to contact the translationAPI.
   @param translation - what is going to be translated
*/

    private void translationAPI(String translation) {
        String language = (String) translationOptionComboBox.getValue();
        String languageCode = getLanguageCode(language);
        searchButton.setDisable(true);
        String encodedTranslation = URLEncoder.encode(translation, StandardCharsets.UTF_8);
        String link = "q=" + encodedTranslation + "&target=";
        link += languageCode + "&source=en";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2"))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("Accept-Encoding", "application/gzip")
                .header("X-RapidAPI-Key", "30ffe388edmsh061050f09b8e98ep111bf4jsnbccf344b952b")
                .header("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                .method("POST", HttpRequest.BodyPublishers.ofString(link))
                .build();
            HttpResponse<String> response = HttpClient
                .newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            transResp = GSON.fromJson(response.body(), TranslationResponse.class);
            String translatedDef = transResp.data.translations[0].translatedText;
            translatedDefinition = translatedDef;
            String holder = "Translation: " + "\n" + translatedDefinition;
            Platform.runLater(() -> translateBarLabel.setText(holder));
            Platform.runLater(() -> translateBarLabel.setWrapText(true));
            if (runOnce == false) {
                Platform.runLater(() -> translateBar.getChildren().add(translateBarLabel));
            }
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
        } catch (NullPointerException NPE) {
            String errorString = uriString + "\n" + "\n" + "\n";
            String NPEstring = errorString += "Exception :" + NPE.getMessage() + "\n";
            NPEstring += "Sorry, our translation API could not translate your definition.";
            final String NPEString2 = NPEstring;
            Platform.runLater(() -> showError(NPEString2));
        }

        searchButton.setDisable(false);
        translationGoButton.setDisable(true);
    }


/**
   Private method to update the GUI for the  definition search.
   @param term to update the search with
*/

    private void updateDictionarySearch(String term) {
        Platform.runLater(() -> searchButton.setDisable(true));
        DictionaryResponse response = dictionaryAPIFetch(term);
        if (response != null) {
            String substringresponse = response.meanings[0].definitions[0].definition;
            String newLabel = "Type in new word or translate existing definition.";
            newLabel += " You will only be able to make one translation";
            newLabel += " unless you search another term.";
            final String newLabel2 = newLabel;
            String newDefinition = "\n" + "First found definition of '" + term + "' :";
            newDefinition += "\n" + "\n" + substringresponse;
            final String newDefinition2 = newDefinition;
            if (errorThrown == false) {
                Platform.runLater(() -> instructionsLabel.setText(newLabel2));
                Platform.runLater(() -> instructionsLabel.setWrapText(true));
                Platform.runLater(() -> definitionLabel.setText(newDefinition2));
                if (runOnce == false) {
                    createTranslate(term, substringresponse);
                }
                Runnable translator = () -> {
                    translationAPI(substringresponse);
                };
                translationGoButton.setOnAction(event -> runInNewThread(translator));
                runOnce = true;
            }
        }
        Platform.runLater(() -> searchButton.setDisable(false));


    }


/**
   Goes to the disctionary and fetches a given term.
   @param term - the term that we want to search in the dictionary.
   @return a class that has the response from the dictionar
*/

    private DictionaryResponse dictionaryAPIFetch(String term) {
        translationGoButton.setDisable(false);
        try {
            errorThrown = false;
            uriString = "https://api.dictionaryapi.dev/api/v2/entries/en/";
            uriString += term;
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriString))
                .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
            String substringresponse = response.body().substring(1, response.body().length() - 1);
            dictResp = GSON.fromJson(substringresponse, DictionaryResponse.class);
        } catch (NullPointerException npe) {
            String errorString = uriString + "\n" + "\n" + "\n";
            String NPEString = errorString += "Exception : " + "\n" + npe.getMessage();
            final String NPEString2 = NPEString;
            Platform.runLater(() -> showError(NPEString2));
            errorThrown = true;
        } catch (IOException IO) {
            String errorString = uriString + "\n" + "\n" + "\n";
            String IOstring = errorString += "Exception: " + IO.getMessage();
            final String IOString2 = IOstring;
            Platform.runLater(() -> showError(IOString2));
            errorThrown = true;
        } catch (InterruptedException IE) {
            String errorString = uriString + "\n" + "\n" + "\n";
            String IEString = errorString += "Exception: " + IE.getMessage();
            final String IEString2 = IEString;
            Platform.runLater(() -> showError(IEString2));
            errorThrown = true;
        } catch (IllegalStateException ISE) {
            String errorString = uriString + "\n" + "\n" + "\n";
            String ISEstring = errorString += "Exception :" + ISE.getMessage();
            final String ISEString2 = ISEstring;
            Platform.runLater(() -> showError(ISEString2));
            errorThrown = true;
        } catch (JsonSyntaxException jse) {
            String errorString = uriString + "\n" + "\n" + "\n";
            String JSEstring = errorString += "Exception : " + "\n" + jse.getMessage();
            JSEstring +=  "\n" + "\n" + "\n";
            JSEstring += "Probably caused by a search that yielded no results.";
            final String JSEString2 = JSEstring;
            Platform.runLater(() -> showError(JSEString2));
            errorThrown = true;
        } catch (IllegalArgumentException IAE) {
            String errorString = "Your term to search in a dictionary can only be one word.";
            Platform.runLater(() -> showError(errorString));
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
        root.setSpacing(15);
        root.getChildren().addAll(topBox, instructions, termBar, definitionBar, translateOptionBar);
        root.getChildren().add(translateBar);
        scene = new Scene(root);

        //setup top bar
        Label termLabel = new Label("Term:");
        Button searchButton = new Button("Search");
        Runnable task = () -> {
            updateDictionarySearch(searchBar.getText());
        };
        searchButton.setOnAction(event -> runInNewThread(task));
        topBox.setSpacing(50);
        translateOptionBar.setSpacing(50);
        topBox.getChildren().addAll(termLabel, searchBar, searchButton);
        translateBar.getChildren().add(translateBarLabel);

        //setup instructions bar
        instructionsLabel = new Label("Type your term into the box and press search.");
        instructions.getChildren().add(instructionsLabel);

        //a little with the definition / term bar
        definitionLabel.setWrapText(true);
        definitionBar.getChildren().add(definitionLabel);

        // setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

} // ApiApp
