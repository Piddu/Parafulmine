package parafulmine;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

//
public class PFCommandManager implements CommandExecutor, Listener{

    public static ParaFulmine plugin;
    public static Server server;
    
    public PFCommandManager(ParaFulmine instance) {
            plugin = instance;
    }
    
    public String pflinkcommand = "pflink";
    public String pfturretcommand = "pfturret";
    public String pfturretuse ="pfturretuse";
    Logger log = Logger.getLogger("Minecraft");//Define your logger

    
    //PFLINK COMMAND CHECKER
    public void pfLinkCommand(Command cmd, Player player, String[] args){
    	//Checks if Arguments are given
		if(args.length == 0){
			player.sendMessage(ChatColor.RED + "No blocks Ids given!");
			player.sendMessage(ChatColor.RED + cmd.getUsage());
			return;
		}
		
		if(args.length > 2 ){
			player.sendMessage(ChatColor.RED + "Too many parameters given!");
			player.sendMessage(ChatColor.RED + cmd.getUsage());
			return;
		}
		
		if(args.length < 2){
			player.sendMessage(ChatColor.RED + "Too few parameters given!");
			player.sendMessage(ChatColor.RED + cmd.getUsage());
			return;
		}
		
        //Check if first arg is valid 
		try{
			Integer.parseInt(args[0]);
		}catch(NumberFormatException ex){
			player.sendMessage(ChatColor.RED + "First Argument is invalid!");
			player.sendMessage(ChatColor.RED + "Use an Id number");
			return;
		}
		
		//Check if second arg is valid
		try{
			Integer.parseInt(args[1]);
		}catch(NumberFormatException ex){
			player.sendMessage(ChatColor.RED + "Second Argument is invalid!");
			player.sendMessage(ChatColor.RED + "Use an Id number");
			return;
		}
		
		//Check if 2 args ar given
		if(args.length == 1){
			player.sendMessage(ChatColor.RED + "Too few Arguments given!");
			player.sendMessage(ChatColor.RED + cmd.getUsage());
			return;
		}
	
		//Check if First PF with given id exist
		int firstid = Integer.parseInt(args[0]);
		PFintoDB check = new PFintoDB();
		int i = check.exists(firstid);
		if( i == 0 ){
			player.sendMessage(ChatColor.RED + "First PF doesn't Exists!");
			return;
		}
		//Creates an obj for first block
		PFintoDB firstobj = new PFintoDB(firstid);
		
		//Check if Second PF with given id exist
		int secondid = Integer.parseInt(args[1]);
		i = check.exists(secondid);
		if( i == 0 ){
			player.sendMessage(ChatColor.RED + "Second PF doesn't Exists!");
			return;
		}
		//check if the same id is given
		if(firstid == secondid){
			player.sendMessage(ChatColor.RED + "Almost right except you are trying to link the same block");
			player.sendMessage(ChatColor.RED + "Piddu is so disappointed about you =( ");
			return;
		}
		
		//Creates an obj for second block
		PFintoDB secondobj = new PFintoDB(secondid);
		
		//Check Player using the command
		//Check player ownership
		String playername = player.getName();
		if(firstobj.isOwner(firstobj.getObjOwner(), playername) == false){
			player.sendMessage(ChatColor.RED + "You Are not the Owner of the First Block!");
			return;
		}
		if(secondobj.isOwner(secondobj.getObjOwner(), playername) == false){
			player.sendMessage(ChatColor.RED + "You Are not the Owner of the Second Block!");
			player.sendMessage(ChatColor.RED + "You Thief!:V");
			return;
		}
		
		//Check if the first PF is Linkable-> Link attribute = 0
		if(firstobj.getObjLink() != 0){
			player.sendMessage(ChatColor.RED + "The first Block must have no Links!");
			return;
		}
		
		//Check if the both the PF are not Turrets
		if(firstobj.getObjTurret() !=0 || secondobj.getObjTurret ()!= 0){
			player.sendMessage(ChatColor.RED + "One of them is a Turret!");
			return;
		}
		
		//Check PF placements
		//Check if the blocks are on the same Y
		if(firstobj.getObjBlockY() != secondobj.getObjBlockY()){
			player.sendMessage(ChatColor.RED + "Blocks are on different heights!");
			
			return;
		}
		
		//Check if the blocks are on the same X or same Z
		if((firstobj.getObjBlockX() == secondobj.getObjBlockX()) ||(firstobj.getObjBlockZ() == secondobj.getObjBlockZ())){
			player.sendMessage(ChatColor.GREEN + "Block # " + String.valueOf(firstobj.getObjId()) + " Linked to: # " + String.valueOf(secondobj.getObjId()));
			firstobj.updateRecordLink(firstobj.getObjId(), secondobj.getObjId()); //FINALLY!!!
			return;
		}
		
		//Check if the blocks are aligned on X axe
		if((firstobj.getObjBlockX() != secondobj.getObjBlockX()) ||(firstobj.getObjBlockZ() != secondobj.getObjBlockZ())){
			player.sendMessage(ChatColor.RED + "Blocks are not aligned on X or Z!");
			return;
		}
		return;
    }
    
