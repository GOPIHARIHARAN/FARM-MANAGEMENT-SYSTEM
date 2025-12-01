package farm_app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FarmDBOperations {

	public void addCrop(String name, String season) throws Exception {

	    Connection con = DBConnection.getConnection();

	    // Check duplicate
	    PreparedStatement check = con.prepareStatement(
	        "select * from crops where lower(crop_name) = lower(?)"
	    );
	    check.setString(1, name);

	    ResultSet rs = check.executeQuery();

	    if(rs.next())
	    {
	        System.out.println("CROP ALREADY EXISTS");
	        con.close();
	        return;
	    }

	    PreparedStatement ps = con.prepareStatement(
	        "insert into crops(crop_name, season) values(?, ?)"
	    );

	    ps.setString(1, name);
	    ps.setString(2, season);

	    ps.executeUpdate();

	    System.out.println("CROP INSERTED");
	    con.close();
	}

    
    public void deleteCrop(int cropId) throws Exception {

        Connection con = DBConnection.getConnection();

        
        PreparedStatement ps1 = con.prepareStatement(
            "delete from yield_data where crop_id=?"
        );
        ps1.setInt(1, cropId);
        ps1.executeUpdate();

        
        PreparedStatement ps2 = con.prepareStatement(
            "delete from fertilizer where crop_id=?"
        );
        ps2.setInt(1, cropId);
        ps2.executeUpdate();

       
        PreparedStatement ps3 = con.prepareStatement(
            "delete from crops where crop_id=?"
        );
        ps3.setInt(1, cropId);

        int rows = ps3.executeUpdate();

        if(rows > 0)
        {
            System.out.println("CROP DELETED SUCCESSFULLY");
        }
        else
        {
            System.out.println("INVALID CROP ID");
        }

        con.close();
    }

    public void viewCrops() throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "select * from crops"
        );

        ResultSet rs = ps.executeQuery();

        boolean found = false;

        while(rs.next())
        {
            found = true;
            System.out.println(
                rs.getInt("crop_id") + " " +
                rs.getString("crop_name") + " " +
                rs.getString("season")
            );
        }

        if(!found)
        {
            System.out.println("NO CROPS AVAILABLE");
        }

        con.close();
    }

    public void addYield(int cropId, int year, double yield) throws Exception {

        Connection con = DBConnection.getConnection();

        // 1️ Check if that crop+year already exists
        PreparedStatement check = con.prepareStatement(
            "select yield_kg from yield_data where crop_id=? and year=?"
        );
        check.setInt(1, cropId);
        check.setInt(2, year);

        ResultSet rs = check.executeQuery();

        if (rs.next()) {
            // Year exists → update only the kg
            double updatedYield =  yield;

            PreparedStatement update = con.prepareStatement(
                "update yield_data set yield_kg=? where crop_id=? and year=?"
            );
            update.setDouble(1, updatedYield);
            update.setInt(2, cropId);
            update.setInt(3, year);

            update.executeUpdate();
            System.out.println("YIELD UPDATED (Added to existing value)");
        } 
        else {
            //  No record → insert new
            PreparedStatement insert = con.prepareStatement(
                "insert into yield_data(crop_id, year, yield_kg) values(?,?,?)"
            );
            insert.setInt(1, cropId);
            insert.setInt(2, year);
            insert.setDouble(3, yield);

            insert.executeUpdate();
            System.out.println("YIELD INSERTED");
        }

        con.close();
    }

    public boolean hasCrops() throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "select count(*) from crops"
        );

        ResultSet rs = ps.executeQuery();
        rs.next();

        int count = rs.getInt(1);

        con.close();
        return count > 0;
    }


    public void viewYield(int cropId) throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "select * from yield_data where crop_id=?"
        );

        ps.setInt(1, cropId);

        ResultSet rs = ps.executeQuery();

        boolean found = false;

        while(rs.next())
        {
            found = true;
            System.out.println(
                rs.getInt("year") + " " +
                rs.getDouble("yield_kg") + " kg"
            );
        }

        if(!found)
        {
            System.out.println("NO YIELD DATA AVAILABLE FOR THIS CROP");
        }

        con.close();
    }

    public void predictYield(int cropId) throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "select avg(yield_kg) as avg_yield from yield_data where crop_id=?"
        );

        ps.setInt(1, cropId);

        ResultSet rs = ps.executeQuery();

        if(rs.next())
        {
            double avg = rs.getDouble("avg_yield");

            if(rs.wasNull())
            {
                System.out.println("NO YIELD DATA AVAILABLE TO PREDICT");
            }
            else
            {
                System.out.println("PREDICTED YIELD : " + avg + " kg");
            }
        }

        con.close();
    }

    public void updateCropName(int cropId, String newName) throws Exception {

        Connection con = DBConnection.getConnection();

        // Check if cropId exists
        PreparedStatement checkId = con.prepareStatement(
            "select crop_id from crops where crop_id=?"
        );
        checkId.setInt(1, cropId);
        ResultSet rsId = checkId.executeQuery();

        if(!rsId.next()) {
            System.out.println("INVALID CROP ID");
            con.close();
            return;
        }

        // Check if new name already exists
        PreparedStatement checkName = con.prepareStatement(
            "select crop_id from crops where lower(crop_name)=lower(?)"
        );
        checkName.setString(1, newName);

        ResultSet rsName = checkName.executeQuery();

        if(rsName.next()) {
            System.out.println("CROP NAME ALREADY EXISTS");
            con.close();
            return;
        }

        // Update crop name
        PreparedStatement ps = con.prepareStatement(
            "update crops set crop_name=? where crop_id=?"
        );
        ps.setString(1, newName);
        ps.setInt(2, cropId);

        ps.executeUpdate();

        System.out.println("CROP NAME UPDATED SUCCESSFULLY");

        con.close();
    }


    public void addFertilizer(int cropId, String name, double qty) throws Exception {

        Connection con = DBConnection.getConnection();

        // 1️⃣ Check if fertilizer already exists for that crop
        PreparedStatement check = con.prepareStatement(
            "select quantity_kg from fertilizer where crop_id=? and lower(fertilizer_used)=lower(?)"
        );
        check.setInt(1, cropId);
        check.setString(2, name);

        ResultSet rs = check.executeQuery();

        if (rs.next()) {
            // Exists → update quantity
            double existingQty = rs.getDouble("quantity_kg");
            double updatedQty = existingQty + qty;

            PreparedStatement update = con.prepareStatement(
                "update fertilizer set quantity_kg=? where crop_id=? and lower(fertilizer_used)=lower(?)"
            );
            update.setDouble(1, updatedQty);
            update.setInt(2, cropId);
            update.setString(3, name);

            update.executeUpdate();
            System.out.println("FERTILIZER QUANTITY UPDATED");
        }
        else {
            // Insert new record
            PreparedStatement insert = con.prepareStatement(
                "insert into fertilizer(crop_id, fertilizer_used, quantity_kg) values(?,?,?)"
            );
            insert.setInt(1, cropId);
            insert.setString(2, name);
            insert.setDouble(3, qty);

            insert.executeUpdate();
            System.out.println("FERTILIZER ADDED SUCCESSFULLY");
        }

        con.close();
    }


    public void viewFertilizer(int cropId) throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "select * from fertilizer where crop_id=?"
        );

        ps.setInt(1, cropId);

        ResultSet rs = ps.executeQuery();

        boolean found = false;

        while(rs.next())
        {
            found = true;
            System.out.println(
                rs.getString("fertilizer_used") + " - " +
                rs.getDouble("quantity_kg") + " kg"
            );
        }

        if(!found)
        {
            System.out.println("NO FERTILIZER RECORDS FOUND");
        }

        con.close();
    }

    public void searchCropByName(String name) throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "select * from crops where crop_name ILIKE ?"
        );

        ps.setString(1, "%" + name + "%");

        ResultSet rs = ps.executeQuery();

        boolean found = false;

        while(rs.next())
        {
            found = true;
            System.out.println(
                rs.getInt("crop_id") + " " +
                rs.getString("crop_name") + " " +
                rs.getString("season")
            );
        }

        if(!found)
        {
            System.out.println("NO MATCHING CROPS FOUND");
        }

        con.close();
    }

}
