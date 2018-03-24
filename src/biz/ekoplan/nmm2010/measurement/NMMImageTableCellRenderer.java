/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.measurement;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author jarek
 */
public class NMMImageTableCellRenderer extends JLabel
                           implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;
 
    public NMMImageTableCellRenderer(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true);                
    }
 
    @Override
    public Component getTableCellRendererComponent(
                            JTable table, Object imgIcon,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        ImageIcon newImageIcon = (ImageIcon)imgIcon;
        this.setIcon(newImageIcon);
        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                              table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                              table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }         
        return this;
    }
    
}
