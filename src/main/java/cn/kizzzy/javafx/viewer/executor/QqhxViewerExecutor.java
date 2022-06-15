package cn.kizzzy.javafx.viewer.executor;

import cn.kizzzy.data.FieldsFile;
import cn.kizzzy.helper.FileHelper;
import cn.kizzzy.helper.LogHelper;
import cn.kizzzy.helper.StringHelper;
import cn.kizzzy.javafx.common.MenuItemArg;
import cn.kizzzy.javafx.display.DisplayOperator;
import cn.kizzzy.javafx.display.DisplayTabView;
import cn.kizzzy.javafx.viewer.ViewerExecutorArgs;
import cn.kizzzy.javafx.viewer.ViewerExecutorAttribute;
import cn.kizzzy.javafx.viewer.ViewerExecutorBinder;
import cn.kizzzy.qqhx.*;
import cn.kizzzy.qqhx.vfs.handler.CseFileHandler;
import cn.kizzzy.qqhx.vfs.handler.FspFileHandler;
import cn.kizzzy.qqhx.vfs.handler.FspItemHandler;
import cn.kizzzy.qqhx.vfs.handler.MaiFileHandler;
import cn.kizzzy.qqhx.vfs.handler.MapFileHandler;
import cn.kizzzy.qqhx.vfs.handler.MfpFileHandler;
import cn.kizzzy.qqhx.vfs.handler.MspFileHandler;
import cn.kizzzy.qqhx.vfs.handler.ResFileHandler;
import cn.kizzzy.qqhx.vfs.handler.SfpFileHandler;
import cn.kizzzy.qqhx.vfs.pack.FspPackage;
import cn.kizzzy.qqhx.vfs.tree.FspTreeBuilder;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.ITree;
import cn.kizzzy.vfs.handler.BufferedImageHandler;
import cn.kizzzy.vfs.handler.FieldsFileHandler;
import cn.kizzzy.vfs.handler.JsonFileHandler;
import cn.kizzzy.vfs.handler.StringFileHandler;
import cn.kizzzy.vfs.pack.FilePackage;
import cn.kizzzy.vfs.tree.FileTreeBuilder;
import cn.kizzzy.vfs.tree.IdGenerator;
import cn.kizzzy.vfs.tree.Leaf;
import cn.kizzzy.vfs.tree.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@ViewerExecutorAttribute
public class QqhxViewerExecutor extends AbstractViewerExecutor {
    
    private static final String CONFIG_PATH = "qqhx/local.config";
    
    private QqhxConfig config;
    
    @Override
    public void initialize(ViewerExecutorArgs args) {
        IPackage userVfs = args.getUserVfs();
        userVfs.addHandler(QqhxConfig.class, new JsonFileHandler<>(QqhxConfig.class));
        
        config = userVfs.load(CONFIG_PATH, QqhxConfig.class);
        config = config != null ? config : new QqhxConfig();
    }
    
    @Override
    public void stop(ViewerExecutorArgs args) {
        IPackage userVfs = args.getUserVfs();
        userVfs.save(CONFIG_PATH, config);
    }
    
    @Override
    public void initOperator(DisplayTabView tabView, IPackage vfs) {
        displayer = new DisplayOperator<>("cn.kizzzy.qqhx.display", tabView, IPackage.class);
        displayer.load();
        displayer.setContext(vfs);
    }
    
