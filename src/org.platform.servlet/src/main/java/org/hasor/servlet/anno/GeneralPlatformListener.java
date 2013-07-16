/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hasor.servlet.anno;
import static org.hasor.Hasor.getIndexStr;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSessionListener;
import org.hasor.Hasor;
import org.hasor.annotation.Module;
import org.hasor.context.ApiBinder;
import org.hasor.context.AppContext;
import org.hasor.context.HasorModule;
import org.hasor.servlet.ErrorHook;
import org.more.util.StringUtils;
/**
 * 支持Bean、WebError、WebFilter、WebServlet注解功能。启动级别：Lv1Max
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@Module(displayName = "GeneralModuleServiceListener", description = "org.platform.general软件包功能支持。", startIndex = HasorModule.Lv_1Max)
public class GeneralPlatformListener implements HasorModule {
    /**初始化.*/
    @Override
    public void initialize(ApiBinder event) {
        if (event.getSettings().getBoolean("framework.generalSupport") == false) {
            Hasor.warning("init General false!");
            return;
        }
        //1.LoadFilter.
        this.loadFilter(event);
        //2.LoadServlet.
        this.loadServlet(event);
        //3.loadErrorHook.
        this.loadErrorHook(event);
        //4.WebSessionListener
        this.loadSessionListener(event);
    }
    //
    /**装载Filter*/
    protected void loadFilter(ApiBinder event) {
        //1.获取
        Set<Class<?>> webFilterSet = event.getClassSet(WebFilter.class);
        if (webFilterSet == null)
            return;
        List<Class<? extends Filter>> webFilterList = new ArrayList<Class<? extends Filter>>();
        for (Class<?> cls : webFilterSet) {
            if (Filter.class.isAssignableFrom(cls) == false) {
                Hasor.warning("not implemented Filter :%s", cls);
            } else {
                webFilterList.add((Class<? extends Filter>) cls);
            }
        }
        //2.排序
        Collections.sort(webFilterList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                WebFilter o1Anno = o1.getAnnotation(WebFilter.class);
                WebFilter o2Anno = o2.getAnnotation(WebFilter.class);
                int o1AnnoIndex = o1Anno.sort();
                int o2AnnoIndex = o2Anno.sort();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.注册
        for (Class<? extends Filter> filterType : webFilterList) {
            WebFilter filterAnno = filterType.getAnnotation(WebFilter.class);
            Map<String, String> initMap = this.toMap(filterAnno.initParams());
            event.filter(null, filterAnno.value()).through(filterType, initMap);
            //
            String filterName = StringUtils.isBlank(filterAnno.filterName()) ? filterType.getSimpleName() : filterAnno.filterName();
            Hasor.info("loadFilter %s[%s] bind %s on %s.", filterName, getIndexStr(filterAnno.sort()), filterType, filterAnno.value());
        }
    }
    //
    /**装载Servlet*/
    protected void loadServlet(ApiBinder event) {
        //1.获取
        Set<Class<?>> webServletSet = event.getClassSet(WebServlet.class);
        if (webServletSet == null)
            return;
        List<Class<? extends HttpServlet>> webServletList = new ArrayList<Class<? extends HttpServlet>>();
        for (Class<?> cls : webServletSet) {
            if (HttpServlet.class.isAssignableFrom(cls) == false) {
                Hasor.warning("not implemented HttpServlet :%s", cls);
            } else {
                webServletList.add((Class<? extends HttpServlet>) cls);
            }
        }
        //2.排序
        Collections.sort(webServletList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                WebServlet o1Anno = o1.getAnnotation(WebServlet.class);
                WebServlet o2Anno = o2.getAnnotation(WebServlet.class);
                int o1AnnoIndex = o1Anno.loadOnStartup();
                int o2AnnoIndex = o2Anno.loadOnStartup();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.注册
        for (Class<? extends HttpServlet> servletType : webServletList) {
            WebServlet servletAnno = servletType.getAnnotation(WebServlet.class);
            Map<String, String> initMap = this.toMap(servletAnno.initParams());
            event.serve(null, servletAnno.value()).with(servletType, initMap);
            //
            String servletName = StringUtils.isBlank(servletAnno.servletName()) ? servletType.getSimpleName() : servletAnno.servletName();
            int sortInt = servletAnno.loadOnStartup();
            Hasor.info("loadServlet %s[%s] bind %s on %s.", servletName, getIndexStr(sortInt), servletType, servletAnno.value());
        }
    }
    //
    /**装载异常处理程序*/
    protected void loadErrorHook(ApiBinder event) {
        //1.获取
        Set<Class<?>> webErrorSet = event.getClassSet(WebError.class);
        if (webErrorSet == null)
            return;
        List<Class<? extends ErrorHook>> webErrorList = new ArrayList<Class<? extends ErrorHook>>();
        for (Class<?> cls : webErrorSet) {
            if (ErrorHook.class.isAssignableFrom(cls) == false) {
                Hasor.warning("not implemented ErrorHook :%s", cls);
            } else {
                webErrorList.add((Class<? extends ErrorHook>) cls);
            }
        }
        //2.排序
        Collections.sort(webErrorList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                WebError o1Anno = o1.getAnnotation(WebError.class);
                WebError o2Anno = o2.getAnnotation(WebError.class);
                int o1AnnoIndex = o1Anno.sort();
                int o2AnnoIndex = o2Anno.sort();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.注册
        for (Class<? extends ErrorHook> errorHookType : webErrorList) {
            WebError errorAnno = errorHookType.getAnnotation(WebError.class);
            Map<String, String> initMap = this.toMap(errorAnno.initParams());
            event.error(errorAnno.value()).bind(errorHookType, initMap);
            //
            int sortInt = errorAnno.sort();
            Hasor.info("loadErrorHook [%s] of %s.", getIndexStr(sortInt), errorHookType);
        }
    }
    //
    /**装载HttpSessionListener*/
    protected void loadSessionListener(ApiBinder event) {
        //1.获取
        Set<Class<?>> sessionListenerSet = event.getClassSet(WebSessionListener.class);
        if (sessionListenerSet == null)
            return;
        List<Class<? extends HttpSessionListener>> sessionListenerList = new ArrayList<Class<? extends HttpSessionListener>>();
        for (Class<?> cls : sessionListenerSet) {
            if (HttpSessionListener.class.isAssignableFrom(cls) == false) {
                Hasor.warning("not implemented HttpSessionListener :%s", cls);
            } else {
                sessionListenerList.add((Class<? extends HttpSessionListener>) cls);
            }
        }
        //2.排序
        Collections.sort(sessionListenerList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                WebSessionListener o1Anno = o1.getAnnotation(WebSessionListener.class);
                WebSessionListener o2Anno = o2.getAnnotation(WebSessionListener.class);
                int o1AnnoIndex = o1Anno.sort();
                int o2AnnoIndex = o2Anno.sort();
                return (o1AnnoIndex < o2AnnoIndex ? -1 : (o1AnnoIndex == o2AnnoIndex ? 0 : 1));
            }
        });
        //3.注册
        for (Class<? extends HttpSessionListener> sessionListener : sessionListenerList) {
            event.sessionListener().bind(sessionListener);
            //
            WebSessionListener anno = sessionListener.getAnnotation(WebSessionListener.class);
            int sortInt = anno.sort();
            Hasor.info("loadSessionListener [%s] bind %s.", getIndexStr(sortInt), sessionListener);
        }
    }
    //
    /**转换参数*/
    protected Map<String, String> toMap(WebInitParam[] initParams) {
        Map<String, String> initMap = new HashMap<String, String>();
        if (initParams != null)
            for (WebInitParam param : initParams)
                if (StringUtils.isBlank(param.name()) == false)
                    initMap.put(param.name(), param.value());
        return initMap;
    }
    @Override
    public void initialized(AppContext appContext) {}
    @Override
    public void destroy(AppContext appContext) {}
}