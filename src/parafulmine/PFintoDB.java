package parafulmine;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.alta189.sqlLibrary.SQLite.*;


public class PFintoDB {
	
	private int Id;
	private int BlockX;
	private int  BlockY;
	private int BlockZ;
	private String Owner;
	private int Link;
	private int Turret;
	private int Use;
	
	//Logger
    Logger log = Logger.getLogger("Minecraft");
	

	//DB Directory Stuff
	public String mainDirectory = "plugins" + File.separator + "ParaFulmine"; 
	public File dir = new File(mainDirectory);
	public String prefix = "[ParaFulmine]";
	public String dbName = "parafulmineDB";
	public sqlCore dbManage = new sqlCore(this.log, this.prefix, this.dbName, this.mainDirectory );
	String tableName = "PFBlocks";
	
	//Construct an PFintoDB obj, to be mapped into PFBlocks Table...
	
	public PFintoDB(){
	}
	
	public PFintoDB(int blockX, int blockY, int blockZ){
		this.Id =0; //Don't care at this now...
		this.BlockX = blockX;
		this.BlockY = blockY;
		this.BlockZ = blockZ;
		this.Owner = "";
		this.Link = 0;
		this.Turret = 0;
		this.Use = 0;
	}
	
	//Construct a PFIntoDB obj by retrieving info from PFBlocks Table...
	public PFintoDB(int id){
		this.Id = id;
		this.Link = this.getRecordLink(id);
		this.Turret = this.getRecordTurret(id);
		this.Owner = this.getRecordOwner(id);
		this.BlockX = this.getRecordBlockX(id);
		this.BlockY = this.getRecordBlockY(id);
		this.BlockZ = this.getRecordBlockZ(id);
		this.Use = this.getRecordUse(id);
	}
	
	//METHODS TO RETRIVE INFO FROM DB Table PFBlocks (QUERIES)...
	//INSERT Query, insert a PFObj into DB as a Record
	public void toRecord(PFintoDB pfobj){
		String insertPF = "INSERT INTO "+  tableName + 
		                  " (BlockX, BlockY, BlockZ, Owner) "+
		 				  "VALUES (" + String.valueOf(pfobj.BlockX)+ "," +
		 				  		  String.valueOf(pfobj.BlockY) + "," +
		 				  		  String.valueOf(pfobj.BlockZ)+ "," +
		 				  		  "'" + pfobj.Owner.toString() + "'" + ")"+ ";";
		dbManage.initialize();
		dbManage.insertQuery(insertPF);
		dbManage.close();
	}
	
	//DELETE Query, delete a Record from DB by giving coordinates
	public void deleteRecord(PFintoDB pfobj){
		String deletePF = "DELETE FROM " + tableName  + " WHERE " + 
		  				  "BlockX" + " = " + "'" + String.valueOf(pfobj.BlockX) + "'" + "AND " +
		  				  "BlockY" + " = " + "'" + String.valueOf(pfobj.BlockY) + "'" +  "AND " +
		  				  "BlockZ" + " = " + "'" + String.valueOf(pfobj.BlockZ)+  "'" + ";" ;
		dbManage.initialize();
		dbManage.deleteQuery(deletePF); 
		dbManage.close();
	}
	
	//These methods are  SELECT QUERIES to get info by passing blocks coordinates.
	//SELECT Query, SELECT the Record with the given coordinates, return its Id
	public int getRecordId(int blockX, int blockY, int blockZ){
		String selectID = "SELECT B_Id FROM " + tableName  + " WHERE " + 
		  				  "BlockX" + " = " + "'" + String.valueOf(blockX) + "'" + "AND " +
		  				  "BlockY" + " = " + "'" + String.valueOf(blockY) + "'" +  "AND " +
		  				  "BlockZ" + " = " + "'" + String.valueOf(blockZ)+  "'" +";" ;
		  dbManage.initialize();
          ResultSet rs = dbManage.sqlQuery(selectID);
          int Id = 0;
          try {
			Id = rs.getInt("B_Id");
          } catch (SQLException e) {
			e.printStackTrace();
          }
          dbManage.close();
          return Id;  
	}
	
