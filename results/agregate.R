library(dplyr)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))

params <- 11 # number of saved parameters
breaks <- 220 # number of bins on histogram
dirName <- "24_05_2021/" # directory where scripts looks for files
fileNames <- list.files(dirName) # all filenames in the directory

realisations <- max(gsub("(^\\D+)([0-9]+)(.*)", "\\2", fileNames)) # number of independent realizations
trimmedFileNames <- gsub("(^\\D+)([0-9]+)(.*)", "\\1\\3", fileNames) # trimmed file names (without realization number)
trimmedFileNames <- trimmedFileNames[duplicated(trimmedFileNames)] # delete duplicates

# Groups every independent realization into one element in the list
filesGroups <- lapply(trimmedFileNames, function(x) {
  sapply(1:realisations, function(i) {
    gsub("(\\D+_)(_.*)", paste("\\1", i, "\\2", sep=""), x)
  })
})

# Saves all realizations into histogram
for(i in 1:length(filesGroups)) {
  
  # Just gets raw data from files
  rawData <- sapply(filesGroups[[i]], function(x) {
      data.frame(read.table(paste(dirName, x, sep=""), skip = params, sep = "\t", header = TRUE))
    })
  
  # Gets parameters from all files
  parameters <- lapply(filesGroups[[i]], function(x) {
      data.frame(read.table(paste(dirName, x, sep=""), nrows = params, sep = "\t"))
    })
  
  # Creates frequency table
  rawData <- lapply(rawData, function(x) {
      data.frame(table(x))$Freq
    })
  
  rawData <- as.data.frame(unlist(rawData, use.names=FALSE)) # save data as data frame
  parameters <- parameters[[1]] # uses only first element of the list as parameter (they are mostly the same)
  
  # Creates histogram of those raw data, but already in log10 scale
  tempHist <- hist(log10(rawData[[1]]),
                   breaks = breaks,
                   plot = FALSE)
    
  factor <- 10^tempHist$breaks[-1] - 10^tempHist$breaks[-length(tempHist$breaks)] # wideness of every bin
  sum <- sum(tempHist$counts) # just a sum of log10 counts
  tempHist <- data.frame(x = tempHist$mids, y = tempHist$counts/factor/sum) # normalize data
  tempHist <- tempHist[tempHist$y != 0,] # delete all zeros
  tempHist$y <- log10(tempHist$y) # take the log10 from the height of the bins 
  
  trimmed <- gsub("(\\D+)(__)(.*)(\\.txt)", "\\1_\\3_hist\\4", trimmedFileNames[i]) # add hist to new files
  newFile <- file(paste(gsub("/", "", dirName), "_hist/", trimmed, sep=""), open = "wt") # create new file
    
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
