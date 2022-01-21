package com.snaplogic.snaps.inputSchema;

import com.google.common.collect.ImmutableSet;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.common.properties.builders.SuggestionBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.*;

import java.util.Set;

import static com.snaplogic.snaps.inputSchema.Messages.*;
import static com.snaplogic.snaps.inputSchema.Messages.SNAP_DOC;

@General(title=SNAP_TITLE2,purpose =SNAP_DESC2,
        author = SNAP_AUTHOR, docLink=SNAP_DOC)
@Inputs(min = 0, max=1, accepts = {ViewType.DOCUMENT})
@Outputs(min =1, max =1, offers = {ViewType.DOCUMENT})
@Version(snap =1)
@Category(snap = SnapCategory.READ)
public class SampleSuggestions extends SimpleSnap {

    private static final Set<Integer> dropDown = ImmutableSet.of(1,2,3,4);

    private String SUGGESTIONS_PROP="key";
    private String SUGGESTIONS_PROP2="key2";
    private String SUGGESTIONS_PROP3="key3";
    @Override
    public void defineProperties(PropertyBuilder pb){
        pb.describe(SUGGESTIONS_PROP, SUGGESTIONS_LABEL, SUGGESTIONS_DESC)
                .withAllowedValues(dropDown)
                .defaultValue(1)
                .type(SnapType.INTEGER)
                .add();
        pb.describe(SUGGESTIONS_PROP2,SUGGESTIONS_LABEL2 , SUGGESTIONS_DESC2)
                .required()
                .withSuggestions((suggestionBuilder, PropertyValues) -> suggestionBuilder
                        .node(SUGGESTIONS_PROP2)
                        .suggestions(new String[]{"MECH","CSE","ECE","EEE"}))
                .add();
        pb.describe(SUGGESTIONS_PROP3, SUGGESTIONS_LABEL3, SUGGESTIONS_DESC3)
                .required()
                .withSuggestions((suggestionBuilder, PropertyValues) -> {
                    String spreadsheetName = PropertyValues.get(SUGGESTIONS_PROP2);
                    if (spreadsheetName.equals("MECH")) {
                        suggestionBuilder.node(SUGGESTIONS_PROP3)
                                .suggestions(new String[]{"Design", "Production"});}
                    else if (spreadsheetName.equals("CSE")) {
                        suggestionBuilder.node(SUGGESTIONS_PROP3)
                                .suggestions(new String[]{"Developer", "QA"});}
                    else if (spreadsheetName.equals( "ECE") ){
                        suggestionBuilder.node(SUGGESTIONS_PROP3)
                                .suggestions(new String[]{"Chip design", "Production"});}
                    else if (spreadsheetName.equals("EEE") ){
                        suggestionBuilder.node(SUGGESTIONS_PROP3)
                                .suggestions(new String[]{"AE", "SE"});}

                })
                .add();
               }
    
    @Override
    protected void process(Document document, String s) {

    }
}
