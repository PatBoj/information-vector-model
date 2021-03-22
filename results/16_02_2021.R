library(ggplot2)
library(dplyr)
library(scales)
library(shiny)

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
      
      factor <- 10^tempHist$breaks[-1] - 10^tempHist$breaks[-length(tempHist$breaks)]
      sum <- sum(tempHist$counts/factor)
      tempHist <- data.frame(x = tempHist$mids, y = tempHist$counts/factor/sum)
      tempHist <- tempHist[tempHist$y != 0,]
      
      tempHist$network_type <- as.factor(rep(levels(data$network_type)[i], nrow(tempHist)))
      tempHist$tau <- as.factor(rep(levels(data$tau)[j], nrow(tempHist)))
      tempHist$eta <- as.factor(rep(levels(data$eta)[k], nrow(tempHist)))
      
      newData[[length(newData) + 1]] <- tempHist
    }
  }
}

newData <- bind_rows(newData)

popularityHistogram <- function(histogram) {
  plot <- ggplot(data = histogram, aes(x = x, y = y, color = network_type, shape = eta)) + 
    geom_point(size = 3) +
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
    xlab("number of shares") +
    ylab("probability density") +
    scale_shape_manual(values = c(4, 20)) +
    scale_x_continuous(labels = math_format(10^.x),
                       limits = c(0, 3),
                       breaks = seq(0,5)) + 
    scale_y_continuous(trans = 'log10',
                       labels = trans_format("log10", math_format(10^.x)),
                       limits = c(.3*10^-8, .3*10^1),
                       breaks = 10^(-9:1)) +
    annotation_logticks(sides="lb")
  plot
}

ui <- fluidPage(
  sidebarLayout(
    sidebarPanel(
      checkboxGroupInput("types", "Network types:",
                         c("Random" = "ER",
                           "Non-scale" = "BA",
                           "Lattice" = "SQ")),
      
      checkboxGroupInput("edit", "Ability to edit:",
                         c("Yes" = "0.05",
                           "No" = "0.0")),
      sliderInput("tau", "Threshold", min = -1, max = 1, step = 0.2, value = 0.0)
    ),
    
    mainPanel(
      plotOutput("my_histogram", height = 900)
    )
  )
)

server <- function(input, output) {
  filtered <- reactive({
    newData %>%
      filter(network_type %in% input$types,
             tau == format(as.numeric(input$tau), nsmall = 1),
             eta %in% input$edit)
  })
  
  output$my_histogram <- renderPlot({
    popularityHistogram(filtered())
  })
}

shinyApp(ui = ui, server = server)