    @Override
    public Iterable<MenuItemArg> showContext(ViewerExecutorArgs args, Node selected) {
        List<MenuItemArg> list = new ArrayList<>();
        list.add(new MenuItemArg(1, "加载/FSP(QQHX)", event -> loadFile(args)));
        list.add(new MenuItemArg(1, "加载/目录(QQHX)", event -> loadFolder(args)));
        if (selected != null) {
            list.add(new MenuItemArg(0, "设置", event -> openSetting(args, config)));
            list.add(new MenuItemArg(2, "打开/根目录", event -> openFolderQqfoData(args)));
            list.add(new MenuItemArg(2, "打开/文件目录", event -> openFolderExportFile(args)));
            list.add(new MenuItemArg(2, "打开/图片目录", event -> openFolderExportImage(args)));
            list.add(new MenuItemArg(3, "导出/文件", event -> exportFile(args, selected, false)));
            list.add(new MenuItemArg(3, "导出/文件(递归)", event -> exportFile(args, selected, true)));
            list.add(new MenuItemArg(3, "导出/图片", event -> exportImage(args, selected, false, false)));
            list.add(new MenuItemArg(3, "导出/图片(递归)", event -> exportImage(args, selected, true, false)));
            list.add(new MenuItemArg(3, "导出/图片(等尺寸)", event -> exportImage(args, selected, false, true)));
            list.add(new MenuItemArg(3, "导出/图片(等尺寸)(递归)", event -> exportImage(args, selected, true, true)));
            if (selected.leaf) {
                list.add(new MenuItemArg(9, "复制路径", event -> copyPath(selected)));
            }
        }
        return list;
    }
    
    private void loadFile(ViewerExecutorArgs args) {
        Stage stage = args.getStage();
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择fsp文件");
        if (StringHelper.isNotNullAndEmpty(config.last_pkg)) {
            File lastFolder = new File(config.last_pkg);
            if (lastFolder.exists()) {
                chooser.setInitialDirectory(lastFolder);
            }
        }
        
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("FSP", "*.fsp"),
            new FileChooser.ExtensionFilter("ALL", "*.*")
        );
        
