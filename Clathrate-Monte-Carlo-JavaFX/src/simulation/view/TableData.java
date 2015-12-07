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

import javafx.beans.property.SimpleStringProperty;

public class TableData {
	 
    private final SimpleStringProperty stepMC;
    private final SimpleStringProperty enU;
    private final SimpleStringProperty enH;
    private final SimpleStringProperty boxX;
    private final SimpleStringProperty boxY;
    private final SimpleStringProperty boxZ;

    TableData(String stepMC, String enU, String enH, String boxX, String boxY, String boxZ) {
        this.stepMC = new SimpleStringProperty(stepMC);
        this.enU = new SimpleStringProperty(enU);
        this.enH = new SimpleStringProperty(enH);
        this.boxX = new SimpleStringProperty(boxX);
        this.boxY = new SimpleStringProperty(boxY);
        this.boxZ = new SimpleStringProperty(boxZ);
    }

	public String getStepMC() {
		return stepMC.get();
	}
	
	public void setStepMC(String srtStepMC) {
		stepMC.set(srtStepMC);
	}

	public String getEnU() {
		return enU.get();
	}
	
	public void getEnU(String strEnU) {
		enU.set(strEnU);
	}

	public String getEnH() {
		return enH.get();
	}
	
	public void setEnH(String strEnH) {
		enH.set(strEnH);
	}

	public String getBoxX() {
		return boxX.get();
	}
	
	public void setBoxX(String strBoxX) {
		boxX.set(strBoxX);
	}

	public String getBoxY() {
		return boxY.get();
	}
	
	public void setBoxY(String strBoxY) {
		boxY.set(strBoxY);
	}

	public String getBoxZ() {
		return boxZ.get();
	}

	public void setBoxZ(String strBoxZ) {
		boxZ.set(strBoxZ);
	}
}


