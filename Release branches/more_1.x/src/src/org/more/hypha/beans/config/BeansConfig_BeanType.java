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
package org.more.hypha.beans.config;
import java.util.List;
import org.more.core.xml.XmlElementHook;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * ���ڽ���c:beanType��ǩ
 * @version : 2011-4-22
 * @author ������ (zyc@byshell.org)
 */
public class BeansConfig_BeanType extends BeansConfig_NS implements XmlElementHook {
    public BeansConfig_BeanType(XmlDefineResource configuration) {
        super(configuration);
    }
    public void beginElement(XmlStackDecorator context, String xpath, StartElementEvent event) {
        List<B_BeanType> btList = (List<B_BeanType>) context.getAttribute(BeansConfig_BeanTypeConfig.BTConfigList);
        B_BeanType bt = new B_BeanType();
        bt.settName(event.getAttributeValue("tName"));
        bt.setClassName(event.getAttributeValue("class"));
        btList.add(bt);
    }
    public void endElement(XmlStackDecorator context, String xpath, EndElementEvent event) {}
}