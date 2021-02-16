library(ggplot2)
library(tikzDevice)
library(dplyr)
library(scales)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))

readRawData <- function() {
  data <- data.frame(read.table("none_th.txt", header=TRUE, fill=TRUE, colClasses = c(rep("numeric", 3), "character", "numeric", rep("NULL", 130))))
  data <- rbind(data, data.frame(read.table("all_th.txt", header=TRUE, fill=TRUE, colClasses = c(rep("numeric", 3), "character", "numeric", rep("NULL", 130)))))
  data <- data[, colSums(is.na(data)) != nrow(data)] #delete all empty columns
  #data$length <- apply(data[,5:104], MARGIN=1, function(x) {length(x[x != "NULL"])})
  data$id <- as.factor(data$id)
  data$repetition <- as.factor(data$repetition)
  data$type <- as.factor(data$type)
  data$threshold <- as.factor(data$threshold)
  return(data)
}

agregadeData <- function() {
  agregaded <- data %>% group_by(type, threshold, repetition, id) %>%  summarise(messages=n())
  #agregaded <- left_join(agregaded, data %>% group_by(type, threhold, repetition, id) %>% summarise(avg_length=mean(length)), by=c("id"="id", "repetition"="repetition", "threshold"="threshold", "type"="type"))
  agregaded[is.na(agregaded)] <- 0
  agregaded <- left_join(agregaded, data %>% group_by(type, threshold, repetition, id) %>% summarize(lifespan=max(time) - min(time)), by=c("id"="id", "repetition"="repetition", "threshold"="threshold", "type"="type"))
  return(agregaded)
}

data <- readRawData()
agregaded <- agregadeData()
#agregaded <- agregaded[as.numeric(agregaded$id) > 1000,]

messagesHistogram <- function(th) {
  breaks <- 220
  histogramNON <- hist(log10(agregaded[agregaded$type == "non" & agregaded$threshold == th, 5][[1]]), breaks=breaks)
  label <- rep("non", length(histogramNON$mids))
  factorNON <- 10^histogramNON$breaks[-1]-10^histogramNON$breaks[-length(histogramNON$breaks)]
  sumNON <- sum(histogramNON$counts/factorNON)
  histogram <- data.frame("mids"=histogramNON$mids, "range"=histogramNON$counts/factorNON/sumNON, "type"=label)
  
  histogramALL <- hist(log10(agregaded[agregaded$type == "all" & agregaded$threshold == th, 5][[1]]), breaks=breaks)
  label <- rep("all", length(histogramALL$mids))
  factorALL <- 10^histogramALL$breaks[-1]-10^histogramALL$breaks[-length(histogramALL$breaks)]
  sumALL <- sum(histogramALL$counts/factorALL)
  histogram <- rbind(histogram, data.frame("mids"=histogramALL$mids, "range"=histogramALL$counts/factorALL/sumALL, "type"=label))
  
  histogram <- histogram[histogram$range != 0,]
  
  plot <- ggplot(data=histogram, aes(x=mids, y=range, color=type)) + 
    geom_point(size=3) +
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
    scale_color_manual(values=c("#4A3678", "#FF62AA"), 
                        breaks=c("all", "non"),
                        labels=c("?? = 0,05 (mo¿liwoœæ edycji)", "?? = 0 (brak mo¿liwoœci edycji)")) +
    xlab("liczba udostêpnieñ jednej informacji") +
    ylab("gêstoœæ prawdopodobieñstwa") +
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

th <- 0
#png(file=paste("lifespan_histogram_types_", th, "_pl.png", sep=""), width=750, height=500)
messagesHistogram(th)
#dev.off()

timeHistogram <- function(th) {
  breaks <- 220
  histogramNON <- hist(agregaded[agregaded$type == "non" & agregaded$threshold == th, 6][[1]], breaks=breaks)
  label <- rep("non", length(histogramNON$mids))
  #factorNON <- 10^histogramNON$breaks[-1]-10^histogramNON$breaks[-length(histogramNON$breaks)]
  sumNON <- sum(histogramNON$counts)
  histogram <- data.frame("mids"=histogramNON$mids, "range"=histogramNON$counts/sumNON, "type"=label)
  
  histogramALL <- hist(agregaded[agregaded$type == "all" & agregaded$threshold == th, 6][[1]], breaks=breaks)
  label <- rep("all", length(histogramALL$mids))
  #factorALL <- 10^histogramALL$breaks[-1]-10^histogramALL$breaks[-length(histogramALL$breaks)]
  sumALL <- sum(histogramALL$counts)
  histogram <- rbind(histogram, data.frame("mids"=histogramALL$mids, "range"=histogramALL$counts/sumALL, "type"=label))
  
  histogram <- histogram[histogram$range != 0,]
  
  plot <- ggplot(data=histogram, aes(x=mids, y=range, color=type)) + 
    geom_point(size=3) +
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
    scale_color_manual(values=c("#4A3678", "#FF62AA"), 
                       breaks=c("all", "non"),
                       labels=c("?? = 0,05 (mo¿liwoœæ edycji)", "?? = 0 (brak mo¿liwoœci edycji)")) +
    xlab("czas ¿ycia informacji") +
    ylab("gêstoœæ prawdopodobieñstwa") +
    scale_x_continuous(limits = c(0, 250000)) + 
    scale_y_continuous(trans = 'log10',
                       labels = trans_format("log10", math_format(10^.x))) +
    annotation_logticks(sides="l")
  print(plot)
}

th <- 0.5
#png(file=paste("lifespan_histogram_types_", th, "_pl.png", sep=""), width=750, height=500)
timeHistogram(th)
#dev.off()