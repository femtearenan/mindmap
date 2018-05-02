package canvas;

import controller.IdeaController;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Display extends Application {
    private static final double SCENE_WIDTH = 800.0;
    private static final double SCENE_HEIGHT = 600.0;
    private static final Color SCENE_BACKGROUND = Color.LIGHTGRAY;

    private Group root = new Group();
    private Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, SCENE_BACKGROUND);

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Image icon = new Image("icon.png");
            primaryStage.getIcons().add(icon);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        IdeaController controller = new IdeaController(scene);
        // Create shapes for progress testing.
        Group ideaGroup = controller.generateIdeaGroup(IdeaController.mindExample());
        root.getChildren().addAll(ideaGroup);
        root.getChildren().addAll(controller.getLines());

        // will contain options list
        scene.setOnMouseClicked(event -> {
            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();

            MouseButton button = event.getButton();
            if (button == MouseButton.SECONDARY) {
                controller.createThoughtAt(sceneX, sceneY);
            }
        });

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Mind Map");
        primaryStage.show();

        controller.moveListOfPanesToFreeSpace(controller.extractPanesFromGroup(ideaGroup));
        controller.updateLines(ideaGroup);

    }

}
