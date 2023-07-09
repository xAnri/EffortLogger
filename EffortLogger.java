/* Name: Kelly Raines
 * After selecting a specific project,
 * the effort log for that project is shown
 * to view progress, add new entries, or make changes.
*/

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class EffortLogger {
    private Stage primaryStage;
    private BorderPane root = new BorderPane();
    private TabPane tabPane;
    private NewLogPane effortLogPane;
    private EditLogPane editLogPane;
    private BacklogPane backlogPane;
    private PlanningPane planningPane;
    private Scene scene2 = new Scene(root, 800, 600);
    private Button homeButton;

    public EffortLogger(Stage primaryStage, Scene projectPage, int projectNum) {
        this.primaryStage = primaryStage;
        effortLogPane = new NewLogPane(projectNum);
        editLogPane = new EditLogPane();
        backlogPane = new BacklogPane();
        planningPane = new PlanningPane();

        tabPane = new TabPane();
        Tab tab1 = new Tab();
        tab1.setText("New Effort Log");
        tab1.setContent(effortLogPane);
        tab1.setClosable(false);

        Tab tab2 = new Tab();
        tab2.setText("Edit Logs");
        tab2.setContent(editLogPane);
        tab2.setClosable(false);
        // update log area when tab is selected
        tab2.setOnSelectionChanged(event -> {
            if (tab2.isSelected()) {
                editLogPane.updateLog(projectNum);
            }
        });

        Tab tab3 = new Tab();
        tab3.setText("Backlog Tasks");
        tab3.setContent(backlogPane);
        tab3.setClosable(false);

        Tab tab4 = new Tab();
        tab4.setText("Planning Poker");
        tab4.setContent(planningPane);
        tab4.setClosable(false);

        tabPane.getTabs().addAll(tab1, tab2, tab3, tab4);

        ImageView imageView = new ImageView(getClass().getResource("home.png").toExternalForm());
        homeButton = new Button();
        homeButton.setGraphic(imageView);
        homeButton.setContentDisplay(ContentDisplay.TOP);
        imageView.setPreserveRatio(true);
        homeButton.setStyle("-fx-background-color:transparent;");

        homeButton.setOnAction(e -> primaryStage.setScene(changeScenes(projectPage)));

        root.setCenter(tabPane);
        BorderPane.setAlignment(homeButton, Pos.BOTTOM_RIGHT);
        root.setBottom(homeButton);
    }
    
    // changes scene after logging in
    public Scene getScene() {
        return scene2;
    }
    private Scene changeScenes(Scene projectPage) {
		primaryStage.setTitle("My Projects");
		return projectPage;
	}
}