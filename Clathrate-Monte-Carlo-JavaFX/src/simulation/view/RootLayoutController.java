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

package simulation.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.util.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import simulation.MainApp;
import simulation.model.Model;


public class RootLayoutController implements Initializable{

    // Reference to the main application.
    private MainApp mainApp;
    private Model theModel;
    public SimulationTask taskSimulation = null;   
    
    private SwingNode swingNode = new SwingNode();
    
	private JPanel panelJMol;    
	private JmolPanel jmolPanel = null;

	public double volume;
    
    private StringProperty strPropNumbOfMCSteps; 
    
	private String mainJMolScriptString = "select molecule <= 272; wireframe on; spacefill off;";
	private String boxJMolScriptString = " boundbox off;";
	private String perspectiveJMolScriptString = " set perspectiveDepth on;";	
    
    private Pane pane;
    
    private final ObservableList<TableData> dataTable =
            FXCollections.observableArrayList(new TableData("", "", "", "", "", ""));
    
    private LineChart.Series<Integer, Double> seriesPotentialEnergy = new LineChart.Series<Integer, Double>();
    private LineChart.Series<Integer, Double> seriesDensity = new LineChart.Series<Integer, Double>();
    private LineChart.Series<Integer, Double> seriesBox = new LineChart.Series<Integer, Double>();
    
    private StringBuilder sbJMol = new StringBuilder();
    private StringBuilder sbWater = new StringBuilder();
    private StringBuilder sbHydrogen = new StringBuilder();
    private StringBuilder sbTHF = new StringBuilder();
    private StringBuilder sbAcetylene = new StringBuilder();
    
    private boolean bH2 = false;
    private boolean bACET = false;
    private boolean bTHF = false;
    private int numbOfMols = 272;
    private int numbOfAtoms = 272*3;

    private UserData userData;
    
    @FXML
    private BorderPane borderPaneJMol;
    @FXML
    private RadioButton radiobtnNVT;
    @FXML
    private RadioButton radiobtnNPT;
    @FXML
    private TextField textfieldTemperature;
    @FXML
    private TextField textfieldPressure;
    @FXML
    private TextField textfieldFrequency;
    @FXML
    private ComboBox<KeyValuePair> comboboxWater;    
    @FXML
    private ComboBox<KeyValuePair> comboboxHydrogen;       
    @FXML
    private ComboBox<KeyValuePair> comboboxPromoter;        
    @FXML
    private TextField textfieldNumbOfMCSteps;
    @FXML
    private Button btnSimulationRun;
    @FXML
    private Button btnSimulationStop; 
    @FXML
    private Label lblSimulationProgress;    
    @FXML
    private ProgressBar progressBarSimulation;    
    @FXML
    private ProgressIndicator progressIndicatorSimulation; 
    @FXML
    private TableView<TableData> tableOutput; 
    @FXML
    private ToggleButton tglbtnBox;
    @FXML
    private ToggleButton getTglBtnPerspective;
    @FXML
    private LineChart<Integer, Double> lineChartPotentialEnergy;
    @FXML
    private LineChart<Integer, Double> lineChartDensity;
    @FXML
    private LineChart<Integer, Double> lineChartBox;
    @FXML
    private LineChart<Double, Double> lineChartRDF;
    @FXML
    private LineChart<Double, Double> lineChartParticleDistrib;
    @FXML
    private NumberAxis yAxisPotEn;
    @FXML
    private NumberAxis yAxisDens;
    @FXML
    private NumberAxis yAxisBox;
    @FXML
    private NumberAxis xAxisRDF;
    @FXML
    private NumberAxis yAxisRDF;
    @FXML
    private NumberAxis xAxisParticleDistrib;
    @FXML
    private NumberAxis yAxisParticleDistrib;
    @FXML
    private NumberAxis xAxisPotEn;    
    @FXML
    private NumberAxis xAxisDens;
    @FXML
    private NumberAxis xAxisBox;    
    @FXML
    private ToolBar toolBarEnsemble;


