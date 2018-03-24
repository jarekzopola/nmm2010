/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NMM2010.java
 *
 * Created on 2010-11-07, 02:52:21
 */

package nmm2010;

import biz.ekoplan.nmm2010.audio.NMMAudioPlayer;
import biz.ekoplan.nmm2010.devices.NMMNoiseMeasurementSet;
import biz.ekoplan.nmm2010.devices.NMMNoiseMeasurementSetProperties;
import biz.ekoplan.nmm2010.enums.RecordValueType;
import biz.ekoplan.nmm2010.enums.TimePeriods;
import biz.ekoplan.nmm2010.events.NMMEventEditor;
import biz.ekoplan.nmm2010.events.NMMEventTypesEdytor;
import biz.ekoplan.nmm2010.measurement.NMMCreateMeasurement;
import biz.ekoplan.nmm2010.measurement.NMMMeasurement;
import biz.ekoplan.nmm2010.measurement.NMMMeasurementsTable;
import biz.ekoplan.nmm2010.measurement.NMMMeasurementsTableModel;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectChangedEvent;
import biz.ekoplan.nmm2010.nmmproject.NMMProjectChangedListener;
import biz.ekoplan.nmm2010.presentations.NMMPresentationCreator;
import biz.ekoplan.nmm2010.presentations.NMMPresentationsManager;
import biz.ekoplan.nmm2010.projectmanager.NMMProjectManagerLeafRenderer;
import biz.ekoplan.nmm2010.projectmanager.ProjectManager;
import biz.ekoplan.nmm2010.reports.NMMReportWriter;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseSourceModelOccupationalExp;
import biz.ekoplan.nmm2010.surcemodel.NMMNoiseWallAttenuationModel;
import biz.ekoplan.nmm2010.surcemodel.NMMPointLocationTableModel;
import biz.ekoplan.nmm2010.surcemodel.NMMRoadSamplingModel;
import biz.ekoplan.nmm2010.surcemodel.NMMSimpleContinuousModel;
import biz.ekoplan.nmm2010.surcemodel.NMMSimpleModel;
import biz.ekoplan.nmm2010.surcemodel.NMMSingleEventsMethodModel;
import biz.ekoplan.nmm2010.surcemodel.NMMSoundPowerLevelModel;
import biz.ekoplan.nmm2010.surcemodel.sminterface.*;
import biz.ekoplan.nmm2010.toolbox.NMMToolbox;
import biz.ekoplan.nmm2010.toolbox.TimeConverter;
import biz.ekoplan.nmm2010.val_ed.NMMValuesEditor;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.sound.sampled.AudioFileFormat;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

/**
 *
 * @author Jarek
 */
public class NMM2018 extends javax.swing.JFrame implements NMMProjectChangedListener, TreeSelectionListener {

     final boolean DEBUG = true;
     private long version=16;
     
     HelpSet hs;
     HelpBroker hb=null;
     biz.ekoplan.nmm2010.licencing.LicenceServerConnection lic;

     Setup nmmSetup = new Setup();
     NMMProject nmmProject = new NMMProject();
     
     Locale localeGlobal;
    
