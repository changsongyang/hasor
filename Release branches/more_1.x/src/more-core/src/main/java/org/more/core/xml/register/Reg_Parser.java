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
package org.more.core.xml.register;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.core.xml.XmlParserHook;
import org.more.core.xml.XmlParserKit;
import org.more.core.xml.stream.StartElementEvent;
import org.more.core.xml.stream.XmlAccept;
import org.more.core.xml.stream.XmlStreamEvent;
/**
 * 该类是为了解析regedit.xml而设立的。
 * @version 2010-9-24
 * @author 赵永春 (zyc@byshell.org)
 */
class Reg_Parser implements XmlAccept {
    private static Log         log        = LogFactory.getLog(Reg_Parser.class);
    private XmlRegister manager    = null;
    private XmlParserKit       currentKit = null;
    private String             currentNS  = null;
    public Reg_Parser(XmlRegister manager) {
        this.manager = manager;
    }
    public void beginAccept() {
        this.currentKit = null;
        this.currentNS = null;
    }
    public void endAccept() {
        this.currentKit = null;
        this.currentNS = null;
    }
    public void sendEvent(XmlStreamEvent e) throws XMLStreamException, IOException {
        if (e.getXpath().equals("/register/namespace") == true)
            this.tag_namespace(e);
        else if (e.getXpath().startsWith("/register/namespace/xmlPath") == true)
            this.tag_xmlPath(e);
    }
    /*--------------------------------------------------------------*/
    //
    private void tag_namespace(XmlStreamEvent e) {
        if (e instanceof StartElementEvent) {
            StartElementEvent ee = (StartElementEvent) e;
            this.currentKit = new XmlParserKit();
            this.currentNS = ee.getAttributeValue("url");
            log.info("regedit namespace ‘%0’.", this.currentNS);
            //TODO:xml工具没有处理schema这个属性。String schema=ee.getAttributeValue("schema");
            this.manager.regeditKit(this.currentNS, this.currentKit);
        }
    }
    private void tag_xmlPath(XmlStreamEvent e) {
        if (e instanceof StartElementEvent) {
            StartElementEvent ee = (StartElementEvent) e;
            String xmlPath = ee.getAttributeValue("xmlPath");
            String classType = ee.getAttributeValue("class");
            try {
                Class<?> hookType = Thread.currentThread().getContextClassLoader().loadClass(classType);
                this.currentKit.regeditHook(xmlPath, (XmlParserHook) hookType.newInstance());
                log.info("regedit xpath ‘%0’ parser in namespace ‘%1’.", xmlPath, this.currentNS);
            } catch (Exception e2) {
                log.error("xpath ‘%0’ in namespace ‘%1’ parser ‘%2’ create error ‘%3’.", xmlPath, this.currentNS, classType, e2);
            }
        }
    }
}