    public RootLayoutController() {   	
    	
    }
	
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		btnSimulationStop.setDisable(true);		
		radiobtnNPT.requestFocus();
		
        userData = new UserData(true,200.0,1.0,2500,250000);
        
        try {
            //Unmarshalling: Converting XML content to Java objects
            userData = JAXBHandler.unmarshal(new File("./input_files/userdata.xml"));
        } catch (JAXBException e) {
            e.printStackTrace();
        } 
        
		textfieldTemperature.setText(Double.toString(userData.getTemperature()));
		textfieldPressure.setText(Double.toString(userData.getPressure()));
		textfieldFrequency.setText(Integer.toString(userData.getFrequency()));
		textfieldNumbOfMCSteps.setText(Integer.toString(userData.getNumbOfMCSteps()));
		
		comboboxWater.getItems().addAll(new KeyValuePair(272, "TIP3P"),
                						new KeyValuePair(272, "SPC/E")
                					   );
		comboboxWater.getSelectionModel().select(0);
		
/*		comboboxWater.getSelectionModel().selectedItemProperty()
			.addListener((observable, oldValue, newValue) -> {
				System.out.println(newValue + " chosen in ChoiceBox");
				System.out.println(observable.getValue().getKey() );
				System.out.println(observable.getValue().toString() );
		});*/
		
		comboboxWater.getSelectionModel().selectedItemProperty()
				.addListener(this::indexComboBoxChanged);

		
		comboboxHydrogen.getItems().addAll(new KeyValuePair(0, "none"),
										   new KeyValuePair(40, "Hydrogen")
										  );
		comboboxHydrogen.getSelectionModel().select(0);		
		comboboxHydrogen.getSelectionModel().selectedItemProperty()
				.addListener(this::indexComboBoxChanged);
		
		comboboxPromoter.getItems().addAll(new KeyValuePair(0, "none"),
										   new KeyValuePair(8, "THF"), 
										   new KeyValuePair(8, "Acetylene")
										   );
		comboboxPromoter.getSelectionModel().select(0);
		comboboxPromoter.getSelectionModel().selectedItemProperty()
				.addListener(this::indexComboBoxChanged);

		strPropNumbOfMCSteps = new SimpleStringProperty();
		strPropNumbOfMCSteps.bind(textfieldNumbOfMCSteps.textProperty());
		
		lblSimulationProgress.setText("");	
		
		tableOutput.setEditable(false);
		
		getTglBtnPerspective.setSelected(true);
		
        TableColumn coltableMCStep = new TableColumn("MC step");
        coltableMCStep.setMinWidth(100);
        coltableMCStep.setStyle( "-fx-alignment: CENTER;");
        coltableMCStep.setCellValueFactory(new PropertyValueFactory<>("stepMC"));
        coltableMCStep.setSortable(false);
        
        TableColumn colPotEnergy = new TableColumn("U, kcal/mol");
        colPotEnergy.setMinWidth(100);
        colPotEnergy.setStyle( "-fx-alignment: CENTER;");
        colPotEnergy.setCellValueFactory(new PropertyValueFactory<>("enU"));
        colPotEnergy.setSortable(false);
        
        TableColumn colEnthalpy = new TableColumn("H, kcal/mol");
        colEnthalpy.setMinWidth(100);
        colEnthalpy.setStyle( "-fx-alignment: CENTER;");
        colEnthalpy.setCellValueFactory(new PropertyValueFactory<>("enH"));
        colEnthalpy.setSortable(false);
        
        TableColumn colXBox = new TableColumn("xBox, A");
        colXBox.setMinWidth(100);
        colXBox.setStyle( "-fx-alignment: CENTER;");
        colXBox.setCellValueFactory(new PropertyValueFactory<>("boxX"));  
        colXBox.setSortable(false);
        
        TableColumn colYBox = new TableColumn("yBox, A");
        colYBox.setMinWidth(100);
        colYBox.setStyle( "-fx-alignment: CENTER;");
        colYBox.setCellValueFactory(new PropertyValueFactory<>("boxY"));  
        colYBox.setSortable(false);
        
