dd-verify-dataset
=================

Curation tool for verifying that a dataset conforms to a set of configured policy rules.


SYNOPSIS
--------

    dd-verify-dataset { server | check }

DESCRIPTION
-----------
The service performs checks on a dataset to see if it is up to the standards defined by data managers. These standards are implemented as rules. The API is
simple. A request to perform the checks is done via an HTTP POST message:

```text
curl -v -X POST -H 'Content-Type: application/json' \
    -d '{"datasetPid": "doi:10.1234/mydoi"}' http://localhost:20345/verify
```

The response is also formatted as JSON: 

```json
{
   "errors": {
      "coordinatesWithinBounds": [
         "coordinate in field 'someFiedIdX' does not conform to scheme 'RD': -3000",
         "coordinate in field 'someFielIdY' does not conform to scheme 'latlon': invalid: abc"
         ],
      "identifiersCanBeResolved": [
         "identifier of type ORCID in field someIdentifierField cannot be resolve: no-orcid"
      ]
   }
}

```


ARGUMENTS
---------

        positional arguments:
        {server,check}         available commands
        
        named arguments:
        -h, --help             show this help message and exit
        -v, --version          show the application version and exit

EXAMPLES
--------

<!-- Add examples of invoking this module from the command line or via HTTP other interfaces -->


INSTALLATION AND CONFIGURATION
------------------------------
Currently this project is built as an RPM package for RHEL7/CentOS7 and later. The RPM will install the binaries to
`/opt/dans.knaw.nl/dd-verify-dataset` and the configuration files to `/etc/opt/dans.knaw.nl/dd-verify-dataset`.

For installation on systems that do no support RPM and/or systemd:

1. Build the tarball (see next section).
2. Extract it to some location on your system, for example `/opt/dans.knaw.nl/dd-verify-dataset`.
3. Start the service with the following command
   ```
   /opt/dans.knaw.nl/dd-verify-dataset/bin/dd-verify-dataset server /opt/dans.knaw.nl/dd-verify-dataset/cfg/config.yml 
   ```

BUILDING FROM SOURCE
--------------------
Prerequisites:

* Java 11 or higher
* Maven 3.3.3 or higher
* RPM

Steps:

    git clone https://github.com/DANS-KNAW/dd-verify-dataset.git
    cd dd-verify-dataset 
    mvn clean install

If the `rpm` executable is found at `/usr/local/bin/rpm`, the build profile that includes the RPM
packaging will be activated. If `rpm` is available, but at a different path, then activate it by using
Maven's `-P` switch: `mvn -Pprm install`.

Alternatively, to build the tarball execute:

    mvn clean install assembly:single
