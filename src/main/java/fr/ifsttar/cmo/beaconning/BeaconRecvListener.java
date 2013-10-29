package fr.ifsttar.cmo.beaconning;

import fr.ifsttar.cmo.beaconning.packet.*;

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

