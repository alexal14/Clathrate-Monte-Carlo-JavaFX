/*****************************************************************************

    Monte Carlo Simulation of sH Clathrate

    Copyright 2014, 2015 Alexander A. Atamas
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*****************************************************************************/

package simulation;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import simulation.view.AboutDialogController;
import simulation.view.RootLayoutController;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;
    private RootLayoutController controller;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Monte Carlo Simulation of sH Clathrate by Alexander Atamas");

        initRootLayout();
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            this.controller = controller;
            controller.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
	@Override
	public void stop() {
		
		if ( controller.taskSimulation != null ){
			controller.setSimulationCanceled();
			controller.saveUserSettings();
		}
	}

    public static void main(String[] args) {
        launch(args);
    }
}