    //PFTURRET COMMAND CHECKER
    void pfTurretCommand(Command cmd, Player player, String[] args){
    	//Checks if Arguments are given
		if(args.length == 0){
			player.sendMessage(ChatColor.RED + "No command args given!");
			player.sendMessage(ChatColor.RED + cmd.getUsage());
			return;
		}
		
		if(args.length > 1){
			player.sendMessage(ChatColor.RED + "Too many parameters given!");
			player.sendMessage(ChatColor.RED + cmd.getUsage());
			return;
		}
		
        //Check if first arg is valid 
		try{
			Integer.parseInt(args[0]);
		}catch(NumberFormatException ex){
			player.sendMessage(ChatColor.RED + "First Argument is invalid!");
			player.sendMessage(ChatColor.RED + "Use an Id number");
			return;
		}
		
		//From here Given arguments should be valid...
		//now check if the given PF can be a Turret...
		int id = Integer.parseInt(args[0]);
		
		PFintoDB check = new PFintoDB();
		int i = check.exists(id);
		if( i == 0 ){
			player.sendMessage(ChatColor.RED + "This PF doesn't Exists!");
			return;
		}
		
		PFintoDB pfobj = new PFintoDB(id);
		
		//check if the player is the owner of the block
		if(!player.getName().equals(pfobj.getObjOwner())){
			player.sendMessage(ChatColor.RED + "This PF doesn't belongs to you!");
			return;
		}
		
		//Check if it's linked (wall part)
		if(pfobj.getObjLink()!=0){
			player.sendMessage(ChatColor.RED + "This PF is linked to an other, you can't use it as Turret!");
			return;
		}
		
		//Check if it's already a turret
		if(pfobj.getObjTurret()!=0){
			player.sendMessage(ChatColor.RED + "This PF is already a turret!");
			return;
		}
		
		//From here the given PF should be valid...
		pfobj.updateRecordTurret(id);
		player.sendMessage(ChatColor.GREEN + "PF set to Turret MODE!");
		return;
    }
    
    //Turret Use Command checker...
    public void pfTurretUse(Command cmd, Player player, String[] args){
    	if(args.length == 0){
			player.sendMessage(ChatColor.RED + "No command args given!");
			player.sendMessage(ChatColor.RED + cmd.getUsage());
			return;
		}
		if(args.length > 1){
			player.sendMessage(ChatColor.RED + "Too many parameters given!");
			player.sendMessage(ChatColor.RED + cmd.getUsage());
			return;
		}
		//Check if first arg is valid 
		try{
			Integer.parseInt(args[0]);
		}catch(NumberFormatException ex){
			player.sendMessage(ChatColor.RED + "First Argument is invalid!");
			player.sendMessage(ChatColor.RED + "Use an Id number");
			return;
		}
		
		int id = Integer.parseInt(args[0]);
		PFintoDB check = new PFintoDB();
		//Check if given PF exists.
		int i = check.exists(id);
		if( i == 0 ){
			player.sendMessage(ChatColor.RED + "This PF doesn't Exists!");
			return;
		}
		
		//check if given id is a Turret
		int t = check.getRecordTurret(id);
		if( t == 0){
			player.sendMessage(ChatColor.RED + "This PF is not a  Turret!");
			return;
		}
		
		//check if the player has already a Turret activated and turn it off
		int j = check.using(player.getName());// j is id of the record...
		if( j != 0 ){
			player.sendMessage(ChatColor.RED + "Turret #" + String.valueOf(j) + "Deactivated!");
			check.updateRecordUse(j , 0);
		}
		
		//From here given value is currect
		PFintoDB pfobj = new PFintoDB(id);
		pfobj.updateRecordUse(id, 1);
		player.sendMessage(ChatColor.GREEN + "Turret #" + String.valueOf(id) + "Activated!");
		return;
    }
    
    public boolean onCommand(CommandSender sender, Command com, String label, String[] args){
    	if(label.equals(pflinkcommand)){
    		Player player = (Player)sender;
    		if (ParaFulmine.permissionHandler.has(player, "Parafulmine.Link")  == true){
    			pfLinkCommand(com, player, args);
    			return false;
    		}
    		else{
    			player.sendMessage(ChatColor.RED + "You don't have permission for this command");
    			return false;
    		}
    	}
    	if(label.equals(pfturretcommand)){
    		Player player = (Player)sender;
    		if (ParaFulmine.permissionHandler.has(player, "Parafulmine.Turret")  == true){
    		pfTurretCommand(com, player, args);
    		return false;
    		}
    		else{
    			player.sendMessage(ChatColor.RED + "You don't have permission for this command");
    			return false;
    		}
    	}
    	if(label.equals(pfturretuse)){
    		Player player = (Player)sender;
    		if (ParaFulmine.permissionHandler.has(player, "Parafulmine.Turret")  == true){
    			pfTurretUse(com, player, args);
    			return false;
    		}
    		else{
    			player.sendMessage(ChatColor.RED + "You don't have permission for this command");
    			return false;
    		}	
    	}
    	return false;
    }
}
