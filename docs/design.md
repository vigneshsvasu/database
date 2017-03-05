# Database

## Data structure design

```
Column
  - Values
  - Type
  - Name
  - get(int rowNumber)

LiteralColumn extends Column
  - Value
  - Size
  - @Override get(int rowNumber)

y > 4
Column() '>' Column() (LiteralColumn)
get(0) > get(0) -> yes -> include row
get(1) > get(1) -> no -> do not include row
```
