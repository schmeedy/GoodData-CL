# THE SIMPLEST EXAMPLE. LOADS A SIMPLE FLAT FILE WITH A STOCK QUOTE DATA

# CREATE A NEW PROJECT
CreateProject(name="Quotes");

# LOAD THE STOCK QUOTES DATA FILE
# THE DATA FILE CONFIG HAS BEEN GENERATED
LoadCsv(csvDataFile="examples/quotes/quotes.csv",header="true",configFile="examples/quotes/quotes.config.xml");

# GENERATE THE STOCK QUOTES MAQL
GenerateMaql(maqlFile="examples/quotes/quotes.maql");

# EXECUTE THE STOCK QUOTES MAQL
ExecuteMaql(maqlFile="examples/quotes/quotes.maql");

# TRANSFER THE STOCK QUOTES DATA
TransferLastSnapshot();
