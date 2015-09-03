package com.stratio.specs;

import static org.assertj.core.api.Assertions.assertThat;
import java.net.MalformedURLException;

import org.openqa.selenium.WebElement;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;

/**
 * Generic Given Specs.
 * 
 */
public class GivenGSpec extends BaseGSpec {

    public static final int PAGE_LOAD_TIMEOUT = 120;
    public static final int IMPLICITLY_WAIT = 10;
    public static final int SCRIPT_TIMEOUT = 30;
    
    /**
     * Generic constructor.
     * 
     * @param spec
     */
    public GivenGSpec(CommonG spec) {
        this.commonspec = spec;
    }

    /**
     * Empty all the indexes of ElasticSearch.
     */
    @Given("^I empty every existing elasticsearch index$")
    public void emptyElasticsearchIndexes() {
        commonspec.getLogger().info("Emptying es indexes");
        commonspec.getElasticSearchClient().emptyIndexes();
    }

    /**
     * Empty a specific index of ElasticSearch.
     * 
     * @param index
     */
    @Given("^I empty an elasticsearch index named '(.+?)'$")
    public void emptyElasticsearchIndex(String index) {
        commonspec.getLogger().info("Emptying an es index: {}", index);
        commonspec.getElasticSearchClient().emptyIndex(index);
    }

    /**
     * Drop all the ElasticSearch indexes.
     */
    @Given("^I drop every existing elasticsearch index$")
    public void dropElasticsearchIndexes() {
        commonspec.getLogger().info("Dropping es indexes");
        commonspec.getElasticSearchClient().dropIndexes();
    }

    /**
     * Drop an specific index of ElasticSearch.
     * 
     * @param index
     */
    @Given("^I drop an elasticsearch index named '(.+?)'$")
    public void dropElasticsearchIndex(String index) {
        commonspec.getLogger().info("Dropping an es index: {}", index);
        commonspec.getElasticSearchClient().dropIndex(index);
    }

    /**
     * Execute a cql file over a Cassandra keyspace.
     * 
     * @param filename
     * @param keyspace
     */
    @Given("a C* script with name '(.+?)' and default keyspace '(.+?)'$")
    public void insertDataOnCassandraFromFile(String filename, String keyspace) {
        commonspec.getLogger().info("Inserting data on cassandra from file");
        commonspec.getCassandraClient().loadTestData(keyspace, "/scripts/" + filename);
    }

    /**
     * Drop a Cassandra Keyspace.
     * 
     * @param keyspace
     */
    @Given("^I drop an C* keyspace '(.+?)'$")
    public void dropCassandraKeyspace(String keyspace) {
        commonspec.getLogger().info("Dropping a C* keyspace", keyspace);
        commonspec.getCassandraClient().dropKeyspace(keyspace);
    }

    /**
     * Create a AeroSpike namespace, table and the data of the table.
     * 
     * @param nameSpace
     * @param tableName
     * @param tab
     */
    @Given("^I create an AeroSpike namespace '(.+?)' with table '(.+?)':$")
    public void createAeroSpikeTable(String nameSpace, String tableName, DataTable tab) {
        commonspec.getLogger().info("Creating a table on AeroSpike");
        if (commonspec.getAerospikeClient().isConnected()) {
            commonspec.getLogger().info("Creating a table on AeroSpike");
        }
        commonspec.getAerospikeClient().insertFromDataTable(nameSpace, tableName, tab);
    }

    /**
     * Create a MongoDB dataBase.
     * 
     * @param databaseName
     */
    @Given("^I create a MongoDB dataBase '(.+?)'$")
    public void createMongoDBDataBase(String databaseName) {
        commonspec.getLogger().info("Creating a database on MongoDB");
        commonspec.getMongoDBClient().connectToMongoDBDataBase(databaseName);

    }

    /**
     * Drop MongoDB Database.
     * 
     * @param databaseName
     */
    @Given("^I drop a MongoDB database '(.+?)'$")
    public void dropMongoDBDataBase(String databaseName) {
        commonspec.getLogger().info("Creating a database on MongoDB");
        commonspec.getMongoDBClient().dropMongoDBDataBase(databaseName);
    }

