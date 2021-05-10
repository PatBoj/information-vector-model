library(dplyr)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))

params <- 11
dirName <- "tests/"
fileNames <- list.files(dirName)
realisations <- max(gsub("(^\\D+)([0-9]+)(.*)", "\\2", fileNames))
trimmedFileNames <- gsub("(^\\D+)([0-9]+)(.*)", "\\1\\3", fileNames)
trimmedFileNames <- trimmedFileNames[duplicated(trimmedFileNames)]
filesGroups <- lapply(trimmedFileNames, function(x) {
  sapply(1:realisations, function(i) {
    gsub("(\\D+_)(_.*)", paste("\\1", i, "\\2", sep=""), x)
  })
})

rawData <- lapply(filesGroups, function(x) {
  sapply(x, function(i) {
    data.frame(read.table(paste(dirName, i, sep=""), skip = params, sep = "\t", header = TRUE))
    })
  })

parameters <- lapply(filesGroups, function(x) {
  lapply(x, function(i) {
      data.frame(read.table(paste(dirName, i, sep=""), nrows = params, sep = "\t"))
    })
  })

rawData <- lapply(rawData, function(x) {
  lapply(x, function(y) {
    data.frame(table(y))$Freq
  })
})

rawData <- lapply(rawData, function(x) { unlist(x, use.names=FALSE) })

parameters <- lapply(parameters, function(x) { x[[1]] })

trimmedFileNames<- gsub("(\\D+)(__)(.*)(\\.txt)", "\\1_\\3_hist\\4", trimmedFileNames)

sapply(seq_along(trimmedFileNames), function(i) {
  newFile <- file(paste(gsub("/", "", dirName), "_hist/", trimmedFileNames[i], sep=""), open = "wt")
  
  apply(parameters[[i]], MARGIN=1, function(x) {
    writeLines(x, sep="\t", newFile)
    writeLines("", newFile)
  })
  writeLines("", newFile)
  
  sapply(rawData[[i]], function(x) {
    writeLines(as.character(x), newFile)
  })
  
  close(fileConn)
})