package cn.kizzzy.qqhx.display;

import cn.kizzzy.data.FieldsFile;
import cn.kizzzy.javafx.display.Display;
import cn.kizzzy.javafx.display.DisplayAAA;
import cn.kizzzy.javafx.display.DisplayAttribute;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.converter.Converter;
import cn.kizzzy.vfs.converter.FieldsFileConverter;

@DisplayAttribute(suffix = {
    "csv",
})
public class CsvDisplay extends Display<IPackage> {
    
    public CsvDisplay(IPackage context, String path) {
        super(context, path);
    }
    
    @Override
    public DisplayAAA load() {
        String text = context.load(path, String.class);
        if (text != null) {
            FieldsFile fieldsFile = Converter.convert(text, String.class)
                .load(FieldsFile.class, new FieldsFileConverter(false, true, "\n", ","))
                .get();
            if (fieldsFile != null) {
                return new DisplayAAA(DisplayType.SHOW_TABLE, fieldsFile);
            }
            return new DisplayAAA(DisplayType.SHOW_TEXT, text);
        }
        
        return null;
    }
}
