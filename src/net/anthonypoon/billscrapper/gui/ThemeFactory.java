
package net.anthonypoon.billscrapper.gui;


import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ThemeFactory {
    private static double orgStageX;
    private static double orgStageY;
    private static double dragStartX;
    private static double dragStartY;
    private static boolean isDraggingDisabled = false;
    public static void makeDraggablePane(Node node) {        
        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!isDraggingDisabled) {
                    Stage stage = (Stage) node.getScene().getWindow();
                    orgStageX = stage.getX();
                    orgStageY = stage.getY();
                    dragStartX = event.getScreenX();
                    dragStartY= event.getScreenY();
                }
            }
        });
        
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!isDraggingDisabled) {
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.setX(orgStageX + (event.getScreenX() - dragStartX));
                    stage.setY(orgStageY + (event.getScreenY() - dragStartY));
                }
            }
        });
    }
    
    public static void makeCloseButton(Node node) {
        node.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage)node.getScene().getWindow();
                stage.close();
            }
        });
        
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                node.getScene().setCursor(Cursor.HAND);
            }
        });
        node.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                node.getScene().setCursor(Cursor.DEFAULT);
            }
        });
        
        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
                isDraggingDisabled = true;
            }
        });
        
        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                isDraggingDisabled = false;
            }
        });
        
    }
}
