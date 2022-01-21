package com.snaplogic.snaps.stringprocessor;

import com.google.common.collect.ImmutableSet;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.SnapProperty;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@General(title = "LookUp", purpose = "read given csv file",
        author = "Snaplogic", docLink = "http://www.docs.com/mysnap")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class LookUp extends SimpleSnap {
    private BigInteger id;
    private String name;
    private String address;
    private BigInteger clas;
    private String grade;
    private ExpressionProperty idExp;
    private ExpressionProperty addExp;
    private static final Set<Integer> SET = ImmutableSet.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        SnapProperty idProperty = propertyBuilder.describe("key1", "ID", " Student's ID")
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .required()
                .type(SnapType.INTEGER)
                .build();
        SnapProperty nameProperty = propertyBuilder.describe("key2", "Name", " Student's Name")
                .required()
                .build();
        SnapProperty addressProperty = propertyBuilder.describe("key3", "Address", " Student's Address")
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .uiRowCount(10)
                .required()
                .build();
        List<SnapProperty> list1 = new ArrayList<>();
        list1.add(idProperty);
        list1.add(nameProperty);
        list1.add(addressProperty);
        propertyBuilder.describe("key4", " Moreinfo ", " Info about Students")
                .type(SnapType.COMPOSITE)
                .withEntries(list1)
                .add();
        SnapProperty classProperty = propertyBuilder.describe("key5", "Class", "class of student")
                .withAllowedValues(SET)
                .required()
                .type(SnapType.INTEGER)
                .build();

        SnapProperty gradeProperty = propertyBuilder.describe("key6", "Grade", "Grade of a Student")
                .required()
                .build();
        List<SnapProperty> list2 = new ArrayList<>();
        list2.add(classProperty);
        list2.add(gradeProperty);
        propertyBuilder.describe("key7", " Performance Info ", " Performance Info about Students")
                .type(SnapType.COMPOSITE)
                .withEntries(list2)
                .add();
    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        idExp = propertyValues.getAsExpression("key4.value.key1");
        name = propertyValues.get("key4.value.key2");
        addExp = propertyValues.getAsExpression("key4.value.key3");
        clas = propertyValues.getInt("key7.value.key5");
        grade = propertyValues.get("key7.value.key6");
    }

    @Override
    protected void process(Document document, String s) {
        id = idExp.eval(document);
        address = addExp.eval(document);
        CSVParser parser = null;
        try {
            parser = new CSVParser(new FileReader("/home/gaian/Downloads/student.csv"),
                    CSVFormat.DEFAULT.withHeader());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> m = new LinkedHashMap<>();
        for (CSVRecord record : parser) {

            String stdid = record.get("ID");
            String stdname = record.get("NAME");
            String stdaddr = record.get("ADDRESS").replaceAll("\\s","");
            String stdcls = record.get("CLASS");
            String stdgrade = record.get("GRADE");

            if (Integer.parseInt(stdid)==id.intValue() && stdaddr.equalsIgnoreCase(address.
                    replaceAll("\\s","")) &&
                    Integer.parseInt(stdcls)==clas.intValue() && stdname.equalsIgnoreCase(name) &&
                    stdgrade.equalsIgnoreCase(grade)  ) {
                m.put("Message", " Record Already Exists");
                outputViews.write(documentUtility.newDocument(m));
                return;
            }
        }
        m.put("Message", " Record Appended ");
        outputViews.write(documentUtility.newDocument(m));

    }
}