	//SELECT Query, SELECT the Record with the given coordinates, return its Owner 
	public String getRecordOwner(int blockX, int blockY, int blockZ){
		String selectID = "SELECT Owner FROM " + tableName  + " WHERE " + 
		  				  "BlockX" + " = " + "'" + String.valueOf(blockX) + "'" + "AND " +
		  				  "BlockY" + " = " + "'" + String.valueOf(blockY) + "'" +  "AND " +
		  				  "BlockZ" + " = " + "'" + String.valueOf(blockZ)+  "'" +";" ;
		dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectID);
        String Owner ="";
		try {
			Owner = rs.getString("Owner");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        dbManage.close();
        return Owner;
	}
	
	//SELECT Query, SELECT the Record with the given coordinates, return its Link
	public int getRecordLinkXYZ(int blockX, int blockY, int blockZ){
		String selectID = "SELECT Link FROM " + tableName  + " WHERE " + 
		  				  "BlockX" + " = " + "'" + String.valueOf(blockX) + "'" + "AND " +
		                  "BlockY" + " = " + "'" + String.valueOf(blockY) + "'" +  "AND " +
		                  "BlockZ" + " = " + "'" + String.valueOf(blockZ)+  "'" +";" ;
        dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectID);
        int Link =0;
		try {
			Link = rs.getInt("Link");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        dbManage.close();
        return Link;
	}
	
	//These methods are SELECT Queries to get info by Id
	
	//SELECT Query, SELECT the Record with the given id, return its Link
	public int getRecordLink(int id2){
		String selectID = "SELECT Link FROM " + tableName  + " WHERE " + 
						  "B_Id " + " = " + "'" + String.valueOf(id2) + "'" + ";" ;
        dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectID);
        int Link =0;
		try {
			Link = rs.getInt("Link");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        dbManage.close();
        return Link;
	}
	//SELECT Query, SELECT the Record with the given id, return its Owner
	public String getRecordOwner(int id){
		String selectID = "SELECT Owner FROM " + tableName  + " WHERE " + 
			              "B_Id " + " = " + "'" + String.valueOf(id) + "'"  + ";" ;
		dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectID);
        String Owner ="";
		try {
			Owner = rs.getString("Owner");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        dbManage.close();
        return Owner;
	}
	
	//SELECT Query, SELECT the Record with the given id, return its Turret
	public int getRecordTurret(int id2){
		String selectID = "SELECT Turret FROM " + tableName  + " WHERE " + 
						  "B_Id " + " = " + "'" + String.valueOf(id2) + "'" + ";" ;
        dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectID);
        int Turret =0;
		try {
			Turret = rs.getInt("Turret");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        dbManage.close();
        return Turret;
	}
	
	//SELECT Query, SELECT the Record with the given id, return its Turret
	public int getRecordUse(int id2){
		String selectID = "SELECT Use FROM " + tableName  + " WHERE " + 
						  "B_Id " + " = " + "'" + String.valueOf(id2) + "'" + ";" ;
        dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectID);
        int use =0;
		try {
			use = rs.getInt("Use");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        dbManage.close();
        return use;
	}
	
	//SELECT Query, SELECT the Record with the given id, return its BlockX
	public int getRecordBlockX(int id){
		String selectID = "SELECT BlockX FROM " + tableName  + " WHERE " + 
						  "B_Id " + " = " + "'" + String.valueOf(id) + "'" + ";" ;
        dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectID);
        int BlockX =0;
		try {
			BlockX = rs.getInt("BlockX");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        dbManage.close();
        return BlockX;
	}
	//SELECT Query, SELECT the Record with the given id, return its BlockY
	public int getRecordBlockY(int id){
		String selectID = "SELECT BlockY FROM " + tableName  + " WHERE " + 
						  "B_Id " + " = " + "'" + String.valueOf(id) + "'" + ";" ;
        dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectID);
        int BlockY =0;
		try {
			BlockY = rs.getInt("BlockY");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        dbManage.close();
        return BlockY;
	}
	
	//SELECT Query, SELECT the Record with the given id, return its BlockZ
	public int getRecordBlockZ(int id){
		String selectID = "SELECT BlockZ FROM " + tableName  + " WHERE " + 
						  "B_Id " + " = " + "'" + String.valueOf(id) + "'" + ";" ;
        dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectID);
        int BlockZ =0;
		try {
			BlockZ = rs.getInt("BlockZ");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        dbManage.close();
        return BlockZ;
	}
	
