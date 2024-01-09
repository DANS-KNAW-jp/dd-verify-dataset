/*
 * Copyright (C) 2022 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.verifydataset.core;

import nl.knaw.dans.lib.dataverse.DataverseException;
import nl.knaw.dans.lib.dataverse.model.file.FileMeta;
import nl.knaw.dans.verifydataset.api.CmdiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class CmdiChecker {
    private static final Logger log = LoggerFactory.getLogger(CmdiChecker.class);
    private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private final DataverseClientWrapper dataverse;

    public CmdiChecker(DataverseClientWrapper dataverse) {
        this.dataverse = dataverse;
    }

    private String fileName(FileMeta f) {
        return (f.getDirectoryLabel() == null ? "" : f.getDirectoryLabel() + "/") + f.getLabel();
    }

    public CmdiResponse find(String pid) throws IOException, DataverseException {
        var dcmiResponse = new CmdiResponse();
        for (FileMeta fileMeta : dataverse.getFiles(pid)) {
            String contentType = fileMeta.getDataFile().getContentType();
            int fileId = fileMeta.getDataFile().getId();
            String name = fileName(fileMeta);
            String extension = fileMeta.getLabel().toLowerCase().replaceAll(".*[.]", "");
            var extensions = List.of("xml", "cmdi");
            if (extensions.contains(extension) || contentType.toLowerCase().endsWith("xml")) {
                try {
                    log.debug(String.format("requesting %d %s", fileId, name));
                    var is = dataverse.getFile(fileId);
                    // To protect from XXE attacks (XML External Entity)
                    documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");

                    Node xmlns = documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(is.getBytes()))
                        .getDocumentElement().getAttributes().getNamedItem("xmlns");
                    if (xmlns != null && xmlns.getNodeValue().toLowerCase().endsWith("//www.clarin.eu/cmd/"))
                        dcmiResponse.getCmdiFiles().add(fileName(fileMeta));
                }
                catch (Exception e) {
                    dcmiResponse.getErrorMessages().add(msgIntro(fileId, name) + e);
                }
            }
        }
        if (!dcmiResponse.getCmdiFiles().isEmpty())
            dcmiResponse.setStatus("yes");
        else if (dcmiResponse.getErrorMessages().isEmpty())
            dcmiResponse.setStatus("no");
        else
            dcmiResponse.setStatus("unknown");
        return dcmiResponse;
    }

    private String msgIntro(int fileId, String name) {
        return String.format("fileID=%d %s CAUSED ", fileId, name);
    }
}