    /**
     * Insert data in a MongoDB table.
     * 
     * @param dataBase
     * @param tabName
     * @param table
     */
    @Given("^I insert into a MongoDB database '(.+?)' and table '(.+?)' this values:$")
    public void insertOnMongoTable(String dataBase, String tabName, DataTable table) {
        commonspec.getLogger().info("Inserting data in a database on MongoDB");
        commonspec.getMongoDBClient().connectToMongoDBDataBase(dataBase);
        commonspec.getMongoDBClient().insertIntoMongoDBCollection(tabName, table);
    }

    /**
     * Truncate table in MongoDB.
     * 
     * @param database
     * @param table
     */
    @Given("^I drop every document at a MongoDB database '(.+?)' and table '(.+?)'")
    public void truncateTableInMongo(String database, String table) {
        commonspec.getLogger().info("Truncating a table in MongoDB");
        commonspec.getMongoDBClient().connectToMongoDBDataBase(database);
        commonspec.getMongoDBClient().dropAllDataMongoDBCollection(table);
    }

    /**
     * Browse to {@code url} using the current browser.
     * 
     * @param url
     */
    @Given("^I browse to '(.+?)'$")
    public void seleniumBrowse(String url) {
	assertThat(url).isNotEmpty();
	String newUrl = commonspec.replacePlaceholders(url);
	commonspec.getLogger().info("Browsing to {}{} with {}", commonspec.getWebURL(), newUrl, commonspec.getBrowserName());
	commonspec.getDriver().get(commonspec.getWebURL() + newUrl);
	commonspec.setParentWindow(commonspec.getDriver().getWindowHandle());
    }
    
    /**
     * Browse to {@code webHost, @code webPort} using the current browser.
     *
     * @param url
     * @throws MalformedURLException 
     */
    @Given("^I set web base url to '([^:]+?)':'([^:]+?)'$")
    public void setupWeb(String webHost, String webPort) throws MalformedURLException {
        assertThat(webHost).isNotEmpty();
        assertThat(webPort).isNotEmpty();
        String newWebHost = commonspec.replacePlaceholders(webHost);
        String newWebPort = commonspec.replacePlaceholders(webPort);
        
        commonspec.setWebHost(newWebHost);
        commonspec.setWebPort(newWebPort);
        commonspec.setWebURL("http://" + newWebHost + ":" + newWebPort + "/");
        
        commonspec.getLogger().info("Set web base URL to  http://{}:{}/", newWebHost, newWebPort);  
    }
    
    /**
     * Send requests to {@code restHost @code restPort}.
     * 
     * @param restHost
     * @param restPort
     */
    @Given("^I send requests to '([^:]+?)':'([^:]+?)'$")
    public void setupRestClient(String restHost, String restPort) {
        assertThat(restHost).isNotEmpty();
        assertThat(restPort).isNotEmpty();
        String newRestHost = commonspec.replacePlaceholders(restHost);
        String newRestPort = commonspec.replacePlaceholders(restPort);
        
        commonspec.setRestHost(newRestHost);
        commonspec.setRestPort(newRestPort);
        commonspec.setRestURL("http://" + newRestHost + ":" + newRestPort + "/");
        commonspec.getLogger().info("Sending requests to http://{}:{}", newRestHost, newRestPort);
    }
    
    /**
     * Maximizes current browser window. Mind the current resolution could break a test.
     * 
     */
    @Given("^I maximize the browser$")
    public void seleniumMaximize(String url) {
        commonspec.getDriver().manage().window().maximize();
    }

    /**
     * Switches to a frame/ iframe.
     * 
     */
    @Given("^I switch to the iframe on index '(\\d+?)' $")
    public void seleniumSwitchFrame(Integer index) {

        assertThat(commonspec.getPreviousWebElements().size()).as("There are less found elements than required")
                .isGreaterThan(index);

        WebElement elem = commonspec.getPreviousWebElements().get(index);
        commonspec.getDriver().switchTo().frame(elem);
    }

    /**
     * Switches to a parent frame/ iframe.
     * 
     */
    @Given("^I switch to a parent frame$")
    public void seleniumSwitchAParentFrame() {
        commonspec.getDriver().switchTo().parentFrame();
    }

    /**
     * Switches to the frames main container.
     * 
     */
    @Given("^I switch to the main frame container$")
    public void seleniumSwitchParentFrame() {
        commonspec.getDriver().switchTo().frame(commonspec.getParentWindow());
    }
}