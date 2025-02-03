package org.genevaers.utilities;

/*
 * Copyright Contributors to the GenevaERS Project.
                                SPDX-License-Identifier: Apache-2.0 (c) Copyright IBM Corporation
                                2008
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.google.common.flogger.FluentLogger;

public class GersFilesUtils {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private  Collection<GersFile> gersFiles = new ArrayList<>();

    private String codpage = null;

    public Collection<GersFile> getGersFiles(String dir) {
        gersFiles.clear();
        if (GersConfigration.isZos()) {
            try {
                Class<?> rrc = Class.forName("org.genevaers.utilities.ZosGersFilesUtils");
                Constructor<?>[] constructors = rrc.getConstructors();
                return ((GersFilesUtils) constructors[0].newInstance()).getGersFiles(dir);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | ClassNotFoundException e) {
                logger.atSevere().log("getGersFiles failed %s", e.getMessage());
            }
            return null;

        } else {
            Path xmlPath = Paths.get(dir);
            if (xmlPath.toFile().exists()) {
                WildcardFileFilter fileFilter = new WildcardFileFilter("*.xml");
                Collection<File> xmlFiles = FileUtils.listFiles(xmlPath.toFile(), fileFilter, TrueFileFilter.TRUE);
                for (File d : xmlFiles) {
                    GersFile gf = new GersFile();
                    gf.setName(d.getAbsolutePath());
                    gersFiles.add(gf);
                }
            } else {
                logger.atSevere().log("WBXML file %s not found", xmlPath.toString());
            }
        }
        return gersFiles;
    }

    public void clear() {
        gersFiles.clear();
    }

    public String getCodePage() {
        if (GersConfigration.isZos() && codpage == null) {
            try {
                Class<?> rrc = Class.forName("org.genevaers.utilities.ZosGersCodePage");
                Constructor<?>[] constructors = rrc.getConstructors();
                codpage = ((GersFilesUtils) constructors[0].newInstance()).getCodePage();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | ClassNotFoundException e) {
                logger.atSevere().log("getGersFiles failed %s", e.getMessage());
            }
        }
        return codpage;
    }

    public static byte[] asciiToEbcdic(String str) {
        Charset utf8charset = Charset.forName("UTF-8");
        Charset ebccharset = Charset.forName(GersConfigration.getZosCodePage());
        ByteBuffer inputBuffer = ByteBuffer.wrap(str.getBytes());
        CharBuffer data = utf8charset.decode(inputBuffer);
        return ebccharset.encode(data).array();
    }

    public static byte[] ebcdicToAscii(byte[] buf) {
        Charset utf8charset = Charset.forName("ISO8859-1");
        Charset ebccharset = Charset.forName(GersConfigration.getZosCodePage());
        ByteBuffer inputBuffer = ByteBuffer.wrap(buf);
        CharBuffer data = ebccharset.decode(inputBuffer);
        return utf8charset.encode(data).array();
    }

}
