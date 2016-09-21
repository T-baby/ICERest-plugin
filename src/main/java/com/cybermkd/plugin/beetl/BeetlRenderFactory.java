package com.cybermkd.plugin.beetl;


import com.cybermkd.common.Render;
import com.cybermkd.log.Logger;
import com.cybermkd.route.render.RenderFactory;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.WebAppResourceLoader;

import java.io.IOException;


public class BeetlRenderFactory extends RenderFactory {
    private static final Logger logger = Logger.getLogger(BeetlRender.class);
    public static String viewExtension = ".html";
    public static GroupTemplate groupTemplate = null;


    public BeetlRenderFactory(String directory) {
        init(null,directory);
    }

    public BeetlRenderFactory(String root,String directory) {
        init(root,directory);
    }

    private void init(String root,String directory) {
        if (groupTemplate != null) {
            groupTemplate.close();
        }
        try {
            Configuration cfg = Configuration.defaultConfiguration();
            WebAppResourceLoader resourceLoader = new WebAppResourceLoader(root);
            resourceLoader.setRoot(resourceLoader.getRoot()+directory);
            groupTemplate = new GroupTemplate(resourceLoader, cfg);
        } catch (IOException e) {
            throw new RuntimeException("加载GroupTemplate失败", e);
        }
    }

    public Render getRender(String view) {
        return new BeetlRender(groupTemplate, view);
    }

    public String getViewExtension() {
        return viewExtension;
    }

    public GroupTemplate getGroupTemplate() {
        return groupTemplate;
    }
}
