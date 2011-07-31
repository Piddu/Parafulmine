package parafulmine;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.alta189.sqlLibrary.SQLite.*;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

public class ParaFulmine  extends JavaPlugin {
	
	static Logger log = Logger.getLogger("Minecraft");//Define your logger
	//DB Directory Stuff
	public static String mainDirectory = "plugins" + File.separator + "ParaFulmine"; 
	public static File dir = new File(mainDirectory);
	public static String prefix = "[ParaFulmine]";
	public static String dbName = "parafulmineDB";
	private static sqlCore dbManage = new sqlCore(log, prefix, dbName, mainDirectory );;
		
	//Create PFBlocks Create table Query
	public String DBTableQuery ="CREATE TABLE PFBlocks" +
			"(" +
			"B_Id INTEGER PRIMARY KEY, " +
			"BlockX int NOT NULL, " +
			"BlockY int NOT NULL, " +
			"BlockZ int NOT NULL, " +
			"Owner varchar(255) , " +
			"Link int DEFAULT '0'," +
			"Turret int DEFAULT '0',"+
			"Use int DEFAULT '0'" +
			")" + ";"; 

	//Create PFBlocks Create table Query
	public String DBTablePFG ="CREATE TABLE PFGoldBlocks" +
			"(" +
			"B_Id INTEGER PRIMARY KEY, " +
			"BlockX int NOT NULL, " +
			"BlockY int NOT NULL, " +
			"BlockZ int NOT NULL, " +
			"Owner varchar(255)   " +
			")" + ";"; 

    //Listeners
    private final PFPlayerListener playerListener = new PFPlayerListener(this);
    private final PFBlockListener blockListener = new PFBlockListener(this);
    
    //Permission handler
    public static PermissionHandler permissionHandler;
    
    //utils
    public static sqlCore getManager(){
    	return dbManage;
    }
           
    public void createPluginFolder(){
    	if(!this.dir.exists()){
    		log.info("Creating Parafulmine Plugin Folder");
    		dir.mkdir();
    	}
    }
    

    public void setupPermissions() {
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (this.permissionHandler == null) {
            if (permissionsPlugin != null) {
                this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
            } else {
                log.info("Permission system not detected, defaulting to OP");
            }
        }
    }
    
    public void onDisable() {
    	if(dbManage != null){
    		dbManage.close();
    	}
    	log.info("ParaFulmine Plugin DISABLED");
    }

    public void onEnable() {     
    PluginManager pm = this.getServer().getPluginManager();
    log.info("ParaFulmine Plugin ENABLED");
    createPluginFolder();
    dbManage.initialize();
    if(!dbManage.checkTable("PFBlocks")){
    	 dbManage.createTable(DBTableQuery);
    	 dbManage.close();
    	 dbManage.initialize();
    	 //Create first row (record) on the table
    	 PFintoDB pfobj = new PFintoDB(0,0,0);
    	 pfobj.toRecord(pfobj);
    	 dbManage.close();
    }
    dbManage.initialize();
    if(!dbManage.checkTable("PFGoldBlocks")){
   	 	 dbManage.createTable(DBTablePFG);
	   	 dbManage.close();
	   	 dbManage.initialize();
	   	 //Create first row (record) on the table
	   	 GoldintoDB gobj = new GoldintoDB(0,0,0);
	   	 gobj.toRecord(gobj);
	   	 dbManage.close();
   }
    //Permissions loading
    setupPermissions();
    //Plugin Commands
	getCommand("pflink").setExecutor(new PFCommandManager(this));
	getCommand("pfturret").setExecutor(new PFCommandManager(this));
	getCommand("pfturretuse").setExecutor(new PFCommandManager(this));
	PFCommandManager.server = this.getServer();
	
	//Event listener
    pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
    pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
    pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Event.Priority.Normal, this);
    }

}
