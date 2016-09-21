package com.cybermkd.plugin.beetl;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.ext.web.SessionWrapper;
import org.beetl.ext.web.WebVariable;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;


public class WebRender {

    GroupTemplate gt = null;

    public WebRender(GroupTemplate gt) {
        this.gt = gt;
    }

    public void render(String key, HttpServletRequest request, HttpServletResponse response, Object... args) {
        PrintWriter writer = null;
        ServletOutputStream os = null;
        String ajaxId = null;
        Template template = null;

        try {
            int e = key.lastIndexOf("#");
            if(e != -1) {
                ajaxId = key.substring(e + 1);
                key = key.substring(0, e);
                template = this.gt.getAjaxTemplate(key, ajaxId);
            } else {
                template = this.gt.getTemplate(key);
            }

            Enumeration attrs = request.getAttributeNames();

            while(attrs.hasMoreElements()) {
                String webVariable = (String)attrs.nextElement();
                template.binding(webVariable, request.getAttribute(webVariable));
            }

            WebVariable webVariable1 = new WebVariable();
            webVariable1.setRequest(request);
            webVariable1.setResponse(response);
            template.binding("session", new SessionWrapper(webVariable1.getRequest(),webVariable1.getSession()));
            template.binding("servlet", webVariable1);
            template.binding("request", request);
            template.binding("ctxPath", request.getContextPath());
            this.modifyTemplate(template, key, request, response, args);
            if(this.gt.getConf().isDirectByteOutput()) {
                os = response.getOutputStream();
                template.renderTo(os);
            } else {
                writer = response.getWriter();
                template.renderTo(writer);
            }
        } catch (IOException var22) {
            this.handleClientError(var22);
        } catch (BeetlException var23) {
            this.handleBeetlException(var23);
        }
    }

    protected void modifyTemplate(Template template, String key, HttpServletRequest request, HttpServletResponse response, Object... args) {
    }

    protected void handleClientError(IOException ex) {
    }

    protected void handleBeetlException(BeetlException ex) {
        throw ex;
    }
}
