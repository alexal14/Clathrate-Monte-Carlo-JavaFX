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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "userdata")
public class UserData {
	
    @XmlAttribute
    private boolean ensembleNPT;
    private double temperature;
    private double pressure;
    private int frequency;
    private int numbOfMCSteps;
     
    //Must have no-argument constructor
    public UserData() { }
    
    public UserData(boolean ensembleNPT,
    		        double temperature,
    		        double pressure,
    		        int frequency,
    		        int numbOfMCSteps
    		       ) {
        this.ensembleNPT = ensembleNPT;
        this.temperature = temperature;
        this.pressure = pressure;
        this.frequency = frequency;
        this.numbOfMCSteps = numbOfMCSteps;    	
    }

	public boolean isEnsembleNPT() {
		return ensembleNPT;
	}

	public void setEnsembleNPT(boolean ensembleNPT) {
		this.ensembleNPT = ensembleNPT;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getNumbOfMCSteps() {
		return numbOfMCSteps;
	}

	public void setNumbOfMCSteps(int numbOfMCSteps) {
		this.numbOfMCSteps = numbOfMCSteps;
	}

	@Override
	public String toString() {
		return "UserData [ensembleNPT=" + ensembleNPT + ", temperature=" + temperature + ", pressure=" + pressure
				+ ", frequency=" + frequency + ", numbOfMCSteps=" + numbOfMCSteps + "]";
	}

}
