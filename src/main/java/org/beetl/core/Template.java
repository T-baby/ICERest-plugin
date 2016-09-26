package org.beetl.core;

import org.beetl.core.exception.BeetlException;
import org.beetl.core.io.ByteWriter_Byte;
import org.beetl.core.io.ByteWriter_Char;
import org.beetl.core.misc.BeetlUtil;
import org.beetl.core.statement.AjaxStatement;
import org.beetl.core.statement.ErrorGrammarProgram;
import org.beetl.core.statement.GrammarToken;
import org.beetl.core.statement.Program;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class Template {
    public Program program;
    public Configuration cf;
    public GroupTemplate gt;
    public boolean isRoot = true;
    public String ajaxId = null;
    Context ctx = new Context();

    protected Template(GroupTemplate gt, Program program, Configuration cf) {
        this.program = program;
        this.cf = cf;
        this.gt = gt;
    }


    public String render() throws BeetlException {
        StringWriter sw = new StringWriter();
        renderTo(sw);
        return sw.toString();
    }

    public void renderTo(Writer writer) throws BeetlException {
        ByteWriter_Char byteWriter = new ByteWriter_Char(writer, cf.charset, ctx);
        this.renderTo(byteWriter);

    }

    public void renderTo(OutputStream os) throws BeetlException {
        ByteWriter_Byte byteWriter = new ByteWriter_Byte(os, cf.charset, ctx);
        this.renderTo(byteWriter);
    }

    public void renderTo(ByteWriter byteWriter) {

        try {
            ctx.byteWriter = byteWriter;
            ctx.byteOutputMode = cf.directByteOutput;
            ctx.gt = this.gt;
            ctx.template = this;
            if (gt.sharedVars != null) {
                for (Entry<String, Object> entry : gt.sharedVars.entrySet()) {
                    ctx.set(entry.getKey(), entry.getValue());
                }
            }
            program.metaData.initContext(ctx);
            if (ajaxId != null) {
                AjaxStatement ajax = program.metaData.getAjax(ajaxId);
                if (ajax == null) {
                    BeetlException be = new BeetlException(BeetlException.AJAX_NOT_FOUND);

                    be.pushToken(new GrammarToken(ajaxId, 0, 0));
                    throw be;
                }
                ajax.execute(ctx);
            } else {
                program.execute(ctx);
            }

//            byteWriter.flush();
        } catch (BeetlException e) {
            if (!(program instanceof ErrorGrammarProgram)) {
                e.pushResource(this.program.res.id);
            }

            // �Ƿ��ӡ�쳣��ֻ�и�ģ����ܴ�ӡ�쳣
            if (!isRoot)
                throw e;

            if (e.detailCode == BeetlException.CLIENT_IO_ERROR_ERROR && ctx.gt.conf.isIgnoreClientIOError) {
                return;
            }

            Writer w = BeetlUtil.getWriterByByteWriter(ctx.byteWriter);

            e.gt = this.program.gt;
            e.cr = this.program.metaData.lineSeparator;
            ErrorHandler errorHandler = this.gt.getErrorHandler();

            if (errorHandler == null) {
                throw e;
            }
            errorHandler.processExcption(e, w);
        } catch (Exception e) {
            if (!ctx.gt.conf.isIgnoreClientIOError) {

                BeetlException be = new BeetlException(BeetlException.CLIENT_IO_ERROR_ERROR, e.getMessage(), e);
                be.pushResource(this.program.res.id);
                be.pushToken(new GrammarToken(this.program.res.id, 0, 0));
                ErrorHandler errorHandler = this.gt.getErrorHandler();

                if (errorHandler == null) {
                    throw be;
                }
                Writer w = BeetlUtil.getWriterByByteWriter(ctx.byteWriter);
                errorHandler.processExcption(be, w);

            } else {
                //do  nothing ,just ignore
            }

        }

    }


    public void binding(String varName, Object o, boolean dynamic) {
        ctx.set(varName, o, dynamic);
        // ctx.globalVar.put(varName, o);
        if (dynamic) {
            ctx.objectKeys.add(varName);
        }
    }


    public void binding(String varName, Object o) {
        this.binding(varName, o, false);
    }

    public void dynamic(Set<String> objectKeys) {
        this.ctx.objectKeys = objectKeys;
    }

    public void dynamic(String key) {
        this.ctx.objectKeys.add(key);
    }


    public void binding(Map map) {
        Map<String, Object> values = map;
        if(values==null) return ;
        for (Entry<String,Object> entry : values.entrySet())
        {
            this.binding(entry.getKey(), entry.getValue());
        }

    }

    public void fastBinding(Map map) {
        ctx.globalVar = map;
    }

    public Context getCtx() {
        return this.ctx;
    }

    //	public void fastRender(Map map, ByteWriter byteWriter)
    //	{
    //		if (ctx.isInit)
    //		{
    //			ctx.globalVar = map;
    //			// ����
    //			for (int i = ctx.tempVarStartIndex; i < ctx.vars.length; i++)
    //			{
    //				ctx.vars[i] = null;
    //			}
    //			ctx.byteWriter = byteWriter;
    //			program.metaData.replaceGlobal(ctx);
    //			program.execute(ctx);
    //		}
    //		else
    //		{
    //			ctx.globalVar = map;
    //			renderTo(byteWriter);
    //		}
    //
    //	}

}
