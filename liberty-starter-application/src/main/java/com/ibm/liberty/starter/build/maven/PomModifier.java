/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ibm.liberty.starter.build.maven;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class PomModifier {

    private final Document pom;
    private Set<PomModifierCommand> commands;

    public PomModifier(InputStream inputStream, Set<PomModifierCommand> commands) throws ParserConfigurationException, SAXException, IOException {
        this.pom = DomUtil.getDocument(inputStream);
        this.commands = commands;
    }

    public byte[] getPomBytes() throws TransformerException, IOException {
        processCommands();
        return convertToBytes();
    }

    private void processCommands() throws IOException {
        for (PomModifierCommand command : commands) {
            command.modifyPom(pom);
        }
    }

    private byte[] convertToBytes() throws TransformerException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(pom);
        StreamResult streamResult = new StreamResult(baos);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(domSource, streamResult);
        return baos.toByteArray();
    }
    
}
