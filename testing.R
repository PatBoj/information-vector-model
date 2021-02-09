library(ggplot2)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))

data <- read.table("metropolis.txt")
data <- data.frame(x = seq(1, nrow(data), 1), y = data$V1)

ggplot(data, aes(x = x, y = y)) +
  geom_point() +
  scale_y_continuous(limits = c(min(data$y), 0))
