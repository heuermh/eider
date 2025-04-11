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
  eider [-hV] [--preserve-whitespace] [--skip-history] [--verbose] [-i=<queryPath>] [-q=<query>] [-u=<url>] [-p=<String=String>]...
        [COMMAND]

OPTIONS
  -u, --url=<url>                    JDBC connection URL, defaults to "jdbc:duckdb:".
  -q, --query=<query>                Inline SQL query, if any.
  -i, --query-path=<queryPath>       SQL query input path, default stdin.
  -p, --parameters=<String=String>   Query template parameters, in KEY=VALUE format. Specify multiple times if necessary.
      --preserve-whitespace          Preserve whitespace in SQL query.
      --skip-history                 Skip writing query to history file.
      --verbose                      Show additional logging messages.
  -h, --help                         Show this help message and exit.
  -V, --version                      Print version information and exit.

COMMANDS
  help                 Display help information about the specified command.
  generate-completion  Generate bash/zsh completion script for eider.
```


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


### Template parameters

`${key}`-style placeholders in SQL query templates can be replaced at runtime via the `-p/--parameters` option
```bash
$ echo "SELECT ${column} FROM ${table}" > template.sql

$ eider \
   ... \
   --parameters column=foo \
   --parameters table=bar \
   --query-path template.sql
```

Note the `-p/--parameters` option can be specified multiple times if necessary.


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


## Installing eider

### Installing eider via Conda

`eider` is available in Conda via Bioconda, https://bioconda.github.io

```bash
$ conda install eider
```


### Installing eider via Docker

`eider` is available in Docker via BioContainers, https://biocontainers.pro

```bash
$ docker pull quay.io/biocontainers/eider:{tag}
```

Find `{tag}` on the tag search page, https://quay.io/repository/biocontainers/eider?tab=tags