     private int mode=-1;
     private int selectionWidth;
     private int mouseMemX=-1;
     private int mouseMemY=-1;
     private int mouseStartMemX=-1;
     private int mouseStartMemY=-1;
     private int mouseOldMemX=-1;
     private int mouseOldMemY=-1;
     int markerWidth=0;
     String memOldLaeq;
     File projectCurrentFile=new File(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NONAME.NMM")+this.version);

     private final int MODE_ZOOM_INITIATED = 0;
     private final int MODE_ZOOM_STARTED = 1;
     private final int MODE_NULL=-1;
     private final int MODE_SELECTION_STARTED=2;
     private final int MODE_SELECTION10_STARTED=4;
     private final int MODE_SELECTION60_STARTED=5;
     private final int MODE_SELECTIONN_STARTED=8;
     private final int MODE_ZOOM_OUT_INITIATED = 3;
     private final int MODE_MTA_INITIATED=6;
     private final int MODE_MTA_STARTED=7;
     private final int MODE_SPLIT_MEASUREMENT=9;

     private DefaultListModel dlm = new DefaultListModel();
     private DefaultListModel dle = new DefaultListModel();

     Image nmmImg = null;
     
     //model drzewka managera projektu     
     DefaultMutableTreeNode measurements = new DefaultMutableTreeNode(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("MEASUREMENTS"));
     DefaultMutableTreeNode events = new DefaultMutableTreeNode(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("EVENTS"));
     DefaultMutableTreeNode mDevices = new DefaultMutableTreeNode(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("MEASUREMENT DEVICES"));
     DefaultMutableTreeNode calcModels = new DefaultMutableTreeNode(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CALCULATION MODELS"));
     DefaultMutableTreeNode presentations = new DefaultMutableTreeNode(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("PRESENTATIONS"));     
     DefaultMutableTreeNode nmmprojecttop = new DefaultMutableTreeNode("NMMProject");
     DefaultTreeModel model = new DefaultTreeModel(nmmprojecttop);
     java.util.ResourceBundle boundle;
     

    /** Creates new form NMM2010 */
    public NMM2018() {

    String helpHS=null;   
                 
    String jezyk=this.nmmSetup.getProperty("NMM_SETUP_LANGUAGE", "English");
        if (jezyk.equals("Polski")) {
            localeGlobal = new Locale("pl", "PL");
            helpHS = "Master.hs";
        }
        if (jezyk.equals("English")) {
            localeGlobal = new Locale("en", "US");
            helpHS = "Master_en.hs";
        }
        if (jezyk.equals("Deutsch")) {
            localeGlobal = new Locale("de", "DE");
            helpHS = "Master_de.hs";
        }
        if (jezyk.equals("Francais")) {
            localeGlobal = new Locale("fr", "FR");
            helpHS = "Master_en.hs";
        }                
        
        try {
            nmmImg = ImageIO.read(new File("nmm.png"));
        } catch (IOException e) {
            System.out.println("Błąd podczas czytania pliku ikony nmm.png:  "+e.toString());
        }

        System.out.println("Lokalizacja defaultowa: "+Locale.getDefault());
        Locale.setDefault(localeGlobal);
        
        //////////////////////////////////////////// POCZĄTEK HELP                       
        
        ClassLoader cl = NMM2018.class.getClassLoader();

        try {            
            //URL hsURL = HelpSet.findHelpSet(cl, helpHS, this.localeGlobal);
            URL hsURL = this.getClass().getResource(helpHS);
            System.out.println("Szukam w : "+hsURL.toString());
            
            hs = new HelpSet(cl, hsURL);

        } catch (Exception ee) {
            System.out.println( "HelpSet " + ee.getMessage());
            System.out.println("HelpSet "+ helpHS +" not found");
        }
        hb = hs.createHelpBroker();
        hb.setFont(new Font("Courier", Font.PLAIN, 12));
        hb.setLocale(this.localeGlobal);
        /////////////////////////////////////////////// KONIEC HELP

        lic = new biz.ekoplan.nmm2010.licencing.LicenceServerConnection();
        lic.establishConnection();
        if (!lic.checkLicense("1")) {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("YOU ARE NOT ALLOWED TO USE THIS SOFTWARE.")+"\n"
                    + java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("GO TO WWW.MAPAAKUSTYCZNA.PL TO REGISTER.")+"\n"
                    + java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("THIS COPY OF NMM2010."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("SOFTWARE NOT REGISTERED!"), JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
        
        
        this.boundle = java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations");
        
        initComponents();
        this.nmmMainChart.setNMMProject(nmmProject);
        this.nmmMainChart.setTimeSelector(this.timeSelector);
        
        
        
        nmmProject.addProjectChangedListener(this);
        nmmProject.addProjectChangedListener(this.projectManager);
        nmmProject.addProjectChangedListener(this.nmmMainChart);
        
        //jeżeli w ustawieniach jest że na stacie ma być ładowany poprzednio
        //edytowany projekt, to należy go teraz załadować
        
        //budowanie podstawowej struktury drzewka projektu                         
        model.setRoot(nmmprojecttop);
        nmmprojecttop.add(measurements);
        nmmprojecttop.add(events);
//        nmmprojecttop.add(mDevices);
//        nmmprojecttop.add(calcModels);
//        nmmprojecttop.add(presentations);
        projectManager.addTreeSelectionListener(this);
        this.projectManager.setCellRenderer(new NMMProjectManagerLeafRenderer());    
        
        readMeasurementDevices();
        
        this.nmmMainChart.setViewPortFullTimeRange();
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser(this.nmmSetup.getProjectPath());
        radiogroupSelectionMode = new javax.swing.ButtonGroup();
        popupMeasurements = new javax.swing.JPopupMenu();
        popupMeasurementsDelete = new javax.swing.JMenuItem();
        popupMasurementsProperties = new javax.swing.JMenuItem();
        popupMeasurementsZoomTo = new javax.swing.JMenuItem();
        popupMeasurement = new javax.swing.JPopupMenu();
        popupMeasurementDelete = new javax.swing.JMenuItem();
        popupMeasurementZoomTo = new javax.swing.JMenuItem();
        popupMeasurementProperties = new javax.swing.JMenuItem();
        popupMeasurementVisible = new javax.swing.JCheckBoxMenuItem();

        popupProjectManagerEvents = new javax.swing.JPopupMenu();
        Properties = new javax.swing.JMenuItem();
        popupMainChart = new javax.swing.JPopupMenu();
        popupMCDeleteEvent = new javax.swing.JMenuItem();
        toolBar = new javax.swing.JToolBar();
        toolbarView = new javax.swing.JToolBar();
        buttonNew = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        buttonZoomOut = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        buttonPlayMP3 = new javax.swing.JButton();
        buttonExclude = new javax.swing.JButton();
        splitPane = new javax.swing.JSplitPane();
        panelMain = new javax.swing.JPanel();
        nmmMainChart = new nmm2010.NMMMainChart();
        timeSelector = new nmm2010.TimeSelector();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelSideInfo = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        labelCurrentMeasurementLeq = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        labelValueAtCursor = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        labelSelectionLAeqValue = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        labelRecId = new javax.swing.JLabel();
        panelProject = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        projectManager = new ProjectManager(nmmprojecttop, measurements, events, mDevices, calcModels, presentations, model);
        panelInfo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        labelTime = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelActiveMeasurement = new javax.swing.JLabel();
        labelSelectionSize = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        menuProject = new javax.swing.JMenu();
        menuPNew = new javax.swing.JMenuItem();
        menuPOpen = new javax.swing.JMenuItem();
        menuPSave = new javax.swing.JMenuItem();
        menuPSaveAs = new javax.swing.JMenuItem();
        menuPProperties = new javax.swing.JMenuItem();
        menuPExportDescription = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuPSetup = new javax.swing.JMenuItem();
        menuPQuit = new javax.swing.JMenuItem();
        menuPExportToXML = new javax.swing.JMenuItem();
        menuDeviceDevices = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuDImport = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        menuZoom100percent = new javax.swing.JMenuItem();
        menuZoom200percent = new javax.swing.JMenuItem();
        menuZoom500percent = new javax.swing.JMenuItem();
        menuVZoomAll = new javax.swing.JMenuItem();
        menuVVerticalScale = new javax.swing.JMenuItem();
        menuEClearSelection = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        menuESmFreeSelection = new javax.swing.JRadioButtonMenuItem();
        menuESm10secSelection = new javax.swing.JRadioButtonMenuItem();
        menuESm60secSelection = new javax.swing.JRadioButtonMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        menuESToBackgroundNoiseLevel = new javax.swing.JMenuItem();
        menuESNeighbNoiseLevel = new javax.swing.JMenuItem();
        menuESCopy = new javax.swing.JMenuItem();
        menuESCut = new javax.swing.JMenuItem();
        menuESDelete = new javax.swing.JMenuItem();
        menuESEditValues = new javax.swing.JMenuItem();
        menuESPaste = new javax.swing.JMenuItem();
        menuESplit = new javax.swing.JMenuItem();
        menuMeasurement = new javax.swing.JMenu();
        jMenu7 = new javax.swing.JMenu();
        menuMImport = new javax.swing.JMenuItem();
        menuMIBK2238sprformat = new javax.swing.JMenuItem();
        menuMIBK2236sprformat = new javax.swing.JMenuItem();
        menuMIBK2250text = new javax.swing.JMenuItem();
        menuMICsVImport = new javax.swing.JMenuItem();
        menuMExport = new javax.swing.JMenuItem();
        menuMInfo = new javax.swing.JMenuItem();
        menuMTrim = new javax.swing.JMenuItem();
        menuMTimeAdjust = new javax.swing.JMenuItem();
        menuMTable = new javax.swing.JMenuItem();
        menuMCreate = new javax.swing.JMenuItem();
        menuEvents = new javax.swing.JMenu();
        menuECreate = new javax.swing.JMenuItem();
        menuEDelete = new javax.swing.JMenu();
        menuEDAllEvents = new javax.swing.JMenuItem();
        menuEDEvent = new javax.swing.JMenuItem();
        menuEEventsTable = new javax.swing.JMenuItem();
        menuEImport = new javax.swing.JMenuItem();
        menuEExport = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        menuModels = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        menuMN_RoadSampling = new javax.swing.JMenuItem();
        menuMSingleEvents = new javax.swing.JMenuItem();
        menuMNSimpleEvBasedMeasurement = new javax.swing.JMenuItem();
        menuMNSimpleContinousMeasurement = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        menuMNOPN9123 = new javax.swing.JMenuItem();
        menuMMSoundPwrLvl = new javax.swing.JMenuItem();
        menuMNInSituNoiseWall = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuRManagePresentations = new javax.swing.JMenuItem();
        menuPCreate = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuHHelp = new javax.swing.JMenuItem();
        menuHOnlinehelp = new javax.swing.JMenuItem();

        fileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        fileChooser.setFileFilter(new FileNameExtensionFilter("NMM "+this.version+java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString(" PROJECT"), "nmm"+this.version));

        popupMeasurementsDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Delete16.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nmm2010/nmm_application"); // NOI18N
        popupMeasurementsDelete.setText(bundle.getString("DELETE")); // NOI18N
        bundle = java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations");
        popupMeasurementsDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupMeasurementsDeleteActionPerformed(evt);
            }
        });
        popupMeasurements.add(popupMeasurementsDelete);

        popupMasurementsProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Preferences16.gif"))); // NOI18N
        popupMasurementsProperties.setText(bundle.getString("PROPERTIES")); // NOI18N
        popupMasurementsProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupMasurementsPropertiesActionPerformed(evt);
            }
        });
        popupMeasurements.add(popupMasurementsProperties);

        popupMeasurementsZoomTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Zoom16.gif"))); // NOI18N
        popupMeasurementsZoomTo.setText(bundle.getString("ZOOM TO")); // NOI18N
        popupMeasurementsZoomTo.setToolTipText(bundle.getString("ZOOM TO MEASUREMENT TIME RANGE.")); // NOI18N
        popupMeasurementsZoomTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupMeasurementsZoomToActionPerformed(evt);
            }
        });
        popupMeasurements.add(popupMeasurementsZoomTo);

        popupMeasurementDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Delete16.gif"))); // NOI18N
        popupMeasurementDelete.setText(bundle.getString("DELETE")); // NOI18N
        popupMeasurementDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupMeasurementDeleteActionPerformed(evt);
            }
        });
        popupMeasurement.add(popupMeasurementDelete);

        popupMeasurementZoomTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/ZoomIn16.gif"))); // NOI18N
        popupMeasurementZoomTo.setText(bundle.getString("ZOOM TO ...")); // NOI18N
        popupMeasurementZoomTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupMeasurementZoomToActionPerformed(evt);
            }
        });
        popupMeasurement.add(popupMeasurementZoomTo);

        popupMeasurementProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Preferences16.gif"))); // NOI18N
        popupMeasurementProperties.setText(bundle.getString("PROPERTIES")); // NOI18N
        popupMeasurementProperties.setToolTipText(bundle.getString("MODIFY EVENT PROPERTIES.")); // NOI18N
        popupMeasurementProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupMeasurementPropertiesActionPerformed(evt);
            }
        });
        popupMeasurement.add(popupMeasurementProperties);

        popupMeasurementVisible.setText(bundle.getString("VISIBLE")); // NOI18N
        popupMeasurementVisible.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Find16.gif"))); // NOI18N
        popupMeasurementVisible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popupMeasurementVisibleActionPerformed(evt);
            }
        });
        popupMeasurement.add(popupMeasurementVisible);

        popupProjectManagerEvents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                popupProjectManagerEventsMouseReleased(evt);
            }
        });

        Properties.setText("jMenuItem8");
        Properties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PropertiesActionPerformed(evt);
            }
        });
        popupProjectManagerEvents.add(Properties);

        popupMCDeleteEvent.setText("Delete event");
        popupMCDeleteEvent.setToolTipText("");
        popupMainChart.add(popupMCDeleteEvent);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("NMM 2017 v 0.3.0");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(nmmImg);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        toolbarView.setFloatable(false);

        buttonNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/New24.gif"))); // NOI18N
        buttonNew.setText(bundle.getString("NEW")); // NOI18N
        buttonNew.setFocusable(false);
        buttonNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNew.setMaximumSize(new java.awt.Dimension(70, 49));
        buttonNew.setMinimumSize(new java.awt.Dimension(70, 49));
        buttonNew.setPreferredSize(new java.awt.Dimension(70, 49));
        buttonNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNewActionPerformed(evt);
            }
        });
        toolbarView.add(buttonNew);

        buttonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Save24.gif"))); // NOI18N
        buttonSave.setText(bundle.getString("SAVE")); // NOI18N
        buttonSave.setFocusable(false);
        buttonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSave.setMaximumSize(new java.awt.Dimension(70, 49));
        buttonSave.setMinimumSize(new java.awt.Dimension(70, 49));
        buttonSave.setPreferredSize(new java.awt.Dimension(70, 49));
        buttonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });
        toolbarView.add(buttonSave);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/ZoomIn24.gif"))); // NOI18N
        jButton1.setText(bundle.getString("ZOOM IN")); // NOI18N
        jButton1.setToolTipText(bundle.getString("ZOOM IN")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setMaximumSize(new java.awt.Dimension(70, 49));
        jButton1.setMinimumSize(new java.awt.Dimension(55, 49));
        jButton1.setPreferredSize(new java.awt.Dimension(70, 49));
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        toolbarView.add(jButton1);

        buttonZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/ZoomOut24.gif"))); // NOI18N
        buttonZoomOut.setText(bundle.getString("ZOOM OUT")); // NOI18N
        buttonZoomOut.setToolTipText(bundle.getString("ZOOM OUT")); // NOI18N
        buttonZoomOut.setFocusable(false);
        buttonZoomOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonZoomOut.setMaximumSize(new java.awt.Dimension(70, 49));
        buttonZoomOut.setMinimumSize(new java.awt.Dimension(70, 49));
        buttonZoomOut.setPreferredSize(new java.awt.Dimension(70, 49));
        buttonZoomOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonZoomOutActionPerformed(evt);
            }
        });
        toolbarView.add(buttonZoomOut);

        toolBar.add(toolbarView);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Zoom24.gif"))); // NOI18N
        jButton3.setText(bundle.getString("ZOOM ALL")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setMaximumSize(new java.awt.Dimension(70, 49));
        jButton3.setMinimumSize(new java.awt.Dimension(70, 49));
        jButton3.setPreferredSize(new java.awt.Dimension(70, 49));
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        toolBar.add(jButton3);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Bookmarks24.gif"))); // NOI18N
        jButton2.setText(bundle.getString("NEW EVENT")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setMaximumSize(new java.awt.Dimension(70, 49));
        jButton2.setMinimumSize(new java.awt.Dimension(70, 49));
        jButton2.setPreferredSize(new java.awt.Dimension(70, 49));
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        toolBar.add(jButton2);

        buttonPlayMP3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/media/Volume24.gif"))); // NOI18N
        buttonPlayMP3.setText("PlayMP3");
        buttonPlayMP3.setToolTipText("Select time range and play associated mp3 file");
        buttonPlayMP3.setFocusable(false);
        buttonPlayMP3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonPlayMP3.setMaximumSize(new java.awt.Dimension(70, 49));
        buttonPlayMP3.setMinimumSize(new java.awt.Dimension(70, 49));
        buttonPlayMP3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonPlayMP3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPlayMP3ActionPerformed(evt);
            }
        });
        toolBar.add(buttonPlayMP3);

        buttonExclude.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/table/ColumnDelete24.gif"))); // NOI18N
        buttonExclude.setText("Ex/Include");
        buttonExclude.setFocusable(false);
        buttonExclude.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonExclude.setMaximumSize(new java.awt.Dimension(70, 49));
        buttonExclude.setMinimumSize(new java.awt.Dimension(70, 49));
        buttonExclude.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonExclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExcludeActionPerformed(evt);
            }
        });
        toolBar.add(buttonExclude);

        splitPane.setBorder(null);
        splitPane.setDividerLocation(-2);
        splitPane.setDividerSize(10);
        splitPane.setOneTouchExpandable(true);

        panelMain.setLayout(new javax.swing.BoxLayout(panelMain, javax.swing.BoxLayout.Y_AXIS));

        nmmMainChart.setBackground(new java.awt.Color(255, 255, 255));
        nmmMainChart.setMaximumSize(new java.awt.Dimension(2000, 2000));
        nmmMainChart.setPreferredSize(new java.awt.Dimension(400, 341));
        nmmMainChart.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                nmmMainChartMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                nmmMainChartMouseMoved(evt);
            }
        });
        nmmMainChart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nmmMainChartMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                nmmMainChartMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                nmmMainChartMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout nmmMainChartLayout = new javax.swing.GroupLayout(nmmMainChart);
        nmmMainChart.setLayout(nmmMainChartLayout);
        nmmMainChartLayout.setHorizontalGroup(
            nmmMainChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 848, Short.MAX_VALUE)
        );
        nmmMainChartLayout.setVerticalGroup(
            nmmMainChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 474, Short.MAX_VALUE)
        );

        panelMain.add(nmmMainChart);

        timeSelector.setMaximum(0);
        timeSelector.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        timeSelector.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                timeSelectorAdjustmentValueChanged(evt);
            }
        });
        panelMain.add(timeSelector);

        splitPane.setRightComponent(panelMain);

        jPanel1.setMinimumSize(new java.awt.Dimension(200, 200));
        jPanel1.setPreferredSize(new java.awt.Dimension(32767, 32767));

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(200, 160));

        panelSideInfo.setMinimumSize(new java.awt.Dimension(150, 300));
        panelSideInfo.setName("Info"); // NOI18N
        panelSideInfo.setPreferredSize(new java.awt.Dimension(195, 300));
        panelSideInfo.setRequestFocusEnabled(false);

        jLabel2.setText(bundle.getString("CURRENT MEASUREMENT:")); // NOI18N

        jLabel5.setText("<html> L<sub>Aeq</sub> = </html>");

        labelCurrentMeasurementLeq.setText("--,- dB");

        jLabel7.setText(bundle.getString("CURSOR:")); // NOI18N

        jLabel9.setText("<html> L<sub>Aeq</sub> = </html>");

        labelValueAtCursor.setText("--,- dB");

        jLabel4.setText(bundle.getString("SELECTION:")); // NOI18N

        jLabel11.setText("<html>\nL<sub>Aeq</sub> =\n</html>");

        labelSelectionLAeqValue.setText("--,- dB");

        jLabel6.setText(bundle.getString("MEASUREMENT RECORD:")); // NOI18N

        labelRecId.setText("# ----- / ------");

        javax.swing.GroupLayout panelSideInfoLayout = new javax.swing.GroupLayout(panelSideInfo);
        panelSideInfo.setLayout(panelSideInfoLayout);
        panelSideInfoLayout.setHorizontalGroup(
            panelSideInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSideInfoLayout.createSequentialGroup()
                .addGroup(panelSideInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(panelSideInfoLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelCurrentMeasurementLeq))
                    .addComponent(jLabel7)
                    .addGroup(panelSideInfoLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelValueAtCursor))
                    .addComponent(jLabel4)
                    .addGroup(panelSideInfoLayout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelSelectionLAeqValue))
                    .addComponent(jLabel6)
                    .addComponent(labelRecId))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelSideInfoLayout.setVerticalGroup(
            panelSideInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSideInfoLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSideInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelCurrentMeasurementLeq))
                .addGap(13, 13, 13)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelSideInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelValueAtCursor))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSideInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelSelectionLAeqValue))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelRecId)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Info", panelSideInfo);

        jSplitPane1.setLeftComponent(jTabbedPane1);

        projectManager.setModel(model);
        projectManager.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                projectManagerMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                projectManagerMouseReleased(evt);
            }
        });
        projectManager.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                projectManagerValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(projectManager);

        javax.swing.GroupLayout panelProjectLayout = new javax.swing.GroupLayout(panelProject);
        panelProject.setLayout(panelProjectLayout);
        panelProjectLayout.setHorizontalGroup(
            panelProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        panelProjectLayout.setVerticalGroup(
            panelProjectLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(panelProject);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jSplitPane1)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 491, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE))
        );

        splitPane.setLeftComponent(jPanel1);

        jLabel1.setText(bundle.getString("TIME:")); // NOI18N

        labelTime.setText("--:--:-- ( --.--.--)");

        jLabel3.setText(bundle.getString("ACTIVE MEASUREMENT: ")); // NOI18N

        labelActiveMeasurement.setText("-");

        labelSelectionSize.setText(bundle.getString("SELECTION: (0,0)")); // NOI18N

        javax.swing.GroupLayout panelInfoLayout = new javax.swing.GroupLayout(panelInfo);
        panelInfo.setLayout(panelInfoLayout);
        panelInfoLayout.setHorizontalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelActiveMeasurement, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelTime, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelSelectionSize)
                .addContainerGap(469, Short.MAX_VALUE))
        );
        panelInfoLayout.setVerticalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addComponent(labelActiveMeasurement)
                .addComponent(labelTime)
                .addComponent(labelSelectionSize))
        );

        menuProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/development/Application16.gif"))); // NOI18N
        menuProject.setText(bundle.getString("PROJECT")); // NOI18N
        menuProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuProjectActionPerformed(evt);
            }
        });

        menuPNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/New16.gif"))); // NOI18N
        menuPNew.setText(bundle.getString("NEW")); // NOI18N
        menuPNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPNewActionPerformed(evt);
            }
        });
        menuProject.add(menuPNew);

        menuPOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Open16.gif"))); // NOI18N
        menuPOpen.setText(bundle.getString("OPEN")); // NOI18N
        menuPOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPOpenActionPerformed(evt);
            }
        });
        menuProject.add(menuPOpen);

        menuPSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Save16.gif"))); // NOI18N
        menuPSave.setText(bundle.getString("SAVE")); // NOI18N
        menuPSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPSaveActionPerformed(evt);
            }
        });
        menuProject.add(menuPSave);

        menuPSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/SaveAs16.gif"))); // NOI18N
        menuPSaveAs.setText(bundle.getString("SAVE AS ...")); // NOI18N
        menuPSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPSaveAsActionPerformed(evt);
            }
        });
        menuProject.add(menuPSaveAs);

        menuPProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Information16.gif"))); // NOI18N
        menuPProperties.setText(bundle.getString("PROPERTIES")); // NOI18N
        menuPProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPPropertiesActionPerformed(evt);
            }
        });
        menuProject.add(menuPProperties);

        menuPExportDescription.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/History16.gif"))); // NOI18N
        menuPExportDescription.setText("Export description");
        menuPExportDescription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPExportDescriptionActionPerformed(evt);
            }
        });
        menuProject.add(menuPExportDescription);
        menuProject.add(jSeparator1);

        menuPSetup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Preferences16.gif"))); // NOI18N
        menuPSetup.setText(bundle.getString("SETUP")); // NOI18N
        menuPSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPSetupActionPerformed(evt);
            }
        });
        menuProject.add(menuPSetup);

        menuPQuit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Stop16.gif"))); // NOI18N
        menuPQuit.setText(bundle.getString("QUIT")); // NOI18N
        menuPQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPQuitActionPerformed(evt);
            }
        });
        menuProject.add(menuPQuit);

        menuPExportToXML.setText(bundle.getString("EXPORT TO XML")); // NOI18N
        menuPExportToXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPExportToXMLActionPerformed(evt);
            }
        });
        menuProject.add(menuPExportToXML);

        menuBar.add(menuProject);

        menuDeviceDevices.setText(bundle.getString("DEVICE")); // NOI18N
        menuDeviceDevices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDeviceDevicesActionPerformed(evt);
            }
        });

        jMenuItem1.setText(bundle.getString("DEVICES")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menuDeviceDevices.add(jMenuItem1);

        menuDImport.setText(bundle.getString("IMPORT")); // NOI18N
        menuDImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDImportActionPerformed(evt);
            }
        });
        menuDeviceDevices.add(menuDImport);

        menuBar.add(menuDeviceDevices);

        menuView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Zoom16.gif"))); // NOI18N
        menuView.setText(bundle.getString("VIEW")); // NOI18N

        jMenu5.setText(bundle.getString("ZOOM...")); // NOI18N

        menuZoom100percent.setText(bundle.getString("100%")); // NOI18N
        menuZoom100percent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuZoom100percentActionPerformed(evt);
            }
        });
        jMenu5.add(menuZoom100percent);

        menuZoom200percent.setText("200%");
        menuZoom200percent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuZoom200percentActionPerformed(evt);
            }
        });
        jMenu5.add(menuZoom200percent);

        menuZoom500percent.setText("500%");
        menuZoom500percent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuZoom500percentActionPerformed(evt);
            }
        });
        jMenu5.add(menuZoom500percent);

        menuView.add(jMenu5);

        menuVZoomAll.setText(bundle.getString("ZOOM ALL")); // NOI18N
        menuVZoomAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuVZoomAllActionPerformed(evt);
            }
        });
        menuView.add(menuVZoomAll);

        menuVVerticalScale.setText(bundle.getString("VERTICAL SCALE")); // NOI18N
        menuVVerticalScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuVVerticalScaleActionPerformed(evt);
            }
        });
        menuView.add(menuVVerticalScale);

        menuBar.add(menuView);

        menuEClearSelection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Edit16.gif"))); // NOI18N
        menuEClearSelection.setText(bundle.getString("EDIT")); // NOI18N
        menuEClearSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEClearSelectionActionPerformed(evt);
            }
        });

        jMenu4.setText(bundle.getString("SELECTION MODE")); // NOI18N

        radiogroupSelectionMode.add(menuESmFreeSelection);
        menuESmFreeSelection.setSelected(true);
        menuESmFreeSelection.setText(bundle.getString("FREE SELECTION")); // NOI18N
        menuESmFreeSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuESmFreeSelectionActionPerformed(evt);
            }
        });
        jMenu4.add(menuESmFreeSelection);

        radiogroupSelectionMode.add(menuESm10secSelection);
        menuESm10secSelection.setText(bundle.getString("10 SECONDS SELECTION")); // NOI18N
        menuESm10secSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuESm10secSelectionActionPerformed(evt);
            }
        });
        jMenu4.add(menuESm10secSelection);

        radiogroupSelectionMode.add(menuESm60secSelection);
        menuESm60secSelection.setText(bundle.getString("60 SECONDS SELECTION")); // NOI18N
        menuESm60secSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuESm60secSelectionActionPerformed(evt);
            }
        });
        jMenu4.add(menuESm60secSelection);

        jMenuItem4.setText(bundle.getString("N SECONDS SELECTION")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem4);

        jMenuItem2.setText(bundle.getString("KEYBOARD SELECTION")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem2);

        menuEClearSelection.add(jMenu4);

        jMenuItem6.setText(bundle.getString("CLEAR SELECTION")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        menuEClearSelection.add(jMenuItem6);

        jMenu1.setText(bundle.getString("SELECTION")); // NOI18N

        menuESToBackgroundNoiseLevel.setText(bundle.getString("TO BACKGROUND NOISE LEVEL")); // NOI18N
        menuESToBackgroundNoiseLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuESToBackgroundNoiseLevelActionPerformed(evt);
            }
        });
        jMenu1.add(menuESToBackgroundNoiseLevel);

        menuESNeighbNoiseLevel.setText(bundle.getString("TO NEIGHBOURHOOD NOISE LEVEL")); // NOI18N
        menuESNeighbNoiseLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuESNeighbNoiseLevelActionPerformed(evt);
            }
        });
        jMenu1.add(menuESNeighbNoiseLevel);

        menuESCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Copy16.gif"))); // NOI18N
        menuESCopy.setText(bundle.getString("COPY TO BUFFER")); // NOI18N
        menuESCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuESCopyActionPerformed(evt);
            }
        });
        jMenu1.add(menuESCopy);

        menuESCut.setText(bundle.getString("CUT")); // NOI18N
        menuESCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuESCutActionPerformed(evt);
            }
        });
        jMenu1.add(menuESCut);

        menuESDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Delete16.gif"))); // NOI18N
        menuESDelete.setText(bundle.getString("DELETE")); // NOI18N
        menuESDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuESDeleteActionPerformed(evt);
            }
        });
        jMenu1.add(menuESDelete);

        menuESEditValues.setText(bundle.getString("EDIT VALUES")); // NOI18N
        menuESEditValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuESEditValuesActionPerformed(evt);
            }
        });
        jMenu1.add(menuESEditValues);

        menuEClearSelection.add(jMenu1);

        menuESPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Paste16.gif"))); // NOI18N
        menuESPaste.setText(bundle.getString("PASTE BUFFER")); // NOI18N
        menuESPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuESPasteActionPerformed(evt);
            }
        });
        menuEClearSelection.add(menuESPaste);

        menuESplit.setText(bundle.getString("SPLIT")); // NOI18N
        menuESplit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuESplitActionPerformed(evt);
            }
        });
        menuEClearSelection.add(menuESplit);

        menuBar.add(menuEClearSelection);

        menuMeasurement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/NewMeasurement16.gif"))); // NOI18N
        menuMeasurement.setText(bundle.getString("MEASUREMENT")); // NOI18N
        menuMeasurement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMeasurementActionPerformed(evt);
            }
        });

        jMenu7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Import16.gif"))); // NOI18N
        jMenu7.setText(bundle.getString("IMPORT")); // NOI18N

        menuMImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Import16.gif"))); // NOI18N
        menuMImport.setText(bundle.getString("ANY TEXT FILE ...")); // NOI18N
        menuMImport.setToolTipText(bundle.getString("IMPORT DATA FORM TEX FILES.")); // NOI18N
        menuMImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMImportActionPerformed(evt);
            }
        });
        jMenu7.add(menuMImport);

        menuMIBK2238sprformat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Import16.gif"))); // NOI18N
        menuMIBK2238sprformat.setText(bundle.getString("B&K 2238 TEXT FORMAT")); // NOI18N
        menuMIBK2238sprformat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMIBK2238sprformatActionPerformed(evt);
            }
        });
        jMenu7.add(menuMIBK2238sprformat);

        menuMIBK2236sprformat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Import16.gif"))); // NOI18N
        menuMIBK2236sprformat.setText(bundle.getString("B&K 2236 TEXT FORMAT")); // NOI18N
        menuMIBK2236sprformat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMIBK2236sprformatActionPerformed(evt);
            }
        });
        jMenu7.add(menuMIBK2236sprformat);

        menuMIBK2250text.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Import16.gif"))); // NOI18N
        menuMIBK2250text.setText(bundle.getString("2 COLUMNS TABLE")); // NOI18N
        menuMIBK2250text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMIBK2250textActionPerformed(evt);
            }
        });
        jMenu7.add(menuMIBK2250text);

        menuMICsVImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Import16.gif"))); // NOI18N
        menuMICsVImport.setText("CSV file");
        menuMICsVImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMICsVImportActionPerformed(evt);
            }
        });
        jMenu7.add(menuMICsVImport);

        menuMeasurement.add(jMenu7);

        menuMExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Export16.gif"))); // NOI18N
        menuMExport.setText(bundle.getString("EXPORT")); // NOI18N
        menuMExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMExportActionPerformed(evt);
            }
        });
        menuMeasurement.add(menuMExport);

        menuMInfo.setText(bundle.getString("PROPERTIES")); // NOI18N
        menuMInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMInfoActionPerformed(evt);
            }
        });
        menuMeasurement.add(menuMInfo);

        menuMTrim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Trim_measurement.gif"))); // NOI18N
        menuMTrim.setText(bundle.getString("TRIM")); // NOI18N
        menuMTrim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMTrimActionPerformed(evt);
            }
        });
        menuMeasurement.add(menuMTrim);

        menuMTimeAdjust.setText(bundle.getString("TIME ADJUSTMENT")); // NOI18N
        menuMTimeAdjust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMTimeAdjustActionPerformed(evt);
            }
        });
        menuMeasurement.add(menuMTimeAdjust);

        menuMTable.setText(bundle.getString("TABLE")); // NOI18N
        menuMTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMTableActionPerformed(evt);
            }
        });
        menuMeasurement.add(menuMTable);

        menuMCreate.setText("Create");
        menuMCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMCreateActionPerformed(evt);
            }
        });
        menuMeasurement.add(menuMCreate);

        menuBar.add(menuMeasurement);

        menuEvents.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Bookmarks16.gif"))); // NOI18N
        menuEvents.setText(bundle.getString("EVENTS")); // NOI18N
        menuEvents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEventsActionPerformed(evt);
            }
        });

        menuECreate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Bookmarks16.gif"))); // NOI18N
        menuECreate.setText(bundle.getString("NEW EVENT")); // NOI18N
        menuECreate.setToolTipText(bundle.getString("CREATE NEW EVENT BASED ON CURRENT SELECTION.")); // NOI18N
        menuECreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuECreateActionPerformed(evt);
            }
        });
        menuEvents.add(menuECreate);

        menuEDelete.setText(bundle.getString("DELETE")); // NOI18N

        menuEDAllEvents.setText(bundle.getString("ALL EVENTS")); // NOI18N
        menuEDAllEvents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEDAllEventsActionPerformed(evt);
            }
        });
        menuEDelete.add(menuEDAllEvents);

        menuEDEvent.setText(bundle.getString("EVENT ...")); // NOI18N
        menuEDEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEDEventActionPerformed(evt);
            }
        });
        menuEDelete.add(menuEDEvent);

        menuEvents.add(menuEDelete);

        menuEEventsTable.setText(bundle.getString("EVENTS TABLE")); // NOI18N
        menuEEventsTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEEventsTableActionPerformed1(evt);
            }
        });
        menuEvents.add(menuEEventsTable);

        menuEImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Import16.gif"))); // NOI18N
        menuEImport.setText(bundle.getString("IMPORT")); // NOI18N
        menuEImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEImportActionPerformed(evt);
            }
        });
        menuEvents.add(menuEImport);

        menuEExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Export16.gif"))); // NOI18N
        menuEExport.setText(bundle.getString("EXPORT")); // NOI18N
        menuEExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEExportActionPerformed(evt);
            }
        });
        menuEvents.add(menuEExport);

        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/EventTypes16.gif"))); // NOI18N
        jMenuItem3.setText(bundle.getString("EVENT TYPES")); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        menuEvents.add(jMenuItem3);

        menuBar.add(menuEvents);

        menuModels.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/development/Jar16.gif"))); // NOI18N
        menuModels.setText(bundle.getString("MODELS")); // NOI18N

        jMenuItem5.setText(bundle.getString("MODELS MANAGER")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        menuModels.add(jMenuItem5);

        jMenu6.setText(bundle.getString("NEW MODEL")); // NOI18N

        menuMN_RoadSampling.setText(bundle.getString("ROAD SAMPLING")); // NOI18N
        menuMN_RoadSampling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMN_RoadSamplingActionPerformed(evt);
            }
        });
        jMenu6.add(menuMN_RoadSampling);

        menuMSingleEvents.setText(bundle.getString("SINGLE EVENTS")); // NOI18N
        menuMSingleEvents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMSingleEventsActionPerformed(evt);
            }
        });
        jMenu6.add(menuMSingleEvents);

        menuMNSimpleEvBasedMeasurement.setText(bundle.getString("SIMPLE EVENTS BASED MEASUREMENT")); // NOI18N
        menuMNSimpleEvBasedMeasurement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMNSimpleEvBasedMeasurementActionPerformed(evt);
            }
        });
        jMenu6.add(menuMNSimpleEvBasedMeasurement);

        menuMNSimpleContinousMeasurement.setText(bundle.getString("SIMPLE MEASUREMENT")); // NOI18N
        menuMNSimpleContinousMeasurement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMNSimpleContinousMeasurementActionPerformed(evt);
            }
        });
        jMenu6.add(menuMNSimpleContinousMeasurement);

        jMenu8.setText(bundle.getString("OCCUPATIONAL NOISE")); // NOI18N
        jMenu8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu8ActionPerformed(evt);
            }
        });

        menuMNOPN9123.setText(bundle.getString("PN-N-01307:1994 (OCCUPATIONAL NOISE)")); // NOI18N
        menuMNOPN9123.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMNOPN9123ActionPerformed(evt);
            }
        });
        jMenu8.add(menuMNOPN9123);

        jMenu6.add(jMenu8);

        menuMMSoundPwrLvl.setText(bundle.getString("SOUND POWER LEVEL")); // NOI18N
        menuMMSoundPwrLvl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMMSoundPwrLvlActionPerformed(evt);
            }
        });
        jMenu6.add(menuMMSoundPwrLvl);

        menuMNInSituNoiseWall.setText(bundle.getString("IN-SITU NOISE WALL ATTENUATION")); // NOI18N
        menuMNInSituNoiseWall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMNInSituNoiseWallActionPerformed(evt);
            }
        });
        jMenu6.add(menuMNInSituNoiseWall);

        jMenuItem7.setText("Industrial noise");
        jMenu6.add(jMenuItem7);

        menuModels.add(jMenu6);

        menuBar.add(menuModels);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/development/War16.gif"))); // NOI18N
        jMenu2.setText(bundle.getString("PRESENTATIONS")); // NOI18N

        menuRManagePresentations.setText(bundle.getString("PRESENTATION MANAGER")); // NOI18N
        menuRManagePresentations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRManagePresentationsActionPerformed(evt);
            }
        });
        menuRManagePresentations.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
                menuRManagePresentationsAncestorMoved(evt);
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jMenu2.add(menuRManagePresentations);

        menuPCreate.setText(bundle.getString("CREATE")); // NOI18N
        menuPCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPCreateActionPerformed(evt);
            }
        });
        jMenu2.add(menuPCreate);

        menuBar.add(jMenu2);

        menuHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Help16.gif"))); // NOI18N
        menuHelp.setText(bundle.getString("HELP")); // NOI18N
        menuHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuHelpActionPerformed(evt);
            }
        });

        menuHHelp.addActionListener(new CSH.DisplayHelpFromSource( hb ));
        menuHHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Help16.gif"))); // NOI18N
        menuHHelp.setText(bundle.getString("HELP")); // NOI18N
        menuHHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuHHelpActionPerformed(evt);
            }
        });
        menuHelp.add(menuHHelp);

        menuHOnlinehelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/toolbarButtonGraphics/general/Search16.gif"))); // NOI18N
        menuHOnlinehelp.setText(bundle.getString("ONLINE HELP")); // NOI18N
        menuHOnlinehelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuHOnlinehelpActionPerformed(evt);
            }
        });
        menuHelp.add(menuHOnlinehelp);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE)
                    .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (!this.nmmProject.isSaved()) {
            int showConfirmDialog = JOptionPane.showConfirmDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("YOUR PROJECT HASN'T BEEN SAVED. QUIT ANYWAY?"),java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("QUIT"), JOptionPane.YES_NO_OPTION);
            if (showConfirmDialog==JOptionPane.OK_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    public Image getIcon() {
        return this.nmmImg;
    }
    
    private void menuPQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPQuitActionPerformed
        this.nmmSetup.saveNMMConfigurationToFile();
        this.formWindowClosing(null);
    }//GEN-LAST:event_menuPQuitActionPerformed

    private void menuPSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPSetupActionPerformed
        NMMSetup tmpNmmSetup = new NMMSetup(null, true, this.nmmSetup, this.projectCurrentFile);
        tmpNmmSetup.setLocationRelativeTo(null);
        tmpNmmSetup.setVisible(true);
    }//GEN-LAST:event_menuPSetupActionPerformed

    private void menuMImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMImportActionPerformed
        DialogTexFileCutter fcd = new DialogTexFileCutter(null , true, 
                this.nmmSetup.getProjectPath(), nmmProject, this.localeGlobal);
        fcd.pack();
        fcd.setLocationRelativeTo(null);
        fcd.setVisible(true);
    }//GEN-LAST:event_menuMImportActionPerformed

    private void menuMeasurementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMeasurementActionPerformed

    }//GEN-LAST:event_menuMeasurementActionPerformed

    private void updateInfoPanel(int cursorLocationX) {
        long ct=0;

        if (this.nmmProject.isDravable()) {
            // time in coursor location
            ct = this.nmmMainChart.getXCoordTime(cursorLocationX);
            NMMMeasurement m = this.nmmProject.getCurrentMeasurement();
            // if coursor within active measurement time range then get
            // record value, otherwise set label to "-"
            if (ct>=m.getMeasurementBeginTime() && ct<=m.getMeasurementEndTime()) {
            double ctv = this.nmmProject.getCurrentMeasurement().getRecordValue(
                    ct, RecordValueType.LAeq);
            this.labelTime.setText(this.nmmMainChart.getXCoordStringTime(
                    cursorLocationX));
            this.labelValueAtCursor.setText(NMMToolbox.formatDouble(ctv,
                    this.nmmSetup.getProperty("NMM_SETUP_INTERFACE_NUMBERFORMAT", null)));
            this.labelRecId.setText("# "+
                    String.valueOf(this.nmmProject.getCurrentMeasurement().
                    getRecordIndex(ct)+1)+" / "+
                    String.valueOf(this.nmmProject.getCurrentMeasurement().
                    getMeasurementLength()));
            this.labelCurrentMeasurementLeq.setText(NMMToolbox.formatDouble(m.getPeriodLeq(TimePeriods.ALL))+" dB");
            } else {
                this.labelValueAtCursor.setText("--,- dB"); //NOI18N
                this.labelRecId.setText("# ------ / ------"); //NOI18N
                this.labelCurrentMeasurementLeq.setText("--,- dB"); //NOI18N
            }
        } else {
            this.labelTime.setText("-"); //NOI18N
        }
    }

    private void nmmMainChartMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nmmMainChartMouseMoved

        this.updateInfoPanel(evt.getX());
        int len=0;
        long ct = this.nmmMainChart.getXCoordTime(evt.getX());


        // split mode is active
        if (this.mode==this.MODE_SPLIT_MEASUREMENT) {
            Graphics g = this.nmmMainChart.getGraphics();
            
            g.setXORMode(Color.GREEN);
            if (this.mouseMemX!=-1) {
                g.drawLine(mouseMemX, this.nmmMainChart.getViewportTopMargin(), 
                        mouseMemX, this.nmmMainChart.getViewportHeight()-this.nmmMainChart.getViewportBottomMargin());            
            }
            this.mouseMemX=evt.getX();
            this.mouseMemY=evt.getY();            
            g.drawLine( evt.getX(), this.nmmMainChart.getViewportTopMargin(), 
                    evt.getX(), this.nmmMainChart.getViewportHeight()-this.nmmMainChart.getViewportBottomMargin());                         
        }
        
        // measurement time adjustment mode is active
        if (this.mode==this.MODE_MTA_STARTED) {
            Graphics g = this.nmmMainChart.getGraphics();
            g.setXORMode(Color.GREEN);

            g.drawLine(this.mouseStartMemX, this.mouseStartMemY, mouseMemX, mouseMemY);
            g.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("TIME SHIFT:"), this.mouseMemX, this.mouseMemY-10);
            this.mouseMemX=evt.getX();
            this.mouseMemY=evt.getY();
            // TODO: A co to jest?! Niedokończone !
            /* String laeq=String.valueOf(
                    NMMToolbox.formatDouble(this.nmmProject.getCurrentMeasurement().
                    getLeq((long)(ct-0.5*len), (long)(ct+0.5*len))));
                    */
            g.drawLine(this.mouseStartMemX, this.mouseStartMemY, evt.getX(), evt.getY());
            g.drawString(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("TIME SHIFT:"), evt.getX(), evt.getY()-10);
        }

        // selection of 10s and 60s span started
        this.checkWhatToDoSelectionModeStarted(evt);
        
        
    }//GEN-LAST:event_nmmMainChartMouseMoved

    private void checkWhatToDoSelectionModeStarted(java.awt.event.MouseEvent evt) {
        
        int len=0;
        long ct = this.nmmMainChart.getXCoordTime(evt.getX());
        
        if ((this.mode==this.MODE_SELECTION10_STARTED) || (this.mode==this.MODE_SELECTION60_STARTED)
                || this.mode==this.MODE_SELECTIONN_STARTED) {
            switch (this.mode) {
                case 4: len=10000; break;
                case 5: len=60000; break;
                case 8: len = this.selectionWidth; break;
            }
            markerWidth=(int)(0.5*this.nmmMainChart.getPixNumberOnDataRecord()*
                    len/(this.nmmProject.getProjectTimeResolution()));            
            Graphics g = this.nmmMainChart.getGraphics();
            g.setXORMode(Color.GREEN);
            
            g.drawLine(this.mouseMemX-markerWidth, this.mouseMemY, this.mouseMemX+markerWidth, this.mouseMemY);
            g.drawString(this.memOldLaeq+" dB", this.mouseMemX, this.mouseMemY-10);
            this.mouseMemX=evt.getX();
            this.mouseMemY=evt.getY();
            String laeq=String.valueOf(
                    NMMToolbox.formatDouble(this.nmmProject.getCurrentMeasurement().
                    getLeq((long)(ct-0.5*len), (long)(ct+0.5*len))));
            this.memOldLaeq=laeq;
            g.drawLine(evt.getX()-markerWidth, evt.getY(), evt.getX()+markerWidth, evt.getY());
            g.drawString(laeq+" dB", evt.getX(), evt.getY()-10);
        }
    }
    
    private boolean saveProject(File file) {

        boolean savedSuccesfully=false;
        
        this.nmmProject.deleteObservers();
                
        if (file!=null) {
            try {
                
                int dotPosition = file.getAbsolutePath().lastIndexOf(".");
                System.out.println("Rozszerzenie pliku:" +file.getAbsolutePath().substring(dotPosition+1));                
                if (file.getAbsolutePath().substring(dotPosition+1).equals("nmmx")) {
                    //serialization do XML file (nmmx)
                    System.out.println("Creating nmmx File !");
                    XMLEncoder e = new XMLEncoder(
                    new BufferedOutputStream(
                    new FileOutputStream("c:\\TestProjektu.nmmx")));                
                    e.writeObject(this.nmmProject);                
                e.close();                    
                } else {                                                                
                    //serialization to binary file
                    FileOutputStream fos = new FileOutputStream(file);
                    GZIPOutputStream gz = new GZIPOutputStream(fos);
                    ObjectOutputStream oos = new ObjectOutputStream(gz);
                    oos.writeObject(this.nmmProject);
                    oos.flush();
                    oos.close();
                    System.out.println("Serializacja powiodła się :-)");
                    this.nmmProject.setSaved(true);
                    savedSuccesfully=true;
                }
            } catch(IOException e) {
                System.out.println("Serialization unsuccessfull :"+e.toString());
                savedSuccesfully=false;
                JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CANNOT SAVE FILE TO CHOOSEN DESTINATION!"), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("ERROR!"), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            savedSuccesfully=false;
        }
        
        
        return savedSuccesfully;
    }

    private void menuPSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPSaveAsActionPerformed

        this.nmmProject.deleteObservers();
        
        fileChooser.setCurrentDirectory(new File(this.nmmSetup.getProjectPath()));
        int returnVal = fileChooser.showSaveDialog(this);
        boolean permission=false;

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String _n=file.getPath();
            if (_n.substring(_n.length()-5).equals("nmm"+this.version)) {
                
            } else {
                _n=_n+".nmm"+this.version;
                file=new File(_n);
            }
            if (file.exists()) {
                if (JOptionPane.showConfirmDialog(this, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("DO YOU WANT TO OVERWRTE SELECTED FILE?"),java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("SELCTED FILE EXISTS!"),
                        JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                    permission=true;
                }
            } else {
                permission=true;
            }
            if (permission){
                boolean s=this.saveProject(file);
                this.projectCurrentFile=file;
                this.setFrameTitle(file);
            }
        }
        
    }//GEN-LAST:event_menuPSaveAsActionPerformed

    private void setFrameTitle(File file) {
        String saved="*";
        if (this.nmmProject.isSaved()) {
            saved="";
        }
        this.setTitle("NMM 2014 (c) Ekoprojekt ("+file.getName()+saved+")");
    }
    
    private void menuPOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPOpenActionPerformed

        System.out.println("Rozpoczynam deserializacje projektu ...");
        fileChooser.setCurrentDirectory(new File(this.nmmSetup.getProjectPath()));
        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                int dotPosition = file.getAbsolutePath().lastIndexOf(".");
                System.out.println("Rozszerzenie pliku:" +file.getAbsolutePath().substring(dotPosition+1));                
                if (file.getAbsolutePath().substring(dotPosition+1).equals("nmmx")) {
                    //deserialization from XML file (nmmx)
                    System.out.println("Reading form XML file (nmmx).");
                    FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    XMLDecoder xmlDecoder = new XMLDecoder(bis);
                    NMMProject mb = (NMMProject) xmlDecoder.readObject();
                    System.out.println(mb.getProjectTitle());
                    System.out.println(mb.getProjectAuthor());
                    this.nmmProject=mb;                                        
                } else {
                    this.deserializeProject(file);
                    this.nmmSetup.setProperty("NMM_SETUP_PREVIOUS_PROJECT", this.projectCurrentFile.toString());
                }                
            } catch(Exception e) {
                JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("INVALID FILE FORMAT."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM ERROR"), JOptionPane.ERROR_MESSAGE);
                System.out.println(e.toString());
            }            
        }        
        this.nmmProject.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this.nmmProject));
        
    }//GEN-LAST:event_menuPOpenActionPerformed

    private void timeSelectorAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_timeSelectorAdjustmentValueChanged

        //pobieramy nową wartość z paska czasu
        int newRecordIndex=0;
        newRecordIndex=evt.getValue();
        //wartośc tę konwertujemy na godzinę która będzie ustawiona jako początkowa
        //godzina viewportu
        long newStartTime = this.nmmMainChart.getProjectWideRecordTime(newRecordIndex);
        long newEndTime = newStartTime+this.nmmMainChart.getNumberOfRecordsInViewport()*this.nmmProject.getProjectTimeResolution();
        this.nmmMainChart.setViewPortTimeRange(newStartTime,newEndTime);
    }//GEN-LAST:event_timeSelectorAdjustmentValueChanged

    private void menuVZoomAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuVZoomAllActionPerformed
              
        this.nmmProject.updateProjectTimeRanges();
        this.nmmMainChart.setViewPortFullTimeRange();
    }//GEN-LAST:event_menuVZoomAllActionPerformed

    private void nmmMainChartMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nmmMainChartMousePressed

        //if thereis no measurement in the project, then do not respond to any
        //mouse clicks
        if (!this.nmmProject.isDravable()) {
            return;
        }

        //jeżeli dotychczas nic nie było robione, to przechodzimy w tryb
        //selecji częsci wykresu
        if (this.mode==this.MODE_NULL) {
            this.mode=this.MODE_SELECTION_STARTED;
            this.mouseOldMemX=-1;
        }

        
        //jeżeli byliśmy w trybie selekcji częsci wykresu, to kończymy ten
        //tryb i przemieszzamy aktywny pomiar w czasie
        if (this.mode==this.MODE_MTA_STARTED) {
            this.mode=this.MODE_NULL;
            long tshift=(this.nmmMainChart.getXCoordTime(evt.getX())-
                    this.nmmMainChart.getXCoordTime(this.mouseStartMemX));
            System.out.println(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("PRZEMIESZCZAM POCZĄTEK POMIARU O ")+tshift/1000+" sekund");
            long ct=this.nmmProject.getCurrentMeasurement().getMeasurementBeginTime();
            this.nmmProject.getCurrentMeasurement().setMeasurementBeginTime(ct+tshift);            
            this.nmmProject.setSaved(false);            
        }

        if (this.mode==this.MODE_MTA_INITIATED) {
            this.mode=this.MODE_MTA_STARTED;
        }

        if (this.mode==this.MODE_ZOOM_INITIATED) {
            this.mode=this.MODE_ZOOM_STARTED;
        }
        this.mouseStartMemX=evt.getX();
        this.mouseStartMemY=evt.getY();
    }//GEN-LAST:event_nmmMainChartMousePressed

    private void nmmMainChartMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nmmMainChartMouseDragged

        this.updateInfoPanel(evt.getX());
        if ((this.mode==this.MODE_ZOOM_STARTED) ||
                (this.mode==this.MODE_SELECTION_STARTED)) {
            Graphics g=this.nmmMainChart.getGraphics();
                            g.setXORMode(Color.RED);
            int width = this.mouseOldMemX-this.mouseStartMemX;
            int height = this.mouseOldMemY-this.mouseStartMemY;
            this.labelSelectionSize.setText(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("SELECTION: (")+width+","+height+")");
            g.drawRect(this.mouseStartMemX, this.mouseStartMemY, width, height);
            
            width = evt.getX()-this.mouseStartMemX;
            height = evt.getY()-this.mouseStartMemY;
            if ((width<=0) || (height<=0)) {
            } else {
                g.drawRect(this.mouseStartMemX, this.mouseStartMemY, width, height);
            }
            
            //rysowanie parametrów zaznaczenia (jeżeli wymagane) LMin, LMax 
            //LStart, LEnd, oraz kryterium poprawnosci SEL

            String value="true";
            boolean result;
             
            //nadrysowywanie(mazanie) poprzednich opisów w oparciu o 
            //mouseOldMemX i mouseOldMemY                        
            
            //pierwsze mazie następuje dopiero wtedy kiedy było pierwsze ryso-
            //wanie. Tylko po pierwszym rysowaniu mouseOldMemX jest większe do -1
            if (this.mouseOldMemX>=0) {
                //mazanie wartości na poczatku wybieranego przedziału                               
                
                result = value.equalsIgnoreCase(this.nmmSetup.
                    getProperty("NMM_SETUP_FREE_SELECTION_DISPLAY_LStart", 
                    "true"));
                if (result) {
                    NMMToolbox.debugMessage("Rysowanie - X wartości="+this.mouseStartMemX,this.DEBUG);
                    g.drawString("LStart= "+
                            NMMToolbox.formatDouble(this.nmmProject.
                                getCurrentMeasurement().
                                getRecordValue(this.nmmMainChart.
                                getXCoordTime(this.mouseStartMemX), RecordValueType.LAeq)),                         
                            this.mouseStartMemX, 
                            this.mouseStartMemY);
                }               
                
                //mazanie wartości na końcu wybieranego przedziału
                result = value.equalsIgnoreCase(this.nmmSetup.getProperty("NMM_SETUP_FREE_SELECTION_DISPLAY_LEnd", "true"));
                if (result) {
                    g.drawString("LEnd= "+
                            NMMToolbox.formatDouble(this.nmmProject.getCurrentMeasurement().getRecordValue(this.nmmMainChart.getXCoordTime(mouseOldMemX), RecordValueType.LAeq)),
                            this.mouseOldMemX, 
                            this.mouseStartMemY);
                }
                //Wartośc maksymalna 
                result = value.equalsIgnoreCase(this.nmmSetup.getProperty("NMM_SETUP_FREE_SELECTION_DISPLAY_LMax", "true"));
                if (result) {
                    g.drawString("LMax= "+
                            NMMToolbox.formatDouble(
                            this.nmmProject.getCurrentMeasurement().
                                getMaxRecordValue(
                                this.nmmMainChart.getXCoordTime(this.mouseStartMemX),
                                this.nmmMainChart.getXCoordTime(this.mouseOldMemX))),
                            this.mouseOldMemX, 
                            this.mouseStartMemY+20);                
                }    
            }                        
                        
            //rysowanie nowych opisów
            //Wartośc na początku zaznaczonego przedziału
            result = value.equalsIgnoreCase(this.nmmSetup.
                    getProperty("NMM_SETUP_FREE_SELECTION_DISPLAY_LStart", 
                    "true"));
            if (result) {
                NMMToolbox.debugMessage("Rysowanie - X wartości="+this.mouseStartMemX,this.DEBUG);
                g.drawString("LStart= "+
                        NMMToolbox.formatDouble(this.nmmProject.
                            getCurrentMeasurement().
                            getRecordValue(this.nmmMainChart.
                            getXCoordTime(this.mouseStartMemX), RecordValueType.LAeq)),                         
                        this.mouseStartMemX, 
                        this.mouseStartMemY);
            }
            
            //Wartośc na końcu zaznaczonego przedziału
            result = value.equalsIgnoreCase(this.nmmSetup.getProperty("NMM_SETUP_FREE_SELECTION_DISPLAY_LEnd", "true"));
            if (result) {
                g.drawString("LEnd= "+
                        NMMToolbox.formatDouble(this.nmmProject.getCurrentMeasurement().
                            getRecordValue(this.nmmMainChart.getXCoordTime(evt.getX()), 
                            RecordValueType.LAeq)),                         
                        evt.getX(), 
                        this.mouseStartMemY);
            }
            
            //Wartośc maksymalna 
            result = value.equalsIgnoreCase(this.nmmSetup.getProperty("NMM_SETUP_FREE_SELECTION_DISPLAY_LMax", "true"));
            if (result) {
                g.drawString("LMax= "+
                        NMMToolbox.formatDouble(this.nmmProject.
                            getCurrentMeasurement().
                            getMaxRecordValue(
                            this.nmmMainChart.getXCoordTime(this.mouseStartMemX),
                            this.nmmMainChart.getXCoordTime(evt.getX()))),
                        evt.getX(), 
                        this.mouseStartMemY+20);
            }
            
            //zapamietanie pozycji myszy jako starej (posłuzy do wymazywania 
            //boxa i cyfr w następnym przebiegu przez funkcję
            this.mouseOldMemX=evt.getX();
            this.mouseOldMemY=evt.getY();                        
         } else {
            //nic nie musi robić, mogło by być bez else;
         }
    }//GEN-LAST:event_nmmMainChartMouseDragged

    private void nmmMainChartMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nmmMainChartMouseReleased

        //if thereis no measurement in the project, then do not respond to any
        //mouse clicks
        if (!this.nmmProject.isDravable()) {
            return;
        }
        
        //---- LAST (third) BUTTON WAS PRESSED
        if ( (evt.getButton()==MouseEvent.BUTTON3)) {
            System.out.println("Menu kontekstowe głównego wykresu ...");
            this.popupMainChart.show(evt.getComponent(), evt.getX(), evt.getY());
        }
        
        //---- FIRST BUTTON WAS PRESSED
        //if zooming was started and left button is pressed then ...
        if ((this.mode==this.MODE_ZOOM_STARTED) && (evt.getButton()==MouseEvent.BUTTON1)) {
           this.mode=this.MODE_NULL;
           long st = this.nmmMainChart.getXCoordTime(mouseStartMemX)
                   -(this.nmmMainChart.getXCoordTime(mouseStartMemX)%this.nmmProject.getProjectTimeResolution());
           long en = this.nmmMainChart.getXCoordTime(evt.getX())+
                   (this.nmmProject.getProjectTimeResolution()
                   -(this.nmmMainChart.getXCoordTime(evt.getX())%this.nmmProject.getProjectTimeResolution()));           
           this.mouseOldMemX=0;
           this.mouseOldMemY=0;
           this.nmmMainChart.setViewPortTimeRange(st, en);
           System.out.println("Polecenie powiększenia do zakresu czasowego: "+st+" - "+en);
           this.nmmMainChart.paintComponent(this.nmmMainChart.getGraphics());
        }

        //is selection model 10, 60, or any is started an left (first) button is pressed then 
        if (((this.mode==this.MODE_SELECTION10_STARTED) || (this.mode==this.MODE_SELECTION60_STARTED)
                || (this.mode==this.MODE_SELECTIONN_STARTED)) && (evt.getButton()==MouseEvent.BUTTON1)) {           

           //addTime - połowa długości wybieranego okresu (bedzie liczona 
           //w przód i w tył od położenia kursora.
           long addTime =0;           
           if (this.mode==this.MODE_SELECTION10_STARTED) {
               addTime=500*10;
           }           
           if (this.mode==this.MODE_SELECTION60_STARTED) {
               addTime=500*60;
           }           
           if (this.mode==this.MODE_SELECTIONN_STARTED) {
               addTime=500*this.selectionWidth/1000;
           }           
            
           //ustalamy dokładny czas początku i końca wybranego okresu
           //long en = this.nmmMainChart.getXCoordTime(evt.getX()+this.markerWidth);
           long en = this.nmmMainChart.getXCoordTime(evt.getX())+(addTime-(this.nmmProject.getProjectTimeResolution()));
           //jeżeli wybrano aż poza zakresem czasowym projektu to poprawiamy
           if (en>this.nmmProject.getProjectEndTime()) {
               en=this.nmmProject.getProjectEndTime();
           }
           //long st = this.nmmMainChart.getXCoordTime(evt.getX()-this.markerWidth);
           long st = this.nmmMainChart.getXCoordTime(evt.getX())-addTime;
           //jeżeli wybrano aż poza zakresem czasowym projektu to poprawiamy
           if (st<this.nmmProject.getProjectBeginTime()) {
               st=this.nmmProject.getProjectBeginTime();
           }

           //czas i koniec okresu musimy zaokrąglić do pełnych wartości
           //zgodnie z rozdzielczością milisekundową projektu
           en=en-(en%this.nmmProject.getProjectTimeResolution());
           st=st-(st%this.nmmProject.getProjectTimeResolution());

           //System.out.println("Koniec selekcji:"+this.nmmMainChart.getXCoordStringTime(evt.getX())+
           //        "   początek selekcji: "+this.nmmMainChart.getXCoordStringTime(mouseStartMemX));

           nmmProject.getCurrentSelection().setStart(st);
           nmmProject.getCurrentSelection().setEnd(en);
           //System.out.println("Czyli: od "+st+" do "+en);
           //System.out.println("A w pikselach to: od "+mouseStartMemX+" "+evt.getX());
           this.nmmMainChart.paintComponent(this.nmmMainChart.getGraphics());

           //update info panel
           this.labelSelectionLAeqValue.setText(NMMToolbox.formatDouble(this.nmmProject.
                   getCurrentMeasurement().getLeq(st, en)));
        }

        if ((this.mode==this.MODE_SELECTION_STARTED)  && (evt.getButton()==MouseEvent.BUTTON1)) {
           this.mode=this.MODE_NULL;

           //ustalamy dokładny czas początku i końca wybranego okresu
           long en = this.nmmMainChart.getXCoordTime(evt.getX());
           //jeżeli wybrano aż poza zakresem czasowym projektu to poprawiamy
           if (en>this.nmmProject.getProjectEndTime()) {
               en=this.nmmProject.getProjectEndTime();
           }
           long st = this.nmmMainChart.getXCoordTime(mouseStartMemX);
           //jeżeli wybrano aż poza zakresem czasowym projektu to poprawiamy
           if (st<this.nmmProject.getProjectBeginTime()) {
               st=this.nmmProject.getProjectBeginTime();
           }

           //czas i koniec okresu musimy zaokrąglić do pełnych wartości
           //zgodnie z rozdzielczością milisekundową projektu
           en=en-(en%this.nmmProject.getProjectTimeResolution());
           st=st-(st%this.nmmProject.getProjectTimeResolution());

           //System.out.println("Koniec selekcji:"+this.nmmMainChart.getXCoordStringTime(evt.getX())+
           //        "   początek selekcji: "+this.nmmMainChart.getXCoordStringTime(mouseStartMemX));
           this.mouseOldMemX=0;
           this.mouseOldMemY=0;

           nmmProject.getCurrentSelection().setStart(st);
           nmmProject.getCurrentSelection().setEnd(en);
           //System.out.println("Czyli: od "+st+" do "+en);
           //System.out.println("A w pikselach to: od "+mouseStartMemX+" "+evt.getX());
           this.nmmMainChart.paintComponent(this.nmmMainChart.getGraphics());
           
           //update info panel
           if ((st!=en) & (this.nmmProject.getCurrentMeasurement().
                   isWithinMeasurement(this.nmmProject.
                   getCurrentSelection()))) {
                this.labelSelectionLAeqValue.setText(NMMToolbox.formatDouble(this.nmmProject.
                   getCurrentMeasurement().getLeq(st, en), this.nmmSetup.getProperty("NMM_SETUP_INTERFACE_NUMBERFORMAT", null)));
           } else {
               this.labelSelectionLAeqValue.setText("--,- dB");
           }
        }

        if (this.mode==this.MODE_ZOOM_OUT_INITIATED) {
            this.mode=this.MODE_NULL;
            this.nmmMainChart.zoomOut();            
        }
        
        if ((this.mode==this.MODE_SPLIT_MEASUREMENT)  && (evt.getButton()==MouseEvent.BUTTON1)) {
            this.mode=this.MODE_NULL;
            long en = this.nmmMainChart.getXCoordTime(evt.getX());
            this.nmmProject.splitMeasurement(en);
            
            /* TODO: dalej kontynuowac procedurę rozcięcia pomiaru na dwa pomiary
             */            
        }
        
        this.mouseMemX=-1;
        this.labelSelectionSize.setText(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("SELECTION: (0,0)"));
    }//GEN-LAST:event_nmmMainChartMouseReleased

    private void menuEClearSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEClearSelectionActionPerformed
        this.nmmProject.currentSelection.setStart(0);
        this.nmmProject.currentSelection.setEnd(0);
        this.nmmMainChart.paintComponent(this.nmmMainChart.getGraphics());
    }//GEN-LAST:event_menuEClearSelectionActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        this.nmmProject.currentSelection.setEnd(this.nmmProject.currentSelection.getStart());
        this.nmmProject.setSaved(false);
        this.nmmMainChart.paintComponent(this.nmmMainChart.getGraphics());
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void menuMInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMInfoActionPerformed
        if (this.nmmProject.getMeasurementsNumber()>0) {
            NMMMeasurementInfo nmmmi = new NMMMeasurementInfo(null, true, this.nmmProject, this.localeGlobal, this.nmmSetup);
            nmmmi.setLocationRelativeTo(null);
            nmmmi.pack();
            nmmmi.setVisible(true);
            this.nmmMainChart.paintComponent(this.nmmMainChart.getGraphics());
        } else {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS IN PROJECT."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS"), JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_menuMInfoActionPerformed

    /**
     * Check if there is time selecton, and rczyk
     * display event dialog box
     * @param evt
     */
    private void menuECreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuECreateActionPerformed
        //creating new event requires selection of time range
        if (this.nmmProject.getCurrentSelection().isSet()) {
            //if there is a selecton then it is possible to establish new event
            NMMEventDialog ed = new NMMEventDialog(null, true, this.nmmProject, 
                    this.localeGlobal, this.nmmSetup);
            ed.setLocationRelativeTo(null);
            ed.setVisible(true);
        } else {
            //if there is no time range selected then display following information
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NAJPIERW WYBIERZ PRZEDZIAŁ CZASU NA WYKRESIE."),
                    java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("INFORMACJA"), JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_menuECreateActionPerformed

    private void menuEEventsTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEEventsTableActionPerformed

        //show table if there are any events defined in the project, otherwise
        //show warning
        if (this.nmmProject.getEventsNumber()>0) {

            //liczba kolumn w tabeli zależy od ilości pomiarów, plus stałe
            //kolumny
            int colsNumber=this.nmmProject.getMeasurementsNumber()+3;

            //tworzymy nowy model danych tabeli
            NMMEventsTableModel tm = new NMMEventsTableModel(colsNumber, 
                    this.nmmProject,
                    this.nmmProject.getEvent(0).getEventType(),
                    this.localeGlobal);
            
            //definiujemy nagłowki tabeli
            //stałe kolumny
            String[] cols = new String[colsNumber];
            cols[0]=java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("START");
            cols[1]=java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("END");
            cols[2]=java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("EVENT");
            //kolumny dla każdego z pomiarów
            for (int i=0; i<nmmProject.getMeasurementsNumber();i++) {
                cols[3+i]=this.nmmProject.getMeasurement(i).getDescription();
                System.out.println(cols.toString());
            }
            tm.setColumns(cols);

                 
            //tworzymy okno samej tabeli
            NMMEventsTable nmmet = new NMMEventsTable(null, true,
                    this.nmmProject, tm);
            nmmet.setLocationRelativeTo(null);
            nmmet.setVisible(true);            
        } else {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("THERE ARE NO EVENTS IN THE PROJECT!"),java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM MESSAGE"), JOptionPane.WARNING_MESSAGE);
            //System.out.println("W projekcie brak zdefiniowanych zdarzeń!");
        }
    }//GEN-LAST:event_menuEEventsTableActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.mode=this.MODE_ZOOM_INITIATED;
    }//GEN-LAST:event_jButton1ActionPerformed

    private void buttonZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonZoomOutActionPerformed
        this.mode=this.MODE_ZOOM_OUT_INITIATED;
    }//GEN-LAST:event_buttonZoomOutActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        NMMEventDialog ed = new NMMEventDialog(null, true, this.nmmProject,
                this.localeGlobal, this.nmmSetup);
        ed.setLocationRelativeTo(null);
        ed.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void menuMExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMExportActionPerformed
        if (this.nmmProject.isDravable()) {
            NMMMeasurementExportOptions meo=new NMMMeasurementExportOptions(null, true, this.nmmProject, this.nmmSetup);
            meo.setLocationRelativeTo(null);
            meo.pack();
            meo.setVisible(true);    
        } else {
            //if there are no measurements then export is impossible
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("THERE ARE NO DATA IN CURRENT PROJECT."),
                    java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("INFORMATION."), JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_menuMExportActionPerformed

    private void menuESToBackgroundNoiseLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuESToBackgroundNoiseLevelActionPerformed
        
        NMMMeasurement cm;
        
        cm=this.nmmProject.getMeasurement(this.nmmProject.getCurrentMeasurementNumber());
        int startRecord=cm.getRecordIndex(this.nmmProject.
                getCurrentSelection().getStart());
        int endRecord=cm.getRecordIndex(this.nmmProject.
                getCurrentSelection().getEnd());
        for (int i=startRecord; i<=endRecord; i++) {
            cm.getRecord(i).setRecordValue(RecordValueType.LAeq, this.nmmProject.getMeasurement(
                    this.nmmProject.getCurrentMeasurementNumber()).
                    getBackgroundNoiseLevel());
        }
        cm.updateLeq();
        this.nmmProject.setSaved(false);
    }//GEN-LAST:event_menuESToBackgroundNoiseLevelActionPerformed

    private void menuESNeighbNoiseLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuESNeighbNoiseLevelActionPerformed

        NMMMeasurement cm;

        //get refference to current measurement
        cm=this.nmmProject.getMeasurement(this.nmmProject.getCurrentMeasurementNumber());

        //identify first and last record
        int startRecord=cm
                .getRecordIndex(this.nmmProject.getCurrentSelection().getStart());
        int endRecord=cm
                .getRecordIndex(this.nmmProject.getCurrentSelection().getEnd());
        
        //length of measurement in miliseconds that will be replaced by new value
        long shift=(1+(endRecord-startRecord))*this.nmmProject.getProjectTimeResolution();

        long rr=this.nmmProject.getCurrentSelection().getEnd()+shift;
        long rl=this.nmmProject.getCurrentSelection().getStart()+shift;
        long ll=this.nmmProject.getCurrentSelection().getStart()-shift;
        long lr=this.nmmProject.getCurrentSelection().getEnd()-shift;

        if ((rr<=cm.getMeasurementEndTime()) && (ll>=cm.getMeasurementStartTime())) {
            //calculate laeq in the neighbourhood (right side)
            double laeq1 = cm.getLeq(rl, rr);
            //calculate laeq in the neighbourhood (left side)
            double laeq2=cm.getLeq(ll,lr);
            float[] tab = new float[2];
            tab[0]=(float) laeq1;
            tab[1]=(float) laeq2;
            System.out.println("L1 i 2 = "+laeq1+" "+laeq2);
            double newlaeq=NMMNoiseCalculator.SredniaLog(tab);
            //set laeq of selected records tocalculated in previous step
            for (int i=startRecord; i<=endRecord; i++) {
                cm.getRecord(i).setRecordValue(RecordValueType.LAeq, newlaeq);
            }
            //update totalLAeq value in measuremet object
            cm.updateLeq();
            //mark project unsaved
            this.nmmProject.setSaved(false);
        } else {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CANNOT CALCULATE LEQ FOR THIS PERIOD"),java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM MESSAGE."),JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_menuESNeighbNoiseLevelActionPerformed

    /**
     * Shift selected part of the measurement to the back of the measurement
     * If selection starts form the very begining of the measurement then 
     * start time of the measurement changes, and if selection is in the middle
     * then slelected time range is filled with later records.
     * @param evt
     */
    private void menuProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuProjectActionPerformed
            this.nmmProject.saveAs("path");
    }//GEN-LAST:event_menuProjectActionPerformed

    private void menuPNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPNewActionPerformed

        boolean permisionGranted = true;

        if (!this.nmmProject.isSaved()) {
            int showConfirmDialog = JOptionPane.showConfirmDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("YOUR PROJECT HASN'T BEEN SAVED. CREATE NEW ONE?"),java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("QUIT"), JOptionPane.YES_NO_OPTION);
            if (showConfirmDialog!=JOptionPane.OK_OPTION) {
                permisionGranted=false;
            }
        }
        if (permisionGranted) {                        
            this.nmmProject=new NMMProject();
            nmmProject.addProjectChangedListener(this);
            nmmProject.addProjectChangedListener(this.projectManager); 
            nmmProject.addProjectChangedListener(this.nmmMainChart);
            this.nmmMainChart.setNMMProject(nmmProject);
            this.nmmProject.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this.nmmProject));            
        }
        this.projectCurrentFile=new File(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NEW NMM PROJECT.NMM")+this.version);
        
    }//GEN-LAST:event_menuPNewActionPerformed

    private void menuPPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPPropertiesActionPerformed

        NMMProjectProperties nmmpp = new NMMProjectProperties(null,true, this.nmmProject, localeGlobal);
        nmmpp.pack();
        nmmpp.setLocationRelativeTo(null);
        nmmpp.setVisible(true);
        this.nmmprojecttop.setUserObject(this.nmmProject.getProjectTitle());
        this.nmmProject.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this.nmmProject));
    }//GEN-LAST:event_menuPPropertiesActionPerformed

    private void menuRManagePresentationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRManagePresentationsActionPerformed
        
        if (this.nmmProject.getPresentations().getSize()>0) {
            NMMPresentationsManager pm = new NMMPresentationsManager(null, true, this.nmmProject);
            pm.setLocationRelativeTo(null);
            pm.pack();
            pm.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO PRESENTATIONS IN CURRENT PROJECT!"),java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM 2010 * INFORMATION"),JOptionPane.INFORMATION_MESSAGE);
        }        
    }//GEN-LAST:event_menuRManagePresentationsActionPerformed

    private void menuEImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEImportActionPerformed
        NMMEventImport ei = new NMMEventImport(null,true,this.nmmProject, 
                this.nmmSetup, this.localeGlobal);
        ei.setLocationRelativeTo(null);
        ei.pack();
        ei.setVisible(true);
        
    }//GEN-LAST:event_menuEImportActionPerformed

    private void menuESmFreeSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuESmFreeSelectionActionPerformed
        this.mode=this.MODE_NULL;
    }//GEN-LAST:event_menuESmFreeSelectionActionPerformed

    private void menuESm10secSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuESm10secSelectionActionPerformed
        this.mode=this.MODE_SELECTION10_STARTED;
    }//GEN-LAST:event_menuESm10secSelectionActionPerformed

    private void menuESm60secSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuESm60secSelectionActionPerformed
        this.mode=this.MODE_SELECTION60_STARTED;
    }//GEN-LAST:event_menuESm60secSelectionActionPerformed

    private void menuVVerticalScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuVVerticalScaleActionPerformed
        NMMVerticalScale vs = new NMMVerticalScale(null, true, this.nmmMainChart);
        vs.setLocationRelativeTo(null);
        vs.pack();
        vs.setVisible(true);
    }//GEN-LAST:event_menuVVerticalScaleActionPerformed

    private void popupMeasurementDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupMeasurementDeleteActionPerformed
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       this.projectManager.getLastSelectedPathComponent();
        if (node.isLeaf()) {
            if (node.getUserObject() instanceof NMMEvent) {
                NMMEvent nmmEvent=(NMMEvent)node.getUserObject();
                this.nmmProject.deleteEvent(nmmEvent);
            } else if (node.getUserObject() instanceof NMMMeasurement) {
                System.out.println("Kasuje pomiar z projektu ...");
                NMMMeasurement nmmMeas = (NMMMeasurement)node.getUserObject();
                this.nmmProject.deleteMeasurement(nmmMeas);
                this.nmmProject.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this.nmmProject));
                System.out.println("---- **** ---- Liczba nasłuchiwaczy modelu drzewka:"+ this.model.getTreeModelListeners().length);
            }
        }                                       
        
    }//GEN-LAST:event_popupMeasurementDeleteActionPerformed
   
    //wyświetla okno dialogowe pozwalająe na określenie długości czasu trwania 
    //zdarzenia do oznaczenia na wykresie
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        NMMTimePeriodSelectionDialog tpsd = new NMMTimePeriodSelectionDialog(this, true, this.nmmProject, this.localeGlobal);
        tpsd.setLocationRelativeTo(null);
        tpsd.pack();
        tpsd.setVisible(true);        
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void menuMTrimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMTrimActionPerformed


        if (this.nmmProject.getMeasurementsNumber()>0) {
            //trim measurement to full hours - generally this cuts records before
            //first full hour, and after the last full hour
            this.nmmProject.getCurrentMeasurement().trimMeasurement();
            this.nmmProject.setSaved(false);            
        } else {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CANNOT TRIM. NO MEASUREMENTS IN PROJECT."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS"), JOptionPane.INFORMATION_MESSAGE);
        }        
    }//GEN-LAST:event_menuMTrimActionPerformed

    private void menuZoom100percentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuZoom100percentActionPerformed
        this.nmmMainChart.setViewPortPercentRange(100);
    }//GEN-LAST:event_menuZoom100percentActionPerformed

    private void menuZoom200percentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuZoom200percentActionPerformed
        this.nmmMainChart.setViewPortPercentRange(200);
    }//GEN-LAST:event_menuZoom200percentActionPerformed

    private void menuZoom500percentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuZoom500percentActionPerformed
        this.nmmMainChart.setViewPortPercentRange(500);
    }//GEN-LAST:event_menuZoom500percentActionPerformed

    private void menuESDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuESDeleteActionPerformed
        NMMMeasurement ms = this.nmmProject.getCurrentMeasurement();
        NMMEvent cs = this.nmmProject.getCurrentSelection();        
        int indexStart=ms.getRecordIndex(cs.getStart());
        int indexEnd=ms.getRecordIndex(cs.getEnd());
        this.nmmProject.getCurrentMeasurement().deleteRecords(indexStart, indexEnd);
        this.nmmProject.updateProjectTimeRanges();        
        this.nmmProject.setSaved(false);
    }//GEN-LAST:event_menuESDeleteActionPerformed

    private void menuESPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuESPasteActionPerformed
        
        NMMMeasurement cm=this.nmmProject.getCurrentMeasurement();
        Object[] options = {java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("AT THE BEGINING"),
                    java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("AT THE END"),
                    java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CANCEL")};
        int n = JOptionPane.showOptionDialog(null,
            java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("WHERE WOULD YOU LIKE TO PASTE BUFFER?"),
            java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM PASTE BUFFER"),
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,null,options,options[2]);
        switch (n) {
            case 0:                
                cm.pasteRecords(0);
                break;
            case 1:   cm.pasteRecords(cm.getMeasurementLength()); break;
            case 2:   /*do nothing*/ break;
        }
        this.nmmProject.updateProjectTimeRanges();
        this.nmmProject.setSaved(false);        
    }//GEN-LAST:event_menuESPasteActionPerformed

    private void menuESCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuESCopyActionPerformed
        NMMMeasurement ms=this.nmmProject.getCurrentMeasurement();
        NMMEvent cs = this.nmmProject.getCurrentSelection();
        int indexStart=ms.getRecordIndex(cs.getStart());
        int indexEnd=ms.getRecordIndex(cs.getEnd()+this.nmmProject.getProjectTimeResolution());
        this.nmmProject.getCurrentMeasurement().copyRecords(indexStart, indexEnd);
    }//GEN-LAST:event_menuESCopyActionPerformed

    private void menuESCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuESCutActionPerformed
        NMMMeasurement ms=this.nmmProject.getCurrentMeasurement();
        NMMEvent cs = this.nmmProject.getCurrentSelection();
        int indexStart=ms.getRecordIndex(cs.getStart());
        int indexEnd=ms.getRecordIndex(cs.getEnd());
        this.nmmProject.getCurrentMeasurement().cutRecords(indexStart, indexEnd);
        this.nmmProject.updateProjectTimeRanges();
    }//GEN-LAST:event_menuESCutActionPerformed

    private void popupMeasurementsDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupMeasurementsDeleteActionPerformed
         DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       this.projectManager.getLastSelectedPathComponent();
        if (node.isLeaf()) {
            if (node.getUserObject() instanceof NMMEvent) {
                NMMEvent nmmEvent=(NMMEvent)node.getUserObject();
                this.nmmProject.deleteEvent(nmmEvent);
            } 
            this.nmmProject.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this.nmmProject));            
        }            
    }//GEN-LAST:event_popupMeasurementsDeleteActionPerformed

    private void menuMTimeAdjustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMTimeAdjustActionPerformed

        if (this.nmmProject.getMeasurementsNumber()>0) {
            this.mode=this.MODE_MTA_INITIATED;
        } else {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CANNOT START TIME ADJUSTMENTMODE. NO MEASUREMENTS IN PROJECT."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS"), JOptionPane.INFORMATION_MESSAGE);
        }              
    }//GEN-LAST:event_menuMTimeAdjustActionPerformed

    private void popupMasurementsPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupMasurementsPropertiesActionPerformed
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       this.projectManager.getLastSelectedPathComponent();
        if (node.isLeaf()) {
            if (node.getUserObject() instanceof NMMEvent) {
                NMMEvent nmmEvent=(NMMEvent)node.getUserObject();
                NMMEventEditor eed = new NMMEventEditor(this,true,nmmEvent, 
                    this.localeGlobal, this.nmmProject);
                eed.setLocationRelativeTo(null);
                eed.pack();
                eed.setVisible(true);
                eed.dispose();
            }            
        }         
    }//GEN-LAST:event_popupMasurementsPropertiesActionPerformed

    private void menuDeviceDevicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDeviceDevicesActionPerformed
        
    }//GEN-LAST:event_menuDeviceDevicesActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        NMMNoiseMeasurementSetProperties nmsp = 
                new NMMNoiseMeasurementSetProperties(null,true, this.nmmProject);
        nmsp.pack();
        nmsp.setLocationRelativeTo(null);
        nmsp.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void menuESEditValuesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuESEditValuesActionPerformed

        NMMEvent ev;
        ev=this.nmmProject.getCurrentSelection();
        NMMMeasurement ms;
        ms=this.nmmProject.getCurrentMeasurement();
        
        //zaznaczony obszar do edycji musi być w granicach czasowych aktywnego zapisu
        if (ms.isWithinMeasurement(ev)) {
            if (ev.isSet()) {
                int recNumb=(int)(ev.getMilisLength()/this.nmmProject.getProjectTimeResolution());
                DefaultTableModel dtm= new DefaultTableModel(recNumb,1);
                int firstRec = ms.getRecordIndex(ev.getStart());
                for (int i=0; i<recNumb; i++) {                
                    dtm.setValueAt(NMMToolbox.formatDouble(ms.getRecord(firstRec+i).getRecordValue(RecordValueType.LAeq)), i, 0);
                }
                NMMValuesEditor nmmve = new NMMValuesEditor(this, true, dtm, this.nmmProject);
                nmmve.setLocationRelativeTo(null);
                nmmve.pack();
                nmmve.setVisible(true);            
            } else {
                JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CREATE SELECTION FIRST."),java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM INFORMATION"),JOptionPane.INFORMATION_MESSAGE);
            }    
        }                
    }//GEN-LAST:event_menuESEditValuesActionPerformed

    private void menuHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuHelpActionPerformed
        
    }//GEN-LAST:event_menuHelpActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        String newLength=JOptionPane.showInputDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("ENTER PERIOD LENGTH [SECONDS]:"), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("TITLE"),1);
        try {
            int newL=Integer.parseInt(newLength);
            this.selectionWidth=newL*1000;
            this.mode=this.MODE_SELECTIONN_STARTED;
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("BAD NUMBER FORMAT!"),java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM INFORMATION"),JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void menuRManagePresentationsAncestorMoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_menuRManagePresentationsAncestorMoved

    }//GEN-LAST:event_menuRManagePresentationsAncestorMoved

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.nmmProject.updateProjectTimeRanges();
        this.nmmMainChart.setViewPortFullTimeRange();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void menuEExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEExportActionPerformed

        if (this.nmmProject.getEventsNumber()>0) {
            NMMEventsExportOptions meo=new NMMEventsExportOptions(null, true,
                    this.nmmProject, this.nmmSetup, this.localeGlobal);
            meo.setLocationRelativeTo(null);
            meo.pack();
            meo.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("THERE ARE NO EVENTS IN THE PROJECT!"),
                    java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM INFORMATION"), JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_menuEExportActionPerformed

    private void menuEEventsTableActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEEventsTableActionPerformed1
        //show table if there are any events defined in the project, otherwise
        //show warning
        if (this.nmmProject.getEventsNumber()>0) {

            //liczba kolumn w tabeli zaleĹĽy od iloĹ›ci pomiarĂłw, plus staĹ‚e
            //kolumny
            int colsNumber=this.nmmProject.getMeasurementsNumber()+3;

            //tworzymy nowy model danych tabeli
            NMMEventsTableModel tm = new NMMEventsTableModel(colsNumber,
                    this.nmmProject,
                    this.nmmProject.getEvent(0).getEventType(),
                    this.localeGlobal);

            //definiujemy nagĹ‚owki tabeli
            //staĹ‚e kolumny
            String[] cols = new String[colsNumber];
            cols[0]=java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("START");
            cols[1]=java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("END");
            cols[2]=java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("EVENT");
            //kolumny dla kaĹĽdego z pomiarĂłw
            for (int i=0; i<nmmProject.getMeasurementsNumber();i++) {
                cols[3+i]=this.nmmProject.getMeasurement(i).getDescription();
                System.out.println(cols.toString());
            }
            tm.setColumns(cols);


            //tworzymy okno samej tabeli
            NMMEventsTable nmmet = new NMMEventsTable(null, true,
                    this.nmmProject, tm);
            nmmet.setLocationRelativeTo(null);
            nmmet.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("BRAK ZDARZEĹ„ W PROJEKCIE! WPROWADĹĽ JAKIEĹ› ZDARZENIA, ABY WYĹ›WIETLIÄ‡")+"\n"
                    + java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("ZESTAWIENIE ODPOWIADAJÄ…CYM IM POMIERZONYCH WARTOĹ›CI."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("UWAGA!"), JOptionPane.WARNING_MESSAGE);            
        }
    
    }//GEN-LAST:event_menuEEventsTableActionPerformed1

    private void popupMeasurementZoomToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupMeasurementZoomToActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       this.projectManager.getLastSelectedPathComponent();
        if (node.isLeaf()) {
            if (node.getUserObject() instanceof NMMEvent) {
                NMMEvent nmmEvent=(NMMEvent)node.getUserObject();
                nmmMainChart.setViewPortTimeRange(nmmEvent.getStart(), nmmEvent.getEnd());
            }
            if (node.getUserObject() instanceof NMMMeasurement) {
                NMMMeasurement nmmMeasurement=(NMMMeasurement)node.getUserObject();
                long newStart = nmmMeasurement.getMeasurementBeginTime();                
                long newEnd = nmmMeasurement.getMeasurementEndTime();
                long startMargin=newStart-this.nmmProject.getProjectBeginTime();
                long endMargin=this.nmmProject.getProjectEndTime()-newEnd;
                long add;
                add = (long)(nmmMeasurement.getMeasurementLength()*this.nmmProject.getProjectTimeResolution()*0.2);
                if (startMargin>0) {                    
                    if (add<startMargin) {
                        newStart=newStart-add;
                    } //w innym wypadku nie dodajemy marginesu
                }
                if (endMargin>0) {
                    if (add<endMargin) {
                        newEnd=newEnd+add;
                    } //w innym wypadku nie dodajemy marginesu
                }
                nmmMainChart.setViewPortTimeRange(newStart, newEnd);
            }
        }                    
    }//GEN-LAST:event_popupMeasurementZoomToActionPerformed

    private void menuEventsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEventsActionPerformed
        NMMEventsExportOptions meo=new NMMEventsExportOptions(null, true,
                this.nmmProject, this.nmmSetup, this.localeGlobal);
        meo.setLocationRelativeTo(null);
        meo.pack();
        meo.setVisible(true);
    }//GEN-LAST:event_menuEventsActionPerformed

    private void menuEDAllEventsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEDAllEventsActionPerformed

        if (this.nmmProject.getEventsNumber()>0) {
            System.out.println("Deleting events: ");
            System.out.println("Total number of events: "+this.nmmProject.getEventsNumber());
            int totalEvents=this.nmmProject.getEventsNumber();
            for (int i=0; i<totalEvents;i++) {
                System.out.println(i+" : "+this.nmmProject.deleteEvent(this.nmmProject.getEventsNumber()-1));
            }
        } else {
            //if there is no time range selected then display following information
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("THERE ARE NO EVENTS!"),
                    java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("INFORMATION"), JOptionPane.INFORMATION_MESSAGE);
        }        
    }//GEN-LAST:event_menuEDAllEventsActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

        NMMEventTypesEdytor nete = new NMMEventTypesEdytor(null, true, this.nmmProject, this.nmmImg);
        nete.pack();
        nete.setLocationRelativeTo(null);
        nete.setVisible(true);        
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void menuPSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPSaveActionPerformed
        
        this.nmmProject.deleteObservers();
        
        boolean saveStatus=this.saveProject(this.projectCurrentFile);
        if (saveStatus) {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("PROJECT SAVED SUCCESSFULLY"), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("SAVE"), JOptionPane.INFORMATION_MESSAGE);
            this.nmmProject.setSaved(true);
        } else {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("SAVING PROJECT FAILED!"), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("SAVE"), JOptionPane.INFORMATION_MESSAGE);
        }        
    }//GEN-LAST:event_menuPSaveActionPerformed

    private void nmmMainChartMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nmmMainChartMouseExited
        this.checkWhatToDoSelectionModeStarted(evt);
    }//GEN-LAST:event_nmmMainChartMouseExited

    private void menuMN_RoadSamplingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMN_RoadSamplingActionPerformed

        NMMRoadSamplingModelInterface rsmi=null;
        NMMRoadSamplingModel newRSM;
        
        //this dialog cannot be shown if there are no events in project
        if (this.nmmProject.getEventsNumber()==0) {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NOT ENOUGH EVENTS!"), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("WARNING"),
                    JOptionPane.OK_OPTION);     
        } else {
            this.nmmProject.addProjectModelsListener(rsmi);
            newRSM= new NMMRoadSamplingModel(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NEW ROAD SAMPLING MODEL"), this.nmmProject, this.nmmProject.getCurrentMeasurement().getMUID());            
            this.nmmProject.addNoiseSourceModel(newRSM);
            rsmi=new NMMRoadSamplingModelInterface(this,true,this.nmmProject, newRSM, this.nmmSetup);            
            rsmi.setLocationRelativeTo(null);
            rsmi.pack();
            rsmi.setVisible(true);
            this.nmmProject.removeProjectModelsListener(rsmi);
        }
    }//GEN-LAST:event_menuMN_RoadSamplingActionPerformed

    private void menuESplitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuESplitActionPerformed
        
        if (this.nmmProject.getMeasurementsNumber()>0) {
            this.mode=this.MODE_SPLIT_MEASUREMENT;
        } else {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CANNOT SPLIT MEASUREMENT. NO MEASUREMENTS IN PROJECT."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS"), JOptionPane.INFORMATION_MESSAGE);
        } 
    }//GEN-LAST:event_menuESplitActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed

        if (this.nmmProject.getNumberOfNoiseSourceModels()>0) {
            NMMSourceModelsManager smm = new NMMSourceModelsManager(null, true, this.nmmProject, this.nmmSetup);
            smm.setLocationRelativeTo(null);
            smm.pack();
            smm.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO NOISE MODELS IN CURRENT PROJECT!"),java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM 2010 * INFORMATION"),JOptionPane.INFORMATION_MESSAGE);
        }
                
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void menuMSingleEventsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMSingleEventsActionPerformed

        //this dialog cannot be shown if there is less then 3 event in the 
        //project
        
        if (this.nmmProject.getEventsNumber()<3) {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NOT ENOUGH EVENTS!"), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("WARNING"),
                    JOptionPane.OK_OPTION);     
        } else {
            NMMSingleEventsMethodModel newSEMM= new NMMSingleEventsMethodModel(
                    this.nmmProject, this.nmmProject.getCurrentMeasurement().getMUID(),
                    this.nmmMainChart.getViewportStartTime(),
                    this.nmmMainChart.getViewportEndTime()
                    );                        
            NMMSingleEventsModelInterface rsmi=new 
                NMMSingleEventsModelInterface(this,true, newSEMM, this.nmmSetup, this.nmmProject);
            this.nmmProject.addProjectModelsListener(rsmi);
            //this.nmmProject.addNoiseSourceModel(newSEMM);
            rsmi.setLocationRelativeTo(null);
            rsmi.pack();
            rsmi.setVisible(true);
        }
    }//GEN-LAST:event_menuMSingleEventsActionPerformed

    private boolean readMeasurementDevices() {
        
        boolean success = false;
        try {                                    
            InputStreamReader fr = new InputStreamReader(new FileInputStream("devices.txt"), "UTF-8");            
            BufferedReader br = new BufferedReader(fr);                
            String s, s1, s2, d1;
            while ((s=br.readLine())!=null) {
                s1=br.readLine();
                s2=br.readLine();
                d1=br.readLine();
                NMMNoiseMeasurementSet itm= new NMMNoiseMeasurementSet(s,s1,s2,Double.valueOf(d1));
                this.nmmProject.addMeasurementSet(itm);
            }            
            fr.close();
            this.nmmProject.setSaved(false);            
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(null,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("PROBLEM WHILE READING INFORMATION ON MEASUREMENT DEVICES: ")+ex.toString(), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM MESSAGE"), JOptionPane.ERROR_MESSAGE);            
        }         
        return success;        
    }
    
    
    private void menuDImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDImportActionPerformed
         
        try {                        
            //czytamy plik tekstowy
            fileChooser.setCurrentDirectory(new File(this.nmmSetup.getProjectPath()));
            int returnVal = fileChooser.showOpenDialog(this);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //FileReader fr = new FileReader(fileChooser.getSelectedFile());
                InputStreamReader fr = new InputStreamReader(new FileInputStream(fileChooser.getSelectedFile()), "UTF-8");
                System.out.println("Kodowanie tekstu: "+fr.getEncoding());                
                BufferedReader br = new BufferedReader(fr);                
                String s, s1, s2, d1;

                while ((s=br.readLine())!=null) {
                    s1=br.readLine();
                    s2=br.readLine();
                    d1=br.readLine();
                    NMMNoiseMeasurementSet itm= new NMMNoiseMeasurementSet(s,s1,s2,Double.valueOf(d1));
                    this.nmmProject.addMeasurementSet(itm);
                }            
                fr.close();
                this.nmmProject.setSaved(false);
                JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("DATA LOADED SUCCESSFULLY!"), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM MESSAGE"), JOptionPane.INFORMATION_MESSAGE);
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(null,ex.toString(), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM MESSAGE"), JOptionPane.ERROR_MESSAGE);
            System.out.println(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("PODCZAS IMPORTU DANYCH WYSTĄPIŁ BŁĄD: ")+ex.toString());
        }  
    }//GEN-LAST:event_menuDImportActionPerformed

    private void menuMNSimpleEvBasedMeasurementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMNSimpleEvBasedMeasurementActionPerformed

        //this dialog cannot be shown if there are no events in project
        if (this.nmmProject.getEventsNumber()==0) {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NOT ENOUGH EVENTS!"), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("WARNING"),
                    JOptionPane.OK_OPTION);     
        } else {                
            NMMSimpleModel newSM= new NMMSimpleModel(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NEW SIMPLE MODEL..."), 
                    this.nmmProject, 
                    this.nmmProject.getCurrentMeasurement().getMUID(),
                    this.nmmMainChart.getViewportStartTime(),
                    this.nmmMainChart.getViewportEndTime());
            
            NMMSimpleModelInterface rsmi=new 
                NMMSimpleModelInterface(this,true,this.nmmProject,newSM);
            this.nmmProject.addProjectModelsListener(rsmi);
            rsmi.setLocationRelativeTo(null);
            rsmi.pack();
            rsmi.setVisible(true);
            this.nmmProject.removeProjectModelsListener(rsmi);
            if (rsmi.isComplete()) {
                //add noise source model to project only if it is completely defined
                this.nmmProject.addNoiseSourceModel(newSM);
            }
        }
    }//GEN-LAST:event_menuMNSimpleEvBasedMeasurementActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
            
        this.setTitle(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NMM2014 * VERSION ")+this.version);    
        if (this.nmmSetup.getProperty("NMM_SETUP_LOAD_PREVIOUS_PROJECT_AT_STARTUP", "false").contains("true")) {
            try {
                System.out.println("Poprzedni projekt to: "+this.nmmSetup.getProperty("NMM_SETUP_PREVIOUS_PROJECT", ""));
                int load = JOptionPane.showConfirmDialog(this,"Do you want to load project you worked on?", "Previous project.",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (load==JOptionPane.YES_OPTION) {
                    this.deserializeProject(new File(this.nmmSetup.getProperty("NMM_SETUP_PREVIOUS_PROJECT", "")));                
                    this.nmmSetup.setProperty("NMM_SETUP_PREVIOUS_PROJECT", this.projectCurrentFile.toString());
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }                        
        }
        this.nmmMainChart.setViewPortFullTimeRange();
    }//GEN-LAST:event_formWindowOpened

    private void popupMeasurementPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupMeasurementPropertiesActionPerformed

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       this.projectManager.getLastSelectedPathComponent();
        if (node.isLeaf()) {
            if (node.getUserObject() instanceof NMMEvent) {
                NMMEvent nmmEvent=(NMMEvent)node.getUserObject();
                NMMEventEditor eed = new NMMEventEditor(this,true,nmmEvent, 
                    this.localeGlobal, this.nmmProject);
                eed.setLocationRelativeTo(null);
                eed.pack();
                eed.setVisible(true);
                eed.dispose();
            } else if (node.getUserObject() instanceof NMMMeasurement) {
                NMMMeasurement nmmMeas=(NMMMeasurement)node.getUserObject();
                NMMMeasurementInfo eed = new NMMMeasurementInfo(this,true, this.nmmProject,
                    nmmMeas, this.localeGlobal);
                eed.setLocationRelativeTo(null);
                eed.pack();
                eed.setVisible(true);
                eed.dispose();
            }            
        }                                                            
    }//GEN-LAST:event_popupMeasurementPropertiesActionPerformed

    private void menuPExportToXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPExportToXMLActionPerformed
        this.nmmProject.deleteObservers();    
        this.nmmProject.saveAs("path");
    }//GEN-LAST:event_menuPExportToXMLActionPerformed

    private void menuPCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPCreateActionPerformed
        
        NMMPresentationCreator pc=new NMMPresentationCreator(this, true, 
                this.nmmProject, this.nmmSetup,
                this.nmmMainChart.getViewportStartTime(), 
                this.nmmMainChart.getViewportEndTime(),
                this.localeGlobal);
        pc.setLocationRelativeTo(null);
        pc.pack();
        pc.setVisible(true);
        
    }//GEN-LAST:event_menuPCreateActionPerformed

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        this.menuPSaveActionPerformed(null);
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void buttonNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNewActionPerformed
        menuPNewActionPerformed(null);
    }//GEN-LAST:event_buttonNewActionPerformed

    private void popupMeasurementsZoomToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupMeasurementsZoomToActionPerformed
       if (this.nmmProject.getMeasurementsNumber()>0) {
           NMMMeasurement _nmmM = nmmProject.getCurrentMeasurement();
           this.nmmMainChart.setViewPortTimeRange(_nmmM.getMeasurementBeginTime(),_nmmM.getMeasurementEndTime());
           this.nmmMainChart.paintComponent(this.nmmMainChart.getGraphics());
        } else {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS IN PROJECT."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS"), JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_popupMeasurementsZoomToActionPerformed

    private void menuMNSimpleContinousMeasurementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMNSimpleContinousMeasurementActionPerformed

        if (this.nmmProject.getMeasurementsNumber()>0) {
            NMMSimpleContinuousModel newSM= new NMMSimpleContinuousModel(this.nmmProject.getCurrentMeasurement().getDescription(), 
                    this.nmmProject, 
                    this.nmmProject.getCurrentMeasurement().getMUID(),
                    this.nmmMainChart.getViewportStartTime(),
                    this.nmmMainChart.getViewportEndTime());
            
            NMMSimpleContinousModelInterface rsmi=new 
                NMMSimpleContinousModelInterface(this,true,this.nmmProject,newSM);
            this.nmmProject.addProjectModelsListener(rsmi);
            rsmi.setLocationRelativeTo(null);
            rsmi.pack();
            rsmi.setVisible(true);            
            this.nmmProject.removeProjectModelsListener(rsmi);
            if (rsmi.isComplete()) {
                this.nmmProject.addNoiseSourceModel(newSM);
            }            
        } else {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS IN PROJECT."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS"), JOptionPane.INFORMATION_MESSAGE);
        }                 
    }//GEN-LAST:event_menuMNSimpleContinousMeasurementActionPerformed

    private void projectManagerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_projectManagerMouseReleased
        
        System.out.println("Kliknięto:" + evt.getComponent().toString());
        
        if (((evt.getModifiers() & InputEvent.BUTTON3_MASK)==InputEvent.BUTTON3_MASK)) {
                                                        
            JTree tmpTree;        
            tmpTree = (JTree) evt.getSource();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tmpTree.getLastSelectedPathComponent();

            if (node == null)
            //Nothing is selected.  
            return;
                                    
            int selRow = tmpTree.getRowForLocation(evt.getX(), evt.getY());
            TreePath selPath = tmpTree.getPathForLocation(evt.getX(), evt.getY());            
            node = (DefaultMutableTreeNode)selPath.getLastPathComponent();                               

            if (node.isLeaf()) {
                if (NMMMeasurement.class == node.getUserObject().getClass()) {
                    System.out.println("Kliknieto liścia pomiaru");
                    MenuElement[] me = this.popupMeasurement.getSubElements();
                    JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) me[3].getComponent();                   
                    cbmi.setSelected(((NMMMeasurement)node.getUserObject()).getVisible());
                    this.popupMeasurement.show(evt.getComponent(), evt.getX(), evt.getY());                                                        
                } else if (NMMEvent.class == node.getUserObject().getClass()) {
                    System.out.println("Kliknieto liść zdarzenia");
                    MenuElement[] me = this.popupMeasurements.getSubElements();                    
                    System.out.println("Tworze popupmenu. Zdarzenie" + this.nmmProject.getCurrentMeasurementNumber() + " ma być:"+this.nmmProject.getCurrentMeasurement().getVisible());                    
                    this.popupMeasurements.show(evt.getComponent(), evt.getX(), evt.getY());                                                        
                }
                this.nmmProject.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this.nmmProject));
            } else {
                System.out.println("This is not leaf !");
            }                                                                                                             
        } else {
            // nic nie rób
        }
        
        
    }//GEN-LAST:event_projectManagerMouseReleased

    private void PropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PropertiesActionPerformed
                        
