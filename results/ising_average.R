library(ggplot2)
library(shiny)
library(dplyr)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))
n <- 20

er <- read.table("ising_test/average_ER.txt", sep="\t", col.names=c("time", "avg"))
ba <- read.table("ising_test/average_BA.txt", sep="\t", col.names=c("time", "avg"))
er$topology <- "ER"
ba$topology <- "BA"

data <- bind_rows(er, ba)

data <- data.frame(
  data %>%
    group_by(time, topology) %>%
    summarise(average = mean(avg), deviation = sd(avg)/sqrt(20))
)

averageSimilarity <- function(data) {
  plot <- ggplot(data = data, aes(x = time, y = average, color = topology)) + 
    geom_ribbon(aes(ymin = average - deviation, ymax = average + deviation, fill = topology))+
    theme(
      text=element_text(size = 28),
      axis.text=element_text(color= "black"),
      axis.ticks.length = unit(0, "cm"),
      plot.title=element_text(hjust = 0.5),
      panel.border=element_rect(fill = NA),
      panel.background=element_blank(),
      legend.key=element_rect(fill = NA, color = NA),
      legend.background=element_rect(fill = (alpha("white", 0))),
      legend.title=element_blank(),
      legend.position=c(0.6, 0.8),
      legend.box.background=element_rect(colour = "black"),
      legend.spacing.y = unit(0, "mm")
    ) + 
    xlab("time") +
    ylab("cosine similarity between agents") 
  plot
}
