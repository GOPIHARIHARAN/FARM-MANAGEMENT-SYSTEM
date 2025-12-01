package farm_app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class FarmTableCreator {

    public static void initializeTables() throws Exception {

        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();

        //                               USERS TABLE 

        ResultSet r4 = st.executeQuery(
            "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE tablename='users')"
        );
        r4.next();

        if(!r4.getBoolean(1))
        {
            st.executeUpdate(
                "CREATE TABLE users (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(50), " +
                "password VARCHAR(50), " +
                "role VARCHAR(20))"
            );

            st.executeUpdate(
                "INSERT INTO users(username, password, role) VALUES" +
                "('admin', 'admin123', 'admin')"
            );

            st.executeUpdate(
                "INSERT INTO users(username, password, role) VALUES" +
                "('farmer1', 'farm123', 'farmer')"
            );

            System.out.println("USERS TABLE CREATED");
        }
        else
        {
            System.out.println("USERS TABLE ALREADY EXISTS");
        }


        //                                CROPS TABLE 

        ResultSet r1 = st.executeQuery(
            "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE tablename='crops')"
        );
        r1.next();

        if(!r1.getBoolean(1))
        {
            st.executeUpdate(
                "CREATE TABLE crops (" +
                "crop_id SERIAL PRIMARY KEY, " +
                "crop_name VARCHAR(100), " +
                "season VARCHAR(50))"
            );
            System.out.println("CROPS TABLE CREATED");
        }
        else
        {
            System.out.println("CROPS TABLE ALREADY EXISTS");
        }


        //                             YIELD TABLE

        ResultSet r2 = st.executeQuery(
            "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE tablename='yield_data')"
        );
        r2.next();

        if(!r2.getBoolean(1))
        {
            st.executeUpdate(
                "CREATE TABLE yield_data (" +
                "id SERIAL PRIMARY KEY, " +
                "crop_id INT REFERENCES crops(crop_id), " +
                "year INT, " +
                "yield_kg DOUBLE PRECISION)"
            );
            System.out.println("YIELD TABLE CREATED");
        }
        else
        {
            System.out.println("YIELD TABLE ALREADY EXISTS");
        }


        //                       FERTILIZER TABLE 

        ResultSet r3 = st.executeQuery(
            "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE tablename='fertilizer')"
        );
        r3.next();

        if(!r3.getBoolean(1))
        {
            st.executeUpdate(
                "CREATE TABLE fertilizer (" +
                "id SERIAL PRIMARY KEY, " +
                "crop_id INT REFERENCES crops(crop_id), " +
                "fertilizer_used VARCHAR(100), " +
                "quantity_kg DOUBLE PRECISION)"
            );
            System.out.println("FERTILIZER TABLE CREATED");
        }
        else
        {
            System.out.println("FERTILIZER TABLE ALREADY EXISTS");
        }

        con.close();
    }
}
