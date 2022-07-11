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

```json
{ 
  "message": "string"
}
```

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

```json
{ 
  "message": "string"
}
```

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

* 204 - Good
* 400 - Error Message

```json
{ 
  "message": "string"
}
```

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
```json
{
  "id": "number"
}
```

* 400 - Error Message
```json
{ 
  "message": "string"
}
```

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
```json
{ 
  "message": "string"
}
```

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

```json
{ 
  "message": "string"
}
```

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

* 204 - Good
* 400 - Error Message

```json
{ 
  "message": "string"
}
```

```json
{ 
  "message": "string"
}
```


**Add product**

_Response_

PUT /api/product/
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

**Add product count**

_Response_

PUT /api/product/add
```json
{
  "name": "string",
  "count": "number"
}
```

_Request_

* 201
```json
{
  "count": "number"
}
```
* 400 - Error Message
```json
{ 
  "message": "string"
}
```


**Remove product count**

_Response_

PUT /api/product/remove
```json
{
  "name": "string",
  "count": "number"
}
```

_Request_

* 201
```json
{
  "count": "number"
}
```
* 400 - Error Message

```json
{ 
  "message": "string"
}
```


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

```json
{ 
  "message": "string"
}
```

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

```json
{ 
  "message": "string"
}
```

