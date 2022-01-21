package com.snaplogic.snaps.stringprocessor;


import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.api.Snap;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;
import com.snaplogic.snap.view.InputView;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
@General(title = "String Proc", purpose = "read given String and process",
        author = "Company Name", docLink = "http://www.docs.com/mysnap")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class StringProc implements Snap{
    String str;
    @Inject
    private DocumentUtility documentUtility;
    @Inject
    private InputViews inputViews;
    @Inject
    private OutputViews outputViews;

    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {

        propertyBuilder.describe("str", "String","String")
                .add();

    }
    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        str = propertyValues.get("str");

    }
    @Override
    public void execute() throws ExecutionException {
       /* InputView inputview = inputViews.get();
        Iterator<Document> iterator = inputViews.getDocumentsFrom(inputview);
        while (iterator.hasNext()) {
            Document doc = iterator.next(); */

            Map<String, Object> map = new LinkedHashMap();
            if (str !=null) {
                map.put("Given String in uppercase :: ", str.toUpperCase()+" ");
                map.put("Given String in Lowecase :: ", str.toLowerCase()+" ");
                map.put("Length of aString :: ", str.length()+" ");
                StringBuilder sb=new StringBuilder(str);
                map.put("Reverse of a String :: ", sb.reverse()+" ");
            }
            outputViews.write(documentUtility.newDocument(map));
        }

    @Override
    public void cleanup() throws ExecutionException {

    }
}


