# Underflow

An undertow based framework making your life easier than it should be.

## Running underflow-sample

For testing purposes you may want to run the tests classes from a jar file.
To do so, you will need to package the project using the `copy-dependencies` profile,
then you will be able to run the sample from a jar.

```shell
mvn -P copy-dependencies package
cd underflow-sample
java -cp 'target/*' com.merim.digitalpayment.underflow.tests.sample.MainSample
```
