package com.snaplogic.snaps.stringprocessor;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@General(title = "CsvFileReader", purpose = "read given csv file",
        author = "Company Name", docLink = "http://www.docs.com/mysnap")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class CsvFileReader extends SimpleSnap {

    private String absolute_path_of_csv_file;
    private BigInteger year_considered;
    private BigInteger cut_off_per;
    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {

        propertyBuilder.describe("absolute_path_of_csv_file", "Absolute path of the CSV File", "file path")
                .add();
        propertyBuilder.describe("year_considered", "Year", "year to be considered")
                .type(SnapType.INTEGER)
                .add();
        propertyBuilder.describe("cut_off_per", "Cut of % ", " cut off percentage ")
                .type(SnapType.INTEGER)
                .add();
    }
    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        absolute_path_of_csv_file = propertyValues.get("absolute_path_of_csv_file");
        year_considered = propertyValues.getInt("year_considered");
        cut_off_per = propertyValues.getInt("cut_off_per");
    }
    @Override
    protected void process(Document document, String s) {
        String SAMPLE_CSV_FILE_PATH = absolute_path_of_csv_file;
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());
            Map<String, List<String>> map = new LinkedHashMap();
            map.put("Student_ID", new ArrayList<String>());
            map.put("Student_Name", new ArrayList<String>());
            map.put("percentage", new ArrayList<String>());
            map.put("year", new ArrayList<String>());
            for (CSVRecord csvRecord : csvParser) {
                String std_ID = csvRecord.get("Student_ID");
                String std_Name = csvRecord.get("Student_Name");
                String year = csvRecord.get("Year");
                String percentage = csvRecord.get("Percentage");
                if( Integer.parseInt(percentage) > cut_off_per.intValue() && year_considered.intValue() == Integer.parseInt(year)) {
                    map.get("Student_ID").add(std_ID);
                    map.get("Student_Name").add(std_Name);
                    map.get("year").add(year);
                    map.get("percentage").add(percentage);
                }
            }
            outputViews.write(documentUtility.newDocument(map));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
