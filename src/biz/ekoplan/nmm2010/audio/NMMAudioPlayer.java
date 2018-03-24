/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.ekoplan.nmm2010.audio;

import java.io.File;
import java.io.FileInputStream;
import javax.swing.JOptionPane;
import javazoom.jl.player.advanced.AdvancedPlayer;
import nmm2010.NMMProject;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

/**
 *
 * @author User
 */
public class NMMAudioPlayer implements Runnable {

    NMMProject nmmProject;
    
    public NMMAudioPlayer(NMMProject _nmmProj) {
        this.nmmProject=_nmmProj;
    }
    
    @Override
    public void run() {
        
        int pausedOnFrame = 0;
        double msPerFrame= 26.122449;
        
        try {        
            File f = this.nmmProject.getCurrentMeasurement().getMeasurementAudio();
            FileInputStream fis = new FileInputStream(f);
            AdvancedPlayer player = new AdvancedPlayer(fis);                    
            AudioFile audioFile = AudioFileIO.read(f);
            int duration= audioFile.getAudioHeader().getTrackLength();
            System.out.println("Zmienna długiośc?: " + audioFile.getAudioHeader().isVariableBitRate());
            System.out.println("Kanały: " + audioFile.getAudioHeader().getChannels());
            System.out.println("Prędkośc sampli: " + audioFile.getAudioHeader().getSampleRate());
            System.out.println("Bitrate: " + audioFile.getAudioHeader().getBitRate());
            System.out.println("Czas trwania MP3: "+duration);           
            
            //długość w sekudnach nagrania do odtworzenia (czas zaznaczony na wykresie przez użytkownika)
            long replLength=this.nmmProject.getCurrentSelection().getMilisLength()/1000;
            
            //obliczenie która to jest sekunda pomiaru na mierniku
            int kolSek=
                (int)(this.nmmProject.getCurrentSelection().getStart()-this.nmmProject.getMeasurement(this.nmmProject.getCurrentMeasurementNumber()).getMeasurementBeginTime())/1000;
            long frame = (long)(kolSek*(1000/msPerFrame));
            System.out.println("Start (sekunda pomiaru): "+kolSek);                
            player.play((int)frame, (int)(frame+(replLength*(1000/msPerFrame))));             
        } catch (Exception e) {
            System.out.println("W czasie odtwarzania MP3 wystąpił błąd!");
            JOptionPane.showMessageDialog(null, "Nie można odtworzyć nagrania.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
        }  
    }
}