	//UPDATE QUERIES
	//UPDATE Link Query
	public void updateRecordLink(int idst, int idnd){
		String updateLink = "UPDATE PFBlocks SET Link " + " = " +
							"'" + String.valueOf(idnd) + "'" +
							"WHERE B_Id" +
		                    " = " + "'" + String.valueOf(idst) + "'" + ";";
		dbManage.initialize();
		dbManage.updateQuery(updateLink);
		dbManage.close();
	}
	
	//UPDATE Turret Query
	public void updateRecordTurret(int id){
		String updateLink = "UPDATE PFBlocks SET Turret " + " = " +
							"'" + String.valueOf(1) + "'" +
							"WHERE B_Id" +
		                    " = " + "'" + String.valueOf(id) + "'" + ";";
		dbManage.initialize();
		dbManage.updateQuery(updateLink);
		dbManage.close();
	}
	//UPDATE Use Query
	public void updateRecordUse(int id, int onoff){
		String updateLink = "UPDATE PFBlocks SET Use " + " = " +
							"'" + String.valueOf(onoff) + "'" +
							"WHERE B_Id" +
		                    " = " + "'" + String.valueOf(id) + "'" + ";";
		dbManage.initialize();
		dbManage.updateQuery(updateLink);
		dbManage.close();
	}
	
	//UPDATE Links Query, set Link value to 0
	public void updateRecordsLinks(int idst){
		String updateLink = "UPDATE PFBlocks SET Link " + " = " +
							"'" + 0 + "'" +
							"WHERE Link" +
		                    " = " + "'" + String.valueOf(idst) + "'" + ";";
		dbManage.initialize();
		dbManage.updateQuery(updateLink);
		dbManage.close();
	}
	
	//SELECT COUNT QUERY
	public int exists(int id){
		String selectcountID = "SELECT COUNT(B_Id) AS result FROM PFBlocks WHERE B_Id = " + "'" + id + "'" + ";"; 
        dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectcountID);
        int count = 1;
		try {
			count = rs.getInt("result");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        dbManage.close();
        return count;
	}
	
	public boolean existsXYZ(int blockX, int blockY, int blockZ){
		String selectcountID = "SELECT COUNT(B_Id) AS result FROM " + tableName +" WHERE " +
		  					   "BlockX" + " = " + "'" + String.valueOf(blockX) + "'" + " AND " +
		  					   "BlockY" + " = " + "'" + String.valueOf(blockY) + "'" +  " AND " +
		  					   "BlockZ" + " = " + "'" + String.valueOf(blockZ)+  "'" +";" ;
        dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectcountID);
        int count = 0;
		try {
			count = rs.getInt("result");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        dbManage.close();
        if(count == 0){
        	return false;
        }
        else{
        	return true;
        }
	}
	//SELECT QUERY used to check if a player is alredy using a turret return is id
	public int using(String playername){
		String selectcount = "SELECT B_Id FROM PFBlocks WHERE Owner = " + "'" + playername + "'" +
							 " AND Use = " + "'" + String.valueOf(1) + "'" +";";
		dbManage.initialize();
        ResultSet rs = dbManage.sqlQuery(selectcount);
        int id = 0;
		try {
			id = rs.getInt("B_Id");
		} catch (SQLException e) {
			//e.printStackTrace();
		}
        dbManage.close();
        return id;
	}
	
	//Checks if the player is the owner of the PF
	public boolean isOwner(String owner, String playername){
		if(owner.equals(playername)){
			return true;
		}
		return false;
	}
	
	
	//Methods for the object	
	public void setObjId(int id){
		this.Id = id;
	}
	
	public void setObjOwner(String owner){
		this.Owner = owner;
	}
	public void setObjLink(int link){
		this.Link = link;
	}
	public void setObjTurret(int turret){
		this.Turret = turret;
	}
	
	public void setObjUse(int use){
		this.Use = use;
	}
	
	public int getObjId(){
		return this.Id;
	}
	
	public String getObjOwner(){
		return this.Owner;
	}
	
	public int getObjLink(){
		return this.Link;
	}
	
	public int getObjTurret(){
		return this.Turret;
	}
	
	public int getObjUse(){
		return this.Use;
	}
	
	public int getObjBlockX(){
		return this.BlockX;
	}
	
	public int getObjBlockY(){
		return this.BlockY;
	}
	
	public int getObjBlockZ(){
		return this.BlockZ;
	}

}
