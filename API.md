## API
### Group

**Get all groups**

_Response_

GET /api/group/

_Request_

* 201
```json
[
  {
    "id": "number",
    "name": "string",
    "description": "string"
  },
  {
    "id": "number",
    "name": "string",
    "description": "string"
  }
]
```
* 400 - Error Message

**Get group by ID**

_Response_

GET /api/group/{id}

_Request_

* 201
```json
  {
    "name": "string",
    "description": "string"
  }
```
* 400 - Error Message

**Update group by ID**

_Response_

PUT /api/group/{id}
```json
  {
    "name": "string",
    "description": "string"
  }
```

_Request_

* 200 - Good
* 400 - Error Message

**Add group**

_Response_

PUT /api/group/
```json
  {
    "name": "string",
    "description": "string"
  }
```

_Request_

* 201
* 201
```json
{
  "id": "number"
}
```
* 400 - Error Message

### Product

**Get all products**

_Response_

GET /api/product/

_Request_

* 201
```json
[
  {
    "id": "number",
    "name": "string",
    "description": "string",
    "vendor": "string",
    "count": "number",
    "price": "number",
    "group": "string"
  },
  {
    "id": "number",
    "name": "string",
    "description": "string",
    "vendor": "string",
    "count": "number",
    "price": "number",
    "group": "string"
  }
]
```
* 400 - Error Message

**Get product by ID**

_Response_

GET /api/product/{id}

_Request_

* 201
```json
{
  "id": "number",
  "name": "string",
  "description": "string",
  "vendor": "string",
  "count": "number",
  "price": "number",
  "group": "string"
}
```
* 400 - Error Message

**Update product by ID**

_Response_

PUT /api/product/{id}
```json
{
  "name": "string",
  "description": "string",
  "vendor": "string",
  "count": "number",
  "price": "number",
  "group": "string"
}
```

_Request_

* 200 - Good
* 400 - Error Message

**Add product**

_Response_

PUT /api/good/
```json
{
  "name": "string",
  "description": "string",
  "vendor": "string",
  "count": "number",
  "price": "number",
  "group": "string"
}
```

_Request_

* 201
```json
{
  "id": "number"
}
```
* 400 - Error Message

### Search

_Response_

GET /api/search/
```json
{
  "nameStart": "string",
  "descriptionStart": "string",
  "vendorStart": "string",
  "countFrom": "number",
  "countTo": "number",
  "priceFrom": "number",
  "priceTo": "number",
  "groupStart": "string"
}
```

_Request_

* 201
```json
[
  {
    "name": "string",
    "description": "string",
    "vendor": "string",
    "count": "number",
    "price": "number",
    "group": "string"
  },
  {
    "name": "string",
    "description": "string",
    "vendor": "string",
    "count": "number",
    "price": "number",
    "group": "string"
  }
]
```
* 400 - Error Message


### Statistics

_Response_

GET /api/statistics/

_Request_

* 201
```json
{
  "groups": 
  [
    {
      "name": "string",
      "description": "string",
      "count": "number",
      "fullPrice": "number",
      "products": [
        {
          "name": "string",
          "description": "string",
          "vendor": "string",
          "count": "number",
          "price": "number"
        },
        {
          "name": "string",
          "description": "string",
          "vendor": "string",
          "count": "number",
          "price": "number"
        }
      ]
    }
  ],
  "fullPrice": "number"
}
```
* 400 - Error Message
