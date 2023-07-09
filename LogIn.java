/* Name: Kelly Raines
* This application will provide a more intuitive
* design for companies and programming teams to
* track project progress
*/

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LogIn extends Application {
	private Stage primaryStage;
	private VBox root;
	private Scene scene;
	private HBox titleHolder;
	private Label title;
	private Separator separate;
	private GridPane logInGrid;
	private GridPane signUpGrid;
	private Label logIn;
	private Label signUp;
	private Label username;
	private Label password;
	private Label entryRules;
	private Label newUsername;
	private Label newPassword;
	private Label confirmPassword;
	private Label code;
	private TextField usernameEntry;
	private TextField passwordEntry;
	private TextField newUsernameEntry;
	private TextField newPasswordEntry;
	private TextField confirmPasswordEntry;
	private TextField codeEntry;
	private Button signUpButton;
	private Button logInButton;

	// switch to projects screen after logging in
	private Scene changeScenes() {
		Project showProjects = new Project(primaryStage, scene);
		Scene scene2 = showProjects.getScene();
		primaryStage.setTitle("My Projects");
		return scene2;
	}

	@Override
	public void start(Stage primaryStage) {		
		this.primaryStage = primaryStage;
		root = new VBox();
		titleHolder = new HBox();
		titleHolder.setStyle("-fx-background-color: #F9EBEA;");
		title = new Label("Welcome to\n\tEffortLogger");
		title.setTextFill(Color.DARKRED);
		title.setStyle("-fx-font: 25px Constantia; -fx-font-weight: bold;");
		titleHolder.setSpacing(20);
		titleHolder.getChildren().add(title);
		titleHolder.setAlignment(Pos.TOP_CENTER);
		
		signUpGrid = new GridPane();
		separate = new Separator(Orientation.HORIZONTAL);
		separate.setPadding(new Insets(20));
		logInGrid = new GridPane();

		signUp = new Label("Sign Up");
		entryRules = new Label("Password must be 8 or more characters long\nand contain at least 1 uppercase letter and 1 number.");
		newUsername = new Label("Username:\t");
		newPassword = new Label("Password:\t");
		confirmPassword = new Label("Confirm Password:\t");
		code = new Label("Employee Token:\t");
		signUp.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		entryRules.setStyle("-fx-font-size: 10px;");
		newUsername.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
		newPassword.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
		confirmPassword.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
		code.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
		newUsernameEntry = new TextField();
		newPasswordEntry = new TextField();
		confirmPasswordEntry = new TextField();
		codeEntry = new TextField();
		codeEntry.setTooltip(new Tooltip("Validates employee to add to a group. Ask for employer to provide token."));
		signUpButton = new Button("Sign Up");

		signUpGrid.add(signUp, 3, 0, 2, 1);
		signUpGrid.add(entryRules, 1, 1, 3, 1);
		signUpGrid.add(newUsername, 1, 2);
		signUpGrid.add(newPassword, 1, 3);
		signUpGrid.add(confirmPassword, 1, 4);
		signUpGrid.add(code, 1, 5);
		signUpGrid.add(newUsernameEntry, 2, 2, 2, 1);
		signUpGrid.add(newPasswordEntry, 2, 3, 2, 1);
		signUpGrid.add(confirmPasswordEntry, 2, 4, 2, 1);
		signUpGrid.add(codeEntry, 2, 5, 2, 1);
		signUpGrid.add(signUpButton, 3, 6);
		signUpGrid.setVgap(7);
		signUpGrid.setHgap(5);
		signUpGrid.setAlignment(Pos.CENTER);
		signUpGrid.setPadding(new Insets(20));

		logIn = new Label("Login");
		username = new Label("Username:\t");
		password = new Label("Password:\t");
		logIn.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		username.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
		password.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
		usernameEntry = new TextField();
		passwordEntry = new TextField();
		logInButton = new Button("Log In");

		logInGrid.add(logIn, 3, 0, 2, 1);
		logInGrid.add(username, 1, 1);
		logInGrid.add(password, 1, 2);
		logInGrid.add(usernameEntry, 2, 1, 2, 1);
		logInGrid.add(passwordEntry, 2, 2, 2, 1);
		logInGrid.add(logInButton, 3, 4);
		logInGrid.setVgap(7);
		logInGrid.setHgap(5);
		logInGrid.setAlignment(Pos.BOTTOM_CENTER);

		root.getChildren().addAll(titleHolder, signUpGrid, separate, logInGrid);

		// button logic
		signUpButton.setOnAction(e -> signUp());
		logInButton.setOnAction(e -> logIn());
		DropShadow shadow = new DropShadow();
		signUpButton.setOnMouseEntered(e -> signUpButton.setEffect(shadow));
		signUpButton.setOnMouseExited(e -> signUpButton.setEffect(null));
        logInButton.setOnMouseEntered(e -> logInButton.setEffect(shadow));
		logInButton.setOnMouseExited(e -> logInButton.setEffect(null));
		
		// Set up the scene and show the stage
		scene = new Scene(root, 800, 600);
		primaryStage.setTitle("Log In");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// check that SQL injections do not occur
    public boolean verify(String input) {
        if (!input.isEmpty()) {
            // prevent SQL injections
            if (input.matches(".*[;&|\"'\\\\]+.*")) {
                return false;
            }
            else {
                return true;
            }
        }
        return false;
    }

	// log in button handler helper function
	public void logIn() {
		if (verify(usernameEntry.getText()) && verify(passwordEntry.getText())) {
			// add functions to check whether password matches the one in database and return here
			String receivedPassword = DatabaseConnection.getPassword(usernameEntry.getText());
			try {
				// call validate password to decrypt stored password and check if they match
				if (PasswordUtil.validatePassword(passwordEntry.getText(), receivedPassword)) {
					// log-in success
					primaryStage.setScene(changeScenes());
				}
			}
			catch (Exception e) {
				Alert error = new Alert(AlertType.ERROR);
				error.setContentText("Error logging in. Please try again later.");
				error.show();
				System.out.println("Error logging in: " + e);
			}
			
		}
		else {
			Alert error = new Alert(AlertType.ERROR);
			error.setContentText("Error processing input. Make sure there are no empty fields, ', or \"");
			error.show();
		}
	}

	// check if password meets the requirements for signup
	public boolean passwordRules(String password) {
		// use regular expression to check that at least one number is present in password
		String regex = "^.*[a-zA-Z0-9]+.*$";		
		if (password.length() >= 8 && password.matches(regex)) {
			return true;
		}
		return false;
	}

	public void signUp() {
		if (verify(newUsernameEntry.getText()) && verify(newPasswordEntry.getText()) && verify(codeEntry.getText())) {
			if (passwordRules(newPasswordEntry.getText())) {
				try {
					String password = PasswordUtil.generateStrongPasswordHash(newPasswordEntry.getText());
					
					// add function to add new user and password to database
					DatabaseConnection.addUser(newUsernameEntry.getText(), password, codeEntry.getText());

					// log-in success
					primaryStage.setScene(changeScenes());
				}
				catch (Exception e) {
					Alert error = new Alert(AlertType.ERROR);
					error.setContentText("Error signing up. Please try again later.");
					error.show();
					System.out.println("Error signing up: " + e);
				}
			}
			else {
				Alert error = new Alert(AlertType.ERROR);
				error.setContentText("ERROR! Make sure that password meets specifications: at least 8 characters long and 1 number.");
				error.show();
			}
		}
		else {
			Alert error = new Alert(AlertType.ERROR);
			error.setContentText("Error processing input. Make sure there are no empty fields, ', or \".");
			error.show();
		}

	}

	public static void main(String[] args) {
		launch(args);
    }
}