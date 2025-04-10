#!/usr/bin/env nextflow

process EIDER {
  container "eider-local"

  output:
  path "out.parquet", emit: parquet

  script:
  '''
  eider --verbose --query "COPY(SELECT 1 AS value) TO 'out.parquet' (FORMAT parquet, COMPRESSION zstd)"
  '''
}

workflow {
  EIDER()
}
