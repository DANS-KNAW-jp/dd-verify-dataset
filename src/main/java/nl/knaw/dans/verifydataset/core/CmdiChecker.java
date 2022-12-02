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

import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.lib.dataverse.DataverseException;
import nl.knaw.dans.lib.dataverse.model.file.FileMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CmdiChecker {
    private static final Logger log = LoggerFactory.getLogger(CmdiChecker.class);
    private static final String cmdiSchema = "http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/1.1/profiles/clarin.eu:cr1:p_1493735943953/xsd";
    private final DataverseClient dataverse;

    public CmdiChecker(DataverseClient dataverse) {
        this.dataverse = dataverse;
    }

    public List<String> find(String pid) throws IOException, DataverseException {
        return dataverse
            .dataset(pid).getLatestVersion().getData().getLatestVersion()
            .getFiles().stream()
            .filter(this::isCMDI)
            .map(this::fileName)
            .collect(Collectors.toList());
    }

    private String fileName(FileMeta f) {
        return f.getDirectoryLabel() + "/" + f.getLabel();
    }

    private boolean isCMDI(FileMeta f) {
        String contentType = f.getDataFile().getContentType();
        int fileId = f.getDataFile().getId();
        String name = fileName(f);
        log.debug(String.format("checking %d %s", fileId, name));
        if (!f.getLabel().toLowerCase().endsWith(".xml")) {
            return false;
        }
        if (!contentType.toLowerCase().endsWith("xml")) {
            log.error(String.format("not expected content type [%s] for %s", contentType, name));
            return false;
        }
        try {
            log.debug(String.format("requesting %d %s", fileId, name));
            var response = dataverse
                .basicFileAccess(fileId)
                .getFile();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != Response.Status.OK.getStatusCode()) {
                log.error(String.format("could not read %d %s, status code:", fileId, name), statusCode);
                return false;
            }
            log.debug(String.format("reading %d %s", fileId, name));
            try (var is = response.getEntity().getContent()) {
                // TODO validate with schema
                return new String(is.readAllBytes()).toLowerCase().contains("cmdi");
            }
        }
        catch (DataverseException | IOException e) {
            log.error(String.format("Exception while reading %d %s", fileId, name), e);
        }
        return false;
    }
}