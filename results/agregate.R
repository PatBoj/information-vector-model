library(dplyr)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))

params <- 11
breaks <- 220
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

for(i in 1:length(filesGroups)) {
  rawData <- sapply(filesGroups[[i]], function(x) {
      data.frame(read.table(paste(dirName, x, sep=""), skip = params, sep = "\t", header = TRUE))
    })
  
  parameters <- lapply(filesGroups[[i]], function(x) {
      data.frame(read.table(paste(dirName, x, sep=""), nrows = params, sep = "\t"))
    })
  
  rawData <- lapply(rawData, function(x) {
      data.frame(table(x))$Freq
    })
  
  rawData <- as.data.frame(unlist(rawData, use.names=FALSE))
  parameters <- parameters[[1]]
  
  tempHist <- hist(log10(rawData[[1]]),
                   breaks = breaks,
                   plot = FALSE)
    
  factor <- 10^tempHist$breaks[-1] - 10^tempHist$breaks[-length(tempHist$breaks)]
  sum <- sum(tempHist$counts)
  tempHist <- data.frame(x = tempHist$mids, y = tempHist$counts/factor/sum)
  tempHist <- tempHist[tempHist$y != 0,]
  tempHist$y <- log10(tempHist$y)
  
  trimmed <- gsub("(\\D+)(__)(.*)(\\.txt)", "\\1_\\3_hist\\4", trimmedFileNames[i])
  newFile <- file(paste(gsub("/", "", dirName), "_hist/", trimmed, sep=""), open = "wt")
    
  apply(parameters, MARGIN=1, function(x) {
    writeLines(paste(x, collapse="\t"), sep="", newFile)
    writeLines("", newFile)
  })
  writeLines("", newFile)
    
  apply(tempHist, MARGIN=1, function(x) {
    writeLines(paste(as.character(x), collapse="\t"), sep="", newFile)
    writeLines("", newFile)
  })
    
  close(newFile)
}
