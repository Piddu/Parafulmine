package parafulmine;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

import com.alta189.sqlLibrary.SQLite.*;


public class PFPlayerListener extends PlayerListener{
	
	//DB Directory Stuff
	public String mainDirectory = "plugins" + File.separator + "ParaFulmine"; 
	public File dir = new File(mainDirectory);
	public String prefix = "[ParaFulmine]";
	public String dbName = "parafulmineDB";
	public sqlCore dbManage = new sqlCore(this.log, this.prefix, this.dbName, this.mainDirectory );
	public String PFTableName = "PFBlocks";
	public String GoldTableName = "PFGoldBlocks";
	
	//PF Blocks Stuff
	public int base = 49; //OBS id
	public int topiron = 42;  //iron id
	public int topgold = 41;  //gold id
	
	//PF Command Prefixes
	public String pflink = "pflink";
	
    public static ParaFulmine plugin;
    
    public PFPlayerListener(ParaFulmine instance) {
            plugin = instance;
    }
    
    Logger log = Logger.getLogger("Minecraft");//Define your logger
    
    public boolean isOwner(PlayerInteractEvent event){
    	return true;
    }
    
    public boolean isParaFulmine(Block clicked){
    	World world = clicked.getWorld();
		Block tblock = world.getBlockAt(clicked.getX(), clicked.getY()+1, clicked.getZ());
		if(tblock.getTypeId() == topiron){
			return true;
		}
    	return false;
    }
    
    public boolean isGold(Block clicked){
    	World world = clicked.getWorld();
		Block tblock = world.getBlockAt(clicked.getX(), clicked.getY()+1, clicked.getZ());
		if(tblock.getTypeId() == topgold){
			return true;
		}
    	return false;
    }
    public boolean isBase(Block clicked){
    	World world = clicked.getWorld();
		Block tblock = world.getBlockAt(clicked.getX(), clicked.getY()-1, clicked.getZ());
		if(tblock.getTypeId() == base){
			return true;
		}
    	return false;
    }
    
    //Get info from Record on PFBlocks Table, send Chat Msg to Player
    public void onScreenPFInfo(Block clicked, Player player){
    	int x = clicked.getX();
		int y = clicked.getY();
		int z = clicked.getZ();
		PFintoDB pfobj = new PFintoDB(x, y, z);
		int Id = pfobj.getRecordId(x, y, z);
		String Owner = pfobj.getRecordOwner(x, y, z);
		int Link = pfobj.getRecordLinkXYZ(x, y, z);
		int Turret = pfobj.getRecordTurret(Id);
		//Set obj attributes
		pfobj.setObjId(Id);
		pfobj.setObjOwner(Owner);
		pfobj.setObjLink(Link);
		pfobj.setObjTurret(Turret);
		String infomsg = "ParaFulmine " + " #" + String.valueOf(Id) + " @ " + 
						  " X:" + String.valueOf(x) + 
						  " Y:" + String.valueOf(y) +
						  " Z:" + String.valueOf(z) +
						  " | Owned By: "   +  Owner +
						  " - Linked To: "  +  String.valueOf(Link)  +
						  " - Is Turret = "  +  String.valueOf(Turret) +
						  " - 0 NO - 1 YES";			     
		player.sendMessage(ChatColor.GRAY + infomsg );
    }
    
    public void onScreenGoldInfo(Block clicked, Player player){
    	int x = clicked.getX();
		int y = clicked.getY();
		int z = clicked.getZ();
		GoldintoDB gobj = new GoldintoDB(x, y, z);
		int Id = gobj.getRecordId(x, y, z);
		String Owner = gobj.getRecordOwner(Id);
		//Set obj attributes
		gobj.setObjId(Id);
		gobj.setObjOwner(Owner);
		String infomsg = "GoldenPF " + " #" + String.valueOf(Id) + " @ " + 
						  " X:" + String.valueOf(x) + 
						  " Y:" + String.valueOf(y) +
						  " Z:" + String.valueOf(z) +
						  " | Owned By: "   +  Owner;
		player.sendMessage(ChatColor.YELLOW + infomsg );
    }
    
