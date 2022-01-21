package com.snaplogic.snaps.stringprocessor;

import com.google.common.collect.ImmutableSet;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;


@General(title = "FileDirectoryReader", purpose = "read given csv file",
            author = "Snaplogic", docLink = "http://www.docs.com/mysnap")
    @Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
    @Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
    @Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
    @Version(snap = 1)
    @Category(snap = SnapCategory.READ)
        public class FileDirectoryReader extends SimpleSnap {
        private String object_Path;
        private String object_Type;
        private boolean display_attribute;
        private String operation;
        static final String FILE="File";
        static final String DIRECTORY="Directory";
        static final String DELETE="Delete";
        static final String READ="Read";

        static final Set<String> OBJECT_TYPE = ImmutableSet.of(FILE,DIRECTORY);
        static final Set<String> OPERATION = ImmutableSet.of(DELETE,READ);

        @Override
        public void defineProperties(PropertyBuilder propertyBuilder) {

            propertyBuilder.describe("objectType", " OBJECT TYPE", "file path")
                    .withAllowedValues(OBJECT_TYPE)
                    .defaultValue(DIRECTORY)
                    .add();
            propertyBuilder.describe("objectPath", "Object Path", " reads the file/directory based on given path")
                    .add();
            propertyBuilder.describe("operation", "Operation", " display's operation types")
                    .withAllowedValues(OPERATION)
                    .defaultValue(READ)
                    .add();
            propertyBuilder.describe("display_attribute", "Display Attributes ", " display attributes")
                    .defaultValue("TRUE")
                    .type(SnapType.BOOLEAN)
                    .add();

        }
        @Override
        public void configure(PropertyValues propertyValues) throws ConfigurationException {
            object_Type = propertyValues.get("objectType");
            object_Path = propertyValues.get("objectPath");
            operation = propertyValues.get("operation");
            display_attribute=Boolean.TRUE.equals(propertyValues.get("display_attribute"));
        }

        @Override
        protected void process(Document document, String s) {
            Map<String, Object> map = new LinkedHashMap();
            File check = new File(object_Path);
            if(object_Type.equals(FILE) && operation.equals(READ)){
                if(check.isFile())
                    map.put("Reading File : : ",readFile(check));
                else
                    map.put(" Given File Path ", object_Type + " is not valid ");
            }else if(object_Type.equals(FILE) && operation.equals(DELETE) && !display_attribute && check.isFile()){
                     check.delete();
                     map.put("Given file "," deleted sucessfully ");
            }
             else if(object_Type.equals(DIRECTORY) && operation.equals(READ) ){
                if(check.isDirectory())
                    map.put("Reading File : : ",readDirectory(check));
                else
                    map.put(" Given Directory Path ", object_Type + " is not valid ");
            }
             else if(object_Type.equals(DIRECTORY) && operation.equals(DELETE) && !display_attribute && check.isDirectory()) {
                check.delete();
                map.put("Given Directory ", " deleted sucessfully ");
            }
            if(display_attribute && object_Type.equals(FILE))
                map.put("Attributes :: ", getAttributes(check));
            else if(display_attribute && object_Type.equals(DIRECTORY)) {
                String[] extensions = new String[]{"txt"};
                List<File> files = (List<File>) FileUtils.listFiles(check, extensions, true);
                List<Object> lt = new ArrayList();
                for (File file : files) {
                    Object ob = getAttributes(file);
                    lt.add(ob);
                }
                    map.put("Attributes :: ", lt);
                }
            else if(!display_attribute)
                map.put(" Attributes Selected ", display_attribute);
            outputViews.write(documentUtility.newDocument(map));
            }
        private Object getAttributes(File check) {
            Path path = check.toPath();
            List<String> it = new ArrayList();
            BasicFileAttributes attr = null;
            try {
                attr = Files.readAttributes(path, BasicFileAttributes.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            it.add("File Name = " + path.getFileName().toString());
            it.add("creationTime Of File  = " + attr.creationTime());
            it.add("lastModifiedTime Of File = " + attr.lastModifiedTime());
            it.add("size Of File  = " + attr.size());

            return it;
        }

        public String readDirectory(File check) {
                String result="";
                String[] extensions = new String[] {"txt"};
                List<File> files = (List<File>) FileUtils.listFiles(check, extensions, true);
                for (File file : files) {
                    result = result.concat(readFile(file))+" ";
                }
                return result;
            }
        private String readFile(File check) {
            String str = null;
            try {
                str = FileUtils.readFileToString(check, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
}
