package canvas;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class Display extends Application {

    // Movable object, credit to this tutorial: http://java-buddy.blogspot.se/2013/07/javafx-drag-and-move-something.html

    Shape shapeRed;
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create shape
        shapeRed = new Ellipse(50.0, 45.0);
        shapeRed.setFill(Color.RED);
        shapeRed.setCursor(Cursor.HAND);
        shapeRed.setOnMousePressed(shapeOnMousePressedEventHandler);
        shapeRed.setOnMouseDragged(shapeOnMouseDraggedEventHandler);

        Group root = new Group();
        root.getChildren().add(shapeRed);

        Scene scene = new Scene(root, 300, 300, Color.BLACK);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Mind Map");
        primaryStage.show();
    }

    EventHandler<MouseEvent> shapeOnMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            orgSceneX = event.getSceneX();
            orgSceneY = event.getSceneY();
            orgTranslateX = ((Shape)(event.getSource())).getTranslateX();
            orgTranslateY = ((Shape)(event.getSource())).getTranslateY();
        }
    };

    EventHandler<MouseEvent> shapeOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            double offsetX = event.getSceneX() - orgSceneX;
            double offsetY = event.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            ((Shape)(event.getSource())).setTranslateX(newTranslateX);
            ((Shape)(event.getSource())).setTranslateY(newTranslateY);
        }
    };
}
