/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nmm2010;

import biz.ekoplan.nmm2010.enums.ChartRenderingMethod;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Obiekt przechowuje konfigurację programu NMM. Podczas tworzenia czyta ją
 * z pliku nmmconfig.ini.
 * @author Jarosław Kowalczyk
 */
public class Setup implements Serializable {

    public static final int NOISE_INDICATOR_SEL = 2;
    public static final int NOISE_INDICATOR_LEQ = 1;
    Properties applicationConfig;
    Properties defaultConfig = new Properties();

    // Ścieżka projektu
    String projectPath="c:\\";
    ChartRenderingMethod crm=ChartRenderingMethod.FULL_DATA;

    String dir = System.getProperty("user.home");

    // W konstruktorze otwierany jest plik konfiguracyjny i czytane są dane
    // o konfiguracji.
    public Setup() {        
        
        //reading default configuration from jar file
        try {
            System.out.println("Reading default configuration...");
            defaultConfig = new Properties();
            System.out.println(Setup.class.getClassLoader().getResource("nmm2010/nmm_default.properties"));
            InputStream inputStream  = Setup.class.getClassLoader().getResourceAsStream("nmm2010/nmm_default.properties");
            defaultConfig.load(inputStream);                                
            System.out.println("Reading default congiguration successfull!");
        } catch (IOException ex) {
            System.out.println("Reading default configuration failed!");
            System.out.println("Error: "+ex.toString());
        }

        //reading user configuraton from user home directory
        try {
            System.out.println("Reading user configuration from: "+dir+File.separator+"nmm_application.properties");
            FileInputStream in2 = new FileInputStream(dir+File.separator+"nmm_application.properties");            
            applicationConfig=new Properties(defaultConfig);
            applicationConfig.load(in2);
            in2.close();
            System.out.println("Reading user configuration successfull!");            
        } catch (IOException ex) {
            System.out.println("Reading user configuration failed!");
            System.out.println("Error: "+ex.toString());
            //creating new properties file in user home directory
            if (JOptionPane.showConfirmDialog(null, "Do you want to create local configuration file at: "+dir+"\n"
                    + "If you choose NO, NMM 2010 will not start up.",
                    "User configuration file missing.", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                try {
                    System.out.print("Writin user configuration file...");
                    FileOutputStream out = new FileOutputStream(dir+File.separator+"nmm_application.properties");
                    defaultConfig.store(out,"Copy of default NMM 2010 properties.");
                    out.close();
                    System.out.println("Successfull!");
                    applicationConfig=defaultConfig;
                } catch(IOException exc) {
                    System.out.println("Failed!");
                    System.out.println("Errr: "+exc.toString());
                }
            } else {                
            }
        }
    }

    public String getProperty(String _key, String _default) {
        return this.applicationConfig.getProperty(_key, _default);
    }

    public void setProperty(String _key, String _value) {
        this.applicationConfig.setProperty(_key, _value);
    }

    public void setChartRenderingMethod(ChartRenderingMethod _crm) {
        this.crm=_crm;
    }
    
    /**
     * Get info on how chart is rendered
     * @return enum type ChartRenderingMethod
     */
    public ChartRenderingMethod getChartRenderingMethod() {
        return this.crm;
    }

    /**
     * Zwraca ścieżke dostepu do katalogu projektu (tam gdzie przechowywane są
     * dane wejściowe i dane wynikowe
     * @return
     * @deprecated
     */
    public String getProjectPath() {
        return this.applicationConfig.getProperty("NMM_SETUP_PROJECT_PATH", "c:\\");
    }

    /**
     * Zapisuje do pliku aktualną konfigurację programu
     * @return
     */
    public void saveNMMConfigurationToFile() {

        FileOutputStream out;
        try {
            System.out.println("Konfigurację użytkownika NMM piszę do: "+dir+File.separator+"nmm_application.properties");
            out = new FileOutputStream(dir+File.separator+"nmm_application.properties");
            this.applicationConfig.store(out, "NMM configuration file");
            out.close();
        } catch (IOException ex) {
            System.out.println("Konfiguracja użytkownika NMM nie została zapisana !!!");
            Toolkit.getDefaultToolkit().beep();
        }        
    }

}
