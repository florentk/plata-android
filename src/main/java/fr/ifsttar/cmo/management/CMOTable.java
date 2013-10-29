package fr.ifsttar.cmo.management;

import java.util.HashMap;

/**
 * CMO table is a HashMap
 * @author florent
 * @has 0..* - - CMOTableEntry
 */
public class CMOTable extends HashMap<String, CMOTableEntry> {
	static final long serialVersionUID = 264532L;
}
