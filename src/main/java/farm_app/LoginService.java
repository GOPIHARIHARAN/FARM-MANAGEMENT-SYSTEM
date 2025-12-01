package farm_app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginService {

    public String login(String username, String password) throws Exception {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "select role from users where username=? and password=?"
        );

        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        if(rs.next())
        {
            return rs.getString("role"); 
        }

        return null;
    }
    
    public boolean changePassword(String username, String oldPass, String newPass) throws Exception {

        Connection con = DBConnection.getConnection();

        // Check old password is correct
        PreparedStatement ps1 = con.prepareStatement(
            "select * from users where username=? and password=?"
        );
        ps1.setString(1, username);
        ps1.setString(2, oldPass);

        ResultSet rs = ps1.executeQuery();

        if(!rs.next())
        {
            return false;  // old password wrong
        }

        // Update password
        PreparedStatement ps2 = con.prepareStatement(
            "update users set password=? where username=?"
        );

        ps2.setString(1, newPass);
        ps2.setString(2, username);

        ps2.executeUpdate();
        con.close();

        return true;
    }
    public boolean addFarmer(String username, String password) throws Exception {

        Connection con = DBConnection.getConnection();

        // Check if username exists
        PreparedStatement ps1 = con.prepareStatement(
            "select * from users where username=?"
        );
        ps1.setString(1, username);

        ResultSet rs = ps1.executeQuery();

        if(rs.next())
        {
            return false; 
        }

        PreparedStatement ps2 = con.prepareStatement(
            "insert into users(username, password, role) values(?,?, 'farmer')"
        );

        ps2.setString(1, username);
        ps2.setString(2, password);

        ps2.executeUpdate();
        con.close();

        return true;
    }
    public boolean adminChangeUserPassword(String targetUser, String newPass) throws Exception {

        Connection con = DBConnection.getConnection();

        // Check if target user exists
        PreparedStatement ps1 = con.prepareStatement(
            "select * from users where username=?"
        );
        ps1.setString(1, targetUser);

        ResultSet rs = ps1.executeQuery();

        if(!rs.next())
        {
            con.close();
            return false; // user doesn't exist
        }

        // Update password
        PreparedStatement ps2 = con.prepareStatement(
            "update users set password=? where username=?"
        );
        ps2.setString(1, newPass);
        ps2.setString(2, targetUser);

        ps2.executeUpdate();
        con.close();
        return true;
    }


}