        File file = chooser.showOpenDialog(stage);
        if (file != null && file.getAbsolutePath().endsWith(".fsp")) {
            config.last_pkg = file.getParent();
            
            loadPkgImpl(args, file);
        }
    }
    
    private void loadPkgImpl(ViewerExecutorArgs args, File file) {
        IdGenerator idGenerator = args.getIdGenerator();
        
        IPackage dataVfs = new FilePackage(file.getParent());
        dataVfs.addHandler(String.class, new StringFileHandler(Charset.forName("GB2312")));
        dataVfs.addHandler(FieldsFile.class, new FieldsFileHandler());
        dataVfs.addHandler(FspFile.class, new FspFileHandler());
        dataVfs.addHandler(FspItem.class, new FspItemHandler());
        dataVfs.addHandler(CseFile.class, new CseFileHandler());
        dataVfs.addHandler(ResFile.class, new ResFileHandler());
        dataVfs.addHandler(SfpFile.class, new SfpFileHandler());
        dataVfs.addHandler(MfpFile.class, new MfpFileHandler());
        dataVfs.addHandler(MspFile.class, new MspFileHandler());
        dataVfs.addHandler(MapFile.class, new MapFileHandler());
        dataVfs.addHandler(MaiFile.class, new MaiFileHandler());
        
        String path = FileHelper.getName(file.getAbsolutePath());
        FspFile fspFile = dataVfs.load(path, FspFile.class);
        if (fspFile == null) {
            return;
        }
        
        ITree tree = new FspTreeBuilder(fspFile, idGenerator).build();
        IPackage pkgVfs = new FspPackage(file.getParent(), tree);
        pkgVfs.addHandler(String.class, new StringFileHandler(Charset.forName("GB2312")));
        
        args.getObservable().setValue(new ViewerExecutorBinder(pkgVfs, this));
    }
    
    private void loadFolder(ViewerExecutorArgs args) {
        Stage stage = args.getStage();
        
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择根目录");
        if (StringHelper.isNotNullAndEmpty(config.last_folder)) {
            File lastFolder = new File(config.last_folder);
            if (lastFolder.exists()) {
                chooser.setInitialDirectory(lastFolder);
            }
        }
        
        File file = chooser.showDialog(stage);
        if (file != null) {
            config.last_folder = file.getAbsolutePath();
            
            loadFolderImpl(args, file);
        }
    }
    
    private void loadFolderImpl(ViewerExecutorArgs args, File file) {
        IdGenerator idGenerator = args.getIdGenerator();
        
        ITree tree = new FileTreeBuilder(file.getAbsolutePath(), idGenerator).build();
        IPackage rootVfs = new FilePackage(file.getAbsolutePath(), tree);
        rootVfs.addHandler(String.class, new StringFileHandler(Charset.forName("GB2312")));
        rootVfs.addHandler(FieldsFile.class, new FieldsFileHandler(Charset.forName("GB2312"), false, true, "\r\n", ","));
        rootVfs.addHandler(FspFile.class, new FspFileHandler());
        rootVfs.addHandler(FspItem.class, new FspItemHandler());
        rootVfs.addHandler(CseFile.class, new CseFileHandler());
        rootVfs.addHandler(ResFile.class, new ResFileHandler());
        rootVfs.addHandler(SfpFile.class, new SfpFileHandler());
        rootVfs.addHandler(MfpFile.class, new MfpFileHandler());
        rootVfs.addHandler(MspFile.class, new MspFileHandler());
        rootVfs.addHandler(MapFile.class, new MapFileHandler());
        rootVfs.addHandler(MaiFile.class, new MaiFileHandler());
        
        args.getObservable().setValue(new ViewerExecutorBinder(rootVfs, this));
    }
    
    private void openFolderQqfoData(ViewerExecutorArgs args) {
        openFolderImpl(config.last_pkg);
    }
    
    private void openFolderExportFile(ViewerExecutorArgs args) {
        openFolderImpl(config.export_file_path);
    }
    
    private void openFolderExportImage(ViewerExecutorArgs args) {
        openFolderImpl(config.export_image_path);
    }
    
    private void exportFile(ViewerExecutorArgs args, Node selected, boolean recursively) {
        Stage stage = args.getStage();
        IPackage vfs = args.getVfs();
        
        if (selected == null) {
            return;
        }
        
        if (StringHelper.isNullOrEmpty(config.export_file_path) || !new File(config.export_file_path).exists()) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("选择保存文件的文件夹");
            File file = chooser.showDialog(stage);
            if (file == null) {
                return;
            }
            config.export_file_path = file.getAbsolutePath();
        }
        
        IPackage target = null;
        
        List<Leaf> list = vfs.listLeaf(selected, true);
        for (Leaf leaf : list) {
            try {
                if (target == null) {
                    String pkgName = leaf.pack.replace(".fsp", "");
                    target = new FilePackage(config.export_file_path + "/" + pkgName);
                }
                
                byte[] data = vfs.load(leaf.path, byte[].class);
                if (data != null && data.length > 0) {
                    target.save(leaf.path, data);
                }
            } catch (Exception e) {
                LogHelper.info(String.format("export file failed: %s", leaf.path), e);
            }
        }
    }
    
    private void exportImage(ViewerExecutorArgs args, Node selected, boolean recursively, boolean fixed) {
        Stage stage = args.getStage();
        IPackage vfs = args.getVfs();
        
        if (StringHelper.isNullOrEmpty(config.export_image_path) || !new File(config.export_image_path).exists()) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("选择保存图片的文件夹");
            File file = chooser.showDialog(stage);
            if (file == null) {
                return;
            }
            config.export_image_path = file.getAbsolutePath();
        }
        
        if (selected == null) {
            return;
        }
        
        IPackage target = null;
        
        List<Leaf> list = vfs.listLeaf(selected, recursively);
        for (Leaf leaf : list) {
            try {
                if (target == null) {
                    String pkgName = leaf.pack.replace(".pkg", "");
                    target = new FilePackage(config.export_image_path + "/" + pkgName);
                    target.addHandler(BufferedImage.class, new BufferedImageHandler());
                }
                
                if (leaf.path.contains(".gso")) {
                    System.out.println("export: " + leaf.path);
                    /*
                    GsoFile file = vfs.load(leaf.path, GsoFile.class);
                    if (file != null) {
                        for (GsoFileItems items : file.items) {
                            for (GsoFileItem item : items.items) {
                                if (item != null) {
                                    BufferedImage image = fixed ? QqfoImgHelper.toImageFix(item) : QqfoImgHelper.toImage(item);
                                    if (image != null) {
                                        String fullPath = leaf.path.replace(".gso", String.format("-%02d-%02d.png", item.i, item.j));
                                        target.save(fullPath, image);
                                    }
                                }
                            }
                        }
                    }*/
                }
            } catch (Exception e) {
                LogHelper.info(String.format("export image failed: %s", leaf.name), e);
            }
        }
    }
}
