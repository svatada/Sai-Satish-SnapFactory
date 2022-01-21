package com.snaplogic.snaps.inputSchema;
import com.google.common.collect.ImmutableMap;
import com.snaplogic.api.InputSchemaProvider;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.*;
import com.snaplogic.snap.schema.api.Schema;
import com.snaplogic.snap.schema.api.SchemaBuilder;
import com.snaplogic.snap.schema.api.SchemaProvider;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.snaplogic.snaps.inputSchema.Messages.*;

@General(title=SNAP_TITLE,purpose =SNAP_DESC,
        author = SNAP_AUTHOR, docLink=SNAP_DOC)
@Inputs(min = 1, max=1, accepts = {ViewType.DOCUMENT})
@Outputs(min =1, max =1, offers = {ViewType.DOCUMENT})
@Version(snap =1)
@Category(snap = SnapCategory.READ)
public class InputSchemaSample extends SimpleSnap implements InputSchemaProvider {
    private static final String SCHEMA_KEY="schema";
    private static final String TYPE_INT="Int";
    private static final String TYPE_STRING="String";
    private static final String TYPE_BOOL="Boolean";
    private static final Map<String, SnapType> TYPES = ImmutableMap.of(
            TYPE_INT,SnapType.INTEGER,
            TYPE_STRING,SnapType.STRING,
            TYPE_BOOL, SnapType.BOOLEAN
    );
    private String schema;
    private Throwable error;
    @Override
    public void defineProperties(PropertyBuilder pb) {
        pb.describe(SCHEMA_KEY, SCHEMA_LABEL, SCHEMA_DESC)
                .uiRowCount(10)
                .required()
                .add();
    }
    @Override
    public void configure(PropertyValues pv){
        schema = pv.get(SCHEMA_KEY);
    }
    @Override
    protected void process(Document document, String s) {
        Map<String ,Object> m = new LinkedHashMap<>();
        m.put(" Result :: ", error==null ? "success" : "failure "+ error.getMessage());
        outputViews.write(documentUtility.newDocument(m));
    }

    @Override
    public void defineInputSchema(SchemaProvider schemaProvider) {
        try{
            Collection<String> views = schemaProvider.getRegisteredInputViewNames();
            if(!views.isEmpty()){
                String vn = views.iterator().next();
                SchemaBuilder sb = schemaProvider.getSchemaBuilder(vn);
                String[] fields = schema.split(System.lineSeparator());
                for(String field : fields){
                    String[] parts = field.split("\\s+");
                    Schema sc = schemaProvider.createSchema(TYPES.get(parts[1]),parts[0]);
                    sb.withChildSchema(sc);
                }
                sb.build();
            }
        }catch (Throwable t){
             error = t;

        }

    }
}
