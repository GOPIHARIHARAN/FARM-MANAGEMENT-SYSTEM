package farm_app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class FarmApp {

    public static void main(String[] args) throws Exception {

        FarmTableCreator.initializeTables();

        Scanner sc = new Scanner(System.in);
        FarmDBOperations db = new FarmDBOperations();
        LoginService login = new LoginService();

        while(true) 
        {
            System.out.println("\n--- LOGIN ---");
            System.out.print("USERNAME : ");
            String username = sc.nextLine();

            System.out.print("PASSWORD : ");
            String password = sc.nextLine();

            String role = login.login(username, password);

            if(role == null)
            {
                System.out.println("INVALID LOGIN");
                continue;
            }

            System.out.println("LOGIN SUCCESSFUL (" + role.toUpperCase() + ")");


           

            if(role.equals("farmer"))
            {
                while(true)
                {
                    System.out.println("\n--- FARMER MENU ---");
                    System.out.println("1 VIEW CROPS");
                    System.out.println("2 VIEW YIELD");
                    System.out.println("3 SEARCH CROP BY NAME");
                    System.out.println("4 VIEW FERTILIZER");
                    System.out.println("5 LOGOUT");

                    int choice = sc.nextInt();
                    sc.nextLine();

                    switch(choice)
                    {
                        case 1:
                            db.viewCrops();
                            break;

                        case 2:
                        	if(!db.hasCrops()) {
                        	    System.out.println("NO CROPS AVAILABLE");
                        	    continue;   
                        	}

                        	System.out.println("\nAVAILABLE CROPS:");
                        	db.viewCrops();

                        	System.out.println("ENTER CROP ID : ");
                        	int id2 = sc.nextInt();

                        	db.viewYield(id2);

                            break;

                        case 3:
                        	if(!db.hasCrops()) {
                        	    System.out.println("NO CROPS AVAILABLE");
                        	    continue;    // return to menu
                        	}

                        	System.out.println("ENTER CROP NAME TO SEARCH : ");
                        	String nm = sc.nextLine();
                        	db.searchCropByName(nm);

                            break;

                        case 4:
                        	if(!db.hasCrops()) {
                        	    System.out.println("NO CROPS AVAILABLE");
                        	    continue;   
                        	}
                        	
                            System.out.println("\nAVAILABLE CROPS:");
                            db.viewCrops();
                            System.out.println("ENTER CROP ID TO VIEW FERTILIZER : ");
                            int id9 = sc.nextInt();
                            db.viewFertilizer(id9);
                            break;
                        	
                        case 5:
                            System.out.println("LOGGING OUT...");
                            break;

                        default:
                            System.out.println("INVALID OPTION");
                    }

                    if(choice == 4)
                        break;
                }
            }

            

            else if(role.equals("admin"))
            {
                while(true)
                {
                    System.out.println("\n--- ADMIN MENU ---");
                    System.out.println("1 VIEW CROPS");
                    System.out.println("2 SEARCH CROP BY NAME");
                    System.out.println("3 ADD CROP");
                    System.out.println("4 UPDATE CROP NAME");
                    System.out.println("5 DELETE CROP");
                    
                    System.out.println("6 VIEW YIELD");
                    System.out.println("7 ADD YIELD");
                    System.out.println("8 PREDICT YIELD");

                   
                    System.out.println("9 VIEW FERTILIZER");
                    System.out.println("10 ADD FERTILIZER");
                    
                    System.out.println("11 CHANGE OWN PASSWORD");
                    System.out.println("12 ADD FARMER ACCOUNT");
                    System.out.println("13 CHANGE FARMER PASSWORD");
                    System.out.println("14 LOGOUT");

                    int choice = sc.nextInt();
                    sc.nextLine();

                    switch(choice)
                    {
                        case 1:
                            db.viewCrops();
                            break;
                            
                        case 2:
                        	if(!db.hasCrops()) {
                        	    System.out.println("NO CROPS AVAILABLE");
                        	    continue;   
                        	}

                        	System.out.println("ENTER CROP NAME TO SEARCH : ");
                        	String nm = sc.nextLine();
                        	db.searchCropByName(nm);

                            break;
                            
                        case 3:
                            System.out.println("ENTER CROP NAME : ");
                            String cn = sc.nextLine();
                            System.out.println("ENTER SEASON : ");
                            String season = sc.nextLine();
                            db.addCrop(cn, season);
                            break;
                            
                        case 4:
                        	if(!db.hasCrops()) {
                        	    System.out.println("NO CROPS AVAILABLE");
                        	    continue;   
                        	}
                        	
                            System.out.println("\nAVAILABLE CROPS:");
                            db.viewCrops();
                            System.out.println("ENTER CROP ID TO UPDATE NAME : ");
                            int id7 = sc.nextInt();
                            sc.nextLine();
                            System.out.println("ENTER NEW CROP NAME : ");
                            String newName = sc.nextLine();
                            db.updateCropName(id7, newName);
                            break;

                        case 5:
                        	if(!db.hasCrops()) {
                        	    System.out.println("NO CROPS AVAILABLE");
                        	    continue;   
                        	}
                            System.out.println("\nAVAILABLE CROPS:");
                            db.viewCrops();
                            System.out.println("ENTER CROP ID TO DELETE : ");
                            int id6 = sc.nextInt();
                            db.deleteCrop(id6);
                            break;

                        case 6:
                        	if(!db.hasCrops()) {
                        	    System.out.println("NO CROPS AVAILABLE");
                        	    continue;   
                        	}

                        	System.out.println("\nAVAILABLE CROPS:");
                        	db.viewCrops();

                        	System.out.println("ENTER CROP ID : ");
                        	int id2 = sc.nextInt();

                        	db.viewYield(id2);

                            break;

                        case 7:
                        	if(!db.hasCrops()) {
                        	    System.out.println("NO CROPS AVAILABLE");
                        	    continue;   
                        	}
                        	
                            System.out.println("\nAVAILABLE CROPS:");
                            db.viewCrops();
                            System.out.println("ENTER CROP ID TO ADD YIELD: ");
                            int id5 = sc.nextInt();
                            System.out.println("ENTER YEAR : ");
                            int year = sc.nextInt();
                            System.out.println("ENTER YIELD (KG) : ");
                            double y = sc.nextDouble();
                            db.addYield(id5, year, y);
                            break;
                            
                        case 8:  // PREDICT YIELD
                        {
                            // Check if any crops exist
                            if (!db.hasCrops()) {
                                System.out.println("NO CROPS AVAILABLE");
                                break;
                            }

                            System.out.print("ENTER CROP ID: ");
                            int cropId = sc.nextInt();

                            //  Validate cropId
                            Connection con = DBConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement(
                                "select crop_id from crops where crop_id=?"
                            );
                            ps.setInt(1, cropId);
                            ResultSet rs = ps.executeQuery();

                            if (!rs.next()) {
                                System.out.println("INVALID CROP ID");
                                con.close();
                                break;
                            }
                            con.close();

                            // If valid → call your existing method
                            db.predictYield(cropId);

                            break;
                        }

                        case 9:
                        	if(!db.hasCrops()) {
                        	    System.out.println("NO CROPS AVAILABLE");
                        	    continue;   
                        	}
                        	
                            System.out.println("\nAVAILABLE CROPS:");
                            db.viewCrops();
                            System.out.println("ENTER CROP ID TO VIEW FERTILIZER : ");
                            int id9 = sc.nextInt();
                            db.viewFertilizer(id9);
                            break;
  
                       
                        case 10:
                        	if(!db.hasCrops()) {
                        	    System.out.println("NO CROPS AVAILABLE");
                        	    continue;   
                        	}
                        	
                            System.out.println("\nAVAILABLE CROPS:");
                            db.viewCrops();
                            System.out.println("ENTER CROP ID TO ADD FERTILIZER : ");
                            int id8 = sc.nextInt();
                            sc.nextLine();
                            System.out.println("ENTER FERTILIZER NAME : ");
                            String fer = sc.nextLine();
                            System.out.println("ENTER QUANTITY (KG) : ");
                            double qty = sc.nextDouble();
                            db.addFertilizer(id8, fer, qty);
                            break;

                       
                        case 11:
                            System.out.print("ENTER OLD PASSWORD: ");
                            String oldp = sc.nextLine();
                            System.out.print("ENTER NEW PASSWORD: ");
                            String newp = sc.nextLine();
                            boolean ok = login.changePassword(username, oldp, newp);
                            if(ok)
                                System.out.println("PASSWORD UPDATED SUCCESSFULLY");
                            else
                                System.out.println("OLD PASSWORD INCORRECT");
                            break;

                        case 12:
                            System.out.print("ENTER FARMER USERNAME: ");
                            String fuser = sc.nextLine();
                            System.out.print("ENTER PASSWORD: ");
                            String fpass = sc.nextLine();
                            boolean added = login.addFarmer(fuser, fpass);
                            if(added)
                                System.out.println("FARMER ACCOUNT CREATED");
                            else
                                System.out.println("USERNAME ALREADY TAKEN");
                            break;

                        case 13:
                            System.out.print("ENTER FARMER USERNAME TO CHANGE PASSWORD: ");
                            String targetUser = sc.nextLine();
                            System.out.print("ENTER NEW PASSWORD: ");
                            String newPassword = sc.nextLine();
                            boolean updated = login.adminChangeUserPassword(targetUser, newPassword);
                            if(updated)
                                System.out.println("PASSWORD UPDATED SUCCESSFULLY");
                            else
                                System.out.println("USER NOT FOUND");
                            break;

                        case 14:
                            System.out.println("LOGGING OUT...");
                            break;

                        default:
                            System.out.println("INVALID OPTION");
                    }

                    if(choice == 14)
                        break;
                }
            }
        }
    }
}
