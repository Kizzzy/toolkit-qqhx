package cn.kizzzy.qqhx.display;

import cn.kizzzy.data.FieldsFile;
import cn.kizzzy.javafx.display.Display;
import cn.kizzzy.javafx.display.DisplayAAA;
import cn.kizzzy.javafx.display.DisplayAttribute;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.qqhx.CseFile;
import cn.kizzzy.qqhx.vfs.converter.CseFileToStringConverter;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.converter.Converter;
import cn.kizzzy.vfs.converter.StringToFieldsFileConverter;

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
            FieldsFile fieldsFile = Converter.convert(cseFile, CseFile.class)
                .load(String.class, new CseFileToStringConverter(Charset.forName("GB2312")))
                .load(FieldsFile.class, new StringToFieldsFileConverter(false, true, "\n", ","))
                .get();
            
            if (fieldsFile != null) {
                return new DisplayAAA(DisplayType.SHOW_TABLE, fieldsFile);
            }
        }
        return null;
    }
}