    public void zapTheTarget(Block target, Player player , int turretid){
    	PFintoDB turret = new PFintoDB(turretid);
    	Location playerloc = player.getLocation();
    	World world = target.getWorld();
    	Integer x = new Integer(turret.getObjBlockX());
    	Integer y = new Integer(turret.getObjBlockY());
    	Integer z = new Integer(turret.getObjBlockZ());
    	Location turretloc = new Location (world , x.doubleValue(), y.doubleValue(),z.doubleValue());
    	Vector playervect = playerloc.toVector();
    	Vector turretvect = turretloc.toVector();
    	double distance = 0;
    	distance = playervect.distanceSquared(turretvect);
    	if(distance > 64){
    		player.sendMessage(ChatColor.DARK_PURPLE + " You are too far from your turret!");
    		return;
    	}
    	world.strikeLightning(target.getLocation());
    	return;
    }
    
    public void onPlayerInteract(PlayerInteractEvent event){
    	Action act = event.getAction();
    	//PF or GoldenPF  info 
    	if(act.toString() == "LEFT_CLICK_BLOCK"){
        	Block clicked = event.getClickedBlock();
        	//Info 
        	if(clicked.getTypeId()== base){
        		if (isParaFulmine(clicked) == true ){
        			PFintoDB check = new PFintoDB();
    				if(check.existsXYZ(clicked.getX(), clicked.getY(), clicked.getZ()) == true){
    					//Get info from Record on PFBlocks Table, send Chat Msg to Player
    					Player player = event.getPlayer();
            			onScreenPFInfo(clicked, player);
            			return;
    				}
        		}
        		if (isGold(clicked) == true ){
        			GoldintoDB check = new GoldintoDB();
    				if(check.existsXYZ(clicked.getX(), clicked.getY(), clicked.getZ()) == true){
    					//Get info from Record on PFGoldBlocks Table, send Chat Msg to Player
    					Player player = event.getPlayer();
    					onScreenGoldInfo(clicked, player);
    					return;
    				}
        			return;
        		}
        	}
        	//Setup IronPF
        	if(clicked.getTypeId()== topiron){
        		//Check if is on top of obsidian
        		if (isBase(clicked) == true ){
        			PFintoDB check = new PFintoDB();
        			if(check.existsXYZ(clicked.getX(), clicked.getY()-1, clicked.getZ()) == false){
	        			//permission check e insert into db
	        			if (ParaFulmine.permissionHandler.has(event.getPlayer(), "Parafulmine.Iron") == true){
	        				PFintoDB pfobj = new PFintoDB(clicked.getX(), clicked.getY()-1, clicked.getZ());
	        				pfobj.setObjOwner(event.getPlayer().getName());
	        				pfobj.toRecord(pfobj);//Query
	        				event.getPlayer().sendMessage(ChatColor.GRAY + "Iron Parafulmine is SetUp!");
	        				return;
	        			}
	        			else{
	        				event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to use this!");
	        				return;
	        			}
        			}
        			return;
        		}
        		return;
        	}
        	//Setup GoldenPF
        	if(clicked.getTypeId()== topgold){
        		//Check if is on top of obsidian
        		if (isBase(clicked) == true ){
        			GoldintoDB check = new GoldintoDB();
        			if(check.existsXYZ(clicked.getX(), clicked.getY()-1, clicked.getZ()) == false){
	        			//permission check e insert into db
	        			if (ParaFulmine.permissionHandler.has(event.getPlayer(), "Parafulmine.Gold")  == true){
	        				GoldintoDB gobj = new GoldintoDB(clicked.getX(), clicked.getY()-1, clicked.getZ());
	        				gobj.setObjOwner(event.getPlayer().getName());
	        				gobj.toRecord(gobj);//Query
	        				event.getPlayer().sendMessage(ChatColor.YELLOW + "Golden Parafulmine is SetUp!");
	        				return;
	        			}
	        			else{
	        				event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to use this!");
	        				return;
	        			}
        			}
        			return;
        		}
        		return;
        	}
    	}
        //Remote controller action	
    	if(act.toString() == "LEFT_CLICK_BLOCK" || act.toString() == "LEFT_CLICK_AIR"){
    		//check if player is holding a button in his hand
    		if(event.getPlayer().getItemInHand().getTypeId() == 77){
    			//check if player owns an activated turret
    			int id = 0;
    			PFintoDB check = new PFintoDB();
    			id = check.using(event.getPlayer().getName());
    			//if an Activated turret is found
    			if(id != 0){
    				Player player = event.getPlayer();
    				Block target = player.getTargetBlock(null , 100);
    				zapTheTarget(target, player, id);
    				return;
    			}
    			
    		}
    	}
    	return;
    }
    		

}
