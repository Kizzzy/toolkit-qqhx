package cn.kizzzy.qqhx.display;

import cn.kizzzy.javafx.display.Display;
import cn.kizzzy.javafx.display.DisplayAAA;
import cn.kizzzy.javafx.display.DisplayAttribute;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.vfs.IPackage;

@DisplayAttribute(suffix = {
    "ini",
    "txt",
    "xml",
    "scp",
}, priority = 99)
public class TxtDisplay extends Display<IPackage> {
    
    public TxtDisplay(IPackage context, String path) {
        super(context, path);
    }
    
    @Override
    public DisplayAAA load() {
        String text = context.load(path, String.class);
        if (text != null) {
            return new DisplayAAA(DisplayType.SHOW_TEXT, text);
        }
        return null;
    }
}
