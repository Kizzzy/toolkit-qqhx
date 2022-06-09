package cn.kizzzy.qqhx.display;

import cn.kizzzy.javafx.display.Display;
import cn.kizzzy.javafx.display.DisplayAAA;
import cn.kizzzy.javafx.display.DisplayAttribute;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.javafx.display.image.DisplayFrame;
import cn.kizzzy.javafx.display.image.DisplayTrack;
import cn.kizzzy.javafx.display.image.DisplayTracks;
import cn.kizzzy.qqhx.MfpFile;
import cn.kizzzy.qqhx.SfpFile;
import cn.kizzzy.qqhx.helper.QqhxImgHelper;
import cn.kizzzy.vfs.IPackage;

import java.awt.image.BufferedImage;

@DisplayAttribute(suffix = {
    "mfp",
})
public class MfpDisplay extends Display<IPackage> {
    
    public MfpDisplay(IPackage context, String path) {
        super(context, path);
    }
    
    @Override
    public DisplayAAA load() {
        MfpFile mfpFile = context.load(path, MfpFile.class);
        if (mfpFile == null) {
            return null;
        }
        
        DisplayTrack track = new DisplayTrack();
        
        int i = 0;
        for (SfpFile sfpFile : mfpFile.files) {
            BufferedImage image = QqhxImgHelper.toImage(sfpFile);
            if (image != null) {
                MfpFile.Frame frameInfo = mfpFile.frames[i];
                
                float offsetX = -frameInfo.width / 2f + frameInfo.offsetX;
                float offsetY = -frameInfo.height + frameInfo.offsetY;
                
                DisplayFrame frame = new DisplayFrame();
                frame.x = 200 + offsetX;
                frame.y = 200 + offsetY;
                frame.width = sfpFile.width;
                frame.height = sfpFile.height;
                frame.image = image;
                frame.time = 167 * i;
                frame.extra = String.format("%02d/%02d(%s : %s)", i + 1, mfpFile.fileCount, sfpFile.width, sfpFile.height);
                
                track.frames.add(frame);
                
                i++;
            }
        }
        
        DisplayTracks tracks = new DisplayTracks();
        tracks.tracks.add(track);
        return new DisplayAAA(DisplayType.SHOW_IMAGE, tracks);
    }
}
