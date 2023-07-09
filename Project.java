/* Name: Kelly Raines
 * After successfully logging in,
 * this class displays the projects
 * that the user has made/ manages.
*/

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Project {
    private Stage primaryStage;
    private BorderPane root =  new BorderPane();
    private GridPane projects;
    private Scene scene1 = new Scene(root, 800, 600);
    // array list stores all project info
    private ArrayList<Button> projectList = new ArrayList<Button>();
    // popup to create, edit, or delete projects depending on the button pushed
    private Popup newProjectInfo = new Popup();
    private VBox allInfo = new VBox();
    private HBox enterInfo = new HBox();
    private Label instructions = new Label();
    private Label editInstructions = new Label("Please enter the new project name: ");
    private TextField projectName = new TextField();
    private TextField projectName2 = new TextField();
    private Button submit = new Button("Submit");
    // buttons to change function
    private Button newProject = new Button("New Project");
    private Button editProject = new Button("Edit Project Name");
    private Button deleteProject = new Button("Delete Project");
    private HBox editInfo = new HBox();
    private HBox editButtons = new HBox();
    // changes function of submit to either create a new project, edit an existing project, or delete a project
    private int configSubmit = 0;
    // sign out button for users
    private Button signOut = new Button("Sign out");
    

    public Project (Stage primaryStage, Scene logInPage) {
        // Connect to the database, if this doesn't work you need to add SQLite to your classpath, check readme
        DatabaseConnection.connect();
        DatabaseConnection.createTables();
        
        this.primaryStage = primaryStage;
        projects = new GridPane();
        projects.setPadding(new Insets(10));
        projects.setHgap(10);
        projects.setVgap(10);

        // check if the user has projects
        if (checkProjects()) {
            updateGridPane();
        }
        else {
            // default first project
            Button project1 = new Button("Project 1");
            DropShadow shadow = new DropShadow();
            project1.setOnMouseEntered(event -> project1.setEffect(shadow));
            project1.setOnMouseExited(event -> project1.setEffect(null));
            projectList.add(project1);
            EffortLogger newEL = new EffortLogger(primaryStage, scene1, 1);
            project1.setOnAction(e -> primaryStage.setScene(changeScenes(newEL)));
            projects.add(project1, 0, 0);
            
            // add project to database
            DatabaseConnection.saveProjectName("Project 1");
        }

        // popup
        enterInfo.setPadding(new Insets(10));
        enterInfo.setSpacing(10);
        editInfo.setPadding(new Insets(10));
        editInfo.setSpacing(10);
        allInfo.setPadding(new Insets(10));
        allInfo.setSpacing(10);

        newProjectInfo.setAutoHide(true);
        
        // create button handler
        newProject.setOnAction(e -> {
            instructions.setText("Please enter project name: ");
            enterInfo.getChildren().clear();
            allInfo.getChildren().clear();
            newProjectInfo.getContent().clear();
            enterInfo.getChildren().addAll(instructions, projectName);
            allInfo.getChildren().addAll(enterInfo, submit);
            newProjectInfo.getContent().add(allInfo);
            newProjectInfo.show(primaryStage);
            configSubmit = 1; 
        });
        
        // edit button handler
        editProject.setOnAction(e -> {
            allInfo.getChildren().clear();
            editInfo.getChildren().clear();
            newProjectInfo.getContent().clear();
            allInfo.getChildren().addAll(enterInfo, editInfo, submit);
            editInfo.getChildren().addAll(editInstructions, projectName2);
            newProjectInfo.getContent().add(allInfo);

            instructions.setText("Please enter project name to change: ");
            newProjectInfo.show(primaryStage);
            configSubmit = 2;
        });

        // delete button handler
        deleteProject.setOnAction(e -> {
            instructions.setText("Please enter project name to delete: ");
            enterInfo.getChildren().clear();
            allInfo.getChildren().clear();
            newProjectInfo.getContent().clear();
            enterInfo.getChildren().addAll(instructions, projectName);
            allInfo.getChildren().addAll(enterInfo, submit);
            newProjectInfo.getContent().add(allInfo);
            newProjectInfo.show(primaryStage);
            configSubmit = 3;
        });

        // create a new project and effort logger area
        submit.setOnAction(e -> {
            boolean done;
            // create function
            if (configSubmit == 1) {
                if (verify(projectName.getText())) {
                    if (checkDuplicate(projectName)) {
                        Alert warning = new Alert(AlertType.WARNING);
                        warning.setContentText("Name matches existing project. Please give a different name.");
                        warning.show();
                    }
                    else {
                        // create a new button to access project
                        Button newProject = new Button(projectName.getText());
                        projectList.add(newProject);
                        
                        // create a new Effort Logger for the project
                        EffortLogger newEL = new EffortLogger(primaryStage, scene1, projectList.size());
                        newProject.setOnAction(event -> primaryStage.setScene(changeScenes(newEL)));

                        // add shadow effect on hover
                        DropShadow shadow = new DropShadow();
                        newProject.setOnMouseEntered(event -> newProject.setEffect(shadow));
                        newProject.setOnMouseExited(event -> newProject.setEffect(null));

                        // add button to grid
                        updateGridPane();

                        // add project to database
                        DatabaseConnection.saveProjectName(projectName.getText());
                    }
                }
                else {
                    Alert error = new Alert(AlertType.ERROR);
                    error.setContentText("Please give the project a name.");
                    error.show();
                }
            }
            // edit function
            else if (configSubmit == 2) {
                done = false;
                if (verify(projectName.getText()) && verify(projectName2.getText())) {
                    for (int i = 0; i < projectList.size(); i++) {
                        if (projectList.get(i).getText().equals(projectName.getText())) {
                            // edit project button
                            projectList.get(i).setText(projectName2.getText());
                            done = true;
                            
                            // edit project from database
                            DatabaseConnection.editProject(projectName.getText(), projectName2.getText());
                        }
                    }
                    // alert user if could not edit project name
                    if (!done) {
                        Alert warning = new Alert(AlertType.WARNING);
                        warning.setContentText("Cannot edit. No matching projects with given name.");
                        warning.show();
                    }
                }
                else {
                    Alert error = new Alert(AlertType.ERROR);
                    error.setContentText("Missing project name(s). Please enter a name for all fields.");
                    error.show();
                }
                allInfo.getChildren().clear();
                allInfo.getChildren().addAll(enterInfo, submit);
                newProjectInfo.hide();
                projectName2.clear();
            }
            // delete function
            else if (configSubmit == 3) {
                done = false;
                if (verify(projectName.getText())) {
                    for (int i = 0; i < projectList.size(); i++) {
                            if (projectList.get(i).getText().equals(projectName.getText())) {
                                // delete project button
                                projectList.remove(projectList.get(i));
                                updateGridPane();
                                done = true;

                                // delete project from database
                                DatabaseConnection.deleteProject(projectName.getText(), i+1);
                            }
                    }
                    if (!done) {
                        Alert warning = new Alert(AlertType.WARNING);
                        warning.setContentText("Cannot delete. No matching projects with given name.");
                        warning.show();
                    }
                }
                else {
                    Alert error = new Alert(AlertType.ERROR);
                    error.setContentText("Please give an existing project name to delete.");
                    error.show();
                }
            }
            projectName.clear();
        });

        editButtons.getChildren().addAll(newProject, editProject, deleteProject);
        editButtons.setPadding(new Insets(10));
        editButtons.setSpacing(10);
        // go back to log-in page
        signOut.setOnAction(e -> changeScene2(logInPage));

        root.setCenter(projects);
        root.setBottom(editButtons);
        BorderPane.setAlignment(signOut, Pos.TOP_RIGHT);
        root.setTop(signOut);
        root.setPadding(new Insets(10));
    }

    // helper method updates the projects buttons seen on the page
    private void updateGridPane() {
        projects.getChildren().clear();
        for (int i = 0; i < projectList.size(); i++) {
            projects.add(projectList.get(i), 0, i);
        }
    }

    // check if the user has projects already saved
    private boolean checkProjects() {
        List<String> projectNames = DatabaseConnection.getProjects();
        if (projectNames.size() != 0) {
            retrieveProjects(projectNames);
            return true;
        }
        return false;
    }

    // function to restore all the projects and logs to the database data stored in the file
    private void retrieveProjects(List<String> projectNames) {
        for (int i = 0; i < projectNames.size(); i++) {
            Button newProject = new Button(projectNames.get(i));
            projectList.add(newProject);
            
            // create a new Effort Logger for the project
            EffortLogger newEL = new EffortLogger(primaryStage, scene1, i+1);
            newProject.setOnAction(event -> primaryStage.setScene(changeScenes(newEL)));
        }
    }

    // switches to the effort logger
    private Scene changeScenes(EffortLogger newEL) {
		Scene scene = newEL.getScene();
		primaryStage.setTitle("Effort Logger");

		return scene;
	}

    // switches to log-in page
    private void changeScene2(Scene logIn) {
        primaryStage.setScene(logIn);
	}

    // switches back to project screen
    public Scene getScene() {
        return scene1;
    }

    // check SQL injections do not occur
    public boolean verify(String projectName) {
        if (!projectName.isEmpty()) {
            // prevent SQL injections
            if (projectName.matches(".*[;&|\"'\\\\]+.*")) {
                return false;
            }
            else {
                return true;
            }
        }
        return false;
    }

    // checks that an inputed project name does not match any existing projects
    public boolean checkDuplicate(TextField projectName) {
        for (int i = 0; i < projectList.size(); i++) {
            if (projectList.get(i).getText().equals(projectName.getText())) {
                return true;
            }
        }
        return false;
    }

    
}
