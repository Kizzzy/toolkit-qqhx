package cn.kizzzy.qqhx.display;

import cn.kizzzy.javafx.display.Display;
import cn.kizzzy.javafx.display.DisplayAAA;
import cn.kizzzy.javafx.display.DisplayAttribute;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.qqhx.CseFile;
import cn.kizzzy.vfs.IPackage;

import java.nio.charset.Charset;

@DisplayAttribute(suffix = {
    "cse",
})
public class CseDisplay extends Display<IPackage> {
    
    public CseDisplay(IPackage context, String path) {
        super(context, path);
    }
    
    @Override
    public DisplayAAA load() {
        CseFile cseFile = context.load(path, CseFile.class);
        if (cseFile != null) {
            return new DisplayAAA(DisplayType.SHOW_TEXT, new String(cseFile.data, Charset.forName("GB2312")));
        }
        return null;
    }
}
