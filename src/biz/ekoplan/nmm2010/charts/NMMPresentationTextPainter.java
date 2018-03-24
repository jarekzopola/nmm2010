/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package biz.ekoplan.nmm2010.charts;

import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import nmm2010.NMMProject;

/**
 *
 * @author Ecoplan
 */
public class NMMPresentationTextPainter {

    int viewportBottomMargin;
    int viewportTopMargin;
    int viewportLeftMargin;
    int viewportRightMargin;
    int verticalScaleMaxDb;
    int verticalScaleMinDb;
    Graphics2D g;
    PageFormat pf;
    NMMProject nmmProject;

     public NMMPresentationTextPainter(Graphics _g, PageFormat _pf, int _vpLeftMargin,
            int _vpBottomMargin, int _vpRightMargin, int _vpTopMargin, NMMProject _nmmProj) {
        this.viewportLeftMargin=_vpLeftMargin;
        this.viewportRightMargin=_vpRightMargin;
        this.viewportBottomMargin=_vpBottomMargin;
        this.viewportTopMargin=_vpTopMargin;
        this.g=(Graphics2D)_g;
        this.pf=_pf;
        this.nmmProject=_nmmProj;
    }

    public void drawText(String _textToDraw) {

            // width and height of whole page
            double viewportWidth=pf.getWidth();
            double viewportHeight=pf.getHeight();

            // height and width of text area
            double wys = viewportHeight-(this.viewportBottomMargin
                    +this.viewportTopMargin);
            double szer =viewportWidth-(this.viewportRightMargin+
                    this.viewportLeftMargin);

            //drawing ext area borderline
            //this.g.drawRect(this.viewportLeftMargin,this.viewportTopMargin,
            //        (int)szer, (int)wys);
            
            String fitString = null;
            String testString=null;
            int lastSt=0;       // previous value of St
            int totChars=0;     // analysed chars in previous lines
            int lc=0;           // number of analysed line
            boolean cont = true;

            //Before entering main loop, we have to replace line feeds with space signs.
            System.out.println(_textToDraw);
            String _newTextToDraw=_textToDraw.replace("\n", " ");
            System.out.println(_newTextToDraw);
            do  {
                //find position of space (" ") in _textToDraw
                //take into account chars form previous lines and previous (totChars)
                //postion od space in current line (lastSt)
                int St = _newTextToDraw.indexOf(" ", totChars+lastSt)-totChars;
                if (St>0) {
                    testString = _newTextToDraw.substring(totChars, totChars+St);
                } else {                    
                    testString = _newTextToDraw.substring(totChars, _textToDraw.length());
                    cont=false;
                }
                if ((szer-6)>g.getFontMetrics().stringWidth(testString) &&
                        (St>0)) {
                    lastSt=St+1;
                    fitString=_newTextToDraw.substring(totChars, totChars+St);
                } else {
                    if ((szer-6)>g.getFontMetrics().stringWidth(testString)) {
                        fitString=_newTextToDraw.substring(totChars, _textToDraw.length());
                        g.drawString(fitString, viewportLeftMargin, viewportTopMargin+lc*9);
                        totChars=totChars+lastSt;
                        lastSt=0;
                        lc++; 
                    } else {
                        g.drawString(fitString, viewportLeftMargin, viewportTopMargin+lc*9);
                        totChars=totChars+lastSt;
                        lastSt=0;
                        lc++;   
                        cont=true;
                    }
                }
            } while (cont);
    }
}
