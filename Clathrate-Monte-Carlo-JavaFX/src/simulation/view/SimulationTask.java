/*****************************************************************************

    Monte Carlo Simulation of sH Clathrate

    Copyright 20014, 2015 Alexander A. Atamas
    
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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import simulation.model.AtomVector;
import simulation.model.IMolecularSimulation;
import simulation.model.Model;

public class SimulationTask extends Task<Object> implements IMolecularSimulation{

	private RootLayoutController rootLayoutController;
	
	private int numbMCSimulations;
	private int numbOfMolsActual;	
	private Model theModel;
	private int outputFrequency;
	private double temperature;
	private double pressure;
	private boolean nvt;
	private StringBuilder sbAllAtomsXYZ = new StringBuilder();
	public ArrayList<Point2D> gr = new ArrayList<Point2D>();
	private boolean bH2;
	private boolean bACET;
	private boolean bTHF;
	private int numbOfAtoms;

	public SimulationTask() {
	}

	public SimulationTask(RootLayoutController rootLayoutController,
			              Model theModel,
			              int numbMCSimulations, 
			              int outputFrequency, 
			              double temperature, 
			              double pressure, 
			              boolean nvt,
			              int numbOfMols,
			              int numbOfAtoms,
			              boolean bH2,
			              boolean bACET,
			              boolean bTHF
			             ) {

		this.rootLayoutController = rootLayoutController;
		this.numbMCSimulations = numbMCSimulations;		
		this.theModel = theModel;
		this.outputFrequency = outputFrequency;
		this.temperature = temperature;
		this.pressure = pressure;
		this.nvt = nvt;
		this.numbOfMolsActual = numbOfMols;
		this.numbOfAtoms = numbOfAtoms;
		this.bH2 = bH2;
		this.bACET = bACET;
		this.bTHF = bTHF;
	}

	@Override
	protected Object call() throws Exception {

		Object results = null;
		String status;
		
		theModel.setupMonteCarloSimulation(numbMCSimulations, 
										   outputFrequency, 
										   temperature, 
										   pressure, 
										   nvt,
										   numbOfMolsActual,
										   numbOfAtoms,
										   bH2,
										   bACET,
										   bTHF
										   );
		
		int iMol, iRan;
		int start, end;
		Random random = new Random();
		ArrayList<Point2D> gr;

		for (int i = 0; i < numbMCSimulations; i++) {

			if (this.isCancelled()) {
				break;
			}
			
			start = 0;
		    end = numbOfMolsActual - 1;		    
		    iMol = RandomInteger(start, end, random);   // generate random index of molecule
		    
			start = 1;
		    end = 2*numbOfMolsActual + 3;		    
		    iRan = RandomInteger(start, end, random);
		    
		    theModel.runMCEngine(i, iMol, iRan);    
			
			if ( i % outputFrequency == 0 ){
				
			    AtomVector box = theModel.getSimulOutputData().box;
				int istep = theModel.getSimulOutputData().i;
			    double enU = theModel.getSimulOutputData().energy;
			    double enH = theModel.getSimulOutputData().energyH;
			    double xBox = theModel.getSimulOutputData().box.x;
			    double yBox = theModel.getSimulOutputData().box.y;
			    double zBox = theModel.getSimulOutputData().box.z;
			    gr = new ArrayList<Point2D>();
			    
			    if (istep==0) istep++;
			    
			    final int j = istep; 
			    
			    gr = theModel.getSimulOutputData().gr;
			    
			    sbAllAtomsXYZ.setLength(0);
			    sbAllAtomsXYZ = theModel.getSimulOutputData().sbAllAtomsXYZ;
			    
			    System.out.printf("i,E,box = %9d    %8.2f   %8.2f   %s\n", istep, enU , enH, box);
		    
			    double vol = xBox*yBox*zBox;
			    double Mw = 18.01528;    					// Mw, molar mass of water molecule, g/mol 
			    Mw = Mw*1e-3;            					// molar mass of water in kg/mol
			    double Mass = 272*Mw;
			    double rho = Mass/vol*1e30/NA/1e3; 			// density, g/cm^3	
				
				Platform.runLater( new Runnable() {
					@Override public void run(){
						rootLayoutController.updateGUI(j, enU, enH, xBox, yBox, zBox, theModel.getSimulOutputData().gr, rho, sbAllAtomsXYZ);
					}
				});	
				
				Locale.setDefault(Locale.US);
				status = String.format("%,d out of %,d", istep, numbMCSimulations);
				updateMessage(status);
				updateProgress(i + 1, numbMCSimulations);		
			}
		}

		return results;
	}

	@Override
	protected void cancelled() {

		super.cancelled();
		updateMessage("Simulation cancelled.");
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				rootLayoutController.getSimulationProgressLabel().setTextFill(Color.web("#42c21b"));
			}
		});
	
	}

	@Override
	protected void failed() {

		super.failed();		
		updateMessage("Simulation failed."); 
		
		Platform.runLater( new Runnable() {
			@Override public void run(){
				rootLayoutController.getSimulationProgressLabel()
				                    .setTextFill(Color.web("#cd0000"));
			}
		});
	}

	@Override
	protected void succeeded() {

		super.failed();		
		updateMessage("Simulation finished successfully.");
		
		Platform.runLater( new Runnable() {
			@Override public void run(){
				
				rootLayoutController.getSimulationProgressLabel()
                				    .setTextFill(Color.web("#42c21b"));
				rootLayoutController.setSimulationFinished();
			}
		});		
		
	}
	
	private static int RandomInteger(int aStart, int aEnd, Random aRandom) {
		if (aStart > aEnd) {
			throw new IllegalArgumentException("Start cannot exceed End.");
		}
		// get the range, casting to long to avoid overflow problems
		long range = (long) aEnd - (long) aStart + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long) (range * aRandom.nextDouble());
		int randomNumber = (int) (fraction + aStart);
		return randomNumber;
	}

}





