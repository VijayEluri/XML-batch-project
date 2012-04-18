
Documentation Generation:
	to generate all documentation run the ant task 'xmlDocTestDist'
	to generate xsd files run the ant task 'run Dump'

	There are a lot of errors/warning but the files should be generated
	

Install into maven repository
	mvn install:install-file -Dfile=target/sql-1.0.0.jar -DgroupId=com.browsexml -DartifactId=sql -Dversion=1.0.0 -Dpackaging=jar
