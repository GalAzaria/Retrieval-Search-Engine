import GuiPartA.Model;
import GuiPartA.ViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage window;
    public static long startTime = System.nanoTime();

    public static void main (String[] args) {
        launch(args);
//
//        semmanticApi api = new semmanticApi();
//        Query q = new Query("123","Falkland petroleum exploration","kaki");


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        Model model = new Model();




        primaryStage.setTitle("SearchEngine");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
        Scene scene = new Scene(root, 20, 20);
        scene.getStylesheets().add("GuiPartA/style.css");




        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        primaryStage.setX(800);
        primaryStage.setY(400);
        primaryStage.setWidth(800);
        primaryStage.setHeight(400);
        primaryStage.setMaximized(false);



        primaryStage.setScene(scene);


        ViewController view = fxmlLoader.getController();
        view.setStage(primaryStage);
        view.setModel(model);
        model.addObserver(view);

        primaryStage.show();

    }


}
