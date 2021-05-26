library(ggplot2)
library(dplyr)
library(scales)
library(shiny)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))

params <- 11 # number of parameters
dirName <- "20_04_2021_other_hist/"
fileNames <- list.files(dirName)

rawData <- lapply(fileNames, function(x) {data.frame(read.table(paste(dirName, x, sep=""), skip = params, sep = "\t", header = FALSE, col.names = c("x", "y")))})
parameters <- lapply(fileNames, function(x) {data.frame(read.table(paste(dirName, x, sep=""), nrows = params, sep = "\t"))})

rawData <- lapply(seq_along(rawData), function(i) {cbind(rawData[[i]], network_type = rep(parameters[[i]][3,2], nrow(rawData[[i]])))})
rawData <- lapply(seq_along(rawData), function(i) {cbind(rawData[[i]], tau = rep(parameters[[i]][6,2], nrow(rawData[[i]])))})
rawData <- lapply(seq_along(rawData), function(i) {cbind(rawData[[i]], eta = rep(parameters[[i]][5,2], nrow(rawData[[i]])))})

data <- bind_rows(rawData)
data$network_type <- as.factor(data$network_type)
data$tau <- as.factor(data$tau)
data$eta <- as.factor(data$eta)

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
    scale_y_continuous(labels = math_format(10^.x),
                       limits = c(-7, 1.7),
                       breaks = seq(-8, 1)) +
    annotation_logticks()
  plot
}

ui <- fluidPage(
  sidebarLayout(
    sidebarPanel(
      checkboxGroupInput("types", "Network types:",
                         c("Random" = "ER",
                           "Non-scale" = "BA")),
      
      checkboxGroupInput("edit", "Ability to edit:",
                         c("Yes" = "0.05",
                           "No" = "0.0")),
      sliderInput("tau", "Threshold", min = -1, max = 1, step = 0.01, value = 0.0)
    ),
    
    mainPanel(
      plotOutput("my_histogram", height = 900)
    )
  )
)

server <- function(input, output) {
  filtered <- reactive({
    data %>%
      filter(network_type %in% input$types,
             tau == format(as.numeric(input$tau), nsmall = 1),
             eta %in% input$edit)
  })
  
  output$my_histogram <- renderPlot({
    popularityHistogram(filtered())
  })
}

shinyApp(ui = ui, server = server)