        TableColumn colZBox = new TableColumn("zBox, A");
        colZBox.setMinWidth(100);
        colZBox.setStyle( "-fx-alignment: CENTER;");
        colZBox.setCellValueFactory(new PropertyValueFactory<>("boxZ"));
        colZBox.setSortable(false);
        
        tableOutput.setItems(dataTable);
        tableOutput.getColumns().addAll(coltableMCStep, colPotEnergy, colEnthalpy, colXBox, colYBox, colZBox);
        
        lineChartPotentialEnergy.setCreateSymbols(false);
        yAxisPotEn.setLabel("U, kcal/mol");
        xAxisPotEn.setLabel("MC step");
        yAxisPotEn.setLowerBound(-21.0);
        yAxisPotEn.setUpperBound(-11.5);
        yAxisPotEn.setTickUnit(5.0);
        
        lineChartDensity.setCreateSymbols(false);
        yAxisDens.setLabel("rho, g/cm^3");
        xAxisDens.setLabel("MC step");
        yAxisDens.setLowerBound(0.76);
        yAxisDens.setUpperBound(0.83);
        yAxisDens.setTickUnit(0.07);
        
        lineChartBox.setCreateSymbols(false);
        yAxisBox.setLabel("box, A");
        xAxisBox.setLabel("MC step");
        yAxisBox.setLowerBound(24.3);
        yAxisBox.setUpperBound(24.8);
        yAxisBox.setTickUnit(0.5);
        
        lineChartRDF.setCreateSymbols(false);
        yAxisRDF.setLabel("g(r), O-H");
        yAxisRDF.setLowerBound(0.0);
        yAxisRDF.setUpperBound(3.5);
        yAxisRDF.setTickUnit(0.5);
        xAxisRDF.setLabel("r, A");
        xAxisRDF.setLowerBound(0.0);
        xAxisRDF.setUpperBound(10.0);
        xAxisRDF.setTickUnit(1.0);
        
        lineChartParticleDistrib.setCreateSymbols(false);
        yAxisParticleDistrib.setLabel("Particle distribution, O-H ");
        yAxisParticleDistrib.setLowerBound(0.0);
        yAxisParticleDistrib.setUpperBound(40.0);
        yAxisParticleDistrib.setTickUnit(5.0);
        xAxisParticleDistrib.setLabel("r, A");        
        xAxisParticleDistrib.setLowerBound(0.0);
        xAxisParticleDistrib.setUpperBound(10.0);
        xAxisParticleDistrib.setTickUnit(1.0);
        
		sbWater     = readVMDlikeFile("input_simulation_water_framework.xyz");
		sbHydrogen  = readVMDlikeFile("input_initial_hyrogens.xyz");
		sbAcetylene = readVMDlikeFile("input_initial_acetylene.xyz");
		sbTHF       = readVMDlikeFile("input_initial_THF.xyz");
		sbJMol.append(816).append("\n\n").append(sbWater.toString());
        
        createAndSetSwingContent(swingNode);        
        pane = new Pane();
        pane.getChildren().add(swingNode); // Adding swing node
        borderPaneJMol.getChildren().add(pane);
        
