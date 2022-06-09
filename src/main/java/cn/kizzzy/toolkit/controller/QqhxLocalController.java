package cn.kizzzy.toolkit.controller;

import cn.kizzzy.javafx.viewer.ViewerExecutor;
import cn.kizzzy.javafx.viewer.executor.QqhxViewerExecutor;

@MenuParameter(path = "文件浏览/QQ华夏/解包器(本地)")
@PluginParameter(url = "/fxml/explorer_view.fxml", title = "文件浏览(QHX)")
public class QqhxLocalController extends ExplorerView {
    
    @Override
    public String getName() {
        return "Qqhx-Display";
    }
    
    @Override
    protected ViewerExecutor initialViewExecutor() {
        return new QqhxViewerExecutor();
    }
}