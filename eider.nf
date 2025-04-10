#!/usr/bin/env nextflow

process EIDER {
  container "eider-local"

  output:
  path "out.csv", emit: csv

  script:
  '''
  eider --verbose --query "COPY(SELECT 1) TO 'out.csv'"
  '''
}

workflow {
  EIDER()
}
