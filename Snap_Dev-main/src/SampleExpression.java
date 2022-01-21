package com.snaplogic.snaps.stringprocessor;

import com.google.common.collect.ImmutableSet;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.SnapProperty;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@General(title="SampleExp",purpose = " sample expression work cases",
        author = "snaplogic", docLink="http://www.docs.com/mysnap")
@Inputs(min = 1, max=1, accepts = {ViewType.DOCUMENT})
@Outputs(min =1, max =1, offers = {ViewType.DOCUMENT})
@Version(snap =1)
@Category(snap = SnapCategory.READ)
public class SampleExpression extends SimpleSnap {
    private static final String DEV = "Development";
    private static final String QA = "Quality Analist";
    private static final String SUPPORT = "Support";
    private static final String DOC = "Document";
    private static final String PS = "ps";
    private static final Set<String> dropDown = ImmutableSet.of(DEV,QA,DOC,SUPPORT,PS);
    private String OrgID;
    private BigInteger emp;
    private String orgName;
    private String orgAddr;
    private ExpressionProperty orgAddrExp;
    private ExpressionProperty empExp;
    private boolean active;
    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        propertyBuilder.describe("key1", " Org_ID", " Number of Deparments in Snaplogic")
                .withAllowedValues(dropDown)
                .defaultValue(DEV)
                .add();
        propertyBuilder.describe("key2", "Org_Name", " Orgnanization name ")
                .expression()
                .add();
        propertyBuilder.describe("key3", "Org_Address","Organization Address")
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .add();
        propertyBuilder.describe("key4","Active","Check box ")
                .expression()
                .defaultValue(true)
                .type(SnapType.BOOLEAN)
                .add();
        propertyBuilder.describe("key5", "Employees","Number of employe's")
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .type(SnapType.INTEGER)
                .add();
    }
    @Override
    public void configure(PropertyValues propertyValues){
        OrgID = propertyValues.get("key1");
        orgName = propertyValues.get("key2");
        orgAddrExp = propertyValues.getAsExpression("key3");
        active= Boolean.TRUE.equals(propertyValues.get("key4"));
        empExp = propertyValues.getAsExpression("key5");
    }
    @Override
    protected void process(Document document, String s) {
        orgAddr = orgAddrExp.eval(document);
        emp = empExp.eval(document);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ID :: ", OrgID);
        m.put("Name :: ", orgName);
        m.put("Address :: ", orgAddr);
        m.put("Active :: ", active);
        m.put("No of Emp :: ", emp);
        if(active )
            m.put("Grade :: ", verifyGrade(emp.intValue()));

        outputViews.write(documentUtility.newDocument(m));
    }
        private String verifyGrade(int emp) {
            if (emp >= 100)
                return "A";
            else if (emp < 100 && emp >= 50)
                return "B";
            else
                return "C";

    }
}
