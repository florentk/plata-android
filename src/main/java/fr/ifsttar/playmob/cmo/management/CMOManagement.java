package fr.ifsttar.playmob.cmo.management;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Collection;

import fr.ifsttar.playmob.cmo.beaconning.BeaconRecvListener;
import fr.ifsttar.playmob.cmo.beaconning.packet.CMOState;



/**
 * 
 * CMO state agregator
 * 
 * 
 * 
 * BeaconRecv --------
 *       ...         |------> CMOManagement -----> CMOTable
 * BeaconRecv --------
 * 
 * 
 * @author Florent Kaisser <florent.kaisser@free.fr>
 * @has 1 - - CMOTable
 * @has 0..* - - CMOTableListener
 */
public class CMOManagement implements BeaconRecvListener {

	/** interval between two expired entry check (in ms) */
	public static final int CHECK_EXPIRED_ENTRY_INTERVAL = 1000;
    private Handler mListenerHandler;

    private static final int TYPE_MSG_TABLE_CHANGED = 1;
    private static final int TYPE_MSG_DELETE_EXPIRED = 2;

    private CMOTable table;
	
	private final Collection<CMOTableListener> listerners = new ArrayList<CMOTableListener>();


    public CMOManagement(){
		table = new CMOTable();
		
		new Timer().schedule(new RemoveExpiredEntry() , 0, CHECK_EXPIRED_ENTRY_INTERVAL);

        mListenerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                _handleMessage(msg);
            }
        };
	}

    private void notifyListenerChanged(CMOTableEntry cmo){
		//Log.i("CMOManagement", "entry_chaged: " + cmo);
		
		//notify the listerners
		for (CMOTableListener l : listerners)
			l.tableChanged(cmo);
	}
	
	private void notifyListenerRemove(CMOTableEntry cmo){
        //Log.i("CMOManagement", "entry_removed: "+cmo);
		
		//notify the listerners
		for (CMOTableListener l : listerners)
			l.tableCMORemoved(cmo);		
	}	
	
	private void notifyListenerAdd(CMOTableEntry cmo){
        //Log.i("CMOManagement", "entry_added: "+cmo);
		
		//notify the listerners
		for (CMOTableListener l : listerners)
			l.tableCMOAdded(cmo);		
	}

	@Override
	public void cmoStatChanged(CMOState newStat) {
        Log.i("CMOManagement", "cmoStatChanged Thread : " + Thread.currentThread().getId());
            Message msg = Message.obtain();
            msg.what = TYPE_MSG_TABLE_CHANGED;
            msg.obj = newStat;
            mListenerHandler.sendMessage(msg);
	}

    private void _handleMessage(Message msg) {
        Log.i("CMOManagement", "_handleMessage Thread : " + Thread.currentThread().getId());
        switch(msg.what) {
            case TYPE_MSG_TABLE_CHANGED:
                updateTable((CMOState)msg.obj);
                break;

            case TYPE_MSG_DELETE_EXPIRED:
                deleteExpiredEntry();
                break;
        }
    }

    public void updateTable(CMOState newStat){
        CMOTableEntry entry;
        if( table.containsKey(newStat.getCmoID())){
                entry = table.get(newStat.getCmoID());

                entry.updateEntry(
                        new Double (newStat.getLongitude()),
                        new Double (newStat.getLatitude()),
                        new Double (newStat.getH()),
                        new Double (newStat.getSpeed()),
                        new Double (newStat.getTrack()),
                        newStat.getLifetime(),
                        newStat.getTime());
            notifyListenerChanged(entry);
        }else{
                table.put(newStat.getCmoID(),
                        entry = new CMOTableEntry(
                                newStat.getCmoID(),
                                newStat.getCmoType(),

                                new Double (newStat.getLongitude()),
                                new Double (newStat.getLatitude()),
                                new Double (newStat.getH()),
                                new Double (newStat.getSpeed()),
                                new Double (newStat.getTrack()),
                                newStat.getLifetime(),
                                newStat.getTime()
                        ));
            notifyListenerAdd(entry);
        }
    }
	
	/**
	 * check in regular interval the expired entry
	 */
	public void deleteExpiredEntry(){
		ArrayList<CMOTableEntry> entryDeleted = new ArrayList<CMOTableEntry>();
		

        for(Iterator<CMOTableEntry> i = table.values().iterator();i.hasNext();){
            CMOTableEntry entry = i.next();

            //expired entry ?
            if(entry.isExpired()){
                i.remove();
                entryDeleted.add(entry);
            }
        }

		
		//notify the listener of the removed entry				
		for (CMOTableEntry entry:entryDeleted){
            notifyListenerRemove(entry);
        }
	}
	

	public Collection<CMOTableEntry> getTable() {
		return new ArrayList<CMOTableEntry>(table.values());
	}	
	
	public CMOTableEntry getEntry(String id) {
		return table.get(id);
	}		
	
	public boolean cmoInTable(String id){
		return table.containsKey(id);
	}
	
	public Set<String> getCMOIds(){
		return new HashSet<String>(table.keySet());
	}
	
	public void addListener(CMOTableListener l){
		listerners.add(l);
	}
	
	public void removeListener(CMOTableListener l){
		listerners.remove(l);
	}	
	
	class RemoveExpiredEntry extends TimerTask{

		public void run() {
            Message msg = Message.obtain();
            mListenerHandler.sendEmptyMessage(TYPE_MSG_DELETE_EXPIRED);
		}
	}

	/*public static void main(String[] args) throws Exception {
		

		org.apache.log4j.BasicConfigurator.configure();
		logger.setLevel(org.apache.log4j.Level.DEBUG);
		
		BeaconRecvEthernet recv = BeaconRecvEthernet.loopPacketFromDevice(args[0]);

		
		CMOManagement m = new CMOManagement();
		
		m.addListener(new CMOTableListener() {
			@Override
			public void tableChanged(CMOTableEntry cmo) {
				System.out.println("Change : " + cmo);
			}
			
			public void tableCMORemoved(CMOTableEntry cmo) {
				System.out.println("Remove : " + cmo);
			}
			
			public void tableCMOAdded(CMOTableEntry cmo) {
				System.out.println("Add : " + cmo);
			}		
		});		
		
		recv.addListener(m);
		recv.start();
		recv.join();
	}

*/

}
