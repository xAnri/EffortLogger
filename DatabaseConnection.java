/* Authors: Michael Fultz and Luke Fasciano and Kelly Raines and Zachary Soe
*  Description: Java file for working with sqlite database
*  to store employee effort log data and projects created
*  by the user.
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

public class DatabaseConnection {
	private static final String DB_URL = "jdbc:sqlite:myDatabase.db";

  	// establish connection to the database
	public static Connection connect() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(DB_URL);
		} catch (SQLException e) {
			System.err.println("Error connecting to the database: " + e.getMessage());
		}
		return connection;
	}
	
	// saves a new project to the table myProject, create a table for logs to go with that project
	public static void saveProjectName(String projectName) {
		try (Connection connection = connect()) {
			String query = "INSERT INTO myProject (project_name) VALUES (?)";
			
			// execute above query to store information to the myProject table
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, projectName);
			preparedStatement.executeUpdate();
			System.out.println("Project successfully saved");
		} catch (SQLException e) {
			System.err.println("Error while saving project: " + e.getMessage());
		}
	}
	
	// saves a new log to the table myLog
	public static void saveLog(int project_id, String projectType, String lifeCycleStep, String effortArea, String description, double timeSpent) {
		try (Connection connection = connect()) {
			String query = "INSERT INTO myLog (project_id, project_type, life_cycle_step, effort_area, description, time_spent) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, project_id);
			preparedStatement.setString(2, projectType);
			preparedStatement.setString(3, lifeCycleStep);
			preparedStatement.setString(4, effortArea);
			preparedStatement.setString(5, description);
			preparedStatement.setDouble(6, timeSpent);
			preparedStatement.executeUpdate();
			System.out.println("Log successfully saved");
		} catch (SQLException e) {
			System.err.println("Error while updating log: " + e.getMessage());
		}
	}
 
  	// create Log and Project tables if not present
	public static void createTables() {
		// columns in the tables collecting data from the user
		String createProjectTableQuery = "CREATE TABLE IF NOT EXISTS myProject (id INTEGER PRIMARY KEY, project_name TEXT NOT NULL)";
		String createLogTableQuery = "CREATE TABLE IF NOT EXISTS myLog (id INTEGER PRIMARY KEY, project_id INTEGER NOT NULL, project_type TEXT NOT NULL, life_cycle_step TEXT NOT NULL, effort_area TEXT NOT NULL, description TEXT NOT NULL, time_spent NUMERIC NOT NULL)";
		String createUserTableQuery = "CREATE TABLE IF NOT EXISTS myUsers (id INTEGER PRIMARY KEY, username TEXT NOT NULL, encrypted_password TEXT NOT NULL, organization_key INTEGER NOT NULL)";
		// connect to database and execute the above queries
		try (Connection connection = connect();
				Statement statement = connection.createStatement()) {
			statement.executeUpdate(createProjectTableQuery);
			System.out.println("Project table created successfully");
			statement.executeUpdate(createLogTableQuery);
			System.out.println("Log table created successfully");
			statement.executeUpdate(createUserTableQuery);
			System.out.println("User table created successfully");
		} catch (SQLException e) {
			System.err.println("Error creating project table: " + e.getMessage());
		}
	}
	
	// get project name from database
	public static List<String> getProjects() {
		List<String> projects = new ArrayList<>();
		String query = "SELECT * FROM myProject";

		try (Connection connection = connect();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {


			while (resultSet.next()) {
				String projectName = resultSet.getString("project_name");
				projects.add(projectName);
			}

		} catch (SQLException e) {
			System.err.println("Error while getting project: " + e.getMessage());
		}

		return projects;
	}

	// get logs from database
	public static List<String> getLogs(int projectID) {
		List<String> logs = new ArrayList<>();
		String query = "SELECT * FROM myLog WHERE project_id = " + projectID;

		try (Connection connection = connect();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String projectType = resultSet.getString("project_type");
				String lifeCycleStep = resultSet.getString("life_cycle_step");
				String effortArea = resultSet.getString("effort_area");
				String description = resultSet.getString("description");
				double timeSpent = resultSet.getDouble("time_spent");
				logs.add("ID: " + id + " | Project Type: " + projectType + " | Life Cycle Step: " + lifeCycleStep +
					" | Effort Area: " + effortArea + " | Description: " + description + " | Time Spent: " + timeSpent);
				
			}
		} catch (SQLException e) {
			System.err.println("Error while getting logs: " + e.getMessage());
		}

		return logs;
	}
  
  	// delete a certain project
	public static void deleteProject(String projectName, int projectID) {
		String query = "DELETE FROM myProject WHERE project_name = '" + projectName + "'";
		String query2 = "DELETE FROM myLog WHERE project_id = " + projectID;

		try (Connection connection = connect();
				Statement statement = connection.createStatement()) {
				statement.executeUpdate(query);
				statement.executeUpdate(query2);
				System.out.println("Project deleted successfully");

		} catch (SQLException e) {
			System.err.println("Error while deleting from project table: " + e.getMessage());
		}
	}

	// edit a certain project
	public static void editProject(String oldProjectName, String newProjectName) {
		String query = "UPDATE myProject SET project_name = '" + newProjectName + "' WHERE project_name = '" + oldProjectName + "'";

		try (Connection connection = connect();
				Statement statement = connection.createStatement()) {
				statement.executeUpdate(query);
				System.out.println("Project edited successfully");

		} catch (SQLException e) {
			System.err.println("Error while editing name from project table: " + e.getMessage());
		}
	}
	
	// edit a certain log in a project
	public static void editLog(int logID, String project, String cycle, String effort) {
			String query = "UPDATE myLog SET project_type = '" + project + "', life_cycle_step = '" + cycle + "', effort_area = '" + effort + "' WHERE id = '" + logID + "'";
			try (Connection connection = connect();
					Statement statement = connection.createStatement()) {
					statement.executeUpdate(query);
					System.out.println("Project edited successfully");

			} catch (SQLException e) {
				System.err.println("Error while editing name from project table: " + e.getMessage());
			}
		}
	
	// get user password
	public static String getPassword(String username) {
		String password = "";
		String query = "SELECT encrypted_password FROM myUsers WHERE username = '" + username + "'";
		try (Connection connection = connect();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				password = resultSet.getString("encrypted_password");
			}
		} catch (SQLException e) {
			System.err.println("Error while getting password from project table: " + e.getMessage());
		}
		return password;
	}
	// add user to database
	public static void addUser(String username, String password, String organization) {
		String query = "INSERT INTO myUsers (username, encrypted_password, organization_key) VALUES (?,?,?)";
		try (Connection connection = connect()) {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, organization);
			preparedStatement.executeUpdate();
			System.out.println("User successfully saved");
		}
		catch (SQLException e) {
			System.err.println("Error while updating users: " + e.getMessage());
		}
	}
}
