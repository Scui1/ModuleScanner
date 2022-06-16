# Scan binaries based on patterns
### Example usage
Compile to fatjar using shadowJar gradle task
Output will be in `build/libs`

`java -jar modulescanner.jar -port=8080 -P:moduleDirectory="your/path/to/modules"`