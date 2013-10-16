package fr.ifsttar.plaiimob.cmo.beaconning;

import fr.ifsttar.plaiimob.cmo.beaconning.packet.*;

/**
 * 
 * Listener for receive the state change
 * 
 * @author Florent Kaisser
 * @dep - - - CMOState
 */

public interface BeaconRecvListener {
	
	/**
	 * A CMO stat has changed
	 * @param stat new state of a CMO
	 */
	void cmoStatChanged(CMOState stat);
}

