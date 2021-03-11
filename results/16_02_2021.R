library(ggplot2)
library(dplyr)
library(scales)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))

params <- 11
dirName <- "16_02_2021/"
fileNames <- list.files(dirName)

rawData <- lapply(fileNames, function(x) {data.frame(read.table(paste(dirName, x, sep=""), skip = params, sep = "\t", header = TRUE))})
parameters <- lapply(fileNames, function(x) {data.frame(read.table(paste(dirName, x, sep=""), nrows = params, sep = "\t"))})

rawData <- lapply(seq_along(rawData), function(i) {cbind(rawData[[i]], network_type = rep(parameters[[i]][3,2], nrow(rawData[[i]])))})
rawData <- lapply(seq_along(rawData), function(i) {cbind(rawData[[i]], tau = rep(parameters[[i]][6,2], nrow(rawData[[i]])))})
rawData <- lapply(seq_along(rawData), function(i) {cbind(rawData[[i]], eta = rep(parameters[[i]][5,2], nrow(rawData[[i]])))})

data <- lapply(rawData, function(x) {
  data.frame(
    x %>% 
      group_by(message_id, network_type, tau, eta) %>%
      summarise(counts = n())
    )
  })

#data <- bind_rows(data, .id = "realisation")
data <- bind_rows(data)
data$network_type <- as.factor(data$network_type)
data$tau <- as.factor(data$tau)
data$eta <- as.factor(data$eta)

newData <- vector(mode = "list")
breaks <- 220

for(i in 1:length(levels(data$network_type))) {
  for(j in 1:length(levels(data$tau))) {
    for(k in 1:length(levels(data$eta))) {
      tempHist <- hist(log10(data[data$network_type == levels(data$network_type)[i] &
                              data$tau == levels(data$tau)[j] &
                              data$eta == levels(data$eta)[k], 5]),
                       breaks = breaks,
                       plot = FALSE)
      newData[[length(newData) + 1]] <- tempHist
    }
  }
}


popularityHistogram <- function() {
  breaks <- 220
  histogram <- hist(log10(data$counts), breaks = breaks)
  factor <- 10^histogram$breaks[-1] - 10^histogram$breaks[-length(histogram$breaks)]
  sum <- sum(histogram$counts/factor)
  histogram <- data.frame(x = histogram$mids, y = histogram$counts/factor/sum)
  histogram <- histogram[histogram$y != 0,]
  
  plot <- ggplot(data = histogram, aes(x = x, y = y)) + 
    geom_point(size = 3) +
    theme(
      text=element_text(size=28),
      axis.text=element_text(color="black"),
      axis.ticks.length = unit(0, "cm"),
      plot.title=element_text(hjust=0.5),
      panel.border=element_rect(fill=NA),
      panel.background=element_blank(),
      legend.key=element_rect(fill=NA, color=NA),
      legend.background=element_rect(fill=(alpha("white", 0))),
      legend.title=element_blank(),
      legend.position=c(0.6, 0.8),
      legend.box.background=element_rect(colour = "black"),
      legend.spacing.y = unit(0, "mm")
    ) + 
    xlab("number of shares") +
    ylab("probability density") +
    scale_x_continuous(labels = math_format(10^.x),
                       limits = c(0, 3),
                       breaks = seq(0,5)) + 
    scale_y_continuous(trans = 'log10',
                       labels = trans_format("log10", math_format(10^.x)),
                       limits = c(.3*10^-8, 10^0),
                       breaks = 10^(-9:0)) +
    annotation_logticks(sides="lb")
  print(plot)
}
