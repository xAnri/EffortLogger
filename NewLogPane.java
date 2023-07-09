/* Name: Michael Fultz and Kelly Raines and Madison Mortenson and Luke Fasciano
 * This tab provides users with the functionality to
 * add a new effort log and specify what work was
 * done and how much time was spent on a task.
 */

import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class NewLogPane extends BorderPane {
    private TextArea logTextArea;
	private boolean clockRunning = false;
    private FlowPane flowPane;
    private GridPane logInfoPane;
    private long start, time;
    private VBox logBox, logInfoVPane;
    private Label elapsedLabel;

    public NewLogPane(int projectNum) {
    	// create flowPane so other objects can access it
        flowPane = new FlowPane();
        flowPane.setAlignment(Pos.CENTER);

        // Initialize Clock Indicator that is displayed at the top
        Text stopText = new Text("Clock is stopped");
        stopText.setFill(Color.WHITE);
        stopText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 30));
        flowPane.setStyle("-fx-background-color: #cc0000;");
        flowPane.getChildren().add(stopText);
        flowPane.alignmentProperty();

        // Log Area Description
        Label logDescription = new Label("Effort Log Description");
        logDescription.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        logTextArea = new TextArea();
        logTextArea.setWrapText(true);
        logTextArea.setPrefWidth(300);
        logTextArea.setPrefHeight(400);
        logBox = new VBox(logDescription, logTextArea);
        logBox.setPadding(new Insets(10));

        // Select info on effort log
        Label infoLabel = new Label("Effort Log Information");
        infoLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        logInfoPane = new GridPane();
        logInfoPane.setPadding(new Insets(10));
        logInfoPane.setVgap(10);
        logInfoPane.setHgap(10);

        // Create select list titles
        Text selectProjectText = new Text("Project:");
        Text selectLCText = new Text("Life Cycle Step:");
        Text selectEffortText = new Text("Effort Category:");

        // create comboboxes
        ComboBox<String> selectProject = new ComboBox<>();
        selectProject.getItems().addAll("Business Project", "Development Project");
        
        ComboBox<String> selectCycle = new ComboBox<>();
        selectCycle.getItems().addAll("Problem Understanding", "Conceptual Design Plan",
                                    "Requirements", "Conceptual Design",
                                    "Conceptual Design Review", "Detailed Design Plan",
                                    "Detailed Design/Prototype", "Detailed Design Review",
                                    "Implementation Plan", "Test Case Generation", 
                                    "Solution Specification", "Solution Review",
                                    "Solution Implementation", "Unit/System Test",
                                    "Reflection", "Repository Update");

        ComboBox<String> selectEffort = new ComboBox<>();
        selectEffort.getItems().addAll("Plans", "Deliverables", "Interruptions", "Defects",  "Other");
        
        
        Button clockButton = new Button("Start Clock");
        
        DropShadow shadow = new DropShadow();
        clockButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                clockButton.setEffect(shadow);
            }
        });
        clockButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
        	@Override public void handle(MouseEvent e) {
            	clockButton.setEffect(null);
        	}
		});

        // start clock if it is not running, end clock it if it is 
        clockButton.setOnAction(event -> {
            if (!clockRunning) {
                startClock(flowPane);
                clockButton.setText("Stop Clock");
                clockRunning = true;
                // get the start time
                start = System.nanoTime();
            }
            else {
                // get the end time
                long end = System.nanoTime();
                stopClock(flowPane);
                clockButton.setText("Start Clock");
                clockRunning = false;
                time = end-start;
                               
               
                // make a new log
                if (verifyEntries(selectProject.getValue(), selectCycle.getValue(), selectEffort.getValue(), logTextArea.getText(), time)) {
                    double seconds = (double)time / 1_000_000_000.0;
                    double minutes = seconds / 60.0;

                    // write log to database
                    DatabaseConnection.saveLog(projectNum, selectProject.getValue(), selectCycle.getValue(), selectEffort.getValue(), logTextArea.getText(), minutes);
                    
                    elapsedLabel = new Label("Time Spent: " + String.format("%.2f", minutes) + " (minutes)");
                    elapsedLabel.setPadding(new Insets(10));
                    elapsedLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
                    BorderPane.setAlignment(elapsedLabel, Pos.CENTER);
                    this.setBottom(elapsedLabel);

                    time = 0;
                }
                else {
                   Alert error = new Alert(AlertType.ERROR);
                   error.setContentText("Please fill out all fields of the Effort Log information.");
                   error.show();
                }
            }
        });

        logInfoPane.add(selectProjectText, 0, 1, 1, 1);
        logInfoPane.add(selectLCText, 2, 1, 1, 1);
        logInfoPane.add(selectProject, 0, 3, 2, 1);
        logInfoPane.add(selectCycle, 2, 3, 3, 1);
        logInfoPane.add(selectEffortText, 0, 4, 2, 1);
        logInfoPane.add(selectEffort, 0, 5, 2, 1);
        logInfoPane.add(clockButton, 0, 8, 4, 1);


        logInfoVPane = new VBox(infoLabel, logInfoPane);
        logInfoVPane.setPadding(new Insets(10));

        this.setTop(flowPane);
        this.setLeft(logInfoVPane);
        this.setRight(logBox);
    }

    private void startClock(FlowPane flowPane) {
        flowPane.setColumnHalignment(HPos.CENTER);
        flowPane.setStyle("-fx-background-color: #00cc00;");
        flowPane.getChildren().clear();
        Text runText = new Text("Clock is running");

        runText.setFill(Color.WHITE);
        runText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 30)); 
        flowPane.getChildren().add(runText);
    }

    private void stopClock(FlowPane flowPane) {
        flowPane.setStyle("-fx-background-color: #cc0000;");
        flowPane.getChildren().clear();
        Text stopText = new Text("Clock is stopped");

        stopText.setFill(Color.WHITE);
        stopText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 30)); 
        flowPane.getChildren().add(stopText);
    }

    public boolean verifyEntries(String project, String cycle, String effort, String description, long time) {
        if (project != null && cycle != null && effort != null && description != null && time > 0) {
            // prevent SQL injections to input areas
            if (project.matches(".*[;&|\"'\\\\]+.*") || cycle.matches(".*[;&|\"'\\\\]+.*") || effort.matches(".*[;&|\"'\\\\]+.*") || description.matches(".*[;&|\"'\\\\]+.*")) {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
}
