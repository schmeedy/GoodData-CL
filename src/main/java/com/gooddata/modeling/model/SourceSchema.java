package com.gooddata.modeling.model;

import com.gooddata.exception.ModelException;
import com.gooddata.util.StringUtil;
import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GoodData source schema. Source schema describes the structure of the source data and its mapping to the LDM types
 *
 * @author zd <zd@gooddata.com>
 * @version 1.0
 */
public class SourceSchema {

    // initial XML config comment
    public static String CONFIG_INITIAL_COMMENT = "<!-- See documentation at " +
            "http://github.com/gooddata/Java-DI-Tool/blob/master/doc/DOCUMENTATION.md#config -->\n\n";
    

    /**
     * The LDM schema name
     */
    private String name;

    /**
     * Schema columns
     */
    private List<SourceColumn> columns;

    /**
     * Constructor
     * @param name schema name
     */
    protected SourceSchema(String name) {
        this.name = name;
        columns = new ArrayList<SourceColumn>();
    }

    /**
     * Creates a new empty schema
     * @param name the schema name
     * @return new SourceSchema
     */
    public static SourceSchema createSchema(String name) {
        return new SourceSchema(name);
    }


    /**
     * Creates a new SourceSchema from the XML config file
     * @param configFile the config file
     * @return new SourceSchema
     * @throws IOException in case of an IO issue 
     */
    public static SourceSchema createSchema(File configFile) throws IOException {
        return fromXml(configFile);
    }
    
    /**
     * Creates a new SourceSchema from the XML config steam
     * @param configStream the config stream
     * @return new SourceSchema
     * @throws IOException in case of an IO issue 
     */
    public static SourceSchema createSchema(InputStream configStream) throws IOException {
        return fromXml(configStream);
    }
    
    /**
     * Get a dataset name that should be used for a server-side dataset corresponding
     * to this schema
     * @return
     */
    public String getDatasetName() {
    	return "dataset." + StringUtil.formatShortName(getName());
    }

    /**
     * Serializes the schema to XML
     * @return the xml representation of the object
     * @throws IOException in case of an IO issue
     */
    protected String toXml() throws IOException {
        XStream xstream = new XStream();
        xstream.alias("column", SourceColumn.class);
        xstream.alias("schema", SourceSchema.class);
        return xstream.toXML(this);
    }

    /**
         * Deserializes the schema from XML
         * @param configFile  the file with the XML definition
         * @throws IOException in case of an IO issue
         */
        protected static SourceSchema fromXml(File configFile) throws IOException {
            return fromXml(new FileInputStream(configFile));
        }

        /**
         * Deserializes the schema from an XML stream
         * @param is the stream of the XML definition
         * @throws IOException in case of an IO issue
         */
        protected static SourceSchema fromXml(InputStream is) throws IOException {
            XStream xstream = new XStream();
            xstream.alias("column", SourceColumn.class);
            xstream.alias("schema", SourceSchema.class);
            Reader r = new InputStreamReader(is, "utf8");
            SourceSchema schema = (SourceSchema)xstream.fromXML(r);
            r.close();
            return schema;
        }

        /**
         * Write the config file
         * @param configFile  the config file
         * @throws IOException in case of an IO issue
         */
        public void writeConfig(File configFile) throws IOException {
            Writer w = new OutputStreamWriter(new FileOutputStream(configFile), "utf8");
            w.write(CONFIG_INITIAL_COMMENT + toXml());
            w.flush();
            w.close();
        }
    

    /**
     * Columns getter
     * @return the List of all columns
     */
    public List<SourceColumn> getColumns() {
        return columns;
    }

    /**
     * Columns setter
     * @param columns the List of columns
     */
    public void setColumns(List<SourceColumn> columns) {
        this.columns = columns;
    }

    /**
     * Adds a new column
     * @param c the new column
     */
    public void addColumn(SourceColumn c) {
        this.columns.add(c);
    }

    /**
     * Name getter
     * @return schema name
     */
    public String getName() {
        return name;
    }

    /**
     * Name setter
     * @param name schema name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a column by it's name
     * @param name name to search for (case sensitive)
     * @return the matching column
     * @throws com.gooddata.exception.ModelException thrown if the column doesn't exist
     */
    public SourceColumn getColumnByName(String name) throws ModelException {
        for (SourceColumn c : columns)
            if (c.getName().equals(name))
                return c;
        throw new ModelException("Column " + name + " not found.");

    }

    /**
     * Returns a column by it's type
     * @param type type to search for (case sensitive)
     * @return the matching columns
     * @throws com.gooddata.exception.ModelException thrown if the column doesn't exist
     */
    public List<SourceColumn> getColumnByType(String type) {
        ArrayList<SourceColumn> l = new ArrayList<SourceColumn>();
        for (SourceColumn c : columns)
            if (type.equals(c.getLdmType()))
                l.add(c);
        return l;
    }

    /**
     * Returns all LABEL columns
     * @return all LABEL columns
     */
    public List<SourceColumn> getLabels() {
        return getColumnByType(SourceColumn.LDM_TYPE_LABEL);
    }

    /**
     * Returns all ATTRIBUTE columns
     * @return all ATTRIBUTE columns
     */
    public List<SourceColumn> getAttributes() {
        return getColumnByType(SourceColumn.LDM_TYPE_ATTRIBUTE);
    }

    /**
     * Returns all FACT columns
     * @return all FACT columns
     */
    public List<SourceColumn> getFacts() {
        return getColumnByType(SourceColumn.LDM_TYPE_FACT);
    }

    /**
     * Returns all REFERENCE columns
     * @return all REFERENCE columns
     */
    public List<SourceColumn> getReferences() {
        return getColumnByType(SourceColumn.LDM_TYPE_REFERENCE);
    }

    /**
     * Returns all CONNECTION POINT columns
     * @return all CONNECTION POINT columns
     */
    public List<SourceColumn> getConnectionPoints() {
        return getColumnByType(SourceColumn.LDM_TYPE_CONNECTION_POINT);
    }

    /**
     * Returns all DATE columns
     * @return all DATE POINT columns
     */
    public List<SourceColumn> getDates() {
        return getColumnByType(SourceColumn.LDM_TYPE_DATE);
    }
    
    @Override
    public String toString() {
    	return new StringBuffer(getName()).append(" [").append(getColumns()).append("]").toString();
    }

    /**
     * Validates the source schema
     * @throws ModelException in case of a validation error
     */
    public void validate() throws ModelException {
        if(name == null || name.length() <=0)
            throw new ModelException("Schema needs to have a name.");
        /*
        if(StringUtil.containsInvvalidIdentifierChar(name))
            throw new ModelException("Schema name contains invalid characters");
        */
        for(SourceColumn c : columns)
            c.validate();
    }

}