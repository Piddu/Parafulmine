package parafulmine;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GoldintoDB extends PFintoDB{
	
	//Attributes
	private int Id;
	private int BlockX;
	private int  BlockY;
	private int BlockZ;
	private String Owner;
	
	//DB stuff
	String tableName = "PFGoldBlocks";
	
	public GoldintoDB(){
		
	}
	
	public GoldintoDB(int blockX, int blockY, int blockZ){
		this.Id = 0;
		this.BlockX = blockX;
		this.BlockY = blockY;
		this.BlockZ = blockZ;
		this.Owner  =  "";
	}

	//INSERT Query, insert a PFObj into DB as a Record
	public void toRecord(GoldintoDB gobj){
		String insertPF = "INSERT INTO "+  tableName + 
		                  " (BlockX, BlockY, BlockZ, Owner) "+
		 				  "VALUES (" + String.valueOf(gobj.BlockX)+ "," +
		 				  		  String.valueOf(gobj.BlockY) + "," +
		 				  		  String.valueOf(gobj.BlockZ)+ "," +
		 				  		  "'" + gobj.Owner.toString() + "'" + ")"+ ";";
		ParaFulmine.getManager().insertQuery(insertPF);
	}
	
	//DELETE Query, delete a Record from DB by giving coordinates
	public void deleteRecord(GoldintoDB gobj){
		String deletePF = "DELETE FROM " + tableName  + " WHERE " + 
		  				  "BlockX" + " = " + "'" + String.valueOf(gobj.BlockX) + "'" + "AND " +
		  				  "BlockY" + " = " + "'" + String.valueOf(gobj.BlockY) + "'" +  "AND " +
		  				  "BlockZ" + " = " + "'" + String.valueOf(gobj.BlockZ)+  "'" + ";" ;
		ParaFulmine.getManager().deleteQuery(deletePF); 
	}
	
	//These methods are  SELECT QUERIES to get info by passing blocks coordinates.
	//SELECT Query, SELECT the Record with the given coordinates, return its Id
	public int getRecordId(int blockX, int blockY, int blockZ){
		String selectID = "SELECT B_Id FROM " + tableName  + " WHERE " + 
		  				  "BlockX" + " = " + "'" + String.valueOf(blockX) + "'" + "AND " +
		  				  "BlockY" + " = " + "'" + String.valueOf(blockY) + "'" +  "AND " +
		  				  "BlockZ" + " = " + "'" + String.valueOf(blockZ)+  "'" +";" ;
          ResultSet rs = ParaFulmine.getManager().sqlQuery(selectID);
          int Id = 0;
          try {
			Id = rs.getInt("B_Id");
          } catch (SQLException e) {
			e.printStackTrace();
          }
          return Id;  
	}
	//SELECT Query, SELECT the Record with the given id, return its Owner
	public String getRecordOwner(int id){
		String selectID = "SELECT Owner FROM " + tableName  + " WHERE " + 
			              "B_Id " + " = " + "'" + String.valueOf(id) + "'"  + ";" ;
        ResultSet rs = ParaFulmine.getManager().sqlQuery(selectID);
        String Owner ="";
		try {
			Owner = rs.getString("Owner");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return Owner;
	}
	
	public boolean existsXYZ(int blockX, int blockY, int blockZ){
		String selectcountID = "SELECT COUNT(B_Id) AS result FROM " + tableName +" WHERE " +
		  					   "BlockX" + " = " + "'" + String.valueOf(blockX) + "'" + " AND " +
		  					   "BlockY" + " = " + "'" + String.valueOf(blockY) + "'" +  " AND " +
		  					   "BlockZ" + " = " + "'" + String.valueOf(blockZ)+  "'" +";" ;
        ResultSet rs = ParaFulmine.getManager().sqlQuery(selectcountID);
        int count = 0;
		try {
			count = rs.getInt("result");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        if(count == 0){
        	return false;
        }
        else{
        	return true;
        }
	}
	
	public void setObjOwner(String owner){
		this.Owner = owner;
	}
}