		theModel = new Model();

	}
	
	// A change listener to track the change in index selection
	public void indexComboBoxChanged(ObservableValue<? extends KeyValuePair> observable,
									 KeyValuePair oldValue, 
									 KeyValuePair newValue
									 ) {
		
		int outputWater = comboboxWater.getSelectionModel().getSelectedItem().getKey();
		int outputH2 = comboboxHydrogen.getSelectionModel().getSelectedItem().getKey();
		int outputProm = comboboxPromoter.getSelectionModel().getSelectedItem().getKey();
		
		numbOfMols = outputWater + outputH2 + outputProm;
		
		numbOfAtoms = outputWater*3 + outputH2*3;
		
		if ( outputH2 == 0 ){			
			bH2 = false;
		} else {
			bH2 = true;
		}
		
		if (outputProm != 0) {
			
			String strSelected = comboboxPromoter.getSelectionModel().getSelectedItem().getValue();

			if (strSelected.compareTo("THF") == 0) {
				numbOfAtoms += outputProm * 13;
				bTHF = true;
				bACET = false;
			} else if (strSelected.compareTo("Acetylene") == 0) {
				numbOfAtoms += outputProm * 4;
				bACET = true;
				bTHF = false;
			}
		}
		
		sbJMol.setLength(0);
		
		sbJMol.append(numbOfAtoms).append("\n\n").append(sbWater.toString());
		
		if ( bH2 ){
			sbJMol.append(sbHydrogen.toString());
		}
		
		if ( bACET ){
			sbJMol.append(sbAcetylene.toString());
		}
		
		if ( bTHF ){
			sbJMol.append(sbTHF.toString());
		}
		
		showCrystalInJMol(sbJMol);
	} 
	
    private void createAndSetSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                jmolPanel = new JmolPanel();
        		jmolPanel.setPreferredSize(new Dimension(620, 620));
        		swingNode.setContent(jmolPanel);
        		
        		showCrystalInJMol(sbJMol);
            }
        });
    }  
    
    
	static class JmolPanel extends JPanel {

		JmolViewer viewer;

		private final Dimension currentSize = new Dimension();

		JmolPanel() {
			viewer = JmolViewer.allocateViewer(this, new SmarterJmolAdapter(), null, null, null, null, null);
		}

		@Override
		public void paint(Graphics g) {
			getSize(currentSize);
			viewer.renderScreenImage(g, currentSize.width, currentSize.height);
		}
	}

    @FXML
    private void handleBtnRun() {

/*        Alert alert = new Alert(AlertType.INFORMATION);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setContentText("Button Run was clicked");
        alert.showAndWait();    */   
    	
    	lblSimulationProgress.setTextFill(Color.web("#0d0dee"));
    	
		double temperature = Double.parseDouble(textfieldTemperature.getText());
		double pressure = Double.parseDouble(textfieldPressure.getText());
		int outputFrequency = Integer.parseInt(textfieldFrequency.getText());
		int numbMC = Integer.parseInt(textfieldNumbOfMCSteps.getText());

		
		boolean nvt = false;			
		if ( radiobtnNVT.isSelected() ){
			nvt = true;
		} else if ( radiobtnNPT.isSelected() ){
			nvt = false;
		}    
		
		dataTable.clear();
    	dataTable.add(new TableData("", "", "", "", "", "") );
    	
    	seriesPotentialEnergy.getData().clear();
    	seriesDensity.getData().clear();
    	seriesBox.getData().clear();
    	
    	disableControls(true);
    	
    	btnSimulationRun.setDisable(true);
    	progressBarSimulation.setProgress(0);
    	progressIndicatorSimulation.setProgress(0);
    	btnSimulationStop.setDisable(false);    	
    	
    	taskSimulation = new SimulationTask(this, 
    			                            theModel, 
    			                            numbMC, 
    			                            outputFrequency, 
    			                            temperature, 
    			                            pressure, 
    			                            nvt,
    			                            numbOfMols,
    			                            numbOfAtoms,
    			                            bH2,
    			                            bACET,
    			                            bTHF
    			                           );
    	
    	Thread backgroundThread = new Thread (taskSimulation);
    	backgroundThread.setDaemon(true);
    	
    	progressBarSimulation.progressProperty().unbind();
    	progressBarSimulation.progressProperty().bind( taskSimulation.progressProperty() );
    	
    	lblSimulationProgress.textProperty().unbind();
    	lblSimulationProgress.textProperty().bind( taskSimulation.messageProperty() );
    	
    	progressIndicatorSimulation.progressProperty().unbind();
    	progressIndicatorSimulation.progressProperty().bind( taskSimulation.progressProperty() );
    	
    	backgroundThread.start();
    }
    
    @FXML
    private void handleBtnStop() {
    	
    	disableControls(false);
    	setSimulationCanceled();
    }
    
    public Label getSimulationProgressLabel(){
    	
    	return lblSimulationProgress;
    }
    
    public void setMainApp(MainApp mainApp) {

    	this.mainApp = mainApp;
    }
    
    public void setSimulationCanceled() {

    	btnSimulationRun.setDisable(false);
    	btnSimulationStop.setDisable(true);
    	
    	if (taskSimulation != null) taskSimulation.cancel(true);
    	
    	progressBarSimulation.progressProperty().unbind();
    	progressBarSimulation.setProgress(0);
    	progressIndicatorSimulation.progressProperty().unbind();
    	progressIndicatorSimulation.setProgress(0);   
    } 
    
    public void setSimulationFinished() {

    	disableControls(false);
    	btnSimulationRun.setDisable(false);
    	btnSimulationStop.setDisable(true);
    	
    	progressBarSimulation.progressProperty().unbind();
    	progressBarSimulation.setProgress(0);
    	progressIndicatorSimulation.progressProperty().unbind();
    	progressIndicatorSimulation.setProgress(0);   
    } 
    
    @FXML
    private void handleAbout() {
    	
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Monte Carlo Simulation of sH Clathrate");
        alert.setHeaderText("About");
        alert.setContentText("Author:  Alexander Atamas");

        alert.showAndWait();
    }
    
    @FXML
    private void handleExit() {
        System.exit(0);
    }
    
    @FXML
    private void handleBtnBox() {
    	
		if (tglbtnBox.isSelected()) {
			
			setBoxJMolScriptString(" boundbox on;");

		} else if (!tglbtnBox.isSelected()) {

			setBoxJMolScriptString(" boundbox off;");
		}
    	
    }
    
    @FXML
    private void handleBtnPerspective() {
    
		if (getTglBtnPerspective.isSelected()) {

			setPerspectiveJMolScriptString(" set perspectiveDepth on;");

		} else if (!getTglBtnPerspective.isSelected()) {

			setPerspectiveJMolScriptString(" set perspectiveDepth off;");
		}
    	
    }
    
	public void setBoxJMolScriptString(String jMolScriptString) {

		boxJMolScriptString = jMolScriptString;

		mainJMolScriptString += boxJMolScriptString;
		mainJMolScriptString += perspectiveJMolScriptString;
		jmolPanel.viewer.evalString(mainJMolScriptString);
	}
	
	public void setPerspectiveJMolScriptString(String jMolScriptString){
		
		perspectiveJMolScriptString = jMolScriptString;

		mainJMolScriptString += boxJMolScriptString;
		mainJMolScriptString += perspectiveJMolScriptString;
		jmolPanel.viewer.evalString(mainJMolScriptString);			
	}
	
	void updateLineChartPotentialEnergy(int x, double y){
		
        ObservableList<XYChart.Series<Integer, Double>> lineChartData = FXCollections.observableArrayList();
        
        lineChartPotentialEnergy.getXAxis().setAutoRanging(true);
        lineChartPotentialEnergy.getYAxis().setAutoRanging(false);
        lineChartPotentialEnergy.setStyle("CHART_COLOR_1: #ba0b2a;");
        
        yAxisPotEn.setLabel("U, kcal/mol");
        yAxisPotEn.setLowerBound(-21.0);
        yAxisPotEn.setUpperBound(-11.5);
        yAxisPotEn.setTickUnit(5.0);
        
        seriesPotentialEnergy.setName("Serie 1");
        seriesPotentialEnergy.getData().add(new XYChart.Data<Integer, Double>(x, y));
        
        lineChartData.add(seriesPotentialEnergy);
        
        lineChartPotentialEnergy.setData(lineChartData);
        lineChartPotentialEnergy.createSymbolsProperty();      
	
	}
	
	void updatelineChartDensity(int x, double y){
		
        ObservableList<XYChart.Series<Integer, Double>> lineChartData = FXCollections.observableArrayList();
        
        lineChartDensity.getXAxis().setAutoRanging(true);
        lineChartDensity.getYAxis().setAutoRanging(false);
        lineChartDensity.setStyle("CHART_COLOR_1: #0ba927;");

        yAxisDens.setLabel("rho, g/cm^3");
        yAxisDens.setLowerBound(0.76);
        yAxisDens.setUpperBound(0.83);
        yAxisDens.setTickUnit(0.07);
        
        seriesDensity.setName("Serie 1");
        seriesDensity.getData().add(new XYChart.Data<Integer, Double>(x, y));
        
        lineChartData.add(seriesDensity);
        
        lineChartDensity.setData(lineChartData);
        lineChartDensity.createSymbolsProperty();      
	
	}
	
	void updatelineChartBox(int x, double y){
		
        ObservableList<XYChart.Series<Integer, Double>> lineChartData = FXCollections.observableArrayList();
        
        lineChartBox.getXAxis().setAutoRanging(true);
        lineChartBox.getYAxis().setAutoRanging(false);
        lineChartBox.setStyle("CHART_COLOR_1: #2613dc;");
        
        yAxisBox.setLabel("box, A");
        yAxisBox.setLowerBound(24.3);
        yAxisBox.setUpperBound(24.8);
        yAxisBox.setTickUnit(0.5);
        
        seriesBox.setName("Serie 1");
        seriesBox.getData().add(new XYChart.Data<Integer, Double>(x, y));
        
        lineChartData.add(seriesBox);
        
        lineChartBox.setData(lineChartData);
        lineChartBox.createSymbolsProperty();	
	}
	
	void updateLineChartRDF(ArrayList<Point2D> gr){
		
		lineChartRDF.getXAxis().setAutoRanging(false);
		lineChartRDF.getYAxis().setAutoRanging(false);
		lineChartRDF.setStyle("CHART_COLOR_1: #2613dc;");
		lineChartRDF.getData().clear();
        yAxisRDF.setLowerBound(0.0);
        yAxisRDF.setUpperBound(3.5);
        yAxisRDF.setTickUnit(0.5);
        xAxisRDF.setLowerBound(0.0);
        xAxisRDF.setUpperBound(10.0);
        xAxisRDF.setTickUnit(1.0);
		
		XYChart.Series<Double, Double> series = new XYChart.Series<Double, Double>();
	    series.setName("My Data");
    
	    for (int i = 0; i < gr.size(); i++) {
        	series.getData().
        		add(new XYChart.Data<Double, Double>(gr.get(i).getX(), gr.get(i).getY()));
		}
	    lineChartRDF.getData().add(series);		
	}
	
	void updateLineChartParticleDistrib(ArrayList<Point2D> gr, double vol){
		
		lineChartParticleDistrib.getXAxis().setAutoRanging(false);
		lineChartParticleDistrib.getYAxis().setAutoRanging(false);
		lineChartParticleDistrib.setStyle("CHART_COLOR_1: #0ba927;");
		lineChartParticleDistrib.getData().clear();
        yAxisParticleDistrib.setLowerBound(0.0);
        yAxisParticleDistrib.setUpperBound(40.0);
        yAxisParticleDistrib.setTickUnit(5.0);
        xAxisParticleDistrib.setLowerBound(0.0);
        xAxisParticleDistrib.setUpperBound(10.0);
        xAxisParticleDistrib.setTickUnit(1.0);
		
		XYChart.Series<Double, Double> series = new XYChart.Series<Double, Double>();
	    series.setName("My Data");
    
		double grNorm, r, r2;
		
        for (int j = 0; j < gr.size(); j++) {
        	
        	r = gr.get(j).getX();
        	r2 = r*r;
        	grNorm = gr.get(j).getY()*4*Math.PI*r2*272/vol;
        	
        	series.getData().
    			add(new XYChart.Data<Double, Double>(r, grNorm));
		} 	    
        lineChartParticleDistrib.getData().add(series);		
	}

    
    public void updateGUI(int i, 
    		              double enU, 
    		              double enH, 
    		              double xBox, 
    		              double yBox, 
    		              double zBox, 
    		              ArrayList<Point2D> gr, 
    		              double density, 
    		              StringBuilder sbAllAtomsXYZ
    		             ){
    	
    	if ( dataTable.get(0).getStepMC() == ""){
    		
    		dataTable.clear();
    	}
    	
		Locale.setDefault(Locale.US);		
    	
    	dataTable.add(new TableData(String.format("%,d", i),
    			                    String.format("%7.3f", enU),
    			                    String.format("%7.3f", enH),
    			                    String.format("%7.3f", xBox),
    			                    String.format("%7.3f", yBox),
    			                    String.format("%7.3f", zBox)
    	));
    	
    	tableOutput.getSelectionModel().clearSelection();
    	tableOutput.requestFocus();
    	int numbOfRows = tableOutput.getItems().size()-1;	
    	tableOutput.getSelectionModel().select(numbOfRows);
    	tableOutput.getFocusModel().focus(numbOfRows);
		tableOutput.scrollTo(numbOfRows);
   	
    	
    	showCrystalInJMol(sbAllAtomsXYZ);
    	
    	updateLineChartPotentialEnergy(i, enU);
    	updatelineChartDensity(i, density);
    	updatelineChartBox(i, xBox);
    	
    	updateLineChartRDF(gr);
    	double vol = xBox*yBox*zBox;
    	updateLineChartParticleDistrib(gr,vol);    	
    }    

	public void showCrystalInJMol(StringBuilder sbAllAtomsXYZ){
		
		if ( jmolPanel != null ){

			String strError = jmolPanel.viewer.openStringInline(sbAllAtomsXYZ.toString());
			
			mainJMolScriptString += boxJMolScriptString;
			mainJMolScriptString += perspectiveJMolScriptString;
			
		    if (strError == null){
		        jmolPanel.viewer.evalString(mainJMolScriptString);
		    } else {
		        Logger.error(strError);
		    }
		    
		    try {
		          Thread.sleep(20);
		    } catch (InterruptedException e) {
		    	  System.out.println("Thread.sleep Exception");
		    }    
		}
	}
	
	public StringBuilder readVMDlikeFile(final String fileName){
		
		StringBuilder sb = new StringBuilder();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("./input_files/" + fileName));			
			ArrayList lines = new ArrayList();
			
			try {				
				for(String line = br.readLine();line != null;line = br.readLine()) {
					
					line = line.trim();		
					sb.append(line).append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		
		return sb;
	}
	
	public void disableControls(boolean bFlag){
		
		toolBarEnsemble.setDisable(bFlag);
	    textfieldTemperature.setDisable(bFlag);	    
	    textfieldPressure.setDisable(bFlag);
	    textfieldFrequency.setDisable(bFlag);
	    comboboxWater.setDisable(bFlag);    
	    comboboxHydrogen.setDisable(bFlag);       
	    comboboxPromoter.setDisable(bFlag);        
	    textfieldNumbOfMCSteps.setDisable(bFlag);
	}
    public void showAboutDialog() {
        try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/aboutDialogLayout.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("About");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainApp.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            // Set the persons into the controller.
            AboutDialogController controller = loader.getController();
            controller.setAboutDialogStage(dialogStage);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void saveUserSettings(){
    	
        try {
        	
    		double temperature = Double.parseDouble(textfieldTemperature.getText());
    		double pressure = Double.parseDouble(textfieldPressure.getText());
    		int outputFrequency = Integer.parseInt(textfieldFrequency.getText());
    		int numbMC = Integer.parseInt(textfieldNumbOfMCSteps.getText());
        	
        	userData.setEnsembleNPT(true);
        	userData.setTemperature(temperature);
        	userData.setPressure(pressure);
        	userData.setFrequency(outputFrequency);
        	userData.setNumbOfMCSteps(numbMC);
        	
            //Marshalling: Writing Java object to XML file
            JAXBHandler.marshal(userData, new File("./input_files/userdata.xml"));
         
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    	
    }
    
}


