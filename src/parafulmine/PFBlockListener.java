package parafulmine;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;

public class PFBlockListener extends BlockListener{
	
    //Blocks IDs
	int base = 49;//id OBS
	int topiron = 42; //id IRON
	int topgold = 41; // id GOLD
	
	//Logger
    Logger log = Logger.getLogger("Minecraft");
	
	public static ParaFulmine plugin;
        
    public PFBlockListener(ParaFulmine instance) {
            plugin = instance;
    }
    
    public void wallOfZap(PFintoDB pfobj, World world){
    	PFintoDB linked = new PFintoDB(pfobj.getObjLink());
    	//check if obj are aligned on X
    	if(pfobj.getObjBlockX() == linked.getObjBlockX()){
    		int i = pfobj.getObjBlockZ();
    		int j = linked.getObjBlockZ();
    		if(i < j){
    			double Y = Double.parseDouble(String.valueOf(pfobj.getObjBlockY()));
    			double X = Double.parseDouble(String.valueOf(pfobj.getObjBlockX()));
    			for(; i<=j;i++){
    				double Z = Double.parseDouble(String.valueOf(i));
    				Location loc = new Location(world, X, Y, Z );
    				world.strikeLightning(loc);
    			}
    			return;
    		}
    		if( i > j){
    			double Y = Double.parseDouble(String.valueOf(pfobj.getObjBlockY()));
    			double X = Double.parseDouble(String.valueOf(pfobj.getObjBlockX()));
    			for(; i>=j; i--){
    				double Z = Double.parseDouble(String.valueOf(i));
    				Location loc = new Location(world, X, Y, Z );
    				world.strikeLightning(loc);
    			}
    			return;
    			
    		}
    		return;
    	}
    	//checks if aligned on Z
    	if(pfobj.getObjBlockZ() == linked.getObjBlockZ()){
    		int i = pfobj.getObjBlockX();
    		int j = linked.getObjBlockX();
    		if(i < j){
    			double Y = Double.parseDouble(String.valueOf(pfobj.getObjBlockY()));
    			double Z = Double.parseDouble(String.valueOf(pfobj.getObjBlockZ()));
    			for(; i<=j; i++){
    				double X = Double.parseDouble(String.valueOf(i));
    				Location loc = new Location(world, X, Y, Z );
    				world.strikeLightning(loc);
    			}
    			return;
    		}
    		if( i > j){
    			double Y = Double.parseDouble(String.valueOf(pfobj.getObjBlockY()));
    			double Z = Double.parseDouble(String.valueOf(pfobj.getObjBlockZ()));
    			for(; i>=j;i--){
    				double X = Double.parseDouble(String.valueOf(i));
    				Location loc = new Location(world, X, Y, Z );
    				world.strikeLightning(loc);
    			}
    			return;
    			
    		}
    		return;
    	}
    	
    }
    
    public void smelt(World world, Block block){
    	Location loc = block.getLocation();
    	if(block.getTypeId() == 4){
    		ItemStack drop = new ItemStack(1,1);
    		block.setTypeId(0);
    		world.dropItem(loc, drop);
    	}
    	if(block.getTypeId() == 12){
    		ItemStack drop = new ItemStack(20,1);
    		block.setTypeId(0);
    		world.dropItem(loc, drop);
    	}
    	if(block.getTypeId() == 13){
    		ItemStack drop = new ItemStack(318,1);
    		block.setTypeId(0);
    		world.dropItem(loc, drop);
    	}
    	if(block.getTypeId() == 14){
    		ItemStack drop = new ItemStack(266,1);
    		block.setTypeId(0);
    		world.dropItem(loc, drop);
    	}
    	if(block.getTypeId() == 15){
    		ItemStack drop = new ItemStack(265,1);
    		block.setTypeId(0);
    		world.dropItem(loc, drop);
    	}
    	if(block.getTypeId() == 17){
    		ItemStack drop = new ItemStack(263,1);
    		block.setTypeId(0);
    		world.dropItem(loc, drop);
    	}
    	return;
    }
    
