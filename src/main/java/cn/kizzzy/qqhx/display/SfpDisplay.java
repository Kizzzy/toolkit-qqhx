package cn.kizzzy.qqhx.display;

import cn.kizzzy.javafx.display.Display;
import cn.kizzzy.javafx.display.DisplayAAA;
import cn.kizzzy.javafx.display.DisplayAttribute;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.javafx.display.image.DisplayFrame;
import cn.kizzzy.javafx.display.image.DisplayTrack;
import cn.kizzzy.javafx.display.image.DisplayTracks;
import cn.kizzzy.qqhx.SfpFile;
import cn.kizzzy.qqhx.helper.QqhxImgHelper;
import cn.kizzzy.vfs.IPackage;

import java.awt.image.BufferedImage;

@DisplayAttribute(suffix = {
    "sfp",
})
public class SfpDisplay extends Display<IPackage> {
    
    public SfpDisplay(IPackage context, String path) {
        super(context, path);
    }
    
    @Override
    public DisplayAAA load() {
        SfpFile sfpFile = context.load(path, SfpFile.class);
        if (sfpFile == null) {
            return null;
        }
        
        DisplayTrack track = new DisplayTrack();
        
        BufferedImage image = QqhxImgHelper.toImage(sfpFile);
        if (image != null) {
            float offsetX = 0;
            float offsetY = 0;
            
            DisplayFrame frame = new DisplayFrame();
            frame.x = 200 + offsetX;
            frame.y = 200 + offsetY;
            frame.width = sfpFile.width;
            frame.height = sfpFile.height;
            frame.image = image;
            frame.time = 0;
            frame.extra = String.format("%02d/%02d(%s : %s)", 1, 1, sfpFile.width, sfpFile.height);
            
            track.frames.add(frame);
        }
        
        DisplayTracks tracks = new DisplayTracks();
        tracks.tracks.add(track);
        return new DisplayAAA(DisplayType.SHOW_IMAGE, tracks);
    }
}
