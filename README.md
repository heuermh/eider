# eider

Command line tools for [DuckDB](https://duckdb.org).

<br/>

![Photo of Polysticta stelleri, by Ray Hennessy](/images/eider.jpg)

Photo from https://www.rayhennessy.com


### Etymology

> Steller's eider (_Polysticta stelleri_) is a migrating Arctic diving duck that breeds
> along the coastlines of Alaska. It is the rarest, smallest, and fastest flying of the
> eider species.

https://en.wikipedia.org/wiki/Steller%27s_eider


## Hacking eider

Install

 * JDK 17 or later, https://openjdk.java.net
 * Apache Maven 3.6.3 or later, https://maven.apache.org

To build
```bash
$ mvn package

$ export PATH=$PATH:`pwd`/target/appassembler/bin
```

## Using eider

### Usage

```bash
$ eider --help
USAGE
  eider [-hV] [--skip-history] [--verbose] [-i=<queryPath>] [-q=<query>] [-u=<url>] [COMMAND]

OPTIONS
  -u, --url=<url>                JDBC connection URL, defaults to "jdbc:duckdb:".
  -q, --query=<query>            Inline SQL query, if any.
  -i, --query-path=<queryPath>   SQL query input path, default stdin.
      --skip-history             Skip writing query to history file.
      --verbose                  Show additional logging messages.
  -h, --help                     Show this help message and exit.
  -V, --version                  Print version information and exit.

COMMANDS
  help                 Display help information about the specified command.
  generate-completion  Generate bash/zsh completion script for eider.
```

### Environment variables


### SQL queries

SQL queries can be provided inline via the `-q`/`--query` option
```bash
$ eider \
    ... \
    --query "SELECT * FROM table LIMIT 4"
```

By default the SQL query is read from `stdin`
```bash
$ echo "SELECT * FROM table LIMIT 4" | eider \
    ... \
```

Or the SQL query can be read from a file via the `-i`/`--query-path` option
```bash
$ echo "SELECT * FROM table LIMIT 4" > query.sql

$ eider \
    ... \
    --query-path query.sql
```

### Execution parameters


### SQL query history file

SQL queries are written to a history file `~/.eider_history`, unless `--skip-history` flag is present
```bash
$ eider \
    ... \
    --query "SELECT * FROM table LIMIT 4"

$ eider \
    ... \
    --skip-history \
    --query "SELECT * FROM table WHERE foo = 'top secret!!' LIMIT 4"

$ cat ~/.eider_history
SELECT * FROM table LIMIT 4
```
