package com.snaplogic.snaps.secsnappack;

        import com.snaplogic.api.ConfigurationException;
        import com.snaplogic.api.ExecutionException;
        import com.snaplogic.api.Snap;
        import com.snaplogic.common.SnapType;
        import com.snaplogic.common.properties.SnapProperty;
        import com.snaplogic.common.properties.builders.PropertyBuilder;
        import com.snaplogic.snap.api.*;
        import com.snaplogic.snap.api.capabilities.*;
        import com.snaplogic.snap.view.InputView;


        import javax.inject.Inject;
        import java.math.BigInteger;
        import java.util.Iterator;
        import java.util.LinkedHashMap;
        import java.util.Map;

@General(title = "MySNAP", purpose = "read given data",
        author = "Company Name", docLink = "http://www.docs.com/mysnap")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class MySecSnapPack implements Snap {
    BigInteger id;
    String name;
    String position;
    Boolean snaplogic;

    @Inject
    private DocumentUtility documentUtility;
    @Inject
    private InputViews inputViews;
    @Inject
    private OutputViews outputViews;

    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        propertyBuilder.describe("id", "ID","ID of the Snaplogic Employee")
                .type(SnapType.INTEGER)
                .add();
        propertyBuilder.describe("name", "Name","Name of the Snaplogic Employee")
                .add();
        propertyBuilder.describe("position", "Position","Position of the Employee")
                .add();
        propertyBuilder.describe("snaplogic", "Snaplogic","Is snaplogic Employee")
                .type(SnapType.BOOLEAN)
                .add();
    }
    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        //BigInteger bid = new BigInteger("id");
        //if(bid != null) {
            id = propertyValues.getInt("id");

        name = propertyValues.get("name");
        position = propertyValues.get("position");
        snaplogic = propertyValues.getBoolean("snaplogic",true);
    }
    @Override
    public void execute() throws ExecutionException {
        InputView inputview = inputViews.get();
        Iterator<Document> iterator = inputViews.getDocumentsFrom(inputview);
        while (iterator.hasNext()) {
            Document doc = iterator.next();
            Map<String, Object> map = new LinkedHashMap();
            if (snaplogic)
                map.put("Given name", name + ":: is a Snaplogic Employee");
            else
                map.put("Given name", name + ":: is not a snaplogic employee");
            outputViews.write(documentUtility.newDocument(map));
        }
    }


    @Override
    public void cleanup() throws ExecutionException {

    }
}
