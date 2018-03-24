/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.toolbox;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author samsung
 */
public class NMMNumberInputVerifier extends InputVerifier {

    @Override
    public boolean verify(JComponent input) {
        JTextField tf = (JTextField) input;
        boolean isNumeric = true;
 	try {
            double d = Double.parseDouble(tf.getText());
        } catch (NumberFormatException e) {
            isNumeric=false;
            JOptionPane.showMessageDialog(null, "Enter numeric value.", "Bad input!", JOptionPane.ERROR_MESSAGE);
        }
        return isNumeric;
    }
}
