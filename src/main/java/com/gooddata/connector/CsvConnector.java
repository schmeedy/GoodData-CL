package com.gooddata.connector;

import com.gooddata.connector.exceptions.InitializationException;
import com.gooddata.connector.exceptions.MetadataFormatException;
import com.gooddata.modeling.exceptions.ModelingException;
import com.gooddata.modeling.model.SourceColumn;
import com.gooddata.modeling.model.SourceSchema;
import com.gooddata.util.StringUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.derby.tools.ij.runScript;

/**
 * GoodData CSV Connector
 *
 * @author zd <zd@gooddata.com>
 * @version 1.0
 */
public class CsvConnector extends AbstractDerbyConnector {

    /**
     * The CSV data file (no headers).
     */
    private File dataFile;

    /**
     * GoodData CSV connector. This constructor creates the connector from a config file
     * @param projectId project id
     * @param name name of the source
     * @param configFileName schema config file name
     * @param dataFileName primary data file
     * @throws InitializationException issues with the initialization
     * @throws MetadataFormatException issues with the metadata definitions
     * @throws IOException in case of an IO issue 
     */
    protected CsvConnector(String projectId, String name, String configFileName, String dataFileName) throws InitializationException,
            MetadataFormatException, IOException {
        super(projectId, name, configFileName);
        this.setDataFile(new File(dataFileName));
    }

    /**
     * Create a new GoodData CSV connector. This constructor creates the connector from a config file
     * @param projectId project id
     * @param name name of the source
     * @param configFileName schema config file name
     * @param dataFileName primary data file
     * @throws InitializationException issues with the initialization
     * @throws MetadataFormatException issues with the metadata definitions
     * @throws IOException in case of an IO issue
     */
    public static CsvConnector createConnector(String projectId, String name, String configFileName, String dataFileName) throws InitializationException,
            MetadataFormatException, IOException {
        return new CsvConnector(projectId, name, configFileName, dataFileName);    
    }

    /**
     * Saves a template of the config file
     * @param configFileName the new config file name
     * @param dataFileName the data file
     * @throws IOException
     */
    public static void saveConfigTemplate(String configFileName, String dataFileName) throws IOException {
        File dataFile = new File(dataFileName);
        String name = dataFile.getName().split("\\.")[0];
        BufferedReader r = new BufferedReader(new FileReader(dataFile));
        String headerLine = r.readLine();
        r.close();
        String[] headers = headerLine.split(",");
        int i = 0;
        SourceSchema s = SourceSchema.createSchema(name);
        for(String header : headers) {
            SourceColumn sc = null;
            switch (i %3) {
                case 0:
                    sc = new SourceColumn(StringUtil.formatShortName(header),SourceColumn.LDM_TYPE_ATTRIBUTE, header,
                            "folder");
                    break;
                case 1:
                    sc = new SourceColumn(StringUtil.formatShortName(header),SourceColumn.LDM_TYPE_FACT, header,
                            "folder");
                    break;
                case 2:
                    sc = new SourceColumn(StringUtil.formatShortName(header),SourceColumn.LDM_TYPE_LABEL, header,
                            "folder", "existing-attribute-name");
                    break;
            }
            s.addColumn(sc);
            i++;
        }
        s.writeConfig(new File(configFileName));
    }

    /**
     * Extracts the source data CSV to the Derby database where it is going to be transformed
     */
    public void extract() {
        Connection con = null;
        try {
            con = connect();
            String sql = sg.generateExtract(schema, dataFile.getAbsolutePath());
            InputStream is = new ByteArrayInputStream(sql.getBytes("UTF-8"));
            int result = runScript(con, is, "UTF-8", System.out, "UTF-8");
        }
        catch (SQLException e) {
            throw new InternalError(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new InternalError(e.getMessage());
        } finally {
            try {
                if (con != null && !con.isClosed())
                    con.close();
            }
            catch (SQLException e) {
                throw new InternalError(e.getMessage());
            }
        }
    }


    /**
     * Data CSV file getter
     * @return the data CSV file
     */
    public File getDataFile() {
        return dataFile;
    }

    /**
     * Data CSV file setter
     * @param dataFile the data CSV file
     */
    public void setDataFile(File dataFile) {
        this.dataFile = dataFile;
    }

}