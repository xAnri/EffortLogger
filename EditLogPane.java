/* Name: Luke Fasciano
 * After selecting a specific project,
 * the effort log for that project is shown
 * to view progress, add new entries, or make changes.
*/

import java.util.List;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;

public class EditLogPane extends BorderPane {
    private GridPane logButtonPane;
    private GridPane logInfoPane;
    private TextArea logTextArea;
    private TextField logIDField = new TextField();
    private VBox logBox;
    private Button editLogButton = new Button("Edit Log");
    private Button submitButton = new Button("Submit");
    private Label logIDLabel = new Label();
    private Label projectLabel = new Label();
    private Label cycleLabel = new Label();
    private Label effortLabel = new Label();
    private int submit;
    private int logID;
    
    
    public EditLogPane() {
    	//create base pane
    	logButtonPane = new GridPane();
        logButtonPane.setPadding(new Insets(10));
        logButtonPane.setVgap(10);
        logButtonPane.setHgap(10);
    	
        //create text area for displaying logs
        logTextArea = new TextArea();
        logTextArea.setPrefSize(400, 300);
        
        logBox = new VBox(logTextArea);
        logBox.setPadding(new Insets(10));

        //Comboboxes for editing project
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
        
        logButtonPane.add(editLogButton, 0, 0, 1, 1);

        //set top log display screen and bottom buttons
        this.setTop(logInfoPane);
        this.setBottom(logButtonPane);
        
        //edit log button, set submit button to next phase
        editLogButton.setOnAction(e -> {
        	//display all text and add elements to screen
        	logIDLabel.setText("Log ID: ");
        	logButtonPane.add(logIDLabel, 0, 2, 3, 1);
            logButtonPane.add(logIDField, 0, 3, 3, 1);
            logButtonPane.add(submitButton, 3, 0, 3, 1);
            submit = 1; 
        });
        
        //submit button with phases
        submitButton.setOnAction(e -> {
        	if(submit == 1) {
            	projectLabel.setText("Project: ");
            	cycleLabel.setText("Life Cycle Step: ");
            	effortLabel.setText("Effort Category: ");
            	logButtonPane.add(projectLabel, 3, 2, 3, 1);
                logButtonPane.add(selectProject, 3, 3, 3, 1);
                logButtonPane.add(cycleLabel, 0, 5, 5, 1);
                logButtonPane.add(selectCycle, 0, 6, 2, 1);
                logButtonPane.add(effortLabel, 3, 5, 5, 1);
                logButtonPane.add(selectEffort, 3, 6, 6, 1);
                logID = Integer.parseInt(logIDField.getText());
                submit = 2;
        	} else if(submit == 2) {
        		if(verifyEntries(selectProject.getValue(), selectCycle.getValue(), selectEffort.getValue())){
        			DatabaseConnection.editLog(logID, selectProject.getValue(), selectCycle.getValue(), selectEffort.getValue());
        		}
        	}
        });
    }
    
    // function updates the log after creating new entries
    public void updateLog(int projectNum) {
        StringBuilder text = writeLogs(projectNum);
        logTextArea.setText(text.toString());
        this.setCenter(logBox);
    }
    
    
    // function pulls from database all the logs respective to the specific project number
    public static StringBuilder writeLogs(int projectNum) {
        int id = projectNum;
    	List<String> logs = DatabaseConnection.getLogs(id);
    	StringBuilder text = new StringBuilder();
    	
    	for(String log : logs) {
    		text.append(log).append("\n");
    	}
    	    	
    	return text;
    }
    
    public boolean verifyEntries(String project, String cycle, String effort) {
        if (project != null && cycle != null && effort != null) {
            // prevent SQL injections to input areas
            if (project.matches(".*[;&|\"'\\\\]+.*") || cycle.matches(".*[;&|\"'\\\\]+.*") || effort.matches(".*[;&|\"'\\\\]+.*")) {
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