    // zapTheIron checks if nearby blocks are obsidian blocks with iron block on top...
    public void zapTheIron(Block bblock , World world){
    	if(bblock.getTypeId() == base){
    		//log.info(" is obsidian");
    		Block tblock = world.getBlockAt(bblock.getX(), bblock.getY()+1, bblock.getZ());
    		if(tblock.getTypeId() == topiron){//If iron block on top , regular lightning with damage
    			world.strikeLightning(tblock.getLocation());
    			//check if block is linked
    			PFintoDB pfobj = new PFintoDB(bblock.getX(), bblock.getY(), bblock.getZ());
    			int link = pfobj.getRecordLinkXYZ(bblock.getX(), bblock.getY(), bblock.getZ());
    			if(link != 0){
    				//wall of lightning
    				pfobj.setObjLink(link);
    				int id = pfobj.getRecordId(bblock.getX(), bblock.getY(), bblock.getZ());
    				pfobj.setObjId(id);
    				wallOfZap(pfobj, world);
    			}
    			return;
    		}
    		else if (tblock.getTypeId() == topgold){//If gold block on top , only lightning effect no damage
				world.strikeLightningEffect(tblock.getLocation());
				Block toBeSmelt = world.getBlockAt(bblock.getX(), bblock.getY()+2, bblock.getZ());
				smelt(world, toBeSmelt);
				return;
    		}
    	}
    	return;
    }
    
    public void onBlockBreak(BlockBreakEvent event){
    	Block block = event.getBlock();
    	//If IronBlock is broken
    	if(block.getTypeId() == topiron){
    		//Check if it was a PF part
    		World world = block.getWorld();
    		Block bblock =  world.getBlockAt(block.getX(), block.getY()-1, block.getZ());
    		if(bblock.getTypeId() == base){
    			//Remove PF from DB
    			PFintoDB pfobj = new PFintoDB(block.getX(), block.getY()-1, block.getZ());
    			if(pfobj.existsXYZ(block.getX(), block.getY()-1, block.getZ())== true){
	    			int id = pfobj.getRecordId(block.getX(), block.getY()-1, block.getZ());
	    			pfobj.updateRecordsLinks(id);
	    			pfobj.deleteRecord(pfobj);
    			}
    		}
    		return;
    	}
    	
    	if(block.getTypeId() == topgold){
    		//Check if it was a PF part
    		World world = block.getWorld();
    		Block bblock =  world.getBlockAt(block.getX(), block.getY()-1, block.getZ());
    		if(bblock.getTypeId() == base){
    			//Remove PF from DB
    			GoldintoDB gobj = new GoldintoDB(block.getX(), block.getY()-1, block.getZ());
    			if(gobj.existsXYZ(block.getX(), block.getY()-1, block.getZ())== true){
    				gobj.deleteRecord(gobj);
    			}
    		}
    		return;
    	}
    	
    	//If a OBSBlock is broken
    	if(block.getTypeId() == base){
    		//Check if was a PF part
    		World world = block.getWorld();
    		Block tblock =  world.getBlockAt(block.getX(), block.getY()+1, block.getZ());
    		if(tblock.getTypeId() == topiron){
    			//Remove PF from DB
    			PFintoDB pfobj = new PFintoDB(block.getX(), block.getY(), block.getZ());
    			int id = pfobj.getRecordId(block.getX(), block.getY(), block.getZ());
    			pfobj.updateRecordsLinks(id);
    			pfobj.deleteRecord(pfobj);
    		}
    		if(tblock.getTypeId() == topgold){
    			//Remove PF from DB
    			GoldintoDB gobj = new GoldintoDB(block.getX(), block.getY(), block.getZ());
    			gobj.deleteRecord(gobj);
    		}
    	}
    	return;
    }
    
    public void onBlockRedstoneChange(BlockRedstoneEvent event){
    	Block block = event.getBlock();
    	World world = block.getWorld();
    	int x = block.getX();
    	int y = block.getY();
    	int z = block.getZ(); 
    	Block ntblock = world.getBlockAt(x , y , z+1);
    	Block stblock = world.getBlockAt(x , y , z-1);
    	Block etblock = world.getBlockAt(x+1 , y , z);
    	Block wtblock = world.getBlockAt(x-1 , y , z);
    	//log.info("redstone event");
    	zapTheIron(ntblock, world);
    	zapTheIron(stblock, world);
    	zapTheIron(etblock, world);
    	zapTheIron(wtblock, world);
    }
}
