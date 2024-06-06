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

## Rules on versioning

Underflow is versioned using X.Y.Z or X.Y.Z-ExTag followed by a potential extra tag.

- X represents the major version. This number indicate an API breaking change.
- Y represents major additions to the version but keeps the existing API functional or
  Deprecate a functionality by offering a new way of working ideally with backward compatibility.
- Z represents minor additions or fixes to a version no API should change here
  except in the case of something that was forgotten during the creation of the major version.

Example

```text
1.0.0 would be the first major version of the framework.
1.1.0 would add to 1.0.0 a new major functionality
1.1.1 would either fix or improve something on 1.1.0
```

An extra tag can be added for some extra information.
This extra tag is usually use for temporary version or specific version
that should not be considered as a final release.

Example

```text
2.0.0-beta1 would indicate a beta test version of the future 2.0.0 version.
2.0.0-rc1 would indicate a release candidate of the 2.0.0 version which should in theory be stable but still in testing
```