//        TreePath tp = this.projectManager.getPathForLocation(evt.getX(), evt.getY());
//        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)tp.getLastPathComponent();
//        NMMEvent et= (NMMEvent)dmtn.getUserObject();
//        
//        int zoomToEventIndex = this.projectManager.getPathForLocation(mode, mode)
//        NMMEvent nmmEvent=this.nmmProject.getEvent(zoomToEventIndex);                
//        NMMEventEditor eed = new NMMEventEditor(this,true,nmmEvent, 
//                this.localeGlobal, this.nmmProject);
//        eed.setLocationRelativeTo(null);
//        eed.pack();
//        eed.setVisible(true);
//        eed.dispose();
    }//GEN-LAST:event_PropertiesActionPerformed

    private void popupProjectManagerEventsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_popupProjectManagerEventsMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_popupProjectManagerEventsMouseReleased

    private void menuMIBK2236sprformatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMIBK2236sprformatActionPerformed
        //import pliku tekstowego zawierająceg wyniki pomiarów zapsane
        //z B&K 2236 w formacie do druku na przenośną drukarkę B&K        
        //lub zapisanych w formacie do importuy (CSV)
                
        //dane istotne zaczynają się w 18 wierszu
        
        //
        //------------------------
        //      Bruel & Kjaer
        //      SLM Type 2236
        //
        //SETTINGS:
        //------------------------
        //F              20-100 dB
        //RMS: A           Peak: C
        //
        //LOGGED RESULTS:
        //------------------------
        //11 Mar 2014     21:38:18
        //
        //          Leq   MaxL  MaxP
        //hh:mm:ss  [dB]  [dB]  [dB]
        //
        //21:38:18  40.5  43.1  60.0 
        //21:38:19  38.9  43.3  58.4 
        //21:38:20  44.2  48.9  64.0 
        //21:38:21  44.1  47.7  63.0 
                
        MeasurementRecord newRecord;
        int licznikWierszyPliku=0;
        String godzina;
        long godzina_l;
        String poziom;
        NMMMeasurement newMeasurement=null;                                       
        
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(this.nmmSetup.getProjectPath()));
        
        int returnVal = fc.showOpenDialog(this);
        long time=0;
        int firstHour;
        int lastHour;
        int firstLevel;
        int lastLevel;
        String s;
        
        int firstRow=0;
        s="";
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String day = JOptionPane.showInputDialog(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("ENTER DATE (YYYY-MM-DD) : "));
            int csv = JOptionPane.showConfirmDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CSV FORMAT?"));
            
            if (csv==0) {
                firstRow=12;
                firstHour=1;
                lastHour=9;
                firstLevel=12;
                lastLevel=16;
            } else {
                firstRow=19;
                firstHour=0;
                lastHour=8;
                firstLevel=9;
                lastLevel=14;
            }            
            
            int shift=0;
            
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                
                //czytamy wiersz po wierszu do czasu aż nie będzie kolejnego wiersza,
                //albo przekroczymy limit 90 tysiecy rekordów
                while (((s=br.readLine())!=null) && (licznikWierszyPliku<=899999)) {
                    licznikWierszyPliku++;
                    //jeżeli jestesmy juz na wysokosci rekordów z danymi
                    //(minęliśmy nagłowke) to zaczynamy czytać i rozbierać poszczególne
                    //linijki na dane
                    if (licznikWierszyPliku>(firstRow-1) && s.length()!=0) {
                       godzina=s.substring(firstHour, lastHour);
                       godzina_l=TimeConverter.StringToLong(day, godzina, new Locale("pl", "PL"), shift);
                       poziom=s.substring(firstLevel,lastLevel);                                              
                       if (poziom.matches("---.-")) {
                           poziom=" 00.0";
                       }
                       //jeżeli jesteśmy w pierwszym wierszu danych to tworzymy nowy obiekt zapisu
                       //ustawiamny parametry pomiaru i dodajemy pierwszy rekord
                       if (licznikWierszyPliku==firstRow) {
                           newMeasurement = new NMMMeasurement(day,godzina, 1000,new Locale("pl", "PL"));                              
                           newMeasurement.addRecord(RecordValueType.LAeq, Double.parseDouble(poziom));
                           time=newMeasurement.getMeasurementBeginTime();
                           this.nmmProject.addMeasurement(newMeasurement);
                       //ejezeli to już kolejny wiersz danych to...
                       } else if (licznikWierszyPliku>(firstRow-1)) {
                           // dodajemy kolejne rekordy jeżeli:
                           // - czas pomiędzy kolejnymi rekordami jest równy rozdzielczości pomiaru
                           // - czas pomiedzy kolejnymi rekordami jest większy niż rozdzielczośc pomiaru
                           //   ale jest to przejście przez północ
                           
                           System.out.println(godzina_l-time+", " +godzina);
                           
                           if(((godzina_l-time)==this.nmmProject.getProjectTimeResolution()) || 
                                   (((godzina_l-time)!=this.nmmProject.getProjectTimeResolution())
                                   && (godzina.matches("00:00:00")))) {                               
                               newMeasurement.addRecord(RecordValueType.LAeq, Double.parseDouble(poziom));                               
                               time=newMeasurement.getMeasurementEndTime()-this.nmmProject.getProjectTimeResolution();
                               if (godzina.matches("00:00:00")) {
                                   shift=24*3600000;
                               }
                           // tworzymy nowy pomiar jezeli czas pomiedzy kolejnymi rekordami jest większy niż rozdzielczośc
                           // pomiaru i nie jest to przejście przez północ (wtedy jerst to nowy zapis po pauzie)
                           } else {                               
                               newMeasurement = new NMMMeasurement(day,godzina, 
                                       this.nmmProject.getProjectTimeResolution(),
                                       new Locale("pl", "PL"));
                               time=newMeasurement.getMeasurementBeginTime();
                               newMeasurement.addRecord(RecordValueType.LAeq, Double.parseDouble(poziom));
                               this.nmmProject.addMeasurement(newMeasurement);
                           }
                       } 
                    //jeżeli linija w pliku wydruku jest pusta, to jeżeli jesteśmy
                    //po czytaniu rekordów możemy opuscić dalsze czytanie, ponieważ
                    //to już koniec pliku
                    } else if (s.length()==0) {
                        if (licznikWierszyPliku>19) {
                            break;
                        }
                    }                           
                }
                fr.close();
                if (licznikWierszyPliku>899999) {
                    JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("ONLY FIRST 90000 RECORDS HAVE BEEN READ FROM FILE!")+"\n");
                }
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("AN ERROR OCCURED WHILE INTERPRETING DATA FILE.")+"\n"
                        + java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("MAKE SURE DATA FILE FULFILLS REQUIREMENTS.")+"\n"
                        + java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CORRUPTED LINE (")+licznikWierszyPliku+"): "+s);
            }                        
        } else {
        }
        System.out.println("W pliku było: "+licznikWierszyPliku+" wierszy.");
        this.nmmProject.updateProjectTimeRanges();   
        this.nmmProject.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this.nmmProject));
        
    }//GEN-LAST:event_menuMIBK2236sprformatActionPerformed

    private void menuMTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMTableActionPerformed
        
        NMMMeasurementsTableModel mtb = new NMMMeasurementsTableModel(this.nmmProject);
        NMMMeasurementsTable mt = new NMMMeasurementsTable(null, true, mtb, this.nmmProject);
        mt.pack();
        mt.setVisible(true);
        
    }//GEN-LAST:event_menuMTableActionPerformed

    private void jMenu8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu8ActionPerformed
        
    
    }//GEN-LAST:event_jMenu8ActionPerformed

    private void menuMNOPN9123ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMNOPN9123ActionPerformed
            
        if (this.nmmProject.getMeasurementsNumber()>0) {
            NMMNoiseSourceModelOccupationalExp newSM= new NMMNoiseSourceModelOccupationalExp(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NEW MODEL - OCCUPATIONAL EXPOSURE..."), 
                    this.nmmProject,
                    this.nmmProject.getCurrentMeasurement().getMUID(),
                    this.nmmMainChart.getViewportStartTime(),
                    this.nmmMainChart.getViewportEndTime());     
            NMMOccupExposureModelInterface rsmi=new
                NMMOccupExposureModelInterface(this,true,this.nmmProject,newSM);
            this.nmmProject.addProjectModelsListener(rsmi);
            
            rsmi.setLocationRelativeTo(null);
            rsmi.pack();
            rsmi.setVisible(true);
            this.nmmProject.removeProjectModelsListener(rsmi);
            if (rsmi.isComplete()) {
                //model dodajemy do projektu tylko pod warunkiem że jest kompletny
                //i nie wciśnieto przypadkiem "Cancel" podczas wypełniania formularza
                //definiującego dane modelu
                this.nmmProject.addNoiseSourceModel(newSM);
            }                                    
        } else {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS IN PROJECT."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS"), JOptionPane.INFORMATION_MESSAGE);
        }           
    }//GEN-LAST:event_menuMNOPN9123ActionPerformed

    private void menuMIBK2250textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMIBK2250textActionPerformed
        //import pliku tekstowego zawierająceg wyniki pomiarów zapsane
        //w formie tabeli 2-kolumnowej (kolumna 1 - godzina, kolumna 2 - poziom)
        //identyfikacja nowego pomiaru po czasie (jeżeli kolejne mają rozstępn >1 sek
        //rozdzielenie kolumn za pomocą tabulatora
        
        //10:14:31	87.86
        //10:14:32	88.23
        //10:14:33	86.46
        //10:14:34	88.07
        //10:14:36	86.08
        
        //UWAGA: Na końcu pliku nie może być pustej linii bo się wyłoży
        
        MeasurementRecord newRecord;
        int licznikWierszyPliku=0;
        String godzina;
        long godzina_l;
        String poziom;
        NMMMeasurement newMeasurement=null;
                                        
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(this.nmmSetup.getProjectPath()));
        
        
        int returnVal = fc.showOpenDialog(this);
        long time=0;
        int firstHour;
        int lastHour;
        int firstLevel;
        int lastLevel;
        
        int firstRow=0;
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            
            File file = fc.getSelectedFile();
            String day = JOptionPane.showInputDialog(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("ENTER DATE (YYYY-MM-DD) : "));
                firstRow=1;  //wiersz od którego zaczyna czytać
                firstHour=0; //pierwszy znak w jakim jest godzina
                lastHour=8;  //ostatni znak w jakim jest godzina w formacie gg:mm:ss
                firstLevel=9; //pierwszy znak w jakim jest poziom dźwieku
                lastLevel=14;  //ostatni znak w jakim jest poziom dźwięku
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String s;
                System.out.println(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("ROZDZIECZOSC CZASOWA PROJEKTU: ")+this.nmmProject.getProjectTimeResolution());
                while (((s=br.readLine())!=null) && (licznikWierszyPliku<=899999)) {
                    licznikWierszyPliku++;
                    if (licznikWierszyPliku>(firstRow-1)) {
                       godzina=s.substring(firstHour, lastHour);                      
                       godzina_l=TimeConverter.StringToLong(day, godzina, localeGlobal);                       
                       poziom=s.substring(firstLevel,lastLevel);                                              
                       if (licznikWierszyPliku==firstRow) {
                           newMeasurement = new NMMMeasurement(day,godzina, 1000,this.localeGlobal);                           
                           newMeasurement.addRecord(RecordValueType.LAeq, Double.parseDouble(poziom));
                           time=newMeasurement.getMeasurementBeginTime();
                           this.nmmProject.addMeasurement(newMeasurement);
                       } else if (licznikWierszyPliku>(firstRow-1)) {
                           if((godzina_l-time)==this.nmmProject.getProjectTimeResolution()) {
                               System.out.println(poziom);
                               newMeasurement.addRecord(RecordValueType.LAeq, Double.parseDouble(poziom));                               
                               time=newMeasurement.getMeasurementEndTime()-this.nmmProject.getProjectTimeResolution();
                               System.out.println(time+" Licznik"+licznikWierszyPliku);
                           } else {
                               System.out.println("Mamy nowy pomiar...");
                               newMeasurement = new NMMMeasurement(day,godzina, 1000,this.localeGlobal);
                               time=newMeasurement.getMeasurementBeginTime();
                               newMeasurement.addRecord(RecordValueType.LAeq, Double.parseDouble(poziom));
                               this.nmmProject.addMeasurement(newMeasurement);
                           }
                       }                                                                     
                    }                    
                }
                fr.close();
                if (licznikWierszyPliku>899999) {
                    JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("ONLY FIRST 90000 RECORDS HAVE BEEN READ FROM FILE!")+"\n");
                }
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this,"Cannot read file. See documentation at www.ekoprojekt.biz\n"
                        + java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("TO LEARN ABOUT CORRECT TEXT DATA FILE FORMATS"), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CANNOT READ FILE!"), JOptionPane.INFORMATION_MESSAGE);
            }                        
        } else {
        }
        System.out.println("W pliku było: "+licznikWierszyPliku+" wierszy.");
    }//GEN-LAST:event_menuMIBK2250textActionPerformed

    private void projectManagerValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_projectManagerValueChanged
        
                       
    }//GEN-LAST:event_projectManagerValueChanged

    private void popupMeasurementVisibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupMeasurementVisibleActionPerformed
        
        JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) evt.getSource();
        
        //turn off display of indicated measurement
        NMMMeasurement nmmMeas = this.nmmProject.getCurrentMeasurement();
        //System.out.println("Stan: "+cbmi.isSelected());
        nmmMeas.setVisible(cbmi.isSelected());
        this.nmmProject.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this.nmmProject));
        //System.out.println("Measurement is visible: "+nmmMeas.getVisible());
    }//GEN-LAST:event_popupMeasurementVisibleActionPerformed

    private void projectManagerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_projectManagerMouseClicked
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       this.projectManager.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        if (node.isLeaf()) {
            if (node.getUserObject() instanceof NMMEvent) {
                //TODO: 
            } else if (node.getUserObject() instanceof NMMMeasurement) {
                System.out.println("Kliknięto w pomiar i czynię go bieżącym pomiarem ...");
                NMMMeasurement nmmMeas = (NMMMeasurement)node.getUserObject();
                this.nmmProject.setCurrentMeasurement(nmmMeas);
                this.nmmProject.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this.nmmProject));
            }
        }          
    }//GEN-LAST:event_projectManagerMouseClicked

    private void menuHHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuHHelpActionPerformed
       
    }//GEN-LAST:event_menuHHelpActionPerformed

    private void menuHOnlinehelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuHOnlinehelpActionPerformed
         try {         
         String url = "http://www.ekoplan.biz";
         java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
       }
       catch (java.io.IOException e) {
           System.out.println(e.getMessage());
       }
    }//GEN-LAST:event_menuHOnlinehelpActionPerformed

    private void menuMMSoundPwrLvlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMMSoundPwrLvlActionPerformed

        NMMPointLocationTableModel pltm = new NMMPointLocationTableModel(this.nmmProject);               
        if (this.nmmProject.getMeasurementsNumber()>0) {
            NMMSoundPowerLevelModel newSPL;
            newSPL = new NMMSoundPowerLevelModel( 
                    this.nmmProject,
                    this.nmmProject.getCurrentMeasurement().getMUID(),
                    this.nmmMainChart.getViewportStartTime(),
                    this.nmmMainChart.getViewportEndTime(),
                    pltm); 
            pltm.addInputDataListener(newSPL);
            
            NMMSoundPowerLevelModelInterface rsmi=new 
                NMMSoundPowerLevelModelInterface(this,true,newSPL,this.nmmSetup, this.nmmProject, pltm);
            this.nmmProject.addProjectModelsListener(rsmi);
            rsmi.setLocationRelativeTo(null);
            rsmi.pack();
            rsmi.setVisible(true);            
            newSPL.removeNoiseSourceModelChangedListener(rsmi);
            this.nmmProject.removeProjectModelsListener(rsmi);
            
        } else {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS IN PROJECT."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS"), JOptionPane.INFORMATION_MESSAGE);
        }                
    }//GEN-LAST:event_menuMMSoundPwrLvlActionPerformed

    private void menuMNInSituNoiseWallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMNInSituNoiseWallActionPerformed

        NMMPointLocationTableModel pltm = new NMMPointLocationTableModel(this.nmmProject);               
        if (this.nmmProject.getMeasurementsNumber()>0) {
            NMMNoiseWallAttenuationModel newSPL;
            newSPL = new NMMNoiseWallAttenuationModel( 
                    this.nmmProject,
                    this.nmmMainChart.getViewportStartTime(),
                    this.nmmMainChart.getViewportEndTime()); 
            pltm.addInputDataListener(newSPL);
            NMMWallAttenuationInterface rsmi=new 
                NMMWallAttenuationInterface(this,true, newSPL);
            this.nmmProject.addProjectModelsListener(rsmi);
            rsmi.setLocationRelativeTo(null);
            rsmi.pack();
            rsmi.setVisible(true);            
            this.nmmProject.removeProjectModelsListener(rsmi);
            
        } else {
            JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS IN PROJECT."), java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("NO MEASUREMENTS"), JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_menuMNInSituNoiseWallActionPerformed

    private void menuMIBK2238sprformatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMIBK2238sprformatActionPerformed
        //import pliku tekstowego zawierająceg wyniki pomiarów zapsane
        //z B&K 2238 w formacie do druku na przenośną drukarkę B&K
        
        //w pliku może być jedna bądź kilka sekcji (plików) z pomiarami.
        //w nagłówku w wierszu 32 znajduje się data, a w pliku 33 znajduje się
        //godzina początku pomiaru
        
        
        // @   25  --------------------------------------
        //              Brel & Kj‘r
        //      Sound Level Meter Type 2238
        //       Logging BZ7124 ver. 1.2.0
        // --------------------------------------
        // FILENAME:                      001.M24
        // 
        // SETTINGS:
        // --------------------------------------
        // Serial no:                     2457205
        // Range:                 30.0 - 110.0 dB
        // Peaks Over:                     140 dB
        // 2nd Exch. Rate:                   4 dB
        // Period Time:                    Normal
        // Logged Every:                    00:01
        // Detector 1 (RMS)
        //   Bandwidth:                Broad Band
        //   Freq. Wgt.:                        A
        // Detector 2 (Br.Band)
        //   Weighting:                    Peak/C
        // Sound Incidence:               Frontal
        // Windscreen Correction:              On
        // 
        // CALIBRATION:
        // --------------------------------------
        // Micr.:                         2499652
        // Sensitivity:                  -29.9 dB
        // Date:             2015 Dec 03 12:43:06
        // 
        // OVERALL RESULTS:
        // --------------------------------------
        // Start Date                 2015 Dec 03
        // Start Time                    18:12:30
        // Elapsed Time                  00:00:11
        // Overload                        0.0  %
        // Underrange                      0.0  %
        // 
        // RMS MEASUREMENT RESULTS:
        // --------------------------------------
        // Bandwidth:                  Broad Band
        // Freq. Wgt.:                          A
        // --------------------------------------
        // LFMax                          94.2 dB
        // LSMax                          94.1 dB
        // LIMax                          94.2 dB
        // LFMin                          94.1 dB
        // LSMin                          94.1 dB
        // LIMin                          94.1 dB
        // LAFTm5                         94.1 dB
        // Leq                            94.1 dB
        // LIeq                           94.1 dB
        // 
        // PEAK MEASUREMENT RESULTS:
        // --------------------------------------
        // Freq. Wgt.:                          C
        // --------------------------------------
        // #Peaks                               0
        // Lpkmax                        101.0 dB
        // 
        // LOGGED RESULTS (1 of 1):
        // --------------------------------------
        //  Marker
        //          LAeq
        // --------------------------------------
        //  OU1234    dB
        // --------------------------------------
        //          94.1
        //          94.1
        //          94.1
        //          94.1
        //          94.1
        //          94.1
        //          94.1
        //          94.1
        //          94.1
        //          94.1
        //          94.1

        MeasurementRecord newRecord;
        int licznikWierszyPliku=0;
        String godzina;
        String data;
        long godzina_l;
        String poziom;
        Double dPoziom;
        NMMMeasurement newMeasurement=null;                                       
        
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(this.nmmSetup.getProjectPath()));
        
        int returnVal = fc.showOpenDialog(this);
        long time=0;
        int firstHour;
        int lastHour;
        int firstLevel;
        int lastLevel;
        String s;
        
        int firstRow=0;
        s="";
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String day = JOptionPane.showInputDialog(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("ENTER DATE (YYYY-MM-DD) : "));

            firstRow=1;
            firstHour=31;
            lastHour=39;
            firstLevel=10;
            lastLevel=14;
            
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                System.out.println(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("ROZDZIECZOSC CZASOWA PROJEKTU: ")+this.nmmProject.getProjectTimeResolution());
                
                //główna pętal idąca przez cały plik tekstowy
                while (((s=br.readLine())!=null) && (licznikWierszyPliku<=899999)) {
                    licznikWierszyPliku++;
                    System.out.println(s);
                    //czytamy plik do momentu kiedy natrafimy na wiersz z ciągiem "OVERALL RESULTS:"
                    while (!(s=br.readLine()).equals(" OVERALL RESULTS:")) {
                        System.out.println(">"+s+"<");
                    }
                    //czytamy jeszcze linię
                    s=br.readLine();
                    System.out.println(s);
                    //czytamy linię z datą rozpoczęcia wykonania zapisu historii pomiarowej
                    s=br.readLine();
                    System.out.println("Data: "+s);
                    s=br.readLine();
                    System.out.println("Godzina: "+s);
                    godzina=s.substring(firstHour, lastHour);
                    godzina_l=TimeConverter.StringToLong(day, godzina, localeGlobal);     
                                        
                    //tworzywy nowy obiekt "Zapis"
                    newMeasurement = new NMMMeasurement(day,godzina, this.nmmProject.getProjectTimeResolution(),this.localeGlobal);
                    
                    //teraz trzeba przeczytać jeszcze 33 linie innych danych
                    for (int i=0; i<33;i++) {
                        s=br.readLine();
                    }
                    //teraz zaczynamy czytać dane historii pomiarowej i tworzymy z nich zapis                    
                    System.out.println("Czytam dane pomiarowe:");
                    try {
                        licznikWierszyPliku=0;
                        while ((s=br.readLine())!=null) {
                            System.out.println(">"+s+"<");
                            poziom=s.substring(firstLevel, lastLevel);
                            dPoziom=Double.parseDouble(poziom);
                            licznikWierszyPliku++;
                            if (licznikWierszyPliku==firstRow) {
                               newMeasurement.addRecord(RecordValueType.LAeq, dPoziom);
                               time=newMeasurement.getMeasurementBeginTime();
                               this.nmmProject.addMeasurement(newMeasurement);
                           } else {                               
                               //System.out.println(dPoziom);
                               newMeasurement.addRecord(RecordValueType.LAeq, dPoziom);                               
                               time=newMeasurement.getMeasurementEndTime()-this.nmmProject.getProjectTimeResolution();
                               //System.out.println(time+" Licznik"+licznikWierszyPliku);                               
                           }  
                        }    
                    } catch (Exception e) {
                    } finally {                            
                    }                                                                       
                }
                fr.close();
                if (licznikWierszyPliku>899999) {
                    JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("ONLY FIRST 90000 RECORDS HAVE BEEN READ FROM FILE!")+"\n");
                }
            } catch(Exception ex) {
                //JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("AN ERROR OCCURED WHILE INTERPRETING DATA FILE.")+"\n"
                        //+ "Make sure data file fulfills requirements.\n");
                       // + java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("CORRUPTED LINE (")+licznikWierszyPliku+"): "+s);
            }                        
        } else {
            //komunikat wyświetlany po porzuceniu przez użytkownika
            //wyboru pliku do zaimportowania
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("IMPORT ABANDONED!"));
        }        
        System.out.println("W pliku było: "+licznikWierszyPliku+" wierszy.");
        this.nmmProject.updateProjectTimeRanges();           
        this.nmmProject.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this.nmmProject));
    }//GEN-LAST:event_menuMIBK2238sprformatActionPerformed

    private void menuEDEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEDEventActionPerformed
        if (this.nmmProject.getEventsNumber()>0) {
            
            String name = JOptionPane.showInputDialog(java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("PODAJ NAZWĘ TYPU ZDARZENIA:"));                                    
            int totalEvents=this.nmmProject.getEventsNumber();
            for (int i=0; i<totalEvents;i++) {
                if (this.nmmProject.getEvent(i).getDescription().equals(name)) {
                    System.out.println(i+" : "+this.nmmProject.deleteEvent(i));
                    --totalEvents;
                    --i;
                }                
            }
        } else {
            //if there is no time range selected then display following information
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("biz/ekoplan/nmm2010/languages/translations").getString("THERE ARE NO EVENTS!"),
                    "Information", JOptionPane.INFORMATION_MESSAGE);
        }      
    }//GEN-LAST:event_menuEDEventActionPerformed

    private void menuMCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMCreateActionPerformed
        
        NMMCreateMeasurement cm;
        
        cm = new NMMCreateMeasurement(this, true, this.localeGlobal, this.nmmProject);
        cm.pack();
        cm.setLocationRelativeTo(null);
        cm.setVisible(true);
        cm.dispose();        
    }//GEN-LAST:event_menuMCreateActionPerformed

    private void menuPExportDescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPExportDescriptionActionPerformed
        
        JFileChooser fc = new JFileChooser(this.nmmSetup.getProperty("NMM_SETUP_PROJECT_PATH", null));
        int returnVal = fc.showOpenDialog(null);
        
         if (returnVal == JFileChooser.APPROVE_OPTION) {             
            FileWriter fr = null;
            try {
                fr = new FileWriter(fc.getSelectedFile());
                BufferedWriter br = new BufferedWriter(fr);
                br.write("<html>");
                br.write("<head>");
                br.write("<meta charset=\"UTF-8\"> ");
                br.write("</head>");
                br.write("<body>");
                br.write("<p>Tabela czasów wykonywania pomiarów poziomu hałasu</p>\n");
                br.write("<table border=\"1\">\n");
                br.write("<tr>\n");
                br.write("<td>Punkt pomiarowy</td><td>Początek pomiaru</td><td>Koniec pomiaru</td>\n");
                br.write("</tr>\n");                                
                //lecimy po wierszach (liczba zapisów)                
                int liczbaZapisów = this.nmmProject.getMeasurementsNumber();
                for (int j=0; j<liczbaZapisów;j++) {                    
                    //lecimy po kolumnach
                    br.write("<tr>\n");
                    br.write("<td>"+this.nmmProject.getMeasurement(j).getDescription()
                        +"</td>\n");                        
                    br.write("<td>"+TimeConverter.LongToTimeString(this.nmmProject.getMeasurement(j).getMeasurementBeginTime(), DateFormat.SHORT, localeGlobal)
                        +"</td>\n");    
                    br.write("<td>"+TimeConverter.LongToTimeString(this.nmmProject.getMeasurement(j).getMeasurementEndTime(), DateFormat.SHORT, localeGlobal)
                        +"</td>\n");                                                        
                    br.write("</tr>\n");
                }               
                br.write("</tr>");
                br.write("</table>");
                br.write("</body>");
                br.write("</html>");
                br.flush();
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(NMMReportWriter.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(NMMReportWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
        }                      
        
    }//GEN-LAST:event_menuPExportDescriptionActionPerformed

    private void menuMICsVImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMICsVImportActionPerformed
        //Otwieranie okna do importu plików w formacie CSV
        
    }//GEN-LAST:event_menuMICsVImportActionPerformed

    private void buttonPlayMP3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPlayMP3ActionPerformed
        
        NMMAudioPlayer ap = new NMMAudioPlayer(this.nmmProject);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(ap);
        
                  
    }//GEN-LAST:event_buttonPlayMP3ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        this.nmmSetup.saveNMMConfigurationToFile();
    }//GEN-LAST:event_formWindowClosed

    private void buttonExcludeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExcludeActionPerformed
        
        int firstRecToExclude;
        int lastRecToExclude;
        boolean isExcluded;
        boolean ifInclude=false;
        
        //TODO: check if current selection is within current measurement
        if (!this.nmmProject.getCurrentMeasurement().isWithinMeasurement(this.nmmProject.currentSelection)) {
            //if NOT
            JOptionPane.showMessageDialog(this, "Cannot Ex/Include outsied of current measurement");
            System.out.println("Selection outside of current measurement. Cannot mark excluded records!");
        } else {
            //mark excluded records
            firstRecToExclude = this.nmmProject.getCurrentMeasurement().getRecordIndex(this.nmmProject.getCurrentSelection().getStart());
            lastRecToExclude = this.nmmProject.getCurrentMeasurement().getRecordIndex(this.nmmProject.getCurrentSelection().getEnd());
            System.out.println("Excluding records: ");
            for (int i=firstRecToExclude; i<=lastRecToExclude;i++) {
                if (this.nmmProject.getCurrentMeasurement().getRecord(i).isExcluded()) {
                    ifInclude=true;
                    break;
                }
            }
            for (int i=firstRecToExclude; i<=lastRecToExclude;i++) {
                System.out.print(i + " ");
                this.nmmProject.getCurrentMeasurement().getRecord(i).setExcluded(!ifInclude);
            }
            System.out.println(" Done.");
        } 
    }//GEN-LAST:event_buttonExcludeActionPerformed
   
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) throws ClassNotFoundException {

    

        // look & feel setup
        try {	    
            //UIManager.setLookAndFeel(
            //    UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            //Manager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");

        }
        catch (UnsupportedLookAndFeelException e) {
           // handle exception
        }
        catch (ClassNotFoundException e) {
           // handle exception
        }
        catch (InstantiationException e) {
           // handle exception
        }
        catch (IllegalAccessException e) {
           // handle exception
        }

        try
            {Thread.sleep(1000);            }
        catch (InterruptedException ex) {
        }

       

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                NMM2018 nmm2010 = new NMM2018();
                nmm2010.setLocationRelativeTo(null);
                nmm2010.setVisible(true);                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Properties;
    private javax.swing.JButton buttonExclude;
    private javax.swing.JButton buttonNew;
    private javax.swing.JButton buttonPlayMP3;
    private javax.swing.JButton buttonSave;
    private javax.swing.JButton buttonZoomOut;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelActiveMeasurement;
    private javax.swing.JLabel labelCurrentMeasurementLeq;
    private javax.swing.JLabel labelRecId;
    private javax.swing.JLabel labelSelectionLAeqValue;
    private javax.swing.JLabel labelSelectionSize;
    private javax.swing.JLabel labelTime;
    private javax.swing.JLabel labelValueAtCursor;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuDImport;
    private javax.swing.JMenu menuDeviceDevices;
    private javax.swing.JMenu menuEClearSelection;
    private javax.swing.JMenuItem menuECreate;
    private javax.swing.JMenuItem menuEDAllEvents;
    private javax.swing.JMenuItem menuEDEvent;
    private javax.swing.JMenu menuEDelete;
    private javax.swing.JMenuItem menuEEventsTable;
    private javax.swing.JMenuItem menuEExport;
    private javax.swing.JMenuItem menuEImport;
    private javax.swing.JMenuItem menuESCopy;
    private javax.swing.JMenuItem menuESCut;
    private javax.swing.JMenuItem menuESDelete;
    private javax.swing.JMenuItem menuESEditValues;
    private javax.swing.JMenuItem menuESNeighbNoiseLevel;
    private javax.swing.JMenuItem menuESPaste;
    private javax.swing.JMenuItem menuESToBackgroundNoiseLevel;
    private javax.swing.JRadioButtonMenuItem menuESm10secSelection;
    private javax.swing.JRadioButtonMenuItem menuESm60secSelection;
    private javax.swing.JRadioButtonMenuItem menuESmFreeSelection;
    private javax.swing.JMenuItem menuESplit;
    private javax.swing.JMenu menuEvents;
    private javax.swing.JMenuItem menuHHelp;
    private javax.swing.JMenuItem menuHOnlinehelp;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuMCreate;
    private javax.swing.JMenuItem menuMExport;
    private javax.swing.JMenuItem menuMIBK2236sprformat;
    private javax.swing.JMenuItem menuMIBK2238sprformat;
    private javax.swing.JMenuItem menuMIBK2250text;
    private javax.swing.JMenuItem menuMICsVImport;
    private javax.swing.JMenuItem menuMImport;
    private javax.swing.JMenuItem menuMInfo;
    private javax.swing.JMenuItem menuMMSoundPwrLvl;
    private javax.swing.JMenuItem menuMNInSituNoiseWall;
    private javax.swing.JMenuItem menuMNOPN9123;
    private javax.swing.JMenuItem menuMNSimpleContinousMeasurement;
    private javax.swing.JMenuItem menuMNSimpleEvBasedMeasurement;
    private javax.swing.JMenuItem menuMN_RoadSampling;
    private javax.swing.JMenuItem menuMSingleEvents;
    private javax.swing.JMenuItem menuMTable;
    private javax.swing.JMenuItem menuMTimeAdjust;
    private javax.swing.JMenuItem menuMTrim;
    private javax.swing.JMenu menuMeasurement;
    private javax.swing.JMenu menuModels;
    private javax.swing.JMenuItem menuPCreate;
    private javax.swing.JMenuItem menuPExportDescription;
    private javax.swing.JMenuItem menuPExportToXML;
    private javax.swing.JMenuItem menuPNew;
    private javax.swing.JMenuItem menuPOpen;
    private javax.swing.JMenuItem menuPProperties;
    private javax.swing.JMenuItem menuPQuit;
    private javax.swing.JMenuItem menuPSave;
    private javax.swing.JMenuItem menuPSaveAs;
    private javax.swing.JMenuItem menuPSetup;
    private javax.swing.JMenu menuProject;
    private javax.swing.JMenuItem menuRManagePresentations;
    private javax.swing.JMenuItem menuVVerticalScale;
    private javax.swing.JMenuItem menuVZoomAll;
    private javax.swing.JMenu menuView;
    private javax.swing.JMenuItem menuZoom100percent;
    private javax.swing.JMenuItem menuZoom200percent;
    private javax.swing.JMenuItem menuZoom500percent;
    private nmm2010.NMMMainChart nmmMainChart;
    private javax.swing.JPanel panelInfo;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelProject;
    private javax.swing.JPanel panelSideInfo;
    private javax.swing.JMenuItem popupMCDeleteEvent;
    private javax.swing.JPopupMenu popupMainChart;
    private javax.swing.JMenuItem popupMasurementsProperties;
    private javax.swing.JPopupMenu popupMeasurement;
    private javax.swing.JMenuItem popupMeasurementDelete;
    private javax.swing.JMenuItem popupMeasurementProperties;
    private javax.swing.JCheckBoxMenuItem popupMeasurementVisible;
    private javax.swing.JMenuItem popupMeasurementZoomTo;
    private javax.swing.JPopupMenu popupMeasurements;
    private javax.swing.JMenuItem popupMeasurementsDelete;
    private javax.swing.JMenuItem popupMeasurementsZoomTo;
    private javax.swing.JPopupMenu popupProjectManagerEvents;
    private biz.ekoplan.nmm2010.projectmanager.ProjectManager projectManager;
    private javax.swing.ButtonGroup radiogroupSelectionMode;
    private javax.swing.JSplitPane splitPane;
    private nmm2010.TimeSelector timeSelector;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JToolBar toolbarView;
    // End of variables declaration//GEN-END:variables

    public void dispatchNMMProjectChangedEvent(NMMProjectChangedEvent _mEvent) {
    
        this.setFrameTitle(this.projectCurrentFile);
        String currentMeasurement;
        
        if (this.nmmProject.getMeasurementsNumber()>0) {
            currentMeasurement= this.nmmProject.getMeasurement(this.nmmProject.getCurrentMeasurementNumber()).getDescription();
        } else {
            currentMeasurement="-";
        }    
        this.labelActiveMeasurement.setText(currentMeasurement);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        ProjectManager pm=(ProjectManager)e.getSource();
        if (this.nmmProject.getMeasurementsNumber()>0) {
            if (!pm.isSelectionEmpty()) {
                DefaultMutableTreeNode dmtn1 = (DefaultMutableTreeNode) pm.getSelectionPath().getLastPathComponent();
                if (dmtn1.getUserObject() instanceof NMMMeasurement) {
                    this.nmmProject.setCurrentMeasurement((NMMMeasurement)dmtn1.getUserObject());            
                }
            }                
        }           
    }

    private void deserializeProject(File _file) throws Exception {
                
        FileInputStream fis = new FileInputStream(_file);
        GZIPInputStream gz = new GZIPInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(gz);
        System.out.println("Rozpoczynam deserializację ...");
        this.nmmProject = (NMMProject)ois.readObject();
        ois.close();
        System.out.println("Deserializacja powiodła się :-) Liczba pomiarów w projekcie: "+this.nmmProject.getMeasurementsNumber());
        System.out.println("Początek i koniec projektu: "+this.nmmProject.getProjectBeginTime()+" - "+
                this.nmmProject.getProjectEndTime());
        this.projectCurrentFile=_file;

        this.nmmProject.addProjectChangedListener(this);

        this.nmmProject.addProjectChangedListener(this.projectManager);
        this.nmmProject.addProjectChangedListener(this.nmmMainChart);

        //trzeba każdemu zdarzeniu projektu podać projekt jako nasłuchiwacz zmian w zdarzeniu
        for (int i=0; i<this.nmmProject.getEventsNumber();i++) {
            this.nmmProject.getEvent(i).addEventChangedListener(this.nmmProject);
        }                    
        //trzeba każdemu pomiarowi w projekcie podać nasłuchiwacza jego zmian
        for (int i=0; i<this.nmmProject.getMeasurementsNumber();i++) {
            this.nmmProject.getMeasurement(i).addMeasurementChangedListener(this.nmmProject);
        }                    
        //trzeba każdemu modelowi w projekcie przywrócić tablicę nasłuchiwaczy
        for (int i=0; i<this.nmmProject.getNoiseSourceModels().length;i++) {
            this.nmmProject.getNoiseSourceModel(i).recreateListenersArray();
        }                                    
        this.nmmProject.setSaved(true);  
        this.nmmMainChart.setViewPortFullTimeRange();
        
        
        this.nmmProject.updateProjectTimeRanges();
        this.nmmProject.fireNMMProjectChangedEvent(new NMMProjectChangedEvent(this.nmmProject));
    }
}
