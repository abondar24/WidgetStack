# Widget stack

## Description

A Widget is an object on a plane in a Cartesian
coordinate system that has coordinates (X, Y),
Z-index, width, height, last modification date, and a
unique identifier. X, Y, and Z-index are integers
(may be negative).
A Z-index is a unique sequence common to all widgets that determines the order of widgets
(regardless of their coordinates). Gaps are allowed. The higher the value, the higher the widget
lies on the plane.

## API

- API DOC: http://localhost:8024/v2/api-docs
- Check Swagger UI: http://localhost:8024/swagger-ui/index.html

## Build and Run

Normal run
```yaml
mvn clean spring-boot:run
```
with in memory db
```yaml
mvn clean spring-boot:run -Pdb
```
