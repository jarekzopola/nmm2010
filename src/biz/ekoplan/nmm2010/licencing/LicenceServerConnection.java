/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.licencing;

/**
 *
 * @author Jarek
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class LicenceServerConnection {

    Connection conn = null;
    final boolean DEBUG = false;
    public void establishConnection() {

        try {
        conn = DriverManager.getConnection("jdbc:mysql://mysql-ekoprojektjk.ogicom.pl/db172489?"+
                "user=db172489_nmm&password=tutajnmm2011");
        System.out.println("Connection established...");
        } catch (SQLException ex) {
            if (this.DEBUG) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
            JOptionPane.showMessageDialog(null, "Internet connection is required to run this software.\n"
                    + "Connect to network and start NMM again.", "No connection!", JOptionPane.WARNING_MESSAGE);
            //System.exit(0);
        }
    }

    public boolean checkLicense(String licNumber) {

        boolean usageAllowed = false;

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();                        
            if (stmt.execute("SELECT * FROM nmm_lic")) {
                rs = stmt.getResultSet();
            }
                rs.first();                
                while (!rs.isAfterLast()) {
                    String licString=rs.getString("user_id");                    
                    if (licString.equals(licNumber)) {
                        usageAllowed=true;
                        if (this.DEBUG) {
                            System.out.println("This user has been found in nmm database.");   
                        }                        
                        rs.last();
                        // add log to database
                        InetAddress thisIp;
                        String AjPi;
                        try {
                            thisIp =InetAddress.getLocalHost();
                            AjPi=thisIp.toString();
                        } catch (UnknownHostException ex) {
                            AjPi="Excepion occured at local host";
                        }
                        stmt.execute("INSERT INTO `db172489`.`nmm_stat` (`user_id` ,`time` ,`remarks`) VALUES ('"+licNumber+"', NOW( ) , '"+AjPi+"')");
                    } else {
                        rs.next();
                    }
                }
                System.out.println("------------------------------");
            }
        catch (SQLException ex){            
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed
            if (rs != null) {
            try {
                rs.close();
            } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore
                stmt = null;
            }
        }
        return usageAllowed;
    }
}
