package myproject.todo_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

@Log4j2
public class App extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        Locale locale = Locale.of(Constants.language);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Texts", locale);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main_view.fxml"), resourceBundle);
        Scene scene = new Scene(fxmlLoader.load(), 540, 670);
        stage.setTitle("Todo");
        stage.setScene(scene);
        stage.show();

    }


    public static void main(String[] args)
    {
        if(args.length == 1)
        {
            Constants.language = args[0];
            log.info("Language: {}", args[0]);
        }


        launch();
    }
}