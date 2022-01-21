package com.snaplogic.snaps.stringprocessor;

import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.*;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
@General(title = "Grade Calculator", purpose = "read given String and process",
        author = "Company Name", docLink = "http://www.docs.com/mysnap")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class GradeCalculator extends SimpleSnap{
        private String student_Name;
        private BigInteger student_ID;
        private boolean senior;
        private BigInteger m1;
        private BigInteger m2;
        private BigInteger m3;

        @Override
        public void defineProperties(PropertyBuilder propertyBuilder) {

            propertyBuilder.describe("student_Name", "Student_Name","student_Name")
                    .add();
            propertyBuilder.describe("student_ID", "Student_ID","student_ID")
                    .type(SnapType.INTEGER)
                    .add();
            propertyBuilder.describe("senior", "Senior","Is senior or not ")
                    .type(SnapType.BOOLEAN)
                    .add();
            propertyBuilder.describe("m1", "Marks1","Marks1")
                    .type(SnapType.INTEGER)
                    .add();
            propertyBuilder.describe("m2", "Marks2","Marks2")
                    .type(SnapType.INTEGER)
                    .add();
            propertyBuilder.describe("m3", "Marks3","Marks3")
                    .type(SnapType.INTEGER)
                    .add();
        }
        @Override
        public void configure(PropertyValues propertyValues) throws ConfigurationException {
            student_Name = propertyValues.get("student_Name");
            student_ID = propertyValues.getInt("student_ID");
            m1 = propertyValues.getInt("m1");
            m2 = propertyValues.getInt("m2");
            m3 = propertyValues.getInt("m3");
            senior=propertyValues.getBoolean("senior",true);
        }
        @Override
        protected void process(Document document, String s) {
            //int per = int.Value((m1.add(m2).add(m3)).divide(BigInteger.valueOf(300)).multiply(BigInteger.valueOf(100)));
            int per =(m1.intValue()+m2.intValue()+m3.intValue())/3;
            Map<String, Object> map = new LinkedHashMap();
            map.put("Student(ID-NAME) :: ", student_ID+ "-"+ student_Name);
            map.put("percentage :: ", per+" ");
            if(senior)
                map.put("Grade :: ", verifyGrade(per));
            outputViews.write(documentUtility.newDocument(map));
        }

    private String verifyGrade(int per) {
        if (per >= 90)
            return "A";
        else if (per < 90 && per >= 75)
            return "B";
        else if (per <75 && per >=55)
            return "C";
        else
            return "D";
    }
}
