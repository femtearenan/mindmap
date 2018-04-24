package canvas;

import controller.IdeaController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Bubble;
import model.BubbleType;
import model.Idea;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Display extends Application {
    private static final double SCENE_WIDTH = 600.0;
    private static final double SCENE_HEIGHT = 600.0;
    private static final Color SCENE_BACKGROUND = Color.LIGHTGRAY;
    private static final float SHAPE_SIZE = 50.0f;
    private static final Color SHAPE_FILL = new Color(1.0, 1.0, 1.0, 0.7);
    private static final float ELLIPSE_RATIO = 0.8f; // height to width ratio

    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    private Map<Idea, Line> ideaLineMap = new HashMap<>();
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

        // Create shapes for progress testing.
        Idea masterIdea = new IdeaController().mindExample();
        root.getChildren().addAll(generateIdeaGroup(masterIdea));
        root.getChildren().addAll(ideaLineMap.values());

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Mind Map");
        primaryStage.show();

    }

    public Group generateIdeaGroup(Idea masterIdea) {
        Group group = new Group();
        for (Idea idea : masterIdea.getFamily()) {
            group.getChildren().add(ideaToPane(idea));
        }
        return group;
    }

    /**
     * Takes an Idea and adds it to a StackPane as a Shape, the Pane gets EventHandlers to be movable.
     * @param idea
     * @return a StackPane.
     */
    public Pane ideaToPane(Idea idea){
        Pane pane = new StackPane();
        Text text = new Text(idea.getTheme());
        Shape shape = ideaToShape(idea);
        Line line = new Line();
        ideaLineMap.put(idea, line);

        text.setFill(getContrastColor((Color)shape.getFill()));

        pane.getChildren().addAll(shape, text);
        pane.setOnMousePressed(paneOnMousePressedEventHandler);
        pane.setOnMouseDragged(paneOnMouseDraggedEventHandler);
        return pane;
    }

    /**
     * Takes an Idea and generates a Shape according to instructions from the Ideas Bubble-object.
     * @param idea contains instructions to generate a Shape
     * @return a subclass-object of the Shape class.
     */
    public Shape ideaToShape(Idea idea) {
        Shape shape = new Ellipse(SHAPE_SIZE, SHAPE_SIZE * ELLIPSE_RATIO);
        Bubble bubble = idea.getBubble();
        BubbleType shapeType = bubble.getType();

        float width = bubble.getSizeX();
        float height = bubble.getSizeY();

        // Change width and height of object if the text to be contained within is larger than the shape.
        Text text = new Text(idea.getTheme());
        float textWidth = (float)text.getLayoutBounds().getWidth();
        float textHeight = (float)text.getLayoutBounds().getHeight();
        if (width < textWidth) {
            width = textWidth + 5;
        }
        if (height < textHeight) {
            height = textHeight + 2;
        }

        shape.setStrokeWidth(bubble.getLineThickness());
        shape.setStroke(bubble.getColor());
        shape.setFill(SHAPE_FILL);

        switch (shapeType) {
            case ELLIPSE:   ((Ellipse)shape).setRadiusX(width);
                            ((Ellipse)shape).setRadiusY(height);
                            break;
        }

        return shape;
    }

    private Point2D closeEmptySpace(Shape shape) {
        double x = 0;
        double y = 0;

        Point2D point = new Point2D(x, y);
        return point;
    }

    private void updateLines(Group ideaGroup) {
        for (Idea idea : ideaLineMap.keySet()) {
            drawLineBetweenIdeaShapes(ideaGroup, idea);
        }
    }

    private void drawLineBetweenIdeaShapes(Group ideaGroup, Idea idea) {
        Idea parent = idea;
        if (parent.hasChildren()) {
            List<Idea> children = idea.getChildren();
            Idea childExample = children.get(0);

            Pane start = getThemePaneFromGroup(parent.getTheme(), ideaGroup);
            Pane end = getThemePaneFromGroup(childExample.getTheme(), ideaGroup);

            Line line = ideaLineMap.get(idea);

            Bounds startBoundsInScene = start.localToScene(start.getBoundsInLocal());
            Bounds endBoundsInScene = end.localToScene(end.getBoundsInLocal());

            // ADJUST START AND END DEPENDING ON WHERE THE SHAPES ARE IN RELATION TO EACH OTHER.
            double startX = startBoundsInScene.getMinX() + startBoundsInScene.getWidth()/2;
            double startY = startBoundsInScene.getMinY() + startBoundsInScene.getHeight()/2;
            double endX = endBoundsInScene.getMinX() + endBoundsInScene.getWidth()/2;
            double endY = endBoundsInScene.getMinY() + endBoundsInScene.getHeight()/2;

            // START is to the left of END
            if (startBoundsInScene.getMaxX() < endBoundsInScene.getMinX()) {
                startX = startBoundsInScene.getMaxX();
                endX = endBoundsInScene.getMinX();
            } else if(startBoundsInScene.getMinX() > endBoundsInScene.getMaxX()) { // START is to the right of END
                startX = startBoundsInScene.getMinX();
                endX = endBoundsInScene.getMaxX();
            } else {
                // START is above the END
                if (startBoundsInScene.getMaxY() < endBoundsInScene.getMinY()) {
                    startY = startBoundsInScene.getMaxY();
                    endY = endBoundsInScene.getMinY();
                } else if (startBoundsInScene.getMinY() > endBoundsInScene.getMaxY()) { // START is below the END
                    startY = startBoundsInScene.getMinY();
                    endY = endBoundsInScene.getMaxY();
                }
            }

            line.setStartX(startX);
            line.setStartY(startY);
            line.setEndX(endX);
            line.setEndY(endY);
            
        }
    }

    private Pane getThemePaneFromGroup(String theme, Group group) {
        Pane themePane = new Pane();

        for (Node node : group.getChildren()) {
            if (node instanceof Pane) {
                Pane pane = (Pane) node;
                for (Node paneNode : pane.getChildren()) {
                    if (paneNode instanceof Text) {
                        String text = ((Text)paneNode).getText();
                        if (text.equals(theme)) {
                            themePane = pane;
                        }
                    }
                }

            }
        }

        return themePane;
    }

    /**
     * Takes a color and returns white or black depending on brightness of color.
     * Credit to brimborium on https://stackoverflow.com/questions/4672271/reverse-opposing-colors
     * @param color as the color to be contrasted to.
     * @return Color.WHITE or Color.BLACK depending on brightness of argument color.
     */
    private static Color getContrastColor(Color color) {
        // Multiplies with 255 to take into account that the example was taken from an awt implementation (not JavaFX)
        double y = (299 * (color.getRed()*255) + 587 * (color.getGreen()*255) + 114 * (color.getBlue()*255)) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    // Movable object, credit to this tutorial: http://java-buddy.blogspot.se/2013/07/javafx-drag-and-move-something.html
    // Now with movable stack.
    private EventHandler<MouseEvent> paneOnMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            orgSceneX = event.getSceneX();
            orgSceneY = event.getSceneY();
            orgTranslateX = ((Pane)(event.getSource())).getTranslateX();
            orgTranslateY = ((Pane)(event.getSource())).getTranslateY();
        }
    };

    private EventHandler<MouseEvent> paneOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            double offsetX = event.getSceneX() - orgSceneX;
            double offsetY = event.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            // focus on newTranslateX/Y
            double sourceWidth = ((Pane)(event.getSource())).getWidth();
            double sourceHeight = ((Pane)(event.getSource())).getHeight();

            // BOUNDS
            // boundary left and right
            if (newTranslateX < -(sourceWidth/2)) {
                newTranslateX = -Math.round(sourceWidth/2);
            } else if (newTranslateX > (SCENE_WIDTH - sourceWidth/2 + 10) ) {
                newTranslateX = SCENE_WIDTH - Math.round(sourceWidth/2) + 10;
            }

            // boundary up and down
            if (newTranslateY < - (sourceHeight/2)) {
                newTranslateY = - Math.round(sourceHeight/2);
            } else if (newTranslateY > (SCENE_HEIGHT - sourceHeight/2 + 10) ) {
                newTranslateY = SCENE_HEIGHT - Math.round(sourceHeight/2) + 10;
            }

            ((Pane)(event.getSource())).setTranslateX(newTranslateX);
            ((Pane)(event.getSource())).setTranslateY(newTranslateY);
            updateLines((Group)(((Pane)event.getSource()).getParent()));
        }
    };

}
