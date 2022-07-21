# Underflow

An undertow based framework making your life easier than it should be.

## Running test server from a jar

For testing purposes you may want to run the tests classes from a jar file.
To do so, you will need to package the project using the `test-as-jar` profile,
then you will be able to run the tests from a jar.

```shell
mvn -P test-as-jar package
java -cp target/* com.merimdigitalmedia.underflow.tests.MainTest
```
