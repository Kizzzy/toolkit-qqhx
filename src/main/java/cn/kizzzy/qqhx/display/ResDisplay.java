package cn.kizzzy.qqhx.display;

import cn.kizzzy.javafx.display.Display;
import cn.kizzzy.javafx.display.DisplayAAA;
import cn.kizzzy.javafx.display.DisplayAttribute;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.qqhx.ResFile;
import cn.kizzzy.vfs.IPackage;

import java.nio.charset.Charset;

@DisplayAttribute(suffix = {
    "res",
})
public class ResDisplay extends Display<IPackage> {
    
    public ResDisplay(IPackage context, String path) {
        super(context, path);
    }
    
    @Override
    public DisplayAAA load() {
        ResFile resFile = context.load(path, ResFile.class);
        if (resFile != null) {
            return new DisplayAAA(DisplayType.SHOW_TEXT, new String(resFile.data, Charset.forName("GB2312")));
        }
        return null;
    }
}
