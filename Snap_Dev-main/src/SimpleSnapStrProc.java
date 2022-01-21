package com.snaplogic.snaps.stringprocessor;

import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.*;

import java.util.LinkedHashMap;
import java.util.Map;

@General(title = "SimpleSnapStrProc", purpose = "read given String and process",
        author = "Company Name", docLink = "http://www.docs.com/mysnap")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class SimpleSnapStrProc extends SimpleSnap {
    private String str;
    private boolean palindrome;
    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {

        propertyBuilder.describe("str", "String","String")
                .add();
        propertyBuilder.describe("palindrome", "Palindrome","Is palindrome or not ")
                .type(SnapType.BOOLEAN)
                .add();
    }
    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        str = propertyValues.get("str");
        palindrome=propertyValues.getBoolean("palindrome",true);
    }
    @Override
    protected void process(Document document, String s) {
        Map<String, Object> map = new LinkedHashMap();
        if (str !=null) {
            map.put("Given String in uppercase :: ", str.toUpperCase()+" ");
            map.put("Given String in Lowecase :: ", str.toLowerCase()+" ");
            map.put("Length of aString :: ", str.length()+" ");
            StringBuilder sb=new StringBuilder(str);
            map.put(":Reverse of a String : ", sb.reverse().toString()+" ");
            if(palindrome)
                map.put("given string is palindrome :: ", verifyPalindrome(str));
        }
        outputViews.write(documentUtility.newDocument(map));
    }

    private boolean verifyPalindrome(String str) {
        StringBuilder sb=new StringBuilder(str);
        return sb.reverse().toString().equals(str);
    }
}

