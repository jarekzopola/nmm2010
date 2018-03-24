/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.projectmanager;

import biz.ekoplan.nmm2010.measurement.NMMMeasurement;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import nmm2010.NMMEvent;

/**
 *
 * @author jarek
 */
public class NMMProjectManagerLeafRenderer implements TreeCellRenderer {

    JLabel descriptionLabel;
    JLabel evDescriptionLabel;
    JLabel authorsLabel;    
    JPanel renderer;   
    JPanel eventRenderer;
    DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
    Color backgroundSelectionColor;
    Color backgroundNonSelectionColor;
        
    public NMMProjectManagerLeafRenderer() {
        
        //Measurement
        renderer = new JPanel(new GridLayout(0, 1));        
        descriptionLabel = new JLabel(" ");
        descriptionLabel.setForeground(Color.BLUE);
        renderer.add(descriptionLabel);
        authorsLabel = new JLabel(" ");
        authorsLabel.setForeground(Color.BLUE);
        renderer.add(authorsLabel);                
        backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
        backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
        renderer.setPreferredSize(new Dimension(200,40));
        
        //Event
        evDescriptionLabel = new JLabel(" ");
        eventRenderer = new JPanel(new GridLayout(0, 1));
        eventRenderer.add(evDescriptionLabel);
        eventRenderer.setPreferredSize(new Dimension(200,20));
        
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row, 
            boolean hasFocus) {

        Component returnValue = null;
        if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
            Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
            
            //Renderer for Measurements
            if (userObject instanceof NMMMeasurement) {
            NMMMeasurement book = (NMMMeasurement)userObject;
            int style = Font.BOLD;
            this.descriptionLabel.setFont(new Font("Tahoma", style, 12));
            this.descriptionLabel.setForeground(book.getMeasurementColor());
            this.authorsLabel.setForeground(Color.GRAY);
            this.authorsLabel.setFont(new Font("Arial",9,9));
            this.backgroundSelectionColor=new Color(160,160,160);
            descriptionLabel.setText(book.getDescription());
            authorsLabel.setText(book.getOperator());                        
            if (selected) {
                renderer.setBackground(backgroundSelectionColor);
            } else {
                renderer.setBackground(backgroundNonSelectionColor);
            }
                renderer.setEnabled(tree.isEnabled());
                returnValue = renderer;
            
            //Renderer for Events
            } else if (userObject instanceof NMMEvent) {
            NMMEvent event = (NMMEvent)userObject;
            int style = Font.BOLD;
            this.evDescriptionLabel.setFont(new Font("Tahoma", style, 12));
            this.evDescriptionLabel.setForeground(event.getColor());            
            this.backgroundSelectionColor=new Color(160,160,160);
            evDescriptionLabel.setText(event.getDescription());            
            if (selected) {
                eventRenderer.setBackground(backgroundSelectionColor);
            } else {
                eventRenderer.setBackground(backgroundNonSelectionColor);
            }
                eventRenderer.setEnabled(tree.isEnabled());
                returnValue = eventRenderer;
            }       
        }
        if (returnValue == null) {
            returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value,
                            selected, expanded, leaf, row, hasFocus);
        }
            return returnValue;
        }
}
