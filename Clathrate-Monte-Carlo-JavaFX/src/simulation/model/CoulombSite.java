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

package simulation.model;

public class CoulombSite {
	
	public String labelSiteCoulomb;
	public double charge;	
	public AtomVector siteCoulomb;
	
	CoulombSite(String labelSiteCoulomb, double charge, AtomVector siteCoulomb){
		
		this.labelSiteCoulomb = labelSiteCoulomb;
		this.charge = charge;
		this.siteCoulomb = siteCoulomb;
	}

	@Override
	public String toString() {
		return "CoulombSite [labelSiteCoulomb=" + labelSiteCoulomb + ", charge=" + charge + ", siteCoulomb=" + siteCoulomb + "]";
	}

}
