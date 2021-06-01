library(ggplot2)
library(dplyr)
library(scales)
library(shiny)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))

dataReader <- function(dirName) {
  params <- 13 # number of parameters
  fileNames <- list.files(dirName)
  
  rawData <- lapply(fileNames, function(x) {data.frame(read.table(paste(dirName, x, sep=""), skip = params, sep = "\t", header = TRUE, col.names = c("x", "y", "length", "emotion")))})
  parameters <- lapply(fileNames, function(x) {data.frame(read.table(paste(dirName, x, sep=""), nrows = params, sep = "\t"))})
  
  rawData <- lapply(seq_along(rawData), function(i) {cbind(rawData[[i]], network_type = rep(parameters[[i]][3,2], nrow(rawData[[i]])))})
  rawData <- lapply(seq_along(rawData), function(i) {cbind(rawData[[i]], tau = rep(parameters[[i]][6,2], nrow(rawData[[i]])))})
  rawData <- lapply(seq_along(rawData), function(i) {cbind(rawData[[i]], eta = rep(parameters[[i]][5,2], nrow(rawData[[i]])))})
  rawData <- lapply(seq_along(rawData), function(i) {cbind(rawData[[i]], type = rep(parameters[[i]][8,2], nrow(rawData[[i]])))})
  rawData <- lapply(seq_along(rawData), function(i) {cbind(rawData[[i]], sim = rep(round(as.numeric(parameters[[i]][9,2]), digits=2), nrow(rawData[[i]])))})
  
  data <- bind_rows(rawData)
  data$network_type <- as.factor(data$network_type)
  data$tau <- as.factor(data$tau)
  data$eta <- as.factor(data$eta)
  data$type <- as.factor(data$type)
  data$sim <- as.factor(data$sim)
  
  return(data)
}

competition <- dataReader("26_05_2021_hist/")
#noCompetition <- dataReader("26_05_2021_other_hist/")

#data <- rbind(competition, noCompetition)
data <- competition

popularityHistogram <- function(histogram) {
  plot <- ggplot(data = histogram, aes(x = x, y = y, color = network_type, shape = eta, fill = emotion, size = length)) + 
    geom_point() +
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
    scale_shape_manual(values = c(21, 22)) +
    scale_x_continuous(labels = math_format(10^.x),
                       limits = c(0, 3),
                       breaks = seq(0,5)) + 
    scale_y_continuous(labels = math_format(10^.x),
                       limits = c(-7, 1.7),
                       breaks = seq(-8, 1)) +
    annotation_logticks()
  
  plot <- plot + scale_color_manual(values = c("red", "green"))
  plot<- plot + scale_fill_gradient2(limits = c(-1, 1), low = "darkred", mid = "white", high = "darkgreen")
  plot<- plot + scale_size_binned()
    
  
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
      checkboxGroupInput("competition", "With competition",
                         c("Yes" = "with_competition",
                           "No" = "without_competition")),
      sliderInput("tau", "Threshold", min = -1, max = 1, step = 0.02, value = 0.0),
      sliderInput("sim", "Similarity", min = 0, max = 0.6, step = 0.2, value = 0.0)
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
             eta %in% input$edit,
             sim %in% input$sim,
             type %in% input$competition)
  })
  
  output$my_histogram <- renderPlot({
    popularityHistogram(filtered())
  })
}

shinyApp(ui = ui, server = server